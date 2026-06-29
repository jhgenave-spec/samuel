package com.example

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Appointment
import com.example.ui.AppointmentViewModel
import com.example.ui.ServiceOption
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.SecondaryLavender
import com.example.ui.theme.HighlightDarkPurple
import com.example.ui.theme.PolishBackground
import com.example.ui.theme.PureWhite
import com.example.ui.theme.CoreDarkText
import com.example.ui.theme.MutedText
import com.example.ui.theme.ThinBorderColor
import com.example.ui.theme.InputBorderColor
import com.example.ui.theme.SoftCardBg
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.DangerRed
import com.example.ui.theme.WarningGold
import com.example.ui.theme.MyApplicationTheme
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import android.widget.Toast

class MainActivity : ComponentActivity() {
    private val viewModel: AppointmentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDark by viewModel.isDarkMode.collectAsState()
            MyApplicationTheme(darkTheme = isDark) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = PolishBackground
                ) { innerPadding ->
                    MainScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    viewModel: AppointmentViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentTab by viewModel.currentTab.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PolishBackground)
    ) {
        // Top Header with Branding Image & Logo
        HeaderSection(viewModel = viewModel)

        // Tab Navigation Bar
        CustomTabBar(
            selectedTab = currentTab,
            onTabSelected = { viewModel.setCurrentTab(it) }
        )

        // Main Screen Content based on active tab
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (currentTab) {
                0 -> BookServiceTab(viewModel = viewModel)
                1 -> CalendarScheduleTab(viewModel = viewModel)
                2 -> RemindersAboutTab(viewModel = viewModel)
            }
        }

        // WhatsApp Interactive Contact Footer (Always Visible at bottom)
        WhatsAppFooter(
            number = "5472 7552",
            onContactClick = { viewModel.openWhatsApp(context) }
        )
    }
}

@Composable
fun HeaderSection(viewModel: AppointmentViewModel) {
    val isDark by viewModel.isDarkMode.collectAsState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(PureWhite)
            .border(width = 1.dp, color = ThinBorderColor)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_coco_logo),
                contentDescription = "COCO Car Detailing Logo",
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .testTag("coco_logo_image"),
                contentScale = ContentScale.Crop
            )
            Column {
                Text(
                    text = "COCO",
                    color = CoreDarkText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.testTag("prospere_brand_name")
                )
                Text(
                    text = "Clean Inside. Gloss Outside.",
                    color = PrimaryPurple,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Dark Mode toggle
            IconButton(
                onClick = { viewModel.setDarkMode(!isDark) },
                modifier = Modifier.size(40.dp)
            ) {
                Text(
                    text = if (isDark) "☀️" else "🌙",
                    fontSize = 20.sp
                )
            }
            
            // Notifications/Status indicator
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable { /* Active feed */ }
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = MutedText,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun CustomTabBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(PureWhite)
            .border(width = 1.dp, color = ThinBorderColor)
            .padding(vertical = 8.dp, horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        TabItem(
            label = "Book Slot",
            icon = "✨",
            isSelected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            modifier = Modifier.weight(1f).testTag("tab_book")
        )
        TabItem(
            label = "Admin Panel",
            icon = "⚙️",
            isSelected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            modifier = Modifier.weight(1f).testTag("tab_schedule")
        )
        TabItem(
            label = "Reminders & Info",
            icon = "🔔",
            isSelected = selectedTab == 2,
            onClick = { onTabSelected(2) },
            modifier = Modifier.weight(1f).testTag("tab_about")
        )
    }
}

@Composable
fun TabItem(
    label: String,
    icon: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(horizontal = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) SecondaryLavender else Color.Transparent)
            .border(
                width = 1.dp,
                color = if (isSelected) PrimaryPurple else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = icon, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = if (isSelected) HighlightDarkPurple else MutedText,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
fun BookServiceTab(
    viewModel: AppointmentViewModel
) {
    val context = LocalContext.current

    val name by viewModel.customerName.collectAsState()
    val phone by viewModel.customerPhone.collectAsState()
    val carModel by viewModel.carModel.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val selectedTimeSlot by viewModel.selectedTimeSlot.collectAsState()
    val selectedServiceType by viewModel.selectedServiceType.collectAsState()
    val isAutomatedReminderOn by viewModel.automatedReminderEnabled.collectAsState()
    val price by viewModel.currentPrice.collectAsState()
    val services by viewModel.services.collectAsState()
    val appointments by viewModel.allAppointments.collectAsState()
    val timeSlots by viewModel.timeSlots.collectAsState()
    
    val bookedSlots = remember(appointments, selectedDate) {
        appointments.filter { it.dateString == selectedDate }.map { it.timeSlot }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // Core instructions / Operational Hours notice
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SoftCardBg),
                border = BorderStroke(1.dp, ThinBorderColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🕒", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Workplace Hours",
                            color = CoreDarkText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Monday to Friday: 16:00 to 20:00 (4:00 PM - 8:00 PM)",
                            color = PrimaryPurple,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Section: Vehicle & Client Details
        item {
            Column {
                SectionTitle(text = "1. Customer & Vehicle Details")
                
                OutlinedTextField(
                    value = name,
                    onValueChange = { viewModel.setCustomerName(it) },
                    label = { Text("Customer Name") },
                    placeholder = { Text("e.g. Jean") },
                    leadingIcon = { Text("👤", modifier = Modifier.padding(start = 8.dp)) },
                    colors = textFieldColors(),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .testTag("input_customer_name")
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { viewModel.setCustomerPhone(it) },
                    label = { Text("Customer Phone Number") },
                    placeholder = { Text("e.g. 5999 9999") },
                    leadingIcon = { Text("📞", modifier = Modifier.padding(start = 8.dp)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    colors = textFieldColors(),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .testTag("input_customer_phone")
                )

                OutlinedTextField(
                    value = carModel,
                    onValueChange = { viewModel.setCarModel(it) },
                    label = { Text("Car Model & Brand") },
                    placeholder = { Text("e.g. Tesla Model 3 Black") },
                    leadingIcon = { Text("🚗", modifier = Modifier.padding(start = 8.dp)) },
                    colors = textFieldColors(),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .testTag("input_car_model")
                )
            }
        }

        // Section: Select Service Option
        item {
            Column {
                SectionTitle(text = "2. Select Detailing Option")
                Spacer(modifier = Modifier.height(4.dp))
                services.forEach { service ->
                    ServiceOptionCard(
                        service = service,
                        isSelected = service.name == selectedServiceType,
                        onSelect = {
                            if (service.price <= 0.0) {
                                Toast.makeText(context, "${service.name} is coming soon and cannot be booked yet!", Toast.LENGTH_SHORT).show()
                            } else {
                                viewModel.setSelectedServiceType(service.name)
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        // Section: Select Day (Scheduler Calendar Interactive Selector)
        item {
            Column {
                SectionTitle(text = "3. Select Booking Date")
                Spacer(modifier = Modifier.height(6.dp))
                
                // Horizontal scrollable Weekday Picker (Next 7 consecutive days)
                HorizontalWeekdayPicker(
                    selectedDate = selectedDate,
                    onDateSelected = { viewModel.setSelectedDate(it) }
                )

                // Select Custom/Future Date Button (Infinite Dates)
                Spacer(modifier = Modifier.height(8.dp))
                val dateList = remember { getNextSevenDays() }
                val isCustomDate = dateList.none { it.formattedString == selectedDate }
                val displayDate = remember(selectedDate) {
                    try {
                        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val outputFormat = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault())
                        val parsed = inputFormat.parse(selectedDate)
                        if (parsed != null) outputFormat.format(parsed) else selectedDate
                    } catch (e: Exception) {
                        selectedDate
                    }
                }

                androidx.compose.material3.OutlinedButton(
                    onClick = {
                        val calendar = Calendar.getInstance()
                        try {
                            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            val parsed = sdf.parse(selectedDate)
                            if (parsed != null) {
                                calendar.time = parsed
                            }
                        } catch (e: Exception) {}

                        val datePickerDialog = android.app.DatePickerDialog(
                            context,
                            { _, year, month, dayOfMonth ->
                                val selectedCal = Calendar.getInstance().apply {
                                    set(Calendar.YEAR, year)
                                    set(Calendar.MONTH, month)
                                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                                }
                                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                viewModel.setSelectedDate(sdf.format(selectedCal.time))
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        )
                        // Allow selecting today or any future date (infinite)
                        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
                        datePickerDialog.show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("custom_date_picker_button"),
                    border = BorderStroke(1.dp, if (isCustomDate) PrimaryPurple else ThinBorderColor),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (isCustomDate) SecondaryLavender else PureWhite,
                        contentColor = PrimaryPurple
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (isCustomDate) "📅 Custom Date: $displayDate" else "📅 Select Other Date...",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // Section: Select Available Time Slot
        item {
            Column {
                val hourRangeText = remember(selectedDate) {
                    val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    try {
                        val date = format.parse(selectedDate)
                        val cal = Calendar.getInstance()
                        if (date != null) cal.time = date
                        when (cal.get(Calendar.DAY_OF_WEEK)) {
                            Calendar.SATURDAY -> "Sat 12:00 to 18:00"
                            Calendar.SUNDAY -> "Sun 07:30 to 17:00"
                            else -> "Mon-Fri 16:00 to 20:00"
                        }
                    } catch (e: Exception) {
                        "16:00 to 20:00"
                    }
                }
                SectionTitle(text = "4. Select Time Slot ($hourRangeText)")
                Spacer(modifier = Modifier.height(6.dp))
                
                // Interactive Grid of Available Time Slots
                TimeSlotSelector(
                    slots = timeSlots,
                    selectedSlot = selectedTimeSlot,
                    bookedSlots = bookedSlots,
                    onSlotSelected = { viewModel.setSelectedTimeSlot(it) }
                )
            }
        }

        // Section: Automated reminders config
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SoftCardBg),
                border = BorderStroke(1.dp, ThinBorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("📩", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Automated Text Reminders",
                                color = CoreDarkText,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Sends scheduled SMS notices automatically before booking.",
                                color = MutedText,
                                fontSize = 11.sp
                            )
                        }
                    }
                    Switch(
                        checked = isAutomatedReminderOn,
                        onCheckedChange = { viewModel.setAutomatedReminderEnabled(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = PureWhite,
                            checkedTrackColor = PrimaryPurple,
                            uncheckedThumbColor = MutedText,
                            uncheckedTrackColor = SecondaryLavender
                        )
                    )
                }
            }
        }

        // Section: Checkout & Booking Summary
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                border = BorderStroke(1.dp, PrimaryPurple),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Booking Summary",
                        color = MutedText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    
                    SummaryRow(label = "Selected Service", value = selectedServiceType)
                    SummaryRow(label = "Date & Time", value = "$selectedDate at $selectedTimeSlot")
                    SummaryRow(label = "Estimated Reminders", value = if (isAutomatedReminderOn) "Active (SMS Auto)" else "Off")
                    
                    Divider(color = ThinBorderColor, modifier = Modifier.padding(vertical = 10.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total Price:",
                            color = CoreDarkText,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Rs ${String.format(Locale.US, "%,.0f", price)}",
                            color = PrimaryPurple,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            viewModel.bookAppointment(context) {
                                // Redirect to calendar upon booking success
                                viewModel.setCurrentTab(1)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                        shape = RoundedCornerShape(28.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("book_slot_button")
                    ) {
                        Text(
                            text = "BOOK DETAILING SLOT",
                            color = PureWhite,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = MutedText, fontSize = 13.sp)
        Text(text = value, color = CoreDarkText, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun CalendarScheduleTab(
    viewModel: AppointmentViewModel
) {
    val context = LocalContext.current
    val appointments by viewModel.allAppointments.collectAsState()
    val selectedFilterDate by viewModel.selectedFilterDate.collectAsState()

    val isUnlocked by viewModel.isScheduleUnlocked.collectAsState()
    val schedulePasscode by viewModel.schedulePasscode.collectAsState()

    var enteredCode by remember { mutableStateOf("") }
    var showIncorrectError by remember { mutableStateOf(false) }
    var showChangeCodeDialog by remember { mutableStateOf(false) }

    // Admin inner sub-tab: 0 = Manage Bookings, 1 = Detailing Options
    var adminSubTab by remember { mutableStateOf(0) }

    if (!isUnlocked) {
        // Protected/Locked screen state
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                border = BorderStroke(1.dp, ThinBorderColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 400.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "🔒",
                        fontSize = 48.sp
                    )

                    Text(
                        text = "Admin Access",
                        color = HighlightDarkPurple,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Please enter the security code to access the Admin Panel and manage options or schedule.",
                        color = MutedText,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )

                    OutlinedTextField(
                        value = enteredCode,
                        onValueChange = {
                            enteredCode = it
                            showIncorrectError = false
                        },
                        label = { Text("Enter Code") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (viewModel.unlockSchedule(enteredCode)) {
                                    enteredCode = ""
                                } else {
                                    showIncorrectError = true
                                }
                            }
                        ),
                        colors = textFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (showIncorrectError) {
                        Text(
                            text = "Incorrect security code. Hint: default is 2011",
                            color = DangerRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }

                    Button(
                        onClick = {
                            if (viewModel.unlockSchedule(enteredCode)) {
                                enteredCode = ""
                            } else {
                                showIncorrectError = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Unlock Admin Panel",
                            color = PureWhite,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    } else {
        // Unlocked Schedule screen state
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionTitle(text = "COCO Admin Panel")

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Change passcode button
                    Button(
                        onClick = { showChangeCodeDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = SecondaryLavender),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text("Change Code", color = HighlightDarkPurple, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    // Lock button
                    IconButton(
                        onClick = { viewModel.lockSchedule() },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Text("🔒", fontSize = 16.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))

            // Sub-tabs Selector Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(SoftCardBg)
                    .border(1.dp, ThinBorderColor, RoundedCornerShape(8.dp)),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { adminSubTab = 0 }
                        .background(if (adminSubTab == 0) SecondaryLavender else Color.Transparent)
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "📅 Bookings List",
                        color = if (adminSubTab == 0) HighlightDarkPurple else CoreDarkText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { adminSubTab = 1 }
                        .background(if (adminSubTab == 1) SecondaryLavender else Color.Transparent)
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⚙️ Detailing Options",
                        color = if (adminSubTab == 1) HighlightDarkPurple else CoreDarkText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (adminSubTab == 0) {
                // --- SUB-TAB 0: MANAGE BOOKINGS ---
                // Weekday selector to filter the schedule
                HorizontalWeekdayPicker(
                    selectedDate = selectedFilterDate,
                    onDateSelected = { viewModel.setSelectedFilterDate(it) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                val filteredAppointments = appointments.filter { it.dateString == selectedFilterDate }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Appointments for: $selectedFilterDate",
                        color = PrimaryPurple,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${filteredAppointments.size} Scheduled",
                        color = MutedText,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (filteredAppointments.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Text(text = "🧼", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No appointments on this day",
                                color = CoreDarkText,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Clean as a whistle! Daily bookings list is empty.",
                                color = MutedText,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredAppointments) { appointment ->
                            AppointmentCard(
                                appointment = appointment,
                                onSendReminder = { viewModel.sendSmsReminder(context, appointment) },
                                onDelete = { viewModel.deleteAppointment(appointment) }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(24.dp)) }
                    }
                }
            } else {
                // --- SUB-TAB 1: DETAILING OPTIONS MANAGER ---
                val dynamicServices by viewModel.services.collectAsState()
                
                var showAddServiceDialog by remember { mutableStateOf(false) }
                var editingServiceIndex by remember { mutableStateOf<Int?>(null) }
                var showEditServiceDialog by remember { mutableStateOf(false) }

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Manage Service Options",
                                color = PrimaryPurple,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Button(
                                onClick = { showAddServiceDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text("Add Option", color = PureWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    
                    itemsIndexed(dynamicServices) { index, service ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = PureWhite),
                            border = BorderStroke(1.dp, ThinBorderColor),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = service.name,
                                        color = CoreDarkText,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = "Rs ${String.format(Locale.US, "%,.0f", service.price)}",
                                        color = PrimaryPurple,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = service.description,
                                    color = MutedText,
                                    fontSize = 11.sp,
                                    lineHeight = 14.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    TextButton(
                                        onClick = {
                                            editingServiceIndex = index
                                            showEditServiceDialog = true
                                        },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text("✏️ Edit", color = PrimaryPurple, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    TextButton(
                                        onClick = {
                                            viewModel.deleteService(index)
                                            Toast.makeText(context, "Service option deleted", Toast.LENGTH_SHORT).show()
                                        },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text("🗑️ Delete", color = DangerRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }

                // Add Service Dialog
                if (showAddServiceDialog) {
                    var newName by remember { mutableStateOf("") }
                    var newPrice by remember { mutableStateOf("") }
                    var newDesc by remember { mutableStateOf("") }
                    var addError by remember { mutableStateOf("") }

                    AlertDialog(
                        onDismissRequest = { showAddServiceDialog = false },
                        title = {
                            Text(
                                text = "Add Service Option",
                                color = HighlightDarkPurple,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                OutlinedTextField(
                                    value = newName,
                                    onValueChange = { newName = it; addError = "" },
                                    label = { Text("Service Name") },
                                    singleLine = true,
                                    colors = textFieldColors(),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                OutlinedTextField(
                                    value = newPrice,
                                    onValueChange = { newPrice = it; addError = "" },
                                    label = { Text("Price (MUR)") },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    colors = textFieldColors(),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                OutlinedTextField(
                                    value = newDesc,
                                    onValueChange = { newDesc = it; addError = "" },
                                    label = { Text("Description") },
                                    maxLines = 3,
                                    colors = textFieldColors(),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                if (addError.isNotEmpty()) {
                                    Text(addError, color = DangerRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    val parsedPrice = newPrice.toDoubleOrNull()
                                    if (newName.trim().isEmpty() || newDesc.trim().isEmpty()) {
                                        addError = "Please fill in all fields"
                                    } else if (parsedPrice == null || parsedPrice <= 0.0) {
                                        addError = "Please enter a valid price"
                                    } else {
                                        viewModel.addService(newName.trim(), parsedPrice, newDesc.trim())
                                        showAddServiceDialog = false
                                        Toast.makeText(context, "New detailing option added!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            ) {
                                Text("Add", color = PrimaryPurple, fontWeight = FontWeight.Bold)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showAddServiceDialog = false }) {
                                Text("Cancel", color = MutedText)
                            }
                        },
                        containerColor = PureWhite
                    )
                }

                // Edit Service Dialog
                if (showEditServiceDialog && editingServiceIndex != null) {
                    val index = editingServiceIndex!!
                    val currentSvc = dynamicServices.getOrNull(index)
                    if (currentSvc != null) {
                        var editName by remember(index) { mutableStateOf(currentSvc.name) }
                        var editPrice by remember(index) { mutableStateOf(currentSvc.price.toString()) }
                        var editDesc by remember(index) { mutableStateOf(currentSvc.description) }
                        var editError by remember { mutableStateOf("") }

                        AlertDialog(
                            onDismissRequest = { showEditServiceDialog = false },
                            title = {
                                Text(
                                    text = "Edit Service Option",
                                    color = HighlightDarkPurple,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            text = {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    OutlinedTextField(
                                        value = editName,
                                        onValueChange = { editName = it; editError = "" },
                                        label = { Text("Service Name") },
                                        singleLine = true,
                                        colors = textFieldColors(),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    OutlinedTextField(
                                        value = editPrice,
                                        onValueChange = { editPrice = it; editError = "" },
                                        label = { Text("Price (MUR)") },
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        colors = textFieldColors(),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    OutlinedTextField(
                                        value = editDesc,
                                        onValueChange = { editDesc = it; editError = "" },
                                        label = { Text("Description") },
                                        maxLines = 3,
                                        colors = textFieldColors(),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    if (editError.isNotEmpty()) {
                                        Text(editError, color = DangerRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        val parsedPrice = editPrice.toDoubleOrNull()
                                        if (editName.trim().isEmpty() || editDesc.trim().isEmpty()) {
                                            editError = "Please fill in all fields"
                                        } else if (parsedPrice == null || parsedPrice <= 0.0) {
                                            editError = "Please enter a valid price"
                                        } else {
                                            viewModel.updateService(index, editName.trim(), parsedPrice, editDesc.trim())
                                            showEditServiceDialog = false
                                            Toast.makeText(context, "Detailing option updated!", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                ) {
                                    Text("Save", color = PrimaryPurple, fontWeight = FontWeight.Bold)
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showEditServiceDialog = false }) {
                                    Text("Cancel", color = MutedText)
                                }
                            },
                            containerColor = PureWhite
                        )
                    }
                }
            }
        }
    }

    // Change Passcode Dialog
    if (showChangeCodeDialog) {
        var oldCodeInput by remember { mutableStateOf("") }
        var newCodeInput by remember { mutableStateOf("") }
        var confirmNewCodeInput by remember { mutableStateOf("") }
        var changeError by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showChangeCodeDialog = false },
            title = {
                Text(
                    text = "Change Security Code",
                    color = HighlightDarkPurple,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Modify the security code required to access the scheduled appointments.",
                        color = MutedText,
                        fontSize = 12.sp
                    )

                    OutlinedTextField(
                        value = oldCodeInput,
                        onValueChange = { oldCodeInput = it; changeError = "" },
                        label = { Text("Current Code") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = textFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newCodeInput,
                        onValueChange = { newCodeInput = it; changeError = "" },
                        label = { Text("New Code (min 4 digits)") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = textFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = confirmNewCodeInput,
                        onValueChange = { confirmNewCodeInput = it; changeError = "" },
                        label = { Text("Confirm New Code") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = textFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (changeError.isNotEmpty()) {
                        Text(
                            text = changeError,
                            color = DangerRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (oldCodeInput != schedulePasscode) {
                            changeError = "Current code is incorrect"
                        } else if (newCodeInput.length < 4) {
                            changeError = "New code must be at least 4 digits long"
                        } else if (newCodeInput != confirmNewCodeInput) {
                            changeError = "New codes do not match"
                        } else {
                            if (viewModel.updatePasscode(newCodeInput)) {
                                showChangeCodeDialog = false
                                android.widget.Toast.makeText(context, "Security code changed successfully!", android.widget.Toast.LENGTH_SHORT).show()
                            } else {
                                changeError = "Failed to update code"
                            }
                        }
                    }
                ) {
                    Text("Save", color = PrimaryPurple, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showChangeCodeDialog = false }) {
                    Text("Cancel", color = MutedText)
                }
            },
            containerColor = PureWhite
        )
    }
}

@Composable
fun AppointmentCard(
    appointment: Appointment,
    onSendReminder: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        border = BorderStroke(1.dp, ThinBorderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Time and Car model
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(PrimaryPurple)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = appointment.timeSlot,
                            color = PureWhite,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = appointment.carModel,
                        color = CoreDarkText,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cancel booking",
                        tint = DangerRed
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Service details
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = when (appointment.serviceType) {
                            "Interior, Exterior and Polishing" -> "👑🏆"
                            "Interior and Exterior" -> "🧼✨"
                            "Interior only" -> "🧹"
                            "Exterior only" -> "🚿"
                            else -> "💿" // polishing
                        },
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = appointment.serviceType,
                            color = CoreDarkText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Client: ${appointment.customerName} (${appointment.customerPhone})",
                            color = MutedText,
                            fontSize = 12.sp
                        )
                    }
                }
                
                Text(
                    text = "Rs ${String.format(Locale.US, "%,.0f", appointment.price)}",
                    color = PrimaryPurple,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = ThinBorderColor)
            Spacer(modifier = Modifier.height(10.dp))

            // Text Reminders & Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Automated remind state badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (appointment.isAutomatedReminderEnabled) SuccessGreen else MutedText)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (appointment.isAutomatedReminderEnabled) "Reminders Auto-Active" else "Reminders Disabled",
                        color = if (appointment.isAutomatedReminderEnabled) SuccessGreen else MutedText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Send manual SMS reminder action
                Button(
                    onClick = onSendReminder,
                    colors = ButtonDefaults.buttonColors(containerColor = SecondaryLavender),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send text reminder",
                        tint = HighlightDarkPurple,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (appointment.reminderSent) "Sent! Re-send" else "Remind SMS",
                        color = HighlightDarkPurple,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun RemindersAboutTab(
    viewModel: AppointmentViewModel
) {
    val logs by viewModel.reminderLogs.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // Core Operating rules card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                border = BorderStroke(1.dp, ThinBorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "COCO DETAILING",
                        color = PrimaryPurple,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "We provide premium automotive styling, deep cleaning, paint correction, and polishing services.",
                        color = CoreDarkText,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    AboutInfoRow(icon = "📅", title = "Days Open", desc = "Monday to Friday")
                    AboutInfoRow(icon = "🕒", title = "Operating Hours", desc = "16:00 to 20:00 (16hr - 20hr)")
                    AboutInfoRow(icon = "📍", title = "Service Mode", desc = "Professional In-Studio Detailing")
                    AboutInfoRow(icon = "📱", title = "Contact WhatsApp", desc = "+230 5472 7552")
                }
            }
        }

        // Automated Reminders Log Simulator section
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "📬", fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Automated Reminders Queue & Logs",
                        color = CoreDarkText,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Prospere schedules automated text notices 2 hours prior to detailing sessions to reduce customer no-shows.",
                    color = MutedText,
                    fontSize = 12.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Card(
                    colors = CardDefaults.cardColors(containerColor = SoftCardBg),
                    border = BorderStroke(1.dp, ThinBorderColor),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                ) {
                    if (logs.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No recent log events", color = MutedText, fontSize = 13.sp)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(logs) { log ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text("⚡", color = WarningGold, fontSize = 11.sp, modifier = Modifier.padding(top = 2.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = log,
                                        color = CoreDarkText,
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
fun AboutInfoRow(icon: String, title: String, desc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = icon, fontSize = 16.sp)
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "$title: ",
            color = MutedText,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = desc,
            color = CoreDarkText,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun HorizontalWeekdayPicker(
    selectedDate: String,
    onDateSelected: (String) -> Unit
) {
    val dateList = remember { getNextSevenDays() }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        dateList.forEach { item ->
            val isSelected = item.formattedString == selectedDate
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 2.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) PrimaryPurple else PureWhite)
                    .border(
                        width = 1.dp,
                        color = if (isSelected) PrimaryPurple else ThinBorderColor,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .clickable { onDateSelected(item.formattedString) }
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = item.dayName,
                    color = if (isSelected) PureWhite else MutedText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.dayNumber,
                    color = if (isSelected) PureWhite else CoreDarkText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(1.dp))
                Text(
                    text = item.monthName,
                    color = if (isSelected) SecondaryLavender else MutedText,
                    fontSize = 9.sp
                )
            }
        }
    }
}

@Composable
fun TimeSlotSelector(
    slots: List<String>,
    selectedSlot: String,
    bookedSlots: List<String>,
    onSlotSelected: (String) -> Unit
) {
    // Elegant flowing grid for time slots
    Column {
        val rows = slots.chunked(3)
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { slot ->
                    val isBooked = bookedSlots.contains(slot)
                    val isSelected = slot == selectedSlot
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isBooked) SoftCardBg
                                else if (isSelected) SecondaryLavender
                                else PureWhite
                            )
                            .border(
                                width = 1.dp,
                                color = if (isBooked) ThinBorderColor
                                        else if (isSelected) PrimaryPurple
                                        else ThinBorderColor,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .then(
                                if (isBooked) Modifier
                                else Modifier.clickable { onSlotSelected(slot) }
                            )
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = slot,
                                color = if (isBooked) MutedText
                                        else if (isSelected) HighlightDarkPurple
                                        else CoreDarkText,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                style = if (isBooked) androidx.compose.ui.text.TextStyle(
                                    textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                                ) else androidx.compose.ui.text.TextStyle.Default
                            )
                            if (isBooked) {
                                Text(
                                    text = "Booked",
                                    color = DangerRed,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ServiceOptionCard(
    service: ServiceOption,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) SecondaryLavender else PureWhite
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected) PrimaryPurple else ThinBorderColor
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Radio Indicator
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .border(
                        width = 2.dp,
                        color = if (isSelected) PrimaryPurple else MutedText,
                        shape = CircleShape
                    )
                    .padding(3.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(PrimaryPurple)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = service.name,
                    color = CoreDarkText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = service.description,
                    color = MutedText,
                    fontSize = 11.sp,
                    lineHeight = 14.sp
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            if (service.price <= 0.0) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(SecondaryLavender)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Soon",
                        color = PrimaryPurple,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            } else {
                Text(
                    text = "Rs ${String.format(Locale.US, "%,.0f", service.price)}",
                    color = PrimaryPurple,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

@Composable
fun WhatsAppFooter(
    number: String,
    onContactClick: () -> Unit
) {
    Surface(
        color = PureWhite,
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = ThinBorderColor)
            .clickable(onClick = onContactClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // WhatsApp green circular badge
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF25D366)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "💬", fontSize = 12.sp, color = Color.White)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "WhatsApp Me: $number",
                    color = CoreDarkText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Tap to chat directly for customized service requests",
                    color = PrimaryPurple,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        color = HighlightDarkPurple,
        fontSize = 13.sp,
        fontWeight = FontWeight.Black,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = PrimaryPurple,
    unfocusedBorderColor = ThinBorderColor,
    focusedTextColor = CoreDarkText,
    unfocusedTextColor = CoreDarkText,
    focusedLabelColor = PrimaryPurple,
    unfocusedLabelColor = MutedText,
    focusedContainerColor = PureWhite,
    unfocusedContainerColor = PureWhite
)

// Helper objects for horizontal date picker
data class DateSelectorItem(
    val formattedString: String, // "yyyy-MM-dd"
    val dayName: String, // "MON", "TUE"
    val dayNumber: String, // "29"
    val monthName: String // "JUN"
)

fun getNextSevenDays(): List<DateSelectorItem> {
    val list = mutableListOf<DateSelectorItem>()
    val cal = Calendar.getInstance()
    val sdfFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val sdfDay = SimpleDateFormat("EEE", Locale.getDefault())
    val sdfNum = SimpleDateFormat("dd", Locale.getDefault())
    val sdfMonth = SimpleDateFormat("MMM", Locale.getDefault())

    for (i in 0 until 7) {
        list.add(
            DateSelectorItem(
                formattedString = sdfFormat.format(cal.time),
                dayName = sdfDay.format(cal.time).uppercase(Locale.getDefault()),
                dayNumber = sdfNum.format(cal.time),
                monthName = sdfMonth.format(cal.time).uppercase(Locale.getDefault())
            )
        )
        cal.add(Calendar.DAY_OF_YEAR, 1)
    }
    return list
}

fun getTodayString(): String {
    val cal = Calendar.getInstance()
    val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return format.format(cal.time)
}
