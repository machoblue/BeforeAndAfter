package org.macho.beforeandafter.shared.extensions

import android.graphics.Canvas
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint


fun Canvas.drawText(text: String, x: Float, y: Float, width: Float, textPaint: TextPaint) {
    save()
    translate(x, y)

//    StaticLayout.Builder.obtain(text, 0, text.length, textPaint, width)

    val alignment = Layout.Alignment.ALIGN_CENTER
    val spacingMultiplier = 1f
    val spacingAddition = 0f
    val includePadding = false
    val staticLayout = StaticLayout(text, textPaint, width.toInt(), alignment, spacingMultiplier, spacingAddition, includePadding)
    staticLayout.draw(this)

    restore()
}