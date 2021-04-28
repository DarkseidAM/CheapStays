package com.cg.cheapstays.model

import android.location.Address

data class Hotels(
    val name : String,
    val address : String,
    val description : String,
    val noOfRooms : Int,
    val imgPath : String,
    val rooms : List<Rooms>
) {
    constructor() : this ("","","",0,"", listOf())
    data class Rooms (
        val roomNo : Int,
        val tariff : Double,
        val type : String
    )
}