package com.inttelgo.tecnicos.logic.persistence

import java.time.DayOfWeek
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * Jornada laboral en hora de Bogotá.
 *
 * Tracking de ubicación: 8:00 a.m. – 7:00 p.m.
 *
 * Ventanas de huella:
 * - Lunes a viernes:
 *   - Ingreso: 8:15 a.m. – 9:00 a.m.
 *   - Salida:  5:00 p.m. – 6:00 p.m.
 * - Sábado:
 *   - Ingreso: 8:15 a.m. – 9:00 a.m.
 *   - Salida:  desde las 12:00 p.m.
 * - Domingo: sin jornada de huella.
 *
 * [BYPASS_JORNADA_SCHEDULE] = true permite probar ingreso/salida a cualquier hora.
 */
object WorkSchedule {
    /**
     * Modo prueba: ignora ventanas de ingreso/salida y permite marcar en cualquier momento.
     * Cambiar a `false` cuando terminen las pruebas.
     */
    const val BYPASS_JORNADA_SCHEDULE: Boolean = false

    private val zoneId: ZoneId = ZoneId.of("America/Bogota")
    private val workStart: LocalTime = LocalTime.of(8, 0)
    private val workEnd: LocalTime = LocalTime.of(19, 0)

    private val checkInStart: LocalTime = LocalTime.of(8, 15)
    private val checkInEnd: LocalTime = LocalTime.of(9, 0)

    private val weekdayCheckOutStart: LocalTime = LocalTime.of(17, 0)
    private val weekdayCheckOutEnd: LocalTime = LocalTime.of(18, 0)

    /** Sábado: salida a partir del mediodía. */
    private val saturdayCheckOutStart: LocalTime = LocalTime.of(12, 0)

    fun nowBogota(): ZonedDateTime = ZonedDateTime.now(zoneId)

    fun isWithinWorkHours(now: ZonedDateTime = nowBogota()): Boolean {
        val time = now.toLocalTime()
        return !time.isBefore(workStart) && time.isBefore(workEnd)
    }

    fun isWorkDay(now: ZonedDateTime = nowBogota()): Boolean {
        val day = now.dayOfWeek
        return day != DayOfWeek.SUNDAY
    }

    fun shouldRequestCheckIn(now: ZonedDateTime = nowBogota()): Boolean {
        if (BYPASS_JORNADA_SCHEDULE) return true
        if (!isWorkDay(now)) return false
        val time = now.toLocalTime()
        // Lun–sáb: misma ventana de ingreso
        return !time.isBefore(checkInStart) && time.isBefore(checkInEnd)
    }

    fun shouldRequestCheckOut(now: ZonedDateTime = nowBogota()): Boolean {
        if (BYPASS_JORNADA_SCHEDULE) return true
        if (!isWorkDay(now)) return false
        val time = now.toLocalTime()
        return when (now.dayOfWeek) {
            DayOfWeek.SATURDAY -> !time.isBefore(saturdayCheckOutStart)
            else -> !time.isBefore(weekdayCheckOutStart) && time.isBefore(weekdayCheckOutEnd)
        }
    }

    /** Epoch millis del próximo inicio de jornada (hoy a las 8 si aún no llegó, o mañana). */
    fun nextWorkStartMillis(now: ZonedDateTime = nowBogota()): Long {
        var next = now.toLocalDate().atTime(workStart).atZone(zoneId)
        if (!now.toLocalTime().isBefore(workStart)) {
            next = next.plusDays(1)
        }
        return next.toInstant().toEpochMilli()
    }

    /** Epoch millis del fin de la jornada de hoy (19:00). Si ya pasó, retorna el próximo fin. */
    fun nextWorkEndMillis(now: ZonedDateTime = nowBogota()): Long {
        var end = now.toLocalDate().atTime(workEnd).atZone(zoneId)
        if (!now.isBefore(end)) {
            end = end.plusDays(1)
        }
        return end.toInstant().toEpochMilli()
    }
}
