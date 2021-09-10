package com.psw9999.car2smarthome.data

data class scheduleData(
    var Title : String? = null,
    var UID : String? = null,
    var repeat : Boolean? = null,
    var modeNum : Int? = null,
    var Active_data : String? = null,
    var Enabled : Boolean = false,
    var Daysofweek : List<Boolean>? = null,
    var Start_time : String? = null,
    var airconEnable : Boolean? = null,
    var airconWindPower : Int? = null,
    var lightEnable : Boolean? = null,
    var lightBrightness : Int? = null,
    var lightColor : Int? = null,
    var lightMode : Int? = null,
    var windowOpen : Boolean? = null,
    var gasValveEnable : Boolean? = null
)
