package com.example.beton

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_order.view.*
import java.time.LocalDateTime
import java.time.LocalDateTime.now
import java.util.*

class ConfirmActivity : AppCompatActivity() {

    private lateinit var priceText: TextView
    private lateinit var addressText: TextView
    private lateinit var typeText: TextView
    private lateinit var countText: TextView
    private lateinit var delivText: TextView
    private lateinit var paymentCard: RelativeLayout
    private lateinit var paymentNal: RelativeLayout
    private lateinit var fabConfirm: FloatingActionButton
    private lateinit var cardNum: EditText
    private lateinit var cardYear: EditText
    private lateinit var cardCVC: EditText
    private lateinit var cardName: EditText
    private lateinit var payCard: Button

    private var priceAllOrder: Int = 0
    private var countTotal: Int = 0
    private var paymentVariant: Int = -1
    private lateinit var product: String
    private lateinit var address: String
    private var deliv = false
    private var user: User = User()

    private lateinit var database: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm)

        database = Firebase.firestore
        auth = Firebase.auth

        database.collection("users")
            .whereEqualTo("uid", auth.currentUser?.uid)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    user.name = document.data["name"].toString()
                    user.email = document.data["email"].toString()
                }
            }
            .addOnFailureListener { exception ->
                Log.w("home", "Error getting documents: ", exception)
            }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        priceText = findViewById(R.id.priceText)
        addressText = findViewById(R.id.addressText)
        typeText = findViewById(R.id.typeText)
        countText = findViewById(R.id.countText)
        delivText = findViewById(R.id.delivText)
        payCard = findViewById(R.id.payCard)
        cardCVC = findViewById(R.id.cardCVC)
        cardName = findViewById(R.id.cardName)
        cardNum = findViewById(R.id.cardNum)
        cardYear = findViewById(R.id.cardYear)

        paymentCard = findViewById(R.id.payment_card)
        paymentNal = findViewById(R.id.payment_nal)

        fabConfirm = findViewById(R.id.confirnFAB)

        val received = intent

        priceAllOrder = received.getIntExtra("priceAllOrder", 0)
        countTotal = received.getIntExtra("countTotal", 0)
        paymentVariant = received.getIntExtra("paymentVariant", 0)
        product = received.getStringExtra("product").toString()
        address = received.getStringExtra("address").toString()
        deliv = received.getBooleanExtra("deliv", false)

        if (paymentVariant == 0) {
            paymentNal.visibility = View.VISIBLE
        } else {
            paymentCard.visibility = View.VISIBLE
            fabConfirm.visibility = View.GONE
        }

        payCard.setOnClickListener {
            if (paymentVariant == 1) {
                if (checkCard()) {
                    addOrderToDatabase(auth.currentUser)
                    startActivity(
                        Intent(
                            this,
                            OrderedActivity::class.java
                        )
                    )
                } else {
                    Toast.makeText(baseContext, "Заполните полностью реквизиты карты.",
                        Toast.LENGTH_LONG).show()
                }
            }
        }

        val order = Order()
        order.address = address
        order.count = countTotal
        order.delivery = deliv
        order.price = priceAllOrder
        order.product = product

        priceText.text = "${order.price} ₽"
        addressText.text = order.address
        typeText.text = order.product
        countText.text = "${order.count} м³"
        delivText.text = if (deliv) "С доставкой" else "Без доставки"

        fabConfirm.setOnClickListener {
            addOrderToDatabase(auth.currentUser)
            startActivity(
                Intent(
                    this,
                    OrderedActivity::class.java
                )
            )
        }
    }

    private fun checkCard(): Boolean {
        return cardNum.text.isNotEmpty() && cardCVC.text.isNotEmpty() && cardYear.text.isNotEmpty() && cardName.text.isNotEmpty()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun addOrderToDatabase(currentUser: FirebaseUser?) {
        val order = hashMapOf(
            "address" to address,
            "product" to product,
            "count" to countTotal,
            "price" to priceAllOrder,
            "delivery" to deliv,
            "paymentVariant" to paymentVariant,
            "status" to 0,
            "user" to user,
            "uid" to auth.currentUser?.uid,
            "time" to now(),
            "id" to UUID.randomUUID().toString()
        )

        database.collection("orders")
            .add(order)
    }
}