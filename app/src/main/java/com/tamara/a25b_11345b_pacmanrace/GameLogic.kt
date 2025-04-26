package com.tamara.a25b_11345b_pacmanrace

import kotlin.random.Random

class GameLogic(private val rows: Int = 7, private val cols: Int = 3) {

    private val obstacleMatrix: Array<IntArray> = Array(rows) { IntArray(cols) { 0 } }
    private var playerCol = 1
    private var generateObstacle = true

    fun getPlayerColumn(): Int = playerCol

    fun getObstacleMatrix(): Array<IntArray> = obstacleMatrix.map { it.clone() }.toTypedArray()

    fun moveLeft() {
        if (playerCol > 0) playerCol--
    }

    fun moveRight() {
        if (playerCol < cols - 1) playerCol++
    }

    fun updateObstacles() {
        for (row in rows - 2 downTo 0) {
            for (col in 0 until cols) {
                obstacleMatrix[row + 1][col] = obstacleMatrix[row][col]
            }
        }

        for (col in 0 until cols) {
            obstacleMatrix[0][col] = 0
        }

        if (generateObstacle) {
            obstacleMatrix[0][Random.nextInt(cols)] = 1
        }

        generateObstacle = !generateObstacle
    }

    fun checkCollision(): Boolean {
        return obstacleMatrix[rows - 1][playerCol] == 1
    }

    fun resetBottomRow() {
        for (col in 0 until cols) {
            obstacleMatrix[rows - 1][col] = 0
        }
    }

    fun resetGame() {
        playerCol = 1
        for (row in 0 until rows) {
            for (col in 0 until cols) {
                obstacleMatrix[row][col] = 0
            }
        }
    }
}
