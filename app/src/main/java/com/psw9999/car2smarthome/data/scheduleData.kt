package com.psw9999.car2smarthome.data

data class scheduleData(
    var title : String? = null,
    var repeat : Boolean = true,
    var modeNum : Int = 0,
    var activeDate : String? = null,
    var enabled : Boolean = true,
    var daysOfWeek: ArrayList<Boolean> = arrayListOf(false,false,false,false,false,false,false),
    var startTime : String = "1200",
    var airconEnable : Boolean = false,
    var airconWindPower : Int = 0,
    var lightEnable : Boolean = false,
    var lightBrightness : Int = 0,
    var lightColor : Int = 0,
    var lightMode : Int = 0,
    var windowOpen : Boolean = false,
    var gasValveEnable : Boolean = false,
    var name : String? = null
) {
}
