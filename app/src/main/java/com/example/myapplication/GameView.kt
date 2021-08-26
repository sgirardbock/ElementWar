package com.example.myapplication

import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator

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
    private var currentDrawIndex = 0
    private var maxDrawIndex: Int = 0


    init {
        setBackgroundColor(Color.BLACK)
        var index = 1
        for (i in 0..numberOfCasesX){
            for (j in 0..numberOfCasesY){
                board.add(GameCase(index, 0, GameCaseState.None))
                index++
            }
        }
    }

    fun resetBoard(){
        board.forEach {
            it.state = GameCaseState.None
        }
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        drawCases(canvas)
    }

    private fun drawCases(c: Canvas?)
    {
        val myPaint = Paint()
        myPaint.setColor(Color.WHITE)
        myPaint.setStrokeWidth(lineWidth)
        myPaint.setStyle(Paint.Style.STROKE)

        for (i in 1 .. numberOfCasesX){
            for(j in 1..numberOfCasesY){
                val curIndex = i + j*(numberOfCasesX)

                when (board[curIndex].state){
                    GameCaseState.Fire-> {
                        myPaint.color = Color.RED
                        myPaint.style = Paint.Style.FILL_AND_STROKE
                    }
                    GameCaseState.Earth-> {
                        myPaint.color = Color.DKGRAY
                        myPaint.style = Paint.Style.FILL_AND_STROKE
                    }
                    GameCaseState.Water-> {
                        myPaint.color = Color.BLUE
                        myPaint.style = Paint.Style.FILL_AND_STROKE
                    }
                    GameCaseState.Wind-> {
                        myPaint.color = Color.WHITE
                        myPaint.style = Paint.Style.FILL_AND_STROKE
                    }
                    GameCaseState.None-> {
                        myPaint.color = Color.WHITE
                        myPaint.style = Paint.Style.STROKE
                    }
                }

                if (board[curIndex].drawIndex != 0 && board[curIndex].drawIndex != currentDrawIndex){
                    myPaint.color = Color.WHITE
                    myPaint.style = Paint.Style.STROKE
                }
                else{
                    board[curIndex].drawIndex = 0
                }

                c?.drawRect(
                    lineWidth + i * caseDimension + i * buffer,
                    lineWidth + j * caseDimension + j * buffer,
                    (i+1) * caseDimension + i * buffer,
                    (j+1) * caseDimension + j * buffer,
                    myPaint)
            }
        }
    }

    fun animateProgress() {
        val valuesHolder = PropertyValuesHolder.ofInt(
            "drawIndex",
            0,
            maxDrawIndex
        )

        val animator = ValueAnimator().apply {
            setValues(valuesHolder)
            duration = 1000
            interpolator = AccelerateDecelerateInterpolator()

            addUpdateListener {
                val drawIndex = it.getAnimatedValue("drawIndex") as Int
                currentDrawIndex = drawIndex
                invalidate()
            }
        }
        animator.start()
        currentDrawIndex = 0
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        if (event != null && (event.x != null || event.y != null)){

            val posX = (event.x / (buffer + caseDimension)).toInt()
            val posY = (event.y / (buffer + caseDimension)).toInt()

            val index = posX + posY * numberOfCasesX
            val affectedCases = getAffectedCases(index)
            for (i in 0..affectedCases.size-1) {
                var case = affectedCases[i]
                if (case.position < board.size && case.position > 0) {
                    case.state = currentCaseState
                }
            }
        }
        animateProgress()

        return super.onTouchEvent(event)
    }

    private fun getAffectedCases(initialIndex: Int): ArrayList<GameCase> {
        var cases = arrayListOf<GameCase>()

        board[initialIndex].drawIndex = 1
        cases.add(board[initialIndex])

        when (currentCaseState){
            GameCaseState.Fire-> {
                //Burn 5 adjacent cases
                var adjacentCases = getAdjacentCases(initialIndex)
                var casesTurnedToFire = getRandomCases(adjacentCases, 5).filter { it.state == GameCaseState.None || it.state == GameCaseState.Wind }.toMutableList()
                cases.addAll( casesTurnedToFire )
            }
            GameCaseState.Earth-> {
                var topLeftIndex = initialIndex - numberOfCasesX - 1
                var topRightIndex = initialIndex - numberOfCasesX + 1
                var bottomLeftIndex = initialIndex + numberOfCasesX - 1
                var bottomRightIndex = initialIndex + numberOfCasesX + 1
                var topLeftCase = board[topLeftIndex]
                var topRightCase = board[topRightIndex]
                var bottomLeftCase = board[bottomLeftIndex]
                var bottomRightCase = board[bottomRightIndex]
                var extraTopRightCase = board[topRightIndex - numberOfCasesX + 1]

                var cornerCases = listOf( topLeftCase, bottomRightCase, bottomLeftCase, topRightCase, extraTopRightCase )

                for (i in 0..cornerCases.size-1)
                {
                    cornerCases[i].drawIndex = i + 1
                }

                cases.addAll(cornerCases)
            }
//            GameCaseState.Water-> {
//
//            }
//            GameCaseState.Wind-> {
//
//            }
//            GameCaseState.None-> {
//
//            }
        }

        for (i in 1..cases.size){
            cases[i].drawIndex = i
        }

        maxDrawIndex = cases.size

        return cases
    }

    private fun getAdjacentCases(index: Int, nbOfLevels: Int = 1): MutableList<Int> {
        var cases = arrayListOf<Int>()

        for(i in index - numberOfCasesX - 1 .. index - numberOfCasesX + 1)
            cases.add(i)

        cases.add(index - 1)
        cases.add(index + 1)

        for(i in index + numberOfCasesX - 1 .. index + numberOfCasesX + 1)
            cases.add(i)

        return cases.toMutableList()
    }

    private fun getRandomCases(cases: MutableList<Int>, numberOfCases: Int): ArrayList<GameCase> {
        var randomCases = arrayListOf<GameCase>()
        for (i in 1..numberOfCases){
            val randomIndex = (0..cases.size-1).random()
            var caseIndex = cases[randomIndex]
            cases.removeAt(randomIndex)
            var case = board[caseIndex]
            case.drawIndex = i
            randomCases.add(case)
        }

        return randomCases
    }
}