package com.example.beton.ui.orders

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.beton.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Text
import java.sql.Array

class OrdersFragment : Fragment() {
    private lateinit var database: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var ordersList: MutableList<Order>

    private lateinit var home: HomeActivity

    private lateinit var ordersListView: ListView
    private lateinit var countOrders: TextView
    private lateinit var sortBySpin: Spinner
    private lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private lateinit var root: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_orders, container, false)

        if (activity != null) {
            home = activity as HomeActivity
        }

        database = Firebase.firestore
        auth = Firebase.auth
        handler = Handler()

        ordersList = mutableListOf()
        ordersListView = root.findViewById(R.id.orders)
        countOrders = root.findViewById(R.id.countOrders)
        sortBySpin = root.findViewById(R.id.sort_by_spin)
        refreshLayout = root.findViewById(R.id.reload_orders)

        refreshLayout.setOnRefreshListener {
            // Initialize a new Runnable
            runnable = Runnable {
                // Update the text view text with a random number
                ordersList.clear()
                getOrders()

                // Hide swipe to refresh icon animation
                refreshLayout.isRefreshing = false
            }

            // Execute the task after specified time
            handler.postDelayed(
                runnable, 3000.toLong()
            )
        }


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


        getOrders()


        return root
    }

    private fun getOrders() {
        database.collection("orders")
            .whereEqualTo("uid", auth.currentUser?.uid)
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
                    order.status = (document.data["status"] as Long).toInt()
                    val list = document.data["time"] as Map<*, *>
                    order.datetime.year = (list["year"] as Long).toInt()
                    order.datetime.month = (list["monthValue"] as Long).toInt()
                    order.datetime.day = (list["dayOfMonth"] as Long).toInt()
                    order.datetime.hour = (list["hour"] as Long).toInt()
                    order.datetime.minute = (list["minute"] as Long).toInt()
                    order.id = document.data["id"].toString()

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
    }
}