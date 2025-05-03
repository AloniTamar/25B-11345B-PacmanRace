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
    companion object {
        private const val NUM_ROWS = 7
        private const val NUM_COLS = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        SignalManager.init(this)

        gameLogic = GameLogic()
        findViews()
        initListeners()
        startGame()
    }

    private fun drawPlayer() {
        val col = gameLogic.getPlayerColumn()
        grid[NUM_ROWS - 1][col].setImageResource(R.drawable.ic_pacman)
    }

    private fun clearPlayer() {
        val col = gameLogic.getPlayerColumn()
        grid[NUM_ROWS - 1][col].setImageDrawable(null)
    }

    private fun startGame() {
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
        for (row in 0..NUM_ROWS - 1) {
            for (col in 0..NUM_COLS - 1) {
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
            findViewById<ImageView>(R.id.main_IMG_heart1),
            findViewById<ImageView>(R.id.main_IMG_heart2),
            findViewById<ImageView>(R.id.main_IMG_heart3)
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

    private fun findViews() {
        leftBtn = findViewById(R.id.main_IMG_leftBtn)
        rightBtn = findViewById(R.id.main_IMG_rightBtn)

        grid = Array(NUM_ROWS) { row ->
            Array(NUM_COLS) { col ->
                val cellId = resources.getIdentifier("main_IMG_cell_${row}_${col}", "id", packageName)
                findViewById(cellId)
            }
        }
    }

    private fun initListeners() {
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
    }
}
