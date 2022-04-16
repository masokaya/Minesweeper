package minesweeper

import java.util.*
import kotlin.random.Random

private val cells = mutableListOf(
    MutableList(9) { '.' },
    MutableList(9) { '.' },
    MutableList(9) { '.' },
    MutableList(9) { '.' },
    MutableList(9) { '.' },
    MutableList(9) { '.' },
    MutableList(9) { '.' },
    MutableList(9) { '.' },
    MutableList(9) { '.' }
)
private val mineLocation = mutableListOf<String>()
var win: Boolean = false
var failed: Boolean = false
var numberOfMarkedMine = 0
var markedPosition = mutableListOf<String>()
var mapPositionNearMines = mutableMapOf<String, Char>()
val nextToCheck = MutableList(90) { String() }
var leftToCheck = nextToCheck.size - mineLocation.count() - 9

fun main() {
    print("How many mines do you want on the field? >")
    val num: Int = readln().toInt()
    addMines(num)
    addHint()
    saveMarkedPosition()
    removeMineMark()
    removeHint()
    display()
    val scanner = Scanner(System.`in`)
    while (leftToCheck != 0) {
        val temp = mutableListOf<String>()
        repeat(nextToCheck.size) {
            leftToCheck = nextToCheck.size - mineLocation.count() - 9
            numberOfMarkedMine = 0
            nextToCheck.forEach {
                if (it.isNotEmpty()) {
                    temp.add(it)
                    nextToCheck[nextToCheck.indexOf(it)] = String()
                }
            }
            temp.forEach {
                val row = it.first().toString().toInt()
                val col = it.last().toString().toInt()
                markNext(row, col)
            }
        }
        cells.forEach { list ->
            list.forEach { c ->
                if (c == '*') {
                    val l = "${cells.indexOf(list)}${list.indexOf(c)}"
                    if (mineLocation.contains(l)) {
                        numberOfMarkedMine += 1
                    } else leftToCheck -= 1
                } else if (c == '/' || c != '.') {
                    leftToCheck -= 1
                }
            }
        }
        println(leftToCheck)
        if (leftToCheck == 0) win = true
        if (leftToCheck != 0) {
            display()
            print("Set/unset mines marks or claim a cell as free: >")
            var x = scanner.nextInt()
            var y = scanner.nextInt()
            var action = scanner.next()
            if (x in 1..9 && y in 1..9) {
                makeDecision(x, y, action)
            } else {
                println("You entered wrong coordinate try again")
                print("Set/unset mines marks or claim a cell as free: >")
                x = scanner.nextInt()
                y = scanner.nextInt()
                action = scanner.next()
                makeDecision(x, y, action)
                display()
            }
        }
        println()
    }
    when {
        failed -> {
            markMine()
            display()
            println("You stepped on a mine and failed!")
        }
        else -> {
            display()
            println("Congratulations! You found all the mines!")
        }
    }
}

fun markNext(row: Int, col: Int) {
    if (row in 0..8 && col in 0..8) {
        val location = "$row$col"
        when {
            mineLocation.contains(location) -> {

            }
            markedPosition.contains(location) -> {
                cells[row][col] = mapPositionNearMines[location]!!

            }
            else -> {
                if (col in 0..8 && row in 0..8) {
                    cells[row][col] = '/'
                    if (col + 1 in 0..8) {
                        val east = "$row${col + 1}"
                        if (markedPosition.contains(east)) {
                            cells[row][col + 1] = mapPositionNearMines[east]!!
                        } else {
                            cells[row][col + 1] = '/'
                            nextToCheck["${row}${col + 1}".toInt()] = "${row}${col + 1}"
                        }
                    }
                    if (col - 1 in 0..8) {
                        val west = "$row${col - 1}"
                        if (markedPosition.contains(west)) {
                            cells[row][col - 1] = mapPositionNearMines[west]!!
                        } else {
                            cells[row][col - 1] = '/'
                            nextToCheck["$row${col - 1}".toInt()] = "$row${col - 1}"
                        }
                    }
                    if (row + 1 in 0..8) {
                        val south = "${row + 1}$col"
                        if (markedPosition.contains(south)) {
                            cells[row + 1][col] = mapPositionNearMines[south]!!
                        } else {
                            cells[row + 1][col] = '/'
                            nextToCheck["${row + 1}$col".toInt()] = "${row + 1}$col"
                        }
                    }
                    if (row - 1 in 0..8) {
                        val north = "${row - 1}$col"
                        if (markedPosition.contains(north)) {
                            cells[row - 1][col] = mapPositionNearMines[north]!!
                        } else {
                            cells[row - 1][col] = '/'
                            nextToCheck["${row - 1}$col".toInt()] = "${row - 1}$col"
                        }
                    }
                    if (row - 1 in 0..8 && col + 1 in 0..8) {
                        val northEast = "${row - 1}${col + 1}"
                        if (markedPosition.contains(northEast)) {
                            cells[row - 1][col + 1] = mapPositionNearMines[northEast]!!
                        } else {
                            cells[row - 1][col + 1] = '/'
                            nextToCheck["${row - 1}${col + 1}".toInt()] = "${row - 1}${col + 1}"
                        }
                    }
                    if (row - 1 in 0..8 && col - 1 in 0..8) {
                        val northWest = "${row - 1}${col - 1}"
                        if (markedPosition.contains(northWest)) {
                            cells[row - 1][col - 1] = mapPositionNearMines[northWest]!!
                        } else {
                            cells[row - 1][col - 1] = '/'
                            nextToCheck["${row - 1}${col - 1}".toInt()] = "${row - 1}${col - 1}"
                        }
                    }
                    if (row + 1 in 0..8 && col - 1 in 0..8) {
                        val southWest = "${row + 1}${col - 1}"
                        if (markedPosition.contains(southWest)) {
                            cells[row + 1][col - 1] = mapPositionNearMines[southWest]!!
                        } else {
                            cells[row + 1][col - 1] = '/'
                            nextToCheck["${row + 1}${col - 1}".toInt()] = "${row + 1}${col - 1}"
                        }
                    }
                    if (row + 1 in 0..8 && col + 1 in 0..8) {
                        val southEast = "${row + 1}${col + 1}"
                        if (markedPosition.contains(southEast)) {
                            cells[row + 1][col + 1] = mapPositionNearMines[southEast]!!
                        } else {
                            cells[row + 1][col + 1] = '/'
                            nextToCheck["${row + 1}${col + 1}".toInt()] = "${row + 1}${col + 1}"
                        }
                    }
                }
            }
        }
    }
}

fun removeHint() {
    markedPosition.forEach {
        val rowIndex = "${it.first()}".toInt()
        val colIndex = "${it.last()}".toInt()
        cells[rowIndex][colIndex] = '.'
    }
}


fun addMines(num: Int) {
    val dice = Random
    val l = mutableListOf<Int>()
    repeat(9) {
        l.add(it, dice.nextInt(9))
    }

    val row = mutableListOf(0, 1, 2, 3, 4, 5, 6, 7, 8)
    val source = MutableList(num) { 1 }.chunked(9)
    source.forEach { list ->
        row.shuffle()
        list.indices.forEach {
            if (cells[it][row[it]] == 'X') {
                val temp = cells[it].find { c -> c == '.' }
                val newPosition = cells[it].indexOf(temp)
                cells[it][newPosition] = 'X'
                // save mine location to refer later
                mineLocation.add("$it$newPosition")
            } else {
                cells[it][row[it]] = 'X'
                mineLocation.add("$it${row[it]}")
            }
        }
    }
}

fun addHint() {
    mineLocation.forEach {
        val rowIndex = "${it.first()}".toInt()
        val colIndex = "${it.last()}".toInt()
        when (rowIndex) {
            0 -> {
                when (colIndex) {
                    0 -> {
                        if (cells[rowIndex][colIndex + 1] != 'X') {
                            if (cells[rowIndex][colIndex + 1] != '.') {
                                var number = cells[rowIndex][colIndex + 1].toString().toInt()
                                ++number
                                cells[rowIndex][colIndex + 1] = number.toString().first()
                            } else cells[rowIndex][colIndex + 1] = '1'
                            markedPosition.add("$rowIndex${colIndex + 1}")
                        }
                        if (cells[rowIndex + 1][colIndex] != 'X') {
                            if (cells[rowIndex + 1][colIndex] != '.') {
                                var number = cells[rowIndex + 1][colIndex].toString().toInt()
                                ++number
                                cells[rowIndex + 1][colIndex] = number.toString().first()
                            } else cells[rowIndex + 1][colIndex] = '1'
                            markedPosition.add("${rowIndex + 1}$colIndex")
                        }
                        if (cells[rowIndex + 1][colIndex + 1] != 'X') {
                            if (cells[rowIndex + 1][colIndex + 1] != '.') {
                                var number = cells[rowIndex + 1][colIndex + 1].toString().toInt()
                                ++number
                                cells[rowIndex + 1][colIndex + 1] = number.toString().first()
                            } else cells[rowIndex + 1][colIndex + 1] = '1'
                            markedPosition.add("${rowIndex + 1}${colIndex + 1}")
                        }
                    }
                    8 -> {
                        if (cells[rowIndex][colIndex - 1] != 'X') {
                            if (cells[rowIndex][colIndex - 1] != '.') {
                                var number = cells[rowIndex][colIndex - 1].toString().toInt()
                                ++number
                                cells[rowIndex][colIndex - 1] = number.toString().first()
                            } else cells[rowIndex][colIndex - 1] = '1'
                            markedPosition.add("$rowIndex${colIndex - 1}")
                        }
                        if (cells[rowIndex + 1][colIndex - 1] != 'X') {
                            if (cells[rowIndex + 1][colIndex - 1] != '.') {
                                var number = cells[rowIndex + 1][colIndex - 1].toString().toInt()
                                ++number
                                cells[rowIndex + 1][colIndex - 1] = number.toString().first()
                            } else cells[rowIndex + 1][colIndex - 1] = '1'
                            markedPosition.add("${rowIndex + 1}${colIndex - 1}")
                        }
                        if (cells[rowIndex + 1][colIndex] != 'X') {
                            if (cells[rowIndex + 1][colIndex] != '.') {
                                var number = cells[rowIndex + 1][colIndex].toString().toInt()
                                ++number
                                cells[rowIndex + 1][colIndex] = number.toString().first()
                            } else cells[rowIndex + 1][colIndex] = '1'
                        }
                        markedPosition.add("${rowIndex + 1}$colIndex")
                    }
                    else -> {
                        if (cells[rowIndex][colIndex + 1] != 'X') {
                            if (cells[rowIndex][colIndex + 1] != '.') {
                                var number = cells[rowIndex][colIndex + 1].toString().toInt()
                                ++number
                                cells[rowIndex][colIndex + 1] = number.toString().first()
                            } else cells[rowIndex][colIndex + 1] = '1'
                            markedPosition.add("$rowIndex${colIndex + 1}")
                        }
                        if (cells[rowIndex + 1][colIndex] != 'X') {
                            if (cells[rowIndex + 1][colIndex] != '.') {
                                var number = cells[rowIndex + 1][colIndex].toString().toInt()
                                ++number
                                cells[rowIndex + 1][colIndex] = number.toString().first()
                            } else cells[rowIndex + 1][colIndex] = '1'
                            markedPosition.add("${rowIndex + 1}${colIndex}")
                        }
                        if (cells[rowIndex + 1][colIndex + 1] != 'X') {
                            if (cells[rowIndex + 1][colIndex + 1] != '.') {
                                var number = cells[rowIndex + 1][colIndex + 1].toString().toInt()
                                ++number
                                cells[rowIndex + 1][colIndex + 1] = number.toString().first()
                            } else cells[rowIndex + 1][colIndex + 1] = '1'
                            markedPosition.add("${rowIndex + 1}${colIndex + 1}")
                        }
                        if (cells[rowIndex][colIndex - 1] != 'X') {
                            if (cells[rowIndex][colIndex - 1] != '.') {
                                var number = cells[rowIndex][colIndex - 1].toString().toInt()
                                ++number
                                cells[rowIndex][colIndex - 1] = number.toString().first()
                            } else cells[rowIndex][colIndex - 1] = '1'
                            markedPosition.add("${rowIndex}${colIndex - 1}")
                        }
                        if (cells[rowIndex + 1][colIndex - 1] != 'X') {
                            if (cells[rowIndex + 1][colIndex - 1] != '.') {
                                var number = cells[rowIndex + 1][colIndex - 1].toString().toInt()
                                ++number
                                cells[rowIndex + 1][colIndex - 1] = number.toString().first()
                            } else cells[rowIndex + 1][colIndex - 1] = '1'
                            markedPosition.add("${rowIndex + 1}${colIndex - 1}")
                        }
                    }
                }
            }
            8 -> {
                when (colIndex) {
                    0 -> {
                        if (cells[rowIndex][colIndex + 1] != 'X') {
                            if (cells[rowIndex][colIndex + 1] != '.') {
                                var number = cells[rowIndex][colIndex + 1].toString().toInt()
                                ++number
                                cells[rowIndex][colIndex + 1] = number.toString().first()
                            } else cells[rowIndex][colIndex + 1] = '1'
                            markedPosition.add("${rowIndex}${colIndex + 1}")
                        }
                        if (cells[rowIndex - 1][colIndex] != 'X') {
                            if (cells[rowIndex - 1][colIndex] != '.') {
                                var number = cells[rowIndex - 1][colIndex].toString().toInt()
                                ++number
                                cells[rowIndex - 1][colIndex] = number.toString().first()
                            } else cells[rowIndex - 1][colIndex] = '1'
                            markedPosition.add("${rowIndex - 1}${colIndex}")
                        }
                        if (cells[rowIndex - 1][colIndex + 1] != 'X') {
                            if (cells[rowIndex - 1][colIndex + 1] != '.') {
                                var number = cells[rowIndex - 1][colIndex + 1].toString().toInt()
                                ++number
                                cells[rowIndex - 1][colIndex + 1] = number.toString().first()
                            } else cells[rowIndex - 1][colIndex + 1] = '1'
                            markedPosition.add("${rowIndex - 1}${colIndex + 1}")
                        }
                    }
                    8 -> {
                        if (cells[rowIndex][colIndex - 1] != 'X') {
                            if (cells[rowIndex][colIndex - 1] != '.') {
                                var number = cells[rowIndex][colIndex - 1].toString().toInt()
                                ++number
                                cells[rowIndex][colIndex - 1] = number.toString().first()
                            } else cells[rowIndex][colIndex - 1] = '1'
                            markedPosition.add("${rowIndex}${colIndex - 1}")
                        }
                        if (cells[rowIndex - 1][colIndex - 1] != 'X') {
                            if (cells[rowIndex - 1][colIndex - 1] != '.') {
                                var number = cells[rowIndex - 1][colIndex - 1].toString().toInt()
                                ++number
                                cells[rowIndex - 1][colIndex - 1] = number.toString().first()
                            } else cells[rowIndex - 1][colIndex - 1] = '1'
                            markedPosition.add("${rowIndex - 1}${colIndex - 1}")
                        }
                        if (cells[rowIndex - 1][colIndex] != 'X') {
                            if (cells[rowIndex - 1][colIndex] != '.') {
                                var number = cells[rowIndex - 1][colIndex].toString().toInt()
                                ++number
                                cells[rowIndex - 1][colIndex] = number.toString().first()
                            } else cells[rowIndex - 1][colIndex] = '1'
                            markedPosition.add("${rowIndex - 1}${colIndex - 1}")
                        }
                    }
                    else -> {
                        if (cells[rowIndex][colIndex + 1] != 'X') {
                            if (cells[rowIndex][colIndex + 1] != '.') {
                                var number = cells[rowIndex][colIndex + 1].toString().toInt()
                                ++number
                                cells[rowIndex][colIndex + 1] = number.toString().first()
                            } else cells[rowIndex][colIndex + 1] = '1'
                            markedPosition.add("${rowIndex}${colIndex + 1}")
                        }
                        if (cells[rowIndex - 1][colIndex] != 'X') {
                            if (cells[rowIndex - 1][colIndex] != '.') {
                                var number = cells[rowIndex - 1][colIndex].toString().toInt()
                                ++number
                                cells[rowIndex - 1][colIndex] = number.toString().first()
                            } else cells[rowIndex - 1][colIndex] = '1'
                            markedPosition.add("${rowIndex - 1}${colIndex}")
                        }
                        if (cells[rowIndex - 1][colIndex + 1] != 'X') {
                            if (cells[rowIndex - 1][colIndex + 1] != '.') {
                                var number = cells[rowIndex - 1][colIndex + 1].toString().toInt()
                                ++number
                                cells[rowIndex - 1][colIndex + 1] = number.toString().first()
                            } else cells[rowIndex - 1][colIndex + 1] = '1'
                            markedPosition.add("${rowIndex - 1}${colIndex + 1}")
                        }
                        if (cells[rowIndex][colIndex - 1] != 'X') {
                            if (cells[rowIndex][colIndex - 1] != '.') {
                                var number = cells[rowIndex][colIndex - 1].toString().toInt()
                                ++number
                                cells[rowIndex][colIndex - 1] = number.toString().first()
                            } else cells[rowIndex][colIndex - 1] = '1'
                            markedPosition.add("${rowIndex}${colIndex - 1}")
                        }
                        if (cells[rowIndex - 1][colIndex - 1] != 'X') {
                            if (cells[rowIndex - 1][colIndex - 1] != '.') {
                                var number = cells[rowIndex - 1][colIndex - 1].toString().toInt()
                                ++number
                                cells[rowIndex - 1][colIndex - 1] = number.toString().first()
                            } else cells[rowIndex - 1][colIndex - 1] = '1'
                            markedPosition.add("${rowIndex - 1}${colIndex - 1}")
                        }
                    }
                }
            }
            else -> {
                when (colIndex) {
                    0 -> {
                        if (cells[rowIndex][colIndex + 1] != 'X') {
                            if (cells[rowIndex][colIndex + 1] != '.') {
                                var number = cells[rowIndex][colIndex + 1].toString().toInt()
                                ++number
                                cells[rowIndex][colIndex + 1] = number.toString().first()
                            } else cells[rowIndex][colIndex + 1] = '1'
                            markedPosition.add("${rowIndex}${colIndex + 1}")
                        }
                        if (cells[rowIndex + 1][colIndex] != 'X') {
                            if (cells[rowIndex + 1][colIndex] != '.') {
                                var number = cells[rowIndex + 1][colIndex].toString().toInt()
                                ++number
                                cells[rowIndex + 1][colIndex] = number.toString().first()
                            } else cells[rowIndex + 1][colIndex] = '1'
                            markedPosition.add("${rowIndex + 1}${colIndex}")
                        }
                        if (cells[rowIndex + 1][colIndex + 1] != 'X') {
                            if (cells[rowIndex + 1][colIndex + 1] != '.') {
                                var number = cells[rowIndex + 1][colIndex + 1].toString().toInt()
                                ++number
                                cells[rowIndex + 1][colIndex + 1] = number.toString().first()
                            } else cells[rowIndex + 1][colIndex + 1] = '1'
                            markedPosition.add("${rowIndex + 1}${colIndex + 1}")
                        }
                        if (cells[rowIndex - 1][colIndex] != 'X') {
                            if (cells[rowIndex - 1][colIndex] != '.') {
                                var number = cells[rowIndex - 1][colIndex].toString().toInt()
                                ++number
                                cells[rowIndex - 1][colIndex] = number.toString().first()
                            } else cells[rowIndex - 1][colIndex] = '1'
                            markedPosition.add("${rowIndex - 1}${colIndex}")
                        }
                        if (cells[rowIndex - 1][colIndex + 1] != 'X') {
                            if (cells[rowIndex - 1][colIndex + 1] != '.') {
                                var number = cells[rowIndex - 1][colIndex + 1].toString().toInt()
                                ++number
                                cells[rowIndex - 1][colIndex + 1] = number.toString().first()
                            } else cells[rowIndex - 1][colIndex + 1] = '1'
                            markedPosition.add("${rowIndex - 1}${colIndex + 1}")
                        }
                    }
                    8 -> {
                        if (cells[rowIndex][colIndex - 1] != 'X') {
                            if (cells[rowIndex][colIndex - 1] != '.') {
                                var number = cells[rowIndex][colIndex - 1].toString().toInt()
                                ++number
                                cells[rowIndex][colIndex - 1] = number.toString().first()
                            } else cells[rowIndex][colIndex - 1] = '1'
                            markedPosition.add("${rowIndex}${colIndex - 1}")
                        }
                        if (cells[rowIndex - 1][colIndex] != 'X') {
                            if (cells[rowIndex - 1][colIndex] != '.') {
                                var number = cells[rowIndex - 1][colIndex].toString().toInt()
                                ++number
                                cells[rowIndex - 1][colIndex] = number.toString().first()
                            } else cells[rowIndex - 1][colIndex] = '1'
                            markedPosition.add("${rowIndex - 1}${colIndex}")
                        }
                        if (cells[rowIndex - 1][colIndex - 1] != 'X') {
                            if (cells[rowIndex - 1][colIndex - 1] != '.') {
                                var number = cells[rowIndex - 1][colIndex - 1].toString().toInt()
                                ++number
                                cells[rowIndex - 1][colIndex - 1] = number.toString().first()
                            } else cells[rowIndex - 1][colIndex - 1] = '1'
                            markedPosition.add("${rowIndex - 1}${colIndex - 1}")
                        }
                        if (cells[rowIndex + 1][colIndex - 1] != 'X') {
                            if (cells[rowIndex + 1][colIndex - 1] != '.') {
                                var number = cells[rowIndex + 1][colIndex - 1].toString().toInt()
                                ++number
                                cells[rowIndex + 1][colIndex - 1] = number.toString().first()
                            } else cells[rowIndex + 1][colIndex - 1] = '1'
                            markedPosition.add("${rowIndex + 1}${colIndex - 1}")
                        }
                        if (cells[rowIndex + 1][colIndex] != 'X') {
                            if (cells[rowIndex + 1][colIndex] != '.') {
                                var number = cells[rowIndex + 1][colIndex].toString().toInt()
                                ++number
                                cells[rowIndex + 1][colIndex] = number.toString().first()
                            } else cells[rowIndex + 1][colIndex] = '1'
                            markedPosition.add("${rowIndex + 1}${colIndex}")
                        }
                    }
                    else -> {
                        if (cells[rowIndex][colIndex - 1] != 'X') {
                            if (cells[rowIndex][colIndex - 1] != '.') {
                                var number = cells[rowIndex][colIndex - 1].toString().toInt()
                                ++number
                                cells[rowIndex][colIndex - 1] = number.toString().first()
                            } else cells[rowIndex][colIndex - 1] = '1'
                            markedPosition.add("${rowIndex}${colIndex - 1}")
                        }
                        if (cells[rowIndex - 1][colIndex] != 'X') {
                            if (cells[rowIndex - 1][colIndex] != '.') {
                                var number = cells[rowIndex - 1][colIndex].toString().toInt()
                                ++number
                                cells[rowIndex - 1][colIndex] = number.toString().first()
                            } else cells[rowIndex - 1][colIndex] = '1'
                            markedPosition.add("${rowIndex - 1}${colIndex}")
                        }
                        if (cells[rowIndex - 1][colIndex - 1] != 'X') {
                            if (cells[rowIndex - 1][colIndex - 1] != '.') {
                                var number = cells[rowIndex - 1][colIndex - 1].toString().toInt()
                                ++number
                                cells[rowIndex - 1][colIndex - 1] = number.toString().first()
                            } else cells[rowIndex - 1][colIndex - 1] = '1'
                            markedPosition.add("${rowIndex - 1}${colIndex - 1}")
                        }
                        if (cells[rowIndex + 1][colIndex - 1] != 'X') {
                            if (cells[rowIndex + 1][colIndex - 1] != '.') {
                                var number = cells[rowIndex + 1][colIndex - 1].toString().toInt()
                                ++number
                                cells[rowIndex + 1][colIndex - 1] = number.toString().first()
                            } else cells[rowIndex + 1][colIndex - 1] = '1'
                            markedPosition.add("${rowIndex + 1}${colIndex - 1}")
                        }
                        if (cells[rowIndex + 1][colIndex] != 'X') {
                            if (cells[rowIndex + 1][colIndex] != '.') {
                                var number = cells[rowIndex + 1][colIndex].toString().toInt()
                                ++number
                                cells[rowIndex + 1][colIndex] = number.toString().first()
                            } else cells[rowIndex + 1][colIndex] = '1'
                            markedPosition.add("${rowIndex + 1}${colIndex}")
                        }
                        if (cells[rowIndex - 1][colIndex + 1] != 'X') {
                            if (cells[rowIndex - 1][colIndex + 1] != '.') {
                                var number = cells[rowIndex - 1][colIndex + 1].toString().toInt()
                                ++number
                                cells[rowIndex - 1][colIndex + 1] = number.toString().first()
                            } else cells[rowIndex - 1][colIndex + 1] = '1'
                            markedPosition.add("${rowIndex - 1}${colIndex + 1}")
                        }
                        if (cells[rowIndex][colIndex + 1] != 'X') {
                            if (cells[rowIndex][colIndex + 1] != '.') {
                                var number = cells[rowIndex][colIndex + 1].toString().toInt()
                                ++number
                                cells[rowIndex][colIndex + 1] = number.toString().first()
                            } else cells[rowIndex][colIndex + 1] = '1'
                            markedPosition.add("${rowIndex}${colIndex + 1}")
                        }
                        if (cells[rowIndex + 1][colIndex + 1] != 'X') {
                            if (cells[rowIndex + 1][colIndex + 1] != '.') {
                                var number = cells[rowIndex + 1][colIndex + 1].toString().toInt()
                                ++number
                                cells[rowIndex + 1][colIndex + 1] = number.toString().first()
                            } else cells[rowIndex + 1][colIndex + 1] = '1'
                            markedPosition.add("${rowIndex + 1}${colIndex + 1}")
                        }
                    }
                }
            }
        }
    }
}

fun saveMarkedPosition() {
    markedPosition.forEach {
        val row = it.first().toString().toInt()
        val col = it.last().toString().toInt()
        mapPositionNearMines[it] = cells[row][col]
    }
}

fun removeMineMark() {
    mineLocation.forEach {
        val row = it.first().toString().toInt()
        val col = it.last().toString().toInt()
        cells[row][col] = '.'
    }
}

fun display() {
    println(
        "  │123456789│\n" +
                " —│—————————│"
    )
    var row = 0
    cells.forEach {
        row += 1
        print("$row |")
        it.forEach { item ->
            print(item)
        }
        print("|")
        println()
    }
    println(" —│—————————│")
}

fun markFree(x: Int, y: Int) {
    val xInitial = x - 1
    val yInitial = y - 1
    val location = "$yInitial$xInitial"
    if (markedPosition.contains(location)) {
        cells[yInitial][xInitial] = mapPositionNearMines[location]!!
    } else {
        if (xInitial in 0..8 && yInitial in 0..8) {
            cells[yInitial][xInitial] = '/'
            if (xInitial + 1 in 0..8) {
                val east = "$yInitial${xInitial + 1}"
                if (markedPosition.contains(east)) {
                    cells[yInitial][xInitial + 1] = mapPositionNearMines[east]!!
                } else {
                    cells[yInitial][xInitial + 1] = '/'
                    nextToCheck["$yInitial${xInitial + 1}".toInt()] = "$yInitial${xInitial + 1}"
                }
            }
            if (xInitial - 1 in 0..8) {
                val west = "$yInitial${xInitial - 1}"
                if (markedPosition.contains(west)) {
                    cells[yInitial][xInitial - 1] = mapPositionNearMines[west]!!
                } else {
                    cells[yInitial][xInitial - 1] = '/'
                    nextToCheck["$yInitial${xInitial - 1}".toInt()] = "$yInitial${xInitial - 1}"
                }
            }
            if (yInitial + 1 in 0..8) {
                val south = "${yInitial + 1}${xInitial}"
                if (markedPosition.contains(south)) {
                    cells[yInitial + 1][xInitial] = mapPositionNearMines[south]!!
                } else {
                    cells[yInitial + 1][xInitial] = '/'
                    nextToCheck["${yInitial + 1}${xInitial}".toInt()] = "${yInitial + 1}${xInitial}"
                }
            }
            if (yInitial - 1 in 0..8) {
                val north = "${yInitial - 1}${xInitial}"
                if (markedPosition.contains(north)) {
                    cells[yInitial - 1][xInitial] = mapPositionNearMines[north]!!
                } else {
                    cells[yInitial - 1][xInitial] = '/'
                    nextToCheck["${yInitial - 1}$xInitial".toInt()] = "${yInitial - 1}$xInitial"
                }
            }
            if (yInitial - 1 in 0..8 && xInitial + 1 in 0..8) {
                val northEast = "${yInitial - 1}${xInitial + 1}"
                if (markedPosition.contains(northEast)) {
                    cells[yInitial - 1][xInitial + 1] = mapPositionNearMines[northEast]!!
                } else {
                    cells[yInitial - 1][xInitial + 1] = '/'
                    nextToCheck["${yInitial - 1}${xInitial + 1}".toInt()] =
                        "${yInitial - 1}${xInitial + 1}"
                }
            }
            if (yInitial - 1 in 0..8 && xInitial - 1 in 0..8) {
                val northWest = "${yInitial - 1}${xInitial - 1}"
                if (markedPosition.contains(northWest)) {
                    cells[yInitial - 1][xInitial - 1] = mapPositionNearMines[northWest]!!
                } else {
                    cells[yInitial - 1][xInitial - 1] = '/'
                    nextToCheck["${yInitial - 1}${xInitial - 1}".toInt()] =
                        "${yInitial - 1}${xInitial - 1}"
                }
            }
            if (yInitial + 1 in 0..8 && xInitial - 1 in 0..8) {
                val southWest = "${yInitial + 1}${xInitial - 1}"
                if (markedPosition.contains(southWest)) {
                    cells[yInitial + 1][xInitial - 1] = mapPositionNearMines[southWest]!!
                } else {
                    cells[yInitial + 1][xInitial - 1] = '/'
                    nextToCheck["${yInitial + 1}${xInitial - 1}".toInt()] =
                        "${yInitial + 1}${xInitial - 1}"
                }
            }
            if (yInitial + 1 in 0..8 && xInitial + 1 in 0..8) {
                val southEast = "${yInitial + 1}${xInitial + 1}"
                if (markedPosition.contains(southEast)) {
                    cells[yInitial + 1][xInitial + 1] = mapPositionNearMines[southEast]!!
                } else {
                    cells[yInitial + 1][xInitial + 1] = '/'
                    nextToCheck["${yInitial + 1}${xInitial + 1}".toInt()] =
                        "${yInitial + 1}${xInitial + 1}"
                }
            }
        }
    }
}

fun freeCell(x: Int, y: Int) {
    val location = "${y - 1}${x - 1}"
    when {
        mineLocation.contains(location) -> {
            failed = true
            leftToCheck = 0
        }
        markedPosition.contains(location) -> {
            markNearMine(x - 1, y - 1)
        }
        else -> {
            markFree(x, y)
        }
    }
}

fun markMine() {
    mineLocation.forEach {
        val row = it.first().toString().toInt()
        val col = it.first().toString().toInt()
        cells[row][col] = 'X'
    }
}

fun mine(x: Int, y: Int) {
    val l = "${y - 1}${x - 1}"
    if (mineLocation.contains(l)) {
        if (cells[y - 1][x - 1] == '*')
            cells[y - 1][x - 1] = '.'
        else
            cells[y - 1][x - 1] = '*'
    } else if (x in 1..9 && y in 1..9) {
        if (cells[y - 1][x - 1] == '*')
            cells[y - 1][x - 1] = '.'
        else
            cells[y - 1][x - 1] = '*'
    }
}

fun makeDecision(x: Int, y: Int, act: String) {
    when (act) {
        "mine" -> {
            mine(x, y)
        }
        "free" -> {
            freeCell(x, y)
        }
        else -> println("Wrong input")
    }
}

fun markNearMine(x: Int, y: Int) {
    val location = "${y}${x}"
    cells[y][x] = mapPositionNearMines[location]!!
}

