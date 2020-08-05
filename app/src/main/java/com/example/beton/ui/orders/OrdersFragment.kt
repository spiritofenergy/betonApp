package com.example.beton.ui.orders

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.beton.HomeActivity
import com.example.beton.Order
import com.example.beton.OrdersAdapter
import com.example.beton.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class OrdersFragment : Fragment() {
    private lateinit var database: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var ordersList: MutableList<Order>

    private lateinit var home: HomeActivity

    private lateinit var ordersListView: ListView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_orders, container, false)

        if (activity != null) {
            home = activity as HomeActivity
        }

        database = Firebase.firestore
        auth = Firebase.auth

        ordersList = mutableListOf()
        ordersListView = root.findViewById(R.id.orders)

        database.collection("orders")
            .whereEqualTo("user", auth.currentUser?.uid)
            .get()
            .addOnSuccessListener { documents ->
                Log.d("size", documents.size().toString())
                for (document in documents) {
                    val order = Order()
                    order.count = (document.data["count"] as Long).toInt()
                    order.product = document.data["product"].toString()
                    order.price = (document.data["price"] as Long).toInt()
                    order.delivery = document.data["delivery"] as Boolean
                    order.address = document.data["address"].toString()
                    order.status = document.data["status"].toString()

                    ordersList.add(order)

                    Log.d("oredrs", "${ordersList[0].status}")
                }

                val ordersAdapter = OrdersAdapter(home, ordersList)
                ordersListView.adapter = ordersAdapter
            }
            .addOnFailureListener { exception ->
                Log.w("home", "Ошибка получения заказов.", exception)
            }

        Log.d("oredrs", "${ordersList}")

        return root
    }
}