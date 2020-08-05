package com.example.beton

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

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

        val priceTextView: TextView = view.findViewById(R.id.priceTextItem)
        val status: TextView = view.findViewById(R.id.status_item)
        val address: TextView = view.findViewById(R.id.addressText_item)
        val countTextView: TextView = view.findViewById(R.id.countText_item)
        val product: TextView = view.findViewById(R.id.typeText_item)
        val deliv: TextView = view.findViewById(R.id.delivText_item)

        priceTextView.text = listData[position].price.toString()
        status.text = listData[position].status
        address.text = listData[position].address
        countTextView.text = listData[position].count.toString()
        product.text = listData[position].product
        deliv.text = if (listData[position].delivery) "С доставкой" else "Без доставки"


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