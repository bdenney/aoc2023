import kotlin.math.max
import kotlin.math.min

fun main() {

    val input = readInput("Day10Input")
    
    val map = PipeMap(input)
//    map.grid.print()
//    println("")

    // collect all boundaries
    val boundaries = mutableListOf<Pair<IntRange, IntRange>>()
    var ptr = 1
    var prevBend = map.verticies[0]
    while (ptr < map.verticies.size ) {
        var nextBend = map.verticies[ptr]

        boundaries.add(Pair(IntRange(min(prevBend.x, nextBend.x), max(nextBend.x, prevBend.x)),
                            IntRange(min(prevBend.y, nextBend.y), max(prevBend.y, nextBend.y))))
        prevBend = nextBend
        ptr++
    }

//    println("boundaries: ${boundaries}")

    // collect all empty spaces
    var empties = mutableListOf<Coordinate>()
    map.grid.rows.forEachIndexed { colIndex, col ->
        col.forEachIndexed { rowIndex, value ->
            val coordinate = Coordinate(rowIndex, colIndex)
            if (value != '*') {
                empties.add(coordinate)
                map.grid.set('.', coordinate)
            }
        }
    }

    // test each empty to see how many of our boundaries it crosses
    val internal = mutableListOf<Coordinate>()
    empties.forEach { emptyTile ->
        var intersections = mutableListOf<Coordinate>()
//        println("testing $emptyTile")
        boundaries.forEach { boundary ->
//            println("\tboundary: $boundary")
            intersections.addAll(intersectsBoundary(emptyTile, boundary))
        }
        var boundariesCrossed = intersections.size
        if (((boundariesCrossed) % 2 != 0)) {
//            println("$emptyTile: $intersections")
            internal.add(emptyTile)
        }
    }
//    println("$internal")
//    internal.forEach {
//        map.grid.set('I', it)
//    }
//    map.grid.print()
    println("Count: ${internal.size}")
}

fun intersectsBoundary(coordinate: Coordinate, boundary: Pair<IntRange,IntRange>): List<Coordinate> {
    var intersections = mutableListOf<Coordinate>()
    for (y in coordinate.y downTo 0) {

        if ((coordinate.x > boundary.first.first && coordinate.x <= boundary.first.last)
            && boundary.second.contains(y)) {
//            print("\t${coordinate.x}, $y: ")
//            println("true")
            intersections.add(Coordinate(coordinate.x, y))
        }
    }
    return intersections
}

class PipeMap(data: List<String>) {
    val grid = Grid(data)
    val verticies: MutableList<Coordinate>
    val start = findStart()
    private val pathHeads = getPathHeads()
    private val bendChars = setOf('L', 'J', '7', 'F')

    init {
        val firstSet = getPossibleTileFromCoords(start, pathHeads.first)
        val secondSet = getPossibleTileFromCoords(start, pathHeads.second)
        grid.set((firstSet intersect secondSet).first(), start)

        verticies = traversePath()
    }

    private fun findStart(): Coordinate {
        var startX = -1
        var startY = -1
        var foundStart = false
        var ptr = 0
        while (!foundStart && ptr < grid.height()) {
            val row = grid.row(ptr)
            val startIndex = row.indexOf('S')
            if (startIndex > -1) {
                startX = startIndex
                startY = ptr
                foundStart = true
            }
            ptr++
        }
        return Coordinate(startX, startY)
    }

    fun findFurthestPoint(): Int {
        val points= traversePath()
        return points.size / 2
    }

    private fun traversePath(): MutableList<Coordinate> {
        val verticies = mutableListOf<Coordinate>()

        // get possible direction from start point
        var current = pathHeads.first
        var previous = start
//        println("\tstarting at: $start")

        if (bendChars.contains(grid.get(start))) {
//            println("\tadded vertex $start")
            verticies.add(start)
        }

        if (bendChars.contains(grid.get(pathHeads.first))) {
//            println("\tadded vertex ${pathHeads.first}")
            verticies.add(pathHeads.first)
        }

        // follow the path until we end back at the start
        while (current != start) {

//            println("")
            // travel one tile in each direction
            val next = findNextTile(current, previous)
            if (bendChars.contains(grid.get(next))) {
//                println("\tadded vertex $next")
                verticies.add(next)
            }
//            println("\t marking $previous")
            grid.set('*', previous)
            previous = current
            current = next
//            grid.print()
        }
        grid.set('*', previous)
//        grid.print()
//        println("")

        // the answer is the count for when the paths meet
//        println(verticies)
        return verticies
    }
    
    private fun findNextTile(start: Coordinate, previous: Coordinate): Coordinate {
        val candidates = grid.getAdjacentPoints(start)
        var next: Coordinate? = null
        
        candidates.forEach {
            if (canTravel(start, it) && it != previous) {
                next = it
            }
        }
        
        if (next == null) {
            println("ERROR - Start: $start, Previous: $previous, Candidates: $candidates")
        }
        
        return next!!
    }
    
    private fun getPathHeads(): Pair<Coordinate, Coordinate> {
        val candidates = grid.getAdjacentPoints(start)
        val pathHeads = mutableListOf<Coordinate>()
        candidates.forEach {
            if (canTravel(it, start)) {
                pathHeads.add(it)
            }
        }

        return Pair(pathHeads[0], pathHeads[1])
    }
    
    private fun getPossibleTileFromCoords(start: Coordinate, to: Coordinate): Set<Char> {
        // replace our start with the correct path symbol
        val pathSymbols = mutableSetOf('|', '-', 'L', 'J', '7', 'F')

        if (to.isAbove(start)) {
            pathSymbols.removeAll(setOf('-', '7', 'F'))
        } else if (to.isBelow(start)) {
            pathSymbols.removeAll(setOf('-', 'J', 'L'))
        } else if (to.isLeftOf(start)) {
            pathSymbols.removeAll(setOf('|', 'L', 'F'))
        } else if (to.isRightOf(start)) {
            pathSymbols.removeAll(setOf('|', 'J', '7'))
        }
        
        return pathSymbols
    }
    
    private fun canTravel(from: Coordinate, to: Coordinate): Boolean {
        when(grid.get(from)) {
            '|' -> return to.isAbove(from) || to.isBelow(from)
            '-' -> return to.isLeftOf(from) || to.isRightOf(from)
            'L' -> return to.isAbove(from) || to.isRightOf(from)
            'J' -> return to.isAbove(from) || to.isLeftOf(from)
            '7' -> return to.isBelow(from) || to.isLeftOf(from)
            'F' -> return to.isBelow(from) || to.isRightOf(from)
            '.' -> return false
            'S' -> return false
            else -> {
                println("Unknown tile!")
                return false
            }
        }
    }
}
