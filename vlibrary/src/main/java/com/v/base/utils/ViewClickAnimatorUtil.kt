package com.v.base.utils


import android.annotation.SuppressLint
import android.graphics.Rect
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.v.base.utils.ext.lifecycleOwner
import com.v.base.utils.ext.log
import com.v.base.utils.ext.logE
import com.v.base.utils.ext.vbInvalidClick
import kotlin.math.roundToInt

/**
 * @Author : ww
 * desc    : 点击动画
 * time    : 2020/12/29 15:22
 */

class ViewClickAnimatorUtil(
    var view: View,
    var clickTime: Long = 500L,
    var onClick: ((v: View) -> Unit)

) : LifecycleObserver {
    private var down = false
    private val timeAnim = 150L
    private val animation =
        ScaleAnimation(this.getF(), 1.0f, this.getF(), 1.0f, 1, 0.5f, 1, 0.5f)

    init {
        addTouchListener()
        view.context.lifecycleOwner()?.lifecycle?.addObserver(this)
    }

    private fun getF(): Float {
        return 1.0f - TypedValue.applyDimension(
            1,
            10.0f,
            view.context.resources.displayMetrics
        ).roundToInt().toFloat() / view.width.toFloat()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun addTouchListener() {

        view.setOnTouchListener { _, event ->

            var animation: ScaleAnimation? = null
            val viewRect = Rect()
            this.view.getLocalVisibleRect(viewRect)
            val b =
                event.x < viewRect.right.toFloat() && event.x > viewRect.left.toFloat() && event.y < viewRect.bottom.toFloat() && event.y > viewRect.top.toFloat()
            when (event.action) {
                MotionEvent.ACTION_DOWN -> if (!this.down) {
                    animation =
                        ScaleAnimation(1.0f, this.getF(), 1.0f, this.getF(), 1, 0.5f, 1, 0.5f)
                    animation.duration = timeAnim
                    animation.fillAfter = true
                    this.view.startAnimation(animation)

                    this.down = true
                    this.view.isPressed = true
                }
                MotionEvent.ACTION_UP -> this.clearAnimation(animation, b, true)
                MotionEvent.ACTION_MOVE -> if (!b) {
                    this.clearAnimation(animation, b, false)
                }
                MotionEvent.ACTION_CANCEL -> this.clearAnimation(animation, b, false)
                else -> this.clearAnimation(animation, b, false)
            }
            true
        }

    }

    private fun clearAnimation(scaleAnimation: ScaleAnimation?, b: Boolean, up: Boolean) {
        try {

            this.view.isPressed = false
            if (this.down) {
                this.down = false


                animation.duration = timeAnim
                if (up) {

                    animation.setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationStart(paramAnimation: Animation) {
                            dispose()
                        }

                        override fun onAnimationRepeat(paramAnimation: Animation) {
                        }

                        override fun onAnimationEnd(paramAnimation: Animation) {
                            dispose()
                        }
                    })
                }
                this.view.startAnimation(animation)
            }


        } catch (e: Exception) {
            e.printStackTrace()
            e.toString().logE()
        }
    }


    private fun dispose() {
        if (!view.vbInvalidClick(clickTime)) {
            "dispose".log()
            onClick(view)
        }

    }


    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        animation.cancel()
    }


}