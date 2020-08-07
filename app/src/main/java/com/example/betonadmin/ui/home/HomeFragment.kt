package com.example.betonadmin.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.betonadmin.HomeActivity
import com.example.betonadmin.Order
import com.example.betonadmin.OrdersAdapter
import com.example.betonadmin.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {
    private lateinit var database: FirebaseFirestore

    private lateinit var ordersList: MutableList<Order>

    private lateinit var home: HomeActivity

    private lateinit var ordersListView: ListView
    private lateinit var countOrders: TextView
    private lateinit var sortBySpin: Spinner

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        if (activity != null) {
            home = activity as HomeActivity
        }

        database = Firebase.firestore

        ordersList = mutableListOf()
        ordersListView = root.findViewById(R.id.orders)
        countOrders = root.findViewById(R.id.countOrders)
        sortBySpin = root.findViewById(R.id.sort_by_spin)

        val spinAdapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            home,
            R.array.sort_by,
            android.R.layout.simple_spinner_item
        )

        spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        sortBySpin.adapter = spinAdapter

        sortBySpin.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, itemSelected: View?, selectedItemPosition: Int, selectedId: Long) {
                when (selectedItemPosition) {
                    0 -> {
                        ordersList.sortWith(
                            compareBy<Order> { it.datetime.year }
                                .thenBy { it.datetime.month }
                                .thenBy { it.datetime.day }
                                .thenBy { it.datetime.hour }
                                .thenBy { it.datetime.minute }
                        )
                        ordersList.reverse()

                        val ordersAdapter = OrdersAdapter(home, ordersList)
                        ordersListView.adapter = ordersAdapter
                    }
                    1 -> {
                        ordersList.sortByDescending { it.price }
                        val ordersAdapter = OrdersAdapter(home, ordersList)
                        ordersListView.adapter = ordersAdapter
                    }

                }
            }

        }

        database.collection("orders")
            .get()
            .addOnSuccessListener { documents ->
                val size = documents.size()
                countOrders.text = size.toString()

                if (size == 0)
                    root.findViewById<TextView>(R.id.orders_null).visibility = View.VISIBLE

                for (document in documents) {
                    val order = Order()
                    order.count = (document.data["count"] as Long).toInt()
                    order.product = document.data["product"].toString()
                    order.price = (document.data["price"] as Long).toInt()
                    order.delivery = document.data["delivery"] as Boolean
                    order.address = document.data["address"].toString()
                    order.status = document.data["status"].toString()
                    val list = document.data["time"] as Map<*, *>
                    order.datetime.year = (list["year"] as Long).toInt()
                    order.datetime.month = (list["monthValue"] as Long).toInt()
                    order.datetime.day = (list["dayOfMonth"] as Long).toInt()
                    order.datetime.hour = (list["hour"] as Long).toInt()
                    order.datetime.minute = (list["minute"] as Long).toInt()
                    order.id = document.data["id"].toString()
                    order.uid = document.data["user"].toString()

                    ordersList.add(order)

                }

                ordersList.sortWith(
                    compareBy<Order> { it.datetime.year }
                        .thenBy { it.datetime.month }
                        .thenBy { it.datetime.day }
                        .thenBy { it.datetime.hour }
                        .thenBy { it.datetime.minute }
                )
                ordersList.reverse()

                val ordersAdapter = OrdersAdapter(home, ordersList)
                ordersListView.adapter = ordersAdapter
            }
            .addOnFailureListener { exception ->
                Log.w("home", "Ошибка получения заказов.", exception)
            }



        return root
    }
}