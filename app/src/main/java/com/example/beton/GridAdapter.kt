package com.example.beton

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import org.w3c.dom.Text


class GridAdapter(ctx: Context, types: Array<String>) : BaseAdapter() {
    private val listData: Array<String> = types
    private val context: Context = ctx
    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val textView: TextView?
        if (convertView == null) {
            view = layoutInflater.inflate(R.layout.item_text, null)
        } else {
            view = convertView
        }
        textView = view.findViewById(R.id.item_text)
        textView?.text = listData[position]

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

