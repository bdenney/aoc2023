
import kotlin.math.abs
import kotlin.math.min

fun main() {
    
    fun part01(input: List<String>) {
        val map = PipeMap(input)
        println(map.findFurthestPoint())
    }
    
    fun part02(input: List<String>) {
        val map = PipeMap(input)
        val copy = map.original.copyOf()
        val bends = mutableListOf<Coordinate>()
        
        val firstBend = map.findNextBend(map.start)
        var nextBend = firstBend 
        do {
            bends.add(nextBend)
            nextBend = map.findNextBend(nextBend)
        } while (nextBend != firstBend)
        
        while (bends.isNotEmpty()) {
            val bend = bends.removeFirst()
            val nextBend = bends.first()
            println("from bend: $bend to $nextBend")
            
            
            // get points between bends
            val startPath = map.path.indexOf(bend)
            var endPath = map.path.indexOf(nextBend) 
            if (endPath == 0) {
                endPath = map.path.size-1
                bends.removeFirst()
            }
            val points = map.path.subList(startPath, endPath)
            points.forEach {
                val index = map.path.indexOf(it)
                
                map.path[index] = DirectionalCoordinate(map.path[index], DirectionalCoordinate.determineCardinal(copy.get(bend), copy.get(nextBend)))
            }
            
            println("path points: $points")
            
            // set directions
            map.path.forEach {
                if (it is DirectionalCoordinate) {
                    copy.set(it.inside.char, it)
                }
            }
            
            copy.print()
        }
    }
    
    val input = readInput("Day10Input")
    
//    print("Part 1: ")
//    part01(input)
    
    println("Part 2: ")
    part02(input)
}

fun Array<CharArray>.print() {
    var count = 0
    this.forEach {
        println("$count  ${it.joinToString("")}")
        count++
    }
}

private fun Array<CharArray>.deepCopyOf(): Array<CharArray> {
    val copy = this.copyOf()
    this.indices.forEach {
        copy[it] = this[it].copyOf()
    }
    return copy
}

class PipeMap(data: List<String>) {
    val original = Grid(data)
    val path : MutableList<Coordinate>
    val start: Coordinate = original.coordinateOf('S')
    private val pathHeads: List<Coordinate>

    init {
        pathHeads = getPathHeads(start)
        path = traverse()
        
        val firstSet = getPossibleTileFromCoords(start, pathHeads[0])
        val secondSet = getPossibleTileFromCoords(start, pathHeads[1])
        original.set((firstSet intersect secondSet).first(), start)
    }
    
    fun findFurthestPoint(): Int {
        val points= traverse()
        return points.size / 2
    }
    
    private fun traverse(): MutableList<Coordinate> {
        val visited = mutableListOf<Coordinate>()
        
        // get possible direction from start point
        var step = pathHeads[0]
        var previous = start
        visited.add(start)

        // follow the path until we end back at the start
        while (step != start) {
            visited.add(step)
            
            // travel one tile in each direction
            val next = findNextTile(step, previous)
            
            previous = step
            step = next
        }
        
        // the answer is the count for when the paths meet
        return visited
    }
    
    fun findNextBend(start: Coordinate): Coordinate {
        var index = path.indexOf(start) + 1
        if (index > path.size-1) {
            index = 0
        }
        
        val bends = setOf('L', 'J', '7', 'F')
        var nextCoord = path[index]
        while (!bends.contains(original.get(nextCoord))) {
            index++
            if (index > path.size-1) {
                index = 0
            }
            nextCoord = path[index]
        }
        return nextCoord
    }
    
    private fun findNextTile(start: Coordinate, previous: Coordinate): Coordinate {
        val candidates = generateAdjacentPoints(start)
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
    
    private fun getPathHeads(start: Coordinate): List<Coordinate> {
        val candidates = generateAdjacentPoints(start)

        // should be two possible coordinates from the start
        val pathHeads = mutableListOf<Coordinate>()
        candidates.forEach {
            if (canTravel(it, start)) {
                pathHeads.add(it)
            }
        }
        
        return pathHeads
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
    
    private fun generateAdjacentPoints(coord: Coordinate): List<Coordinate> {
        val adjacentPoints = mutableListOf<Coordinate>()
        val x = coord.x
        val y = coord.y
        
        // left
        if (x != 0) {
            adjacentPoints.add(Coordinate(x-1, y))
        }
        
        // right
        if (x != original.width-1) {
            adjacentPoints.add(Coordinate(x+1, y))
        }
        
        // above
        if (y != 0) {
            adjacentPoints.add(Coordinate(x, y-1))
        }
        
        // below
        if (y != original.height-1) {
            adjacentPoints.add(Coordinate(x, y+1))
        }
        return adjacentPoints
    }
    
    private fun canTravel(there: Coordinate, here: Coordinate): Boolean {
        when(original.get(there)) {
            '|' -> return here.isAbove(there) || here.isBelow(there)
            '-' -> return here.isLeftOf(there) || here.isRightOf(there)
            'L' -> return here.isAbove(there) || here.isRightOf(there)
            'J' -> return here.isAbove(there) || here.isLeftOf(there)
            '7' -> return here.isBelow(there) || here.isLeftOf(there)
            'F' -> return here.isBelow(there) || here.isRightOf(there)
            '.' -> return false
            'S' -> return false
            else -> {
                println("Unknown tile!")
                return false
            }
        }
    }
}

class DirectionalCoordinate(coordinate: Coordinate, var inside: Cardinal = Cardinal.UNKNOWN) : Coordinate(coordinate.x, coordinate.y) {
    companion object {
        fun determineCardinal(b1: Char, b2: Char) : Cardinal {
            if (b1 == 'L' && b2 == 'J') {
                return Cardinal.UP
            } else if (b1 == '7' && b2 == 'J') {
                return Cardinal.LEFT
            } else if (b1 == 'J' && b2 == 'L') {
                return Cardinal.UP
            } else if (b1 == 'L' && b2 == 'F') {
                return Cardinal.RIGHT
            } else if (b1 == 'F' && b2 == 'J') {
                return Cardinal.DOWN
            } else if (b1 == 'J' && b2 == '7') {
                return Cardinal.
            }
            
            return Cardinal.UNKNOWN
        }
    }
    enum class Cardinal(val char: Char) {
        UP('u'),
        RIGHT('r'),
        DOWN('d'),
        LEFT('l'),
        UNKNOWN('?')
    }
}
