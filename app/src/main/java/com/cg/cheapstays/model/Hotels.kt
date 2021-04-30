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

}
data class Rooms (
        val single : Single,
        val double : Doubles
){
    constructor() : this(Single(0,0), Doubles(0,0))
}
data class Single(
        val tariff : Int,
        val noOfRooms : Int
){
    constructor() : this(0,0)
}
data class Doubles(
        val tariff : Int,
        val noOfRooms : Int
){
    constructor() : this(0,0)
}