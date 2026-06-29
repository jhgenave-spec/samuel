package com.example.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.Appointment
import com.example.data.AppointmentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AppointmentViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AppointmentRepository
    val allAppointments: StateFlow<List<Appointment>>

    // Passcode Configuration & Storage for entering Schedule
    private val sharedPrefs = application.getSharedPreferences("prospere_prefs", Context.MODE_PRIVATE)

    // Dark/Light Mode Preference State
    private val _isDarkMode = MutableStateFlow(sharedPrefs.getBoolean("is_dark_mode", false))
    val isDarkMode = _isDarkMode.asStateFlow()

    fun setDarkMode(dark: Boolean) {
        _isDarkMode.value = dark
        sharedPrefs.edit().putBoolean("is_dark_mode", dark).apply()
    }

    // Dynamic Detailing services state
    private val _services = MutableStateFlow<List<ServiceOption>>(emptyList())
    val services = _services.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = AppointmentRepository(database.appointmentDao())
        
        allAppointments = repository.allAppointments
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

        // Version migration to force load the new default prices/services
        val servicesVersion = sharedPrefs.getInt("services_version_v2", 0)
        if (servicesVersion < 2) {
            sharedPrefs.edit()
                .remove("detailing_services")
                .putInt("services_version_v2", 2)
                .apply()
        }

        // Load dynamic services
        _services.value = loadServices()
    }

    // Input States
    private val _customerName = MutableStateFlow("")
    val customerName = _customerName.asStateFlow()

    private val _customerPhone = MutableStateFlow("")
    val customerPhone = _customerPhone.asStateFlow()

    private val _carModel = MutableStateFlow("")
    val carModel = _carModel.asStateFlow()

    private val _selectedDate = MutableStateFlow(getTodayString())
    val selectedDate = _selectedDate.asStateFlow()

    private val _selectedFilterDate = MutableStateFlow(getTodayString())
    val selectedFilterDate = _selectedFilterDate.asStateFlow()

    fun setSelectedFilterDate(date: String) {
        _selectedFilterDate.value = date
    }

    private val _selectedTimeSlot = MutableStateFlow("16:00")
    val selectedTimeSlot = _selectedTimeSlot.asStateFlow()

    private val _selectedServiceType = MutableStateFlow("Interior ek Exterior")
    val selectedServiceType = _selectedServiceType.asStateFlow()

    private val _automatedReminderEnabled = MutableStateFlow(true)
    val automatedReminderEnabled = _automatedReminderEnabled.asStateFlow()


    private val _schedulePasscode = MutableStateFlow(sharedPrefs.getString("schedule_passcode", "2011") ?: "2011")
    val schedulePasscode = _schedulePasscode.asStateFlow()

    private val _isScheduleUnlocked = MutableStateFlow(false)
    val isScheduleUnlocked = _isScheduleUnlocked.asStateFlow()

    fun unlockSchedule(code: String): Boolean {
        if (code == _schedulePasscode.value) {
            _isScheduleUnlocked.value = true
            return true
        }
        return false
    }

    fun lockSchedule() {
        _isScheduleUnlocked.value = false
    }

    fun updatePasscode(newCode: String): Boolean {
        if (newCode.length >= 4) {
            _schedulePasscode.value = newCode
            sharedPrefs.edit().putString("schedule_passcode", newCode).apply()
            return true
        }
        return false
    }

    // Screen State
    private val _currentTab = MutableStateFlow(0) // 0 = Book, 1 = Calendar, 2 = About/Reminders
    val currentTab = _currentTab.asStateFlow()

    // Reminder Simulated Logs State
    private val _reminderLogs = MutableStateFlow<List<String>>(
        listOf(
            "System initialized. Automated reminders active.",
            "SMS gateway running. Pre-booked reminders queue monitored."
        )
    )
    val reminderLogs = _reminderLogs.asStateFlow()

    // Available operating hours slots
    val timeSlots: StateFlow<List<String>> = _selectedDate
        .map { date -> getTimeSlotsForDate(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), getDefaultTimeSlots())

    fun getTimeSlotsForDate(dateString: String): List<String> {
        return try {
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = format.parse(dateString) ?: return getDefaultTimeSlots()
            val cal = Calendar.getInstance()
            cal.time = date
            when (cal.get(Calendar.DAY_OF_WEEK)) {
                Calendar.SATURDAY -> {
                    // Open from 12:00 to 18:00
                    listOf(
                        "12:00", "12:30", "13:00", "13:30",
                        "14:00", "14:30", "15:00", "15:30",
                        "16:00", "16:30", "17:00", "17:30",
                        "18:00"
                    )
                }
                Calendar.SUNDAY -> {
                    // Open from 07:30 to 17:00
                    listOf(
                        "07:30", "08:00", "08:30", "09:00", "09:30",
                        "10:00", "10:30", "11:00", "11:30", "12:00",
                        "12:30", "13:00", "13:30", "14:00", "14:30",
                        "15:00", "15:30", "16:00", "16:30", "17:00"
                    )
                }
                else -> {
                    getDefaultTimeSlots()
                }
            }
        } catch (e: Exception) {
            getDefaultTimeSlots()
        }
    }

    private fun getDefaultTimeSlots(): List<String> {
        return listOf(
            "16:00", "16:30", "17:00", "17:30", 
            "18:00", "18:30", "19:00", "19:30", "20:00"
        )
    }

    // Current calculated price based on selection
    val currentPrice: StateFlow<Double> = combine(_selectedServiceType, _services) { service, serviceList ->
        serviceList.find { it.name == service }?.price ?: 0.0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 500.0)

    // Load services from SharedPreferences using JSONObject/JSONArray
    private fun loadServices(): List<ServiceOption> {
        val jsonStr = sharedPrefs.getString("detailing_services", null)
        if (jsonStr == null) {
            return listOf(
                ServiceOption("Interior ek Exterior", 500.0, "Complete inside & outside deep wash, vacuum, dashboard cleaning"),
                ServiceOption("Interior only", 200.0, "Steam deep-cleaning, vacuum, dashboard conditioning, glass cleaning"),
                ServiceOption("Exterior only", 300.0, "Premium foam hand wash, high-gloss tire shine, and spray wax protection"),
                ServiceOption("Motorcycle", 200.0, "Detailed bike wash, chain cleaning/lube, tire dressing, and wax shine"),
                ServiceOption("Wheel Polishing", 100.0, "Intense wheel arch & rim cleaning, brake dust removal, and metallic polish"),
                ServiceOption("Polishing", 0.0, "Available soon"),
                ServiceOption("Detailing", 0.0, "Available soon")
            )
        }
        val list = mutableListOf<ServiceOption>()
        try {
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    ServiceOption(
                        name = obj.getString("name"),
                        price = obj.getDouble("price"),
                        description = obj.getString("description")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    // Save services to SharedPreferences
    private fun saveServices(list: List<ServiceOption>) {
        val array = JSONArray()
        for (service in list) {
            val obj = JSONObject()
            obj.put("name", service.name)
            obj.put("price", service.price)
            obj.put("description", service.description)
            array.put(obj)
        }
        sharedPrefs.edit().putString("detailing_services", array.toString()).apply()
    }

    // Admin function: update an existing service option
    fun updateService(index: Int, name: String, price: Double, description: String) {
        val currentList = _services.value.toMutableList()
        if (index in currentList.indices) {
            currentList[index] = ServiceOption(name, price, description)
            _services.value = currentList
            saveServices(currentList)
        }
    }

    // Admin function: add a new service option
    fun addService(name: String, price: Double, description: String) {
        val currentList = _services.value.toMutableList()
        currentList.add(ServiceOption(name, price, description))
        _services.value = currentList
        saveServices(currentList)
    }

    // Admin function: delete a service option
    fun deleteService(index: Int) {
        val currentList = _services.value.toMutableList()
        if (index in currentList.indices) {
            currentList.removeAt(index)
            _services.value = currentList
            saveServices(currentList)
            // Reset selection if it was deleted
            if (currentList.isNotEmpty()) {
                _selectedServiceType.value = currentList[0].name
            }
        }
    }

    fun setCustomerName(name: String) { _customerName.value = name }
    fun setCustomerPhone(phone: String) { _customerPhone.value = phone }
    fun setCarModel(model: String) { _carModel.value = model }
    fun setSelectedDate(date: String) {
        _selectedDate.value = date
        val validSlots = getTimeSlotsForDate(date)
        if (!validSlots.contains(_selectedTimeSlot.value)) {
            _selectedTimeSlot.value = validSlots.firstOrNull() ?: "16:00"
        }
    }
    fun setSelectedTimeSlot(slot: String) { _selectedTimeSlot.value = slot }
    fun setSelectedServiceType(service: String) { _selectedServiceType.value = service }
    fun setAutomatedReminderEnabled(enabled: Boolean) { _automatedReminderEnabled.value = enabled }
    fun setCurrentTab(tab: Int) { _currentTab.value = tab }

    // Check if the selected date falls on a weekday (Monday to Friday)
    fun isValidWeekday(dateString: String): Boolean {
        return true
    }

    // Book Detail appointment
    fun bookAppointment(context: Context, onSuccess: () -> Unit) {
        val name = _customerName.value.trim()
        val phone = _customerPhone.value.trim()
        val car = _carModel.value.trim()
        val date = _selectedDate.value
        val time = _selectedTimeSlot.value
        val service = _selectedServiceType.value
        val isReminderOn = _automatedReminderEnabled.value

        if (name.isEmpty() || phone.isEmpty() || car.isEmpty()) {
            Toast.makeText(context, "Please fill in all customer and car details", Toast.LENGTH_LONG).show()
            return
        }

        // Validate time slot is within operating hours for the selected day
        val dayOfWeek = try {
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val parsedDate = format.parse(date)
            val cal = Calendar.getInstance()
            if (parsedDate != null) cal.time = parsedDate
            cal.get(Calendar.DAY_OF_WEEK)
        } catch (e: Exception) {
            Calendar.MONDAY
        }

        val validSlots = getTimeSlotsForDate(date)
        if (!validSlots.contains(time)) {
            val errorMsg = when (dayOfWeek) {
                Calendar.SATURDAY -> "On Saturday we are only open between 12:00 and 18:00!"
                Calendar.SUNDAY -> "On Sunday we are only open between 07:30 and 17:00!"
                else -> "On weekdays we are only open between 16:00 and 20:00!"
            }
            Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
            return
        }

        val isAlreadyBooked = allAppointments.value.any { it.dateString == date && it.timeSlot == time }
        if (isAlreadyBooked) {
            Toast.makeText(context, "This slot ($time) on $date is already booked! Please select another slot.", Toast.LENGTH_LONG).show()
            return
        }

        val price = services.value.find { it.name == service }?.price ?: 40.0

        viewModelScope.launch {
            val appointment = Appointment(
                customerName = name,
                customerPhone = phone,
                carModel = car,
                dateString = date,
                timeSlot = time,
                serviceType = service,
                price = price,
                isAutomatedReminderEnabled = isReminderOn
            )
            repository.insert(appointment)

            // Auto-unlock schedule and focus the newly booked date for the owner panel immediately
            _selectedFilterDate.value = date
            _isScheduleUnlocked.value = true

            // Log simulation of scheduled reminder
            val reminderTime = "14:00"
            val logMsg = "Scheduled automated text reminder for $name ($phone) at $reminderTime on $date"
            val updatedLogs = _reminderLogs.value.toMutableList()
            updatedLogs.add(0, logMsg)
            _reminderLogs.value = updatedLogs

            Toast.makeText(context, "Booking successfully scheduled at $time!", Toast.LENGTH_LONG).show()
            
            // Launch WhatsApp/SMS confirmation message to 54727552
            sendWhatsAppConfirmation(context, name, car, service, date, time, price)

            // Clear inputs
            _customerName.value = ""
            _customerPhone.value = ""
            _carModel.value = ""
            
            onSuccess()
        }
    }

    // Trigger sending confirmation message to WhatsApp / SMS at 54727552
    fun sendWhatsAppConfirmation(
        context: Context,
        name: String,
        car: String,
        service: String,
        date: String,
        time: String,
        price: Double
    ) {
        val number = "54727552"
        val cleanNumber = "230$number" // Mauritius code is +230
        val message = "Hello COCO Car Detailing, I would like to confirm my detailing booking:\n" +
                "👤 Name: $name\n" +
                "🚗 Car: $car\n" +
                "✨ Service: $service\n" +
                "📅 Date: $date\n" +
                "🕒 Time: $time\n" +
                "💰 Total: Rs ${String.format(Locale.US, "%,.0f", price)}"
        
        val url = "https://wa.me/$cleanNumber?text=${Uri.encode(message)}"
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback to SMS
            try {
                val uri = Uri.parse("smsto:$number")
                val smsIntent = Intent(Intent.ACTION_SENDTO, uri).apply {
                    putExtra("sms_body", message)
                }
                context.startActivity(smsIntent)
            } catch (ex: Exception) {
                Toast.makeText(context, "Could not open WhatsApp or SMS", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Cancel appointment
    fun deleteAppointment(appointment: Appointment) {
        viewModelScope.launch {
            repository.delete(appointment)
            val logMsg = "Cancelled booking & automated reminders for ${appointment.customerName} on ${appointment.dateString}"
            val updatedLogs = _reminderLogs.value.toMutableList()
            updatedLogs.add(0, logMsg)
            _reminderLogs.value = updatedLogs
        }
    }

    // Send instant manual text reminder using prefilled SMS intent
    fun sendSmsReminder(context: Context, appointment: Appointment) {
        val message = "Hi ${appointment.customerName}, this is Prospere Detailing. Just reminding you of your ${appointment.serviceType} appointment for your ${appointment.carModel} on ${appointment.dateString} at ${appointment.timeSlot}. Thank you!"
        try {
            val uri = Uri.parse("smsto:${appointment.customerPhone}")
            val intent = Intent(Intent.ACTION_SENDTO, uri).apply {
                putExtra("sms_body", message)
            }
            context.startActivity(intent)
            
            // Log this action
            viewModelScope.launch {
                val updatedAppt = appointment.copy(reminderSent = true)
                repository.update(updatedAppt)
                
                val logMsg = "Manual SMS Remind triggered for ${appointment.customerName} (${appointment.customerPhone})"
                val updatedLogs = _reminderLogs.value.toMutableList()
                updatedLogs.add(0, logMsg)
                _reminderLogs.value = updatedLogs
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to launch SMS application: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    // Trigger open WhatsApp with the configured number: 54727552
    fun openWhatsApp(context: Context) {
        val number = "23054727552"
        val url = "https://wa.me/$number"
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Could not open WhatsApp. Opening web browser...", Toast.LENGTH_SHORT).show()
            try {
                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(webIntent)
            } catch (ex: Exception) {
                Toast.makeText(context, "Error: ${ex.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Helper to get today, in yyyy-MM-dd format
    private fun getTodayString(): String {
        val cal = Calendar.getInstance()
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return format.format(cal.time)
    }
}

data class ServiceOption(
    val name: String,
    val price: Double,
    val description: String
)
