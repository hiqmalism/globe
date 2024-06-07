package com.hiqmalism.mystorysubmission.view.customview

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.hiqmalism.mystorysubmission.R

class OutlinedButton : AppCompatButton {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    private var txtColor: ColorStateList? = ContextCompat.getColorStateList(context, R.color.text_state)
    private var outlinedBackground: Drawable = ContextCompat.getDrawable(context, R.drawable.bg_button_outlined) as Drawable

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        background = outlinedBackground
        setTextColor(txtColor)
        textSize = 12f
        gravity = Gravity.CENTER
    }
}