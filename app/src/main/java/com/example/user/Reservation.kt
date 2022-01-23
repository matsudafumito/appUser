package com.example.user

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import java.sql.Timestamp

class Reservation(val restaurantId: Int, val restaurantName: String,
                  val reservationTime_start: Int,
                  val reservationTime_end: Int,
                  val reservationId: Int, val numPeople: Int)


class ReservationListAdapter(val context: Context, val reservationList: MutableList<Reservation>): BaseAdapter(){

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_user_reservation, null)
        val restaurantName = view.findViewById<TextView>(R.id.restaurant_name)
        val reservationTime = view.findViewById<TextView>(R.id.reservation_time)
        val buttonToDetail = view.findViewById<Button>(R.id.buttonToDetail)

        val reservation = reservationList[position]

        restaurantName.text = reservation.restaurantName

        //this should be transform timestamp -> date time
        val startDateTime = this.getFormattedDateTime(reservation.reservationTime_start.toLong()*1000)
        val endDateTime = this.getFormattedDateTime(reservation.reservationTime_end.toLong()*1000)

        reservationTime.text = "$startDateTime - $endDateTime"

        buttonToDetail.setOnClickListener {
            val intent = Intent(context, UserShowReservationDetail::class.java)
            intent.putExtra("reservation_id", reservation.reservationId)
            intent.putExtra("restaurant_id", reservation.restaurantId)
            intent.putExtra("restaurant_name", reservation.restaurantName)
            intent.putExtra("time_start", reservation.reservationTime_start)
            intent.putExtra("time_end", reservation.reservationTime_end)
            intent.putExtra("format_time_start", startDateTime)
            intent.putExtra("format_time_end", endDateTime)
            intent.putExtra("num_people", reservation.numPeople)
            context.startActivity(intent)
        }

        return view
    }

    private fun getFormattedDateTime(unixTs: Long): String {
        val timestamp = Timestamp(unixTs)
        var month = timestamp.month + 1
        var date = timestamp.date
        var hour = timestamp.hours
        var minute = timestamp.minutes

        var strMonth = "$month"
        var strDate = "$date"
        var strHour = "$hour"
        var strMinute = "$minute"

        if (month < 10) {
            strMonth = "0$month"
        }
        if (date < 10) {
            strDate = "0${date}"
        }
        if (hour < 10) {
            strHour = "0${hour}"
        }
        if (minute < 10) {
            strMinute = "0${minute}"
        }

        return "$strMonth/$strDate: $strHour:$strMinute"
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