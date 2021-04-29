package com.cg.cheapstays.model

// TODO check noOfRooms is being used or not
data class Hotels(
    val name : String,
    val address : String,
    val description : String,
    val price : Int,
    val rating : Double,
    val imgPath : String,
    val specialOffer : String
) {
    constructor() : this ("","","",0,0.00,"","None")
    data class Rooms (
        val tariff : Double,
        val type : String
    ){
        constructor() : this(0.00,"")
    }

}