package com.cg.cheapstays.model

data class Users (val name:String, val email:String, val userType : String){
    constructor() : this("","","")
}
