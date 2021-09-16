package com.psw9999.car2smarthome.data

data class alarmData(
    //TODO : 추후 icon 사용안하면 수정하기
    //TODO : Parcelable 혹은 Serializable 로 데이터 전달해보기
    var date : String? = null,
    var icon : String? = null,
    var inform : String? = null
)
