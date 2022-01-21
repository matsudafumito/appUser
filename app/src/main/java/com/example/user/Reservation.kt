package com.example.user

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class Reservation(val restaurantId: Int, val restaurantName: String, val reservationTime_start: Int, val reservationTime_end: Int, val reservationId: Int)

class ReservationListAdapter(val context: Context, val reservationList: MutableList<Reservation>): BaseAdapter(){

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_user_reservation, null)
        val restaurantName = view.findViewById<TextView>(R.id.restaurant_name)
        val reservationTime = view.findViewById<TextView>(R.id.reservation_time)

        val reservation = reservationList[position]

        restaurantName.text = reservation.restaurantName

        //this should be transform timestamp -> date time
        reservationTime.text = "${reservation.reservationTime_start} - ${reservation.reservationTime_end}"

        return view
    }

    override fun getItem(position: Int): Any {
        return reservationList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return reservationList.size
    }

}