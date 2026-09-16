package com.novapvp.client

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import android.view.View

class GameView(context: Context) : View(context) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var touchX = 0f
    private var touchY = 0f

    init {
        paint.textAlign = Paint.Align.CENTER
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawColor(Color.rgb(35, 35, 35))

        paint.color = Color.rgb(80, 160, 80)

        canvas.drawRect(
            0f,
            height * 0.65f,
            width.toFloat(),
            height.toFloat(),
            paint
        )

        paint.color = Color.WHITE
        paint.textSize = 48f

        canvas.drawText(
            "NovaPvP",
            width / 2f,
            height / 2f - 30f,
            paint
        )

        paint.textSize = 20f

        canvas.drawText(
            "Игровой мир",
            width / 2f,
            height / 2f + 20f,
            paint
        )

        paint.textSize = 16f

        canvas.drawText(
            "Проведи пальцем по экрану",
            width / 2f,
            height / 2f + 60f,
            paint
        )
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        when (event.action) {

            MotionEvent.ACTION_DOWN -> {
                touchX = event.x
                touchY = event.y
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                touchX = event.x
                touchY = event.y
                invalidate()
                return true
            }

            MotionEvent.ACTION_UP -> {
                return true
            }
        }

        return true
    }
}
