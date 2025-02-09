package org.nanking.knightingal.militaryumpire

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View

class CapCover : View {
    var coverWidth: Int = 0

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
        p.color = Color.GREEN
        p.style = Paint.Style.STROKE

        val rectWidth = width / 2
        val top = height / 2 - rectWidth / 2
        val bottom = height / 2 + rectWidth / 2
        val left = width / 4
        val right = left + rectWidth
        coverWidth = rectWidth
        canvas.drawRect(Rect(left, top, right, bottom), p)
    }


}