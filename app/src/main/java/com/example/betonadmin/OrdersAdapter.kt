package com.example.betonadmin

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.RelativeLayout
import android.widget.TextView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class OrdersAdapter(ctx: Context, types: List<Order>) : BaseAdapter() {
    private val listData: List<Order> = types
    private val context: Context = ctx
    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        if (convertView == null) {
            view = layoutInflater.inflate(R.layout.order_item, null)
        } else {
            view = convertView
        }

        val database = Firebase.firestore

        val priceTextView: TextView = view.findViewById(R.id.priceTextItem)
        val status: TextView = view.findViewById(R.id.status_item)
        val address: TextView = view.findViewById(R.id.addressText_item)
        val countTextView: TextView = view.findViewById(R.id.countText_item)
        val product: TextView = view.findViewById(R.id.typeText_item)
        val deliv: TextView = view.findViewById(R.id.delivText_item)
        val times: TextView = view.findViewById(R.id.times2)
        val idText: TextView = view.findViewById(R.id.id_text)
        val userName: TextView = view.findViewById(R.id.user_name)
        val userEmail: TextView = view.findViewById(R.id.user_email)

        status.setOnClickListener {
            Log.d("id", idText.text.toString())

        }

        val uid = listData[position].uid.toString()



        database.collection("users")
            .whereEqualTo("uid", uid)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    //Log.d("uid", "$uid ${document.data["name"]}")
                    userName.text = "${document.data["name"].toString()}"
                    userEmail.text = "<${document.data["email"].toString()}>"
                    priceTextView.text = "${listData[position].price}  ₽"
                    status.text = listData[position].status
                    address.text = listData[position].address
                    countTextView.text = "${listData[position].count} м³"
                    product.text = listData[position].product
                    deliv.text = if (listData[position].delivery) "С доставкой" else "Без доставки"
                    times.text = "${listData[position].datetime.day}.${listData[position].datetime.month}.${listData[position].datetime.year} ${listData[position].datetime.hour}:${listData[position].datetime.minute}"
                    idText.text = "${listData[position].id}"
                }
            }
            .addOnFailureListener { exception ->
                Log.w("home", "Error getting documents: ", exception)
            }



        return view
    }

    override fun getItem(position: Int): Any {
        return listData[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return listData.size
    }
}