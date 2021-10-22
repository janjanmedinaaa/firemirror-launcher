package com.medina.juanantonio.firemirror.common.extensions

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator

fun View.animateDimensions(
    originalViewDimensions: Pair<Int, Int>,
    increasePercentage: Float,
    duration: Long = 200
) {
    val originalViewWidth = originalViewDimensions.first.toFloat()
    val originalViewHeight = originalViewDimensions.second.toFloat()

    val increaseViewWidth = originalViewWidth + (originalViewWidth * increasePercentage)
    val increaseViewHeight = originalViewHeight + (originalViewHeight * increasePercentage)

    val valueWidthAnimator =
        ValueAnimator.ofFloat(layoutParams.width.toFloat(), increaseViewWidth)
    val valueHeightAnimator =
        ValueAnimator.ofFloat(layoutParams.height.toFloat(), increaseViewHeight)

    valueWidthAnimator.addUpdateListener { animation ->
        val viewLayoutParams = layoutParams
        layoutParams.width = (animation?.animatedValue as Float).toInt()
        layoutParams = viewLayoutParams
        requestLayout()
    }

    valueHeightAnimator.addUpdateListener { animation ->
        val viewLayoutParams = layoutParams
        layoutParams.height = (animation?.animatedValue as Float).toInt()
        layoutParams = viewLayoutParams
        requestLayout()
    }

    AnimatorSet().run {
        play(valueWidthAnimator)
        play(valueHeightAnimator)
        this.duration = duration
        interpolator = AccelerateDecelerateInterpolator()
        start()
    }
}