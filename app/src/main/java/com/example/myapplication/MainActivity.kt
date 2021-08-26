package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager

class MainActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onClick(v: View?){
        when(v?.id){
            R.id.fireButton->{
                findViewById<GameView>(R.id.gameView).currentCaseState = GameCaseState.Fire
            }
            R.id.waterButton->{
                findViewById<GameView>(R.id.gameView).currentCaseState = GameCaseState.Water
            }
            R.id.earthButton->{
                findViewById<GameView>(R.id.gameView).currentCaseState = GameCaseState.Earth
            }
            R.id.windButton->{
                findViewById<GameView>(R.id.gameView).currentCaseState = GameCaseState.Wind
            }
            R.id.replayButton->{
                findViewById<GameView>(R.id.gameView).resetBoard()
            }
        }
    }
}