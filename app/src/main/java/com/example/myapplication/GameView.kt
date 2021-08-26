package com.example.myapplication

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class GameView(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val caseDimension: Float = 60F
    private val buffer: Float = 30F
    private val numberOfCasesX: Int = 8
    private val numberOfCasesY: Int = 8
    private val lineWidth = 3F;
    private var board = ArrayList<GameCase>()
    var currentCaseState = GameCaseState.Fire


    init {
        var index = 1
        for (i in 0..numberOfCasesX){
            for (j in 0..numberOfCasesY){
                board.add(GameCase(index, GameCaseState.None))
                index++
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        drawCases(canvas)
    }

    private fun drawCases(c: Canvas?)
    {
        val myPaint = Paint()
        myPaint.setColor(Color.rgb(0, 0, 0))
        myPaint.setStrokeWidth(lineWidth)
        myPaint.setStyle(Paint.Style.STROKE)

        for (i in 1 .. numberOfCasesX){
            for(j in 1..numberOfCasesY){
                if (board[i + j*(numberOfCasesX)].state == GameCaseState.Fire)
                    myPaint.style = Paint.Style.FILL_AND_STROKE
                else
                    myPaint.style = Paint.Style.STROKE
                c?.drawRect(
                    lineWidth + i * caseDimension + i * buffer,
                    lineWidth + j * caseDimension + j * buffer,
                    (i+1) * caseDimension + i * buffer,
                    (j+1) * caseDimension + j * buffer,
                    myPaint)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        if (event != null && (event.x != null || event.y != null)){

            val posX = (event.x / (buffer + caseDimension)).toInt()
            val posY = (event.y / (buffer + caseDimension)).toInt()

            val index = posX + posY * numberOfCasesX
            board[index].state = currentCaseState
        }
        invalidate()

        return super.onTouchEvent(event)
    }
}