package com.medina.juanantonio.firemirror.data.managers

import com.medina.juanantonio.firemirror.data.models.Holiday

object HolidayManager : IHolidayManager {

    override fun getHolidays(): ArrayList<Holiday> {
        return arrayListOf(
            Holiday(name = "New Year's Day", constant = true).apply {
                month = 1
                dayOfMonth = 1
            },
            Holiday(name = "Chinese New Year", constant = false).apply {
                month = 2
                dayOfMonth = 1
            },
            Holiday(name = "EDSA Revolution Anniversary", constant = true).apply {
                month = 2
                dayOfMonth = 25
            },
            Holiday(name = "Maundy Thursday", constant = false).apply {
                month = 4
                dayOfMonth = 14
            },
            Holiday(name = "Good Friday", constant = false).apply {
                month = 4
                dayOfMonth = 15
            },
            Holiday(name = "Black Saturday", constant = false).apply {
                month = 4
                dayOfMonth = 16
            },
            Holiday(name = "Araw ng Kagitingan", constant = true).apply {
                month = 4
                dayOfMonth = 9
            },
            Holiday(name = "Labor Day", constant = true).apply {
                month = 5
                dayOfMonth = 1
            },
            Holiday(name = "Independence Day", constant = true).apply {
                month = 6
                dayOfMonth = 12
            },
            Holiday(name = "Ninoy Aquino Day", constant = true).apply {
                month = 8
                dayOfMonth = 21
            },
            Holiday(name = "National Heroes' Day", constant = true).apply {
                month = 8
                dayOfMonth = 29
            },
            Holiday(name = "All Saints' Day", constant = true).apply {
                month = 11
                dayOfMonth = 1
            },
            Holiday(name = "All Souls' Day", constant = true).apply {
                month = 11
                dayOfMonth = 2
            },
            Holiday(name = "Bonifacio Day", constant = true).apply {
                month = 11
                dayOfMonth = 30
            },
            Holiday(name = "Feast of the Immaculate Conception of Mary", constant = true).apply {
                month = 12
                dayOfMonth = 8
            },
            Holiday(name = "Christmas Eve", constant = true).apply {
                month = 12
                dayOfMonth = 24
            },
            Holiday(name = "Christmas Day", constant = true).apply {
                month = 12
                dayOfMonth = 25
            },
            Holiday(name = "Rizal Day", constant = true).apply {
                month = 12
                dayOfMonth = 30
            },
            Holiday(name = "Last day of the year", constant = true).apply {
                month = 12
                dayOfMonth = 31
            },
        )
    }
}

interface IHolidayManager {
    fun getHolidays(): ArrayList<Holiday>
}