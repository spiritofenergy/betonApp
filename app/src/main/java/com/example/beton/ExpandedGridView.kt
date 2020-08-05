package com.example.beton

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import java.text.AttributedCharacterIterator
import java.util.ArrayList

class ExpandedGridView : GridView {
    private var expanded: Boolean = false

    constructor(ctx: Context) : super(ctx){ }

    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs) { }

    constructor(ctx: Context, attrs: AttributeSet, defStyle: Int) : super(ctx, attrs, defStyle) { }

    fun isExpanded() : Boolean {
        return expanded
    }

    fun setExpanded(expanded: Boolean) {
        this.expanded = expanded
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (isExpanded()) {
            val expandSpec: Int = MeasureSpec.makeMeasureSpec(View.MEASURED_SIZE_MASK, MeasureSpec.AT_MOST)
            super.onMeasure(widthMeasureSpec, expandSpec)

            val params: ViewGroup.LayoutParams = layoutParams
            params.height = measuredHeight
        }else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

}