package com.cg.cheapstays.view.ui.home

import android.net.Uri
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.cg.cheapstays.R
import com.cg.cheapstays.model.Hotels
import com.cg.cheapstays.view.USER_TYPE
import com.google.firebase.auth.FirebaseAuth


class MyHotelsRecyclerViewAdapter(private var values: List<Hotels>, var listener : (position:Int)->(Unit))
    : RecyclerView.Adapter<MyHotelsRecyclerViewAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_hotel_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.hotelName.text = item.name
        holder.address.text = item.address
        holder.price.text = "₹${item.price.toString()}"
        holder.rating.text = "${item.rating}"
        Glide.with(holder.itemView.context).load(Uri.parse(item.imgPath)).placeholder(R.drawable.default_hotel).into(holder.image)

        holder.itemView.setOnClickListener {

            if(USER_TYPE!="admin")
                listener(position)
        }

    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val hotelName : TextView = view.findViewById(R.id.hotelNameT)
        val image : ImageView = view.findViewById(R.id.imageView6)
        val address : TextView = view.findViewById(R.id.hotelAddressT)
        val price : TextView = view.findViewById(R.id.hotelPriceT)
        val rating : TextView = view.findViewById(R.id.hotelRatingT)

    }

    fun filter(filteredValue : List<Hotels>){
        values = filteredValue
        notifyDataSetChanged()
    }

}