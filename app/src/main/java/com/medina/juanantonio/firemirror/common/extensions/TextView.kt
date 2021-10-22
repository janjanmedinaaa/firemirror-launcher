package com.medina.juanantonio.firemirror.common.extensions

import android.widget.TextView
import android.animation.ValueAnimator

fun TextView.animateTextSize(endSize: Float, duration: Long = 200) {
    val animator = ValueAnimator.ofFloat(textSize.toInt().dp.toFloat(), endSize)
    animator.duration = duration
    animator.addUpdateListener { valueAnimator ->
        val animatedValue = valueAnimator.animatedValue as Float
        textSize = animatedValue
    }

    animator.start()
}