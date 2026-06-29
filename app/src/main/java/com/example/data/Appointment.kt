package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "appointments")
data class Appointment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val customerName: String,
    val customerPhone: String,
    val carModel: String,
    val dateString: String, // e.g., "2026-06-29"
    val timeSlot: String, // e.g., "16:00", "17:00", etc.
    val serviceType: String, // "Interior and Exterior", "Interior only", "Exterior only", "Polishing"
    val price: Double,
    val reminderSent: Boolean = false,
    val isAutomatedReminderEnabled: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)
