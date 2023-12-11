import kotlin.math.max
import kotlin.math.min

fun main() {
    fun part01(input: List<String>) {
        val universe = Universe(input, 1000000)
        
        val queue = ArrayDeque<Coordinate>()
        queue.addAll(universe.galaxies)
        
        var total = 0L
        while (queue.isNotEmpty()) {
            val galaxy = queue.removeFirst()
            queue.forEach { there ->
                total += universe.shortestPath(galaxy, there)
            }
        }
        
        println(total)
    }
    
    val input = readInput("Day11Input")
    part01(input)
}

class Universe(var input: List<String>, val factor: Int = 1) {
    private val grid: Grid = Grid(input)
    val emptyRows = mutableListOf<Int>()
    val emptyCols = mutableListOf<Int>()
    val galaxies = mutableListOf<Coordinate>()
    
    init {
        expand()
        discoverGalaxies()
//        println("x dead space: $emptyRows")
//        println("y dead space: $emptyCols")
//        grid.print()
    }
    
    fun shortestPath(here: Coordinate, there: Coordinate): Int {
//        println("shortest path between $here and $there")
        val xRange = IntRange(min(here.x, there.x), max(here.x, there.x))
//        println("\tx: $xRange")
        val yRange = IntRange(min(here.y, there.y), max(here.y, there.y))
//        println("\ty: $yRange")
        
        var xMultiple = 0
        emptyRows.forEach {
            if (xRange.contains(it) && xRange.endInclusive != it) {
//                println("\tcrosses X dead space ${it}")
                xMultiple++
            }
        }
        
        var yMultiple = 0
        emptyCols.forEach {
            if (yRange.contains(it) && yRange.endInclusive != it) {
//                println("\tcrosses Y dead space ${it}")
                yMultiple++
            }
        }
        
//        println("\tbase distance: ${here.shortestPath(there)}")
//        println("\tadjusted distance: ${here.shortestPath(there) + (xMultiple * factor) + (yMultiple * factor)}")
        
        return here.shortestPath(there) + ((xMultiple * factor) - xMultiple) + ((yMultiple * factor) - yMultiple)
    }
    
    private fun discoverGalaxies() {
        grid.rows.forEachIndexed { x, row ->
            row.forEachIndexed { y, char ->
                if (char == '#') {
                    galaxies.add(Coordinate(x,y))
                }
            }
        }
    }
    
    private fun expand() {
        grid.rows.forEachIndexed { idx, row ->
            if (row.none { it != '.' }) {
                emptyRows.add(idx)
            }
        }

        grid.cols.forEachIndexed { idx, col ->
            if (col.none { it != '.' }) {
                emptyCols.add(idx)
            }
        }
    }
}