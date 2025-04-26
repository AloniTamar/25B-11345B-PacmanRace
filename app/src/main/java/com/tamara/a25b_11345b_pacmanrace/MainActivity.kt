package com.tamara.a25b_11345b_pacmanrace

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.os.CountDownTimer
import com.tamara.a25b_11345b_pacmanrace.utilities.SignalManager

class MainActivity : AppCompatActivity() {

    private lateinit var gameLogic: GameLogic
    private lateinit var grid: Array<Array<ImageView>>
    private lateinit var leftBtn: FloatingActionButton
    private lateinit var rightBtn: FloatingActionButton
    private lateinit var gameTimer: CountDownTimer
    private var lives = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        SignalManager.init(this)

        gameLogic = GameLogic()

        leftBtn = findViewById(R.id.leftBtn)
        rightBtn = findViewById(R.id.rightBtn)

        grid = Array(7) { row ->
            Array(3) { col ->
                val cellId = resources.getIdentifier("cell_${row}_${col}", "id", packageName)
                findViewById(cellId)
            }
        }

        drawPlayer()

        leftBtn.setOnClickListener {
            clearPlayer()
            gameLogic.moveLeft()
            drawPlayer()
        }

        rightBtn.setOnClickListener {
            clearPlayer()
            gameLogic.moveRight()
            drawPlayer()
        }

        startGameLoop()

    }

    private fun drawPlayer() {
        val col = gameLogic.getPlayerColumn()
        grid[6][col].setImageResource(R.drawable.ic_pacman)
    }

    private fun clearPlayer() {
        val col = gameLogic.getPlayerColumn()
        grid[6][col].setImageDrawable(null)
    }

    private fun startGameLoop() {
        gameTimer = object : CountDownTimer(Long.MAX_VALUE, 700) {
            override fun onTick(millisUntilFinished: Long) {
                gameLogic.updateObstacles()

                if (gameLogic.checkCollision()) {
                    SignalManager.getInstance().vibrate()
                    SignalManager.getInstance().toast("Ouch! You hit a ghost!")

                    lives--
                    updateHeartsUI()
                    gameLogic.resetBottomRow()

                    if (lives == 0) {
                        grid[0][0].postDelayed({
                            SignalManager.getInstance().vibrate()
                            SignalManager.getInstance().toast("Game Over!")
                            resetGame()
                        }, 1000)
                    }
                }

                drawObstacles()
            }

            override fun onFinish() {
                // TBD in the future
            }
        }

        gameTimer.start()
    }


    private fun drawObstacles() {
        val matrix = gameLogic.getObstacleMatrix()
        for (row in 0..6) {
            for (col in 0..2) {
                if (matrix[row][col] == 1) {
                    grid[row][col].setImageResource(R.drawable.ic_game)
                } else {
                    grid[row][col].setImageDrawable(null)
                }
            }
        }
        drawPlayer()
    }

    private fun updateHeartsUI() {
        val hearts = listOf(
            findViewById<ImageView>(R.id.heart1),
            findViewById<ImageView>(R.id.heart2),
            findViewById<ImageView>(R.id.heart3)
        )

        for (i in hearts.indices) {
            hearts[i].alpha = if (i < lives) 1.0f else 0.2f
        }
    }

    private fun resetGame() {
        lives = 3
        updateHeartsUI()
        gameLogic.resetGame()
    }
}
