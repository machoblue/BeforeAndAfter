package org.macho.beforeandafter.graph

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import org.macho.beforeandafter.shared.util.LogUtil

class TwoScaleGraphView: View {
    constructor(context: Context): super(context) {
    }

    constructor(context: Context, attrs: AttributeSet?): super(context, attrs) {
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        LogUtil.d(this, "width : ${this.width}")
        LogUtil.d(this, "height: ${this.height}")
    }
}