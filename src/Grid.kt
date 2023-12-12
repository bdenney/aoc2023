import java.lang.StringBuilder

class Grid(val rows: MutableList<String>, val cols: MutableList<String>) {
    
    val width = cols.size
    val height = rows.size
    
    constructor(data: List<String>) : this(data.toMutableList(),
                                           mutableListOf<String>()) {
        recalculateCols()
    }
    
    fun row(index: Int): String {
        return rows[index]
    }
    
    fun column(index: Int): String {
        return cols[index]
    }
    
    fun insertRow(row: String, afterIndex: Int) {
        rows.add(afterIndex+1, row)
        recalculateCols()
    }
    
    fun insertColumn(col: String, afterIndex: Int) {
        cols.add(afterIndex+1, col)
        recalculateRows()
    }
    
    fun copyOf(): Grid {
        return Grid(rows, cols)
    }

    fun coordinateOf(value: Char): Coordinate {
        var x = -1
        var y = -1
        rows.forEachIndexed { idx, row ->
            val charIndex = row.indexOf(value)
            if (charIndex > 0) {
                y = idx
                x = charIndex
                return Coordinate(charIndex, y)
            }
        }
        return Coordinate(x,y)
    }

    fun set(value: Char, at: Coordinate) {
        val rowCopy = rows[at.y].toCharArray().toMutableList()
        rowCopy[at.x] = value
        rows[at.y] = rowCopy.joinToString("")
        recalculateCols()
    }

    fun get(at: Coordinate): Char {
        return rows[at.y][at.x]
    }

    fun traverse(onVisit: (Coordinate, Char) -> Unit) {
        rows.forEachIndexed { x, row ->
            cols.forEachIndexed{ y, col ->
                onVisit(Coordinate(x,y), row[y])
            }
        }
    }

    private fun recalculateCols() {
        cols.clear()
        val sb = StringBuilder()
        rows[0].indices.forEach { col ->
            for (index in 0..<rows.size) {
                sb.append(rows[index][col])
            }
            cols.add(sb.toString())
            sb.clear()
        }
    }
    
    private fun recalculateRows() {
        rows.clear()
        val sb = StringBuilder()
        cols[0].indices.forEach { row ->
            for (index in 0..<cols.size) {
                sb.append(cols[index][row])
            }
            rows.add(sb.toString())
            sb.clear()
        }
    }
    
    fun print() {
        var count = 0
        this.rows.forEach {
            println("$count\t$it")
            count++
        }
    }
}