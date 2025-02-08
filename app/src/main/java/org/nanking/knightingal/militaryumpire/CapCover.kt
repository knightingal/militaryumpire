package org.nanking.knightingal.militaryumpire

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class CapCover : View {

    constructor(
        context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    constructor(
        context: Context, attrs: AttributeSet?, defStyleAttr: Int
    ) : this(context, attrs, defStyleAttr, 0)

    constructor(
        context: Context, attrs: AttributeSet?
    ) : this(context, attrs, 0)

    constructor(
        context: Context
    ) : this(context, null)


    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        val p = Paint()
        p.color = Color.BLUE
        val width:Float = (width / 2).toFloat()
        canvas.drawLine(0f, 0f, width, width, p)
    }


}