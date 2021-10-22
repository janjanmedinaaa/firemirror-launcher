package com.medina.juanantonio.firemirror.data.managers

import com.medina.juanantonio.firemirror.data.models.Holiday

class HolidayManager : IHolidayManager {

    override fun getHolidays(): ArrayList<Holiday> {
        return arrayListOf(
            Holiday(date = "11/01", day = "M", name = "All Saints' Day", isLongWeekend = true),
            Holiday(date = "11/02", day = "T", name = "All Souls' Day", isLongWeekend = false),
            Holiday(date = "11/30", day = "T", name = "Bonifacio Day", isLongWeekend = false),
            Holiday(date = "12/06", day = "M", name = "Parents' Day", isLongWeekend = false),
            Holiday(
                date = "12/08",
                day = "W",
                name = "Feast of the Immaculate Conception",
                isLongWeekend = false
            ),
            Holiday(date = "12/21", day = "T", name = "December Solstice", isLongWeekend = false),
            Holiday(date = "12/24", day = "F", name = "Christmas Eve", isLongWeekend = false),
            Holiday(date = "12/25", day = "Sa", name = "Christmas Day", isLongWeekend = false),
            Holiday(date = "12/30", day = "Th", name = "Rizal Day", isLongWeekend = false),
            Holiday(date = "12/31", day = "F", name = "New Year's Eve", isLongWeekend = false)
        )
    }
}

interface IHolidayManager {
    fun getHolidays(): ArrayList<Holiday>
}