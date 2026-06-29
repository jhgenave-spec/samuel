package com.example.data

import kotlinx.coroutines.flow.Flow

class AppointmentRepository(private val appointmentDao: AppointmentDao) {
    val allAppointments: Flow<List<Appointment>> = appointmentDao.getAllAppointments()

    fun getAppointmentsForDate(dateString: String): Flow<List<Appointment>> {
        return appointmentDao.getAppointmentsForDate(dateString)
    }

    suspend fun insert(appointment: Appointment): Long {
        return appointmentDao.insertAppointment(appointment)
    }

    suspend fun update(appointment: Appointment) {
        appointmentDao.updateAppointment(appointment)
    }

    suspend fun delete(appointment: Appointment) {
        appointmentDao.deleteAppointment(appointment)
    }

    suspend fun deleteById(id: Int) {
        appointmentDao.deleteAppointmentById(id)
    }
}
