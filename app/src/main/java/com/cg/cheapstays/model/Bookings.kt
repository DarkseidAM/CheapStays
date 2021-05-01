package com.cg.cheapstays.model

data class Bookings(
    val uid : String,
    val hotelId : String,
    val date : String,
    val noOfRooms : Int,
    val roomType : String,
    val totalPrice : Double
){
    constructor() : this ("","","",0,"",0.0)
}