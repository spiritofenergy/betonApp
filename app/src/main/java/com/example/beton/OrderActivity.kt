package com.example.beton

import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.SuperscriptSpan
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.math.roundToInt

class OrderActivity : AppCompatActivity() {
    private var distance: Float? = null
    private lateinit var chosenAddress: String
    private lateinit var address: TextView
    private lateinit var priceAll: TextView
    private lateinit var deliveryPrice: TextView
    private lateinit var countTotalText: TextView
    private lateinit var deliveryCheck: CheckBox
    private lateinit var seekBar: SeekBar
    private lateinit var chooseType: GridView
    private lateinit var typeOrder: ExpandedGridView
    private lateinit var payment: GridView
    private lateinit var fab: FloatingActionButton

    private lateinit var priceOne: LinearLayout
    private lateinit var priceOneText: TextView

    private var priceOrder: Int = 0
    private var priceAllOrder: Int = 0
    private var priceDelivery: Int = 0
    private var priceDeliverySum: Int = 0
    private var countTotal: Int = 0
    private var paymentVariant: Int = -1
    private lateinit var typeProduct: String
    private lateinit var product: String
    private var deliv = false

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        val types = resources.getStringArray(R.array.types)
        val beton = resources.getStringArray(R.array.order_type_beton)
        val rastvor = resources.getStringArray(R.array.order_type_rastvor)
        val paymentArr = resources.getStringArray(R.array.payment_arr)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        address = findViewById(R.id.address)
        priceAll = findViewById(R.id.priceAll)
        deliveryPrice = findViewById(R.id.deliveryPrice)
        deliveryCheck = findViewById(R.id.deliveryCheck)
        seekBar = findViewById(R.id.seekBar)
        countTotalText = findViewById(R.id.countTotalText)
        chooseType = findViewById(R.id.chooseType)
        typeOrder = findViewById(R.id.typeOrder)
        priceOne = findViewById(R.id.priceOne)
        priceOneText = findViewById(R.id.priceOneText)
        payment = findViewById(R.id.payment)
        fab = findViewById(R.id.fab)

        fab.setOnClickListener {
            if (isOrderAll()) {
                val intent = Intent(
                    this,
                    ConfirmActivity::class.java
                )

                intent.putExtra("address", chosenAddress)
                intent.putExtra("paymentVariant", paymentVariant)
                intent.putExtra("priceAllOrder", priceAllOrder)
                intent.putExtra("deliv", deliv)
                intent.putExtra("product", product)
                intent.putExtra("countTotal", countTotal)

                startActivity(intent)
            } else {
                Toast.makeText(baseContext, "Заказ не полон.",
                    Toast.LENGTH_SHORT).show()
            }

        }

        typeOrder.setExpanded(true)

        var nPrevSelChooseType = -1
        var nPrevSelTypeOrder = -1
        var nPrevSelPayment = -1

        val gridPaymentAdapter = GridAdapter(this, paymentArr)
        payment.adapter = gridPaymentAdapter

        payment.onItemClickListener = AdapterView.OnItemClickListener { _, view, position, _ ->
            if (nPrevSelPayment != -1) {
                val viewPrev = payment.getChildAt(nPrevSelPayment) as View
                viewPrev.background = ContextCompat.getDrawable(this, R.drawable.border_item)
            }

            nPrevSelPayment = position
            if (nPrevSelPayment == position) {
                view.background = ContextCompat.getDrawable(this, R.drawable.border_item_selected)
            }

            when(payment.getItemAtPosition(position).toString()) {
                paymentArr[0] -> {
                    paymentVariant = 0
                }
                paymentArr[1] -> {
                    paymentVariant = 1
                }
            }
        }

        val gridAdapter = GridAdapter(this, types)
        chooseType.adapter = gridAdapter

        chooseType.onItemClickListener = AdapterView.OnItemClickListener { _, view, position, _ ->
            if (nPrevSelChooseType != -1) {
                val viewPrev = chooseType.getChildAt(nPrevSelChooseType) as View
                viewPrev.background = ContextCompat.getDrawable(this, R.drawable.border_item)
            }

            nPrevSelChooseType = position
            if (nPrevSelChooseType == position) {
                view.background = ContextCompat.getDrawable(this, R.drawable.border_item_selected)
            }

            typeProduct = chooseType.getItemAtPosition(position).toString()

            when(typeProduct) {
                types[0] -> {
                    val gridTypeAdapter = GridAdapter(this, beton)
                    typeOrder.adapter = gridTypeAdapter
                    nPrevSelTypeOrder = -1
                }
                types[1] -> {
                    val gridTypeAdapter = GridAdapter(this, rastvor)
                    typeOrder.adapter = gridTypeAdapter
                    nPrevSelTypeOrder = -1
                }
            }
        }
        typeOrder.onItemClickListener = AdapterView.OnItemClickListener { _, view, position, _ ->
            if (nPrevSelTypeOrder != -1) {
                val viewPrev = typeOrder.getChildAt(nPrevSelTypeOrder) as View
                viewPrev.background = ContextCompat.getDrawable(this, R.drawable.border_item)
            }

            nPrevSelTypeOrder = position
            if (nPrevSelTypeOrder == position) {
                view.background = ContextCompat.getDrawable(this, R.drawable.border_item_selected)
            }

            priceOne.visibility = View.VISIBLE

            product = typeOrder.getItemAtPosition(position).toString()

            when(product) {
                beton[0] -> {
                    priceOneText.text = "3110 ₽/м³"
                    priceOrder = 3110
                }
                beton[1] -> {
                    priceOneText.text = "3220 ₽/м³"
                    priceOrder = 3220
                }
                beton[2] -> {
                    priceOneText.text = "3330 ₽/м³"
                    priceOrder = 3330
                }
                beton[3] -> {
                    priceOneText.text = "3450 ₽/м³"
                    priceOrder = 3450
                }
                beton[4] -> {
                    priceOneText.text = "3550 ₽/м³"
                    priceOrder = 3550
                }
                beton[5] -> {
                    priceOneText.text = "3700 ₽/м³"
                    priceOrder = 3700
                }
                beton[6] -> {
                    priceOneText.text = "3880 ₽/м³"
                    priceOrder = 3880
                }
                beton[7] -> {
                    priceOneText.text = "4130 ₽/м³"
                    priceOrder = 4130
                }
                beton[8] -> {
                    priceOneText.text = "4440 ₽/м³"
                    priceOrder = 4440
                }
            }
            priceAllOrder = priceOrder * countTotal + priceDelivery
            priceAll.text = "${priceAllOrder} ₽"
        }

        seekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar,
                                           progress: Int, fromUser: Boolean) {
                countTotal = seek.progress

                countTotalText.text = "${countTotal} м³"
                priceAllOrder = priceOrder * countTotal + priceDelivery
                priceAll.text = "${priceAllOrder} ₽"

            }

            override fun onStartTrackingTouch(seek: SeekBar) {
                // write custom code for progress is started
            }

            override fun onStopTrackingTouch(seek: SeekBar) {
                countTotal = seek.progress

                countTotalText.text = "${countTotal} м³"
                priceAllOrder = priceOrder * countTotal + priceDelivery
                priceAll.text = "${priceAllOrder} ₽"
            }
        })

        deliveryCheck.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { _, b ->
            if (b) {
                deliv = b
                priceDelivery = priceDeliverySum
                priceAllOrder = priceOrder * countTotal + priceDelivery
                priceAll.text = "${priceAllOrder} ₽"
            } else {
                deliv = b
                priceDelivery = 0
                priceAllOrder = priceOrder * countTotal - priceDelivery
                priceAll.text = "${priceAllOrder} ₽"
            }
        })

        val receivedIntent = intent
        distance = receivedIntent.getFloatExtra("dist", 0F)
        chosenAddress = receivedIntent.getStringExtra("address").toString()

        val km = distance!! / 1000
        val zone = (km / 10).roundToInt()
        priceDeliverySum = zone * 200

        address.text = chosenAddress
        countTotalText.text = "${countTotal} м³"
        deliveryPrice.text = "${priceDeliverySum} ₽"
        priceAll.text = "${priceAllOrder} ₽"

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

    private fun isOrderAll() : Boolean {
        return chosenAddress.isNotEmpty() && countTotal != 0 && paymentVariant != -1 && product.isNotEmpty() && typeProduct.isNotEmpty()
    }
}