import java.lang.StringBuilder

class Grid(originalData: List<String>) {
    
    fun width(): Int = cols.size
    fun height(): Int = rows.size

    val rows = Array(originalData.size) { CharArray(originalData[0].length) }
    
    val cols  = Array(originalData[0].length) { CharArray(originalData.size) }

    init {
        originalData.forEachIndexed { row, input ->
            // each line in the input is a row
            val rowArray = input.toCharArray()
            rowArray.copyInto(rows[row])

            // each entry in the row array is a column value at index $col
            rowArray.forEachIndexed { col, char -> cols[col][row] = char }
        }
    }

    fun row(index: Int): CharArray {
        return rows[index].copyOf()
    }

    fun column(index: Int): CharArray {
        return cols[index].copyOf()
    }
    
    fun insertRow(row: String, afterIndex: Int) {
        throw NotImplementedError("Insert row not yet implemented!")
    }

    fun replaceRow(row: CharArray, index: Int) {
        row.copyInto(rows[index])
        row.forEachIndexed { col, char -> cols[col][index] = char }
    }

    fun insertColumn(col: String, afterIndex: Int) {
        throw NotImplementedError("Insert column not yet implemented!")
    }

    fun replaceColumn(col: CharArray, index: Int) {
        col.copyInto(cols[index])
        col.forEachIndexed { row, char -> rows[row][index] = char }
    }

    fun copyOf(): Grid {
        throw NotImplementedError("copyOf not yet implemented.")
    }

    fun set(value: Char, at: Coordinate) {
        rows[at.y][at.x] = value
        cols[at.x][at.y] = value
    }

    /**
     * Returns the list of points to the top, left, right, and bottom to the the given
     * coordinate if it exists within the grid.
     *
     * @param coordinate the coordinate a get the adjacent points for
     * @return list of coordinates that are adjacent to the given point
     */
    fun getAdjacentPoints(coordinate: Coordinate): List<Coordinate> {
        val adjacentPoints = mutableListOf<Coordinate>()
        val x = coordinate.x
        val y = coordinate.y

        // left
        if (x != 0) {
            adjacentPoints.add(Coordinate(x-1, y))
        }

        // right
        if (x != width()-1) {
            adjacentPoints.add(Coordinate(x+1, y))
        }

        // above
        if (y != 0) {
            adjacentPoints.add(Coordinate(x, y-1))
        }

        // below
        if (y != height()-1) {
            adjacentPoints.add(Coordinate(x, y+1))
        }
        return adjacentPoints
    }

    fun get(at: Coordinate): Char {
        return rows[at.y][at.x]
    }

    fun isInBounds(coordinate: Coordinate): Boolean {
        return isInBounds(coordinate.x, coordinate.y)
    }

    fun isInBounds(x: Int, y: Int): Boolean {
        /*     left      right               above       below          */
        return x >= 0 && (x <= width()-1) && (y >= 0) && (y <= height()-1)
    }

    fun traverse(onVisit: (Coordinate, Char) -> Unit) {
        throw NotImplementedError("traverse not yet implemented")
    }

    private fun recalculateCols(forRowIndex: Int) {
        rows[forRowIndex].forEachIndexed { col, char -> cols[forRowIndex][col] = char }
    }
    
    private fun recalculateRows(forColIndex: Int) {
        cols[forColIndex].forEachIndexed { row, char -> rows[row][forColIndex] = char }
    }
    
    fun print() {
        var count = 0
        rows.forEach {
            println("$count\t${it.joinToString("")}")
            count++
        }
    }

    override fun hashCode(): Int {
        var sb = StringBuilder()
        cols.forEach {
            sb.append(it.joinToString(""))
        }
        return sb.hashCode()
    }
}