package org.macho.beforeandafter.shared.view

import android.content.Context
import android.os.Handler
import android.support.v7.widget.AppCompatImageButton
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent

class PressButton: AppCompatImageButton {
    companion object {
        private const val TAG = "PressButton"
        private const val REPEAT_DELAY: Long = 50
    }

    private var continuePerformingClick = false
    private var myHandler = Handler()

    constructor(context: Context): super(context) {
        configure()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        configure()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        configure()
    }

    private fun configure() {
        setOnLongClickListener {
            continuePerformingClick = true
            handler.post(RepeatableRunnable())
            return@setOnLongClickListener false
        }

        setOnTouchListener { view, motionEvent ->
            continuePerformingClick = motionEvent.action != MotionEvent.ACTION_UP
            return@setOnTouchListener false
        }
    }

    inner class RepeatableRunnable(): Runnable {
        override fun run() {
            Log.d(TAG, "*** repeat ***")
            if (!continuePerformingClick) {
                return
            }
            performClick()
            myHandler.postDelayed(RepeatableRunnable(), REPEAT_DELAY)
        }
    }
}