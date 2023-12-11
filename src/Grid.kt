import java.lang.StringBuilder

class Grid(val rows: MutableList<String>, val cols: MutableList<String>) {
    
    val width = rows.size
    val height = cols.size
    
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