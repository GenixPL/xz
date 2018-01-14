package pw.xz.xdd.xz.Other

import java.util.*

/**
 * Created by emile on 13-Jan-18.
 */

class CalendarToStringConverter {
    val days = mapOf<Int, String>(
            Calendar.MONDAY to "Monday",
            Calendar.TUESDAY to "Tuesday",
            Calendar.WEDNESDAY to "Wednesday",
            Calendar.THURSDAY to "Thursday",
            Calendar.FRIDAY to "Friday",
            Calendar.SUNDAY to "Sunday",
            Calendar.SATURDAY to "Saturday")


    fun getDayFromCalendarEnum(calendarDay: Int): String? {
        return days[calendarDay]
    }

    fun getCalendarEnumFromDay(day: String): Int? {
        for ((key, value) in days) {
            if (value == day) {
                return key
            }
        }
        return null
    }
}