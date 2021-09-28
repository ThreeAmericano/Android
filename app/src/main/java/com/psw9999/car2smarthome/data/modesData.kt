package com.psw9999.car2smarthome.data

data class mode(
    var modeName : String,
    var airconEnable : Boolean,
    var airconWindPower : Int,
    var lightEnable : Boolean,
    var lightBrightness : Int,
    var lightColor : Int?,
    var lightMode : Int?,
    var windowOpen : Boolean,
    var gasValveEnable : Boolean,
    var modeNum : Int
)