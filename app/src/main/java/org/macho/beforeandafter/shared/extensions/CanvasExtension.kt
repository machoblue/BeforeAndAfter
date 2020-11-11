package org.macho.beforeandafter.shared.extensions

import android.graphics.Canvas
import android.graphics.RectF
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint

fun Canvas.drawText(text: String, rect: RectF, textPaint: TextPaint) {
    val alignment = Layout.Alignment.ALIGN_CENTER
    val spacingMultiplier = 1f
    val spacingAddition = 0f
    val includePadding = false
    val width = rect.right - rect.left
    val staticLayout = StaticLayout(text, textPaint, width.toInt(), alignment, spacingMultiplier, spacingAddition, includePadding)

    val textHeight = staticLayout.height
    val rectHeight = rect.bottom - rect.top
    val paddingTop = (rectHeight - textHeight) / 2
    val textY = rect.top + paddingTop

    save()
    translate(rect.left, textY)

    staticLayout.draw(this)

    restore()
}