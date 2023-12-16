import javax.print.attribute.IntegerSyntax

fun main() {
    val input = readInput("Day16Input")
    val map = MirrorMap(input)

    // get all possible edges and direction combinations
    val edges = mutableSetOf<DirectionalCoordinate>()
    for (x in 0..<map.grid.width()) {
        edges.add(DirectionalCoordinate(x, 0, Direction.DOWN))
        edges.add(DirectionalCoordinate(x, 0, Direction.LEFT))
        edges.add(DirectionalCoordinate(x, 0, Direction.RIGHT))
        edges.add(DirectionalCoordinate(x, 0, Direction.UP))

        edges.add(DirectionalCoordinate(x, map.grid.height() - 1, Direction.DOWN))
        edges.add(DirectionalCoordinate(x, map.grid.height() - 1, Direction.LEFT))
        edges.add(DirectionalCoordinate(x, map.grid.height() - 1, Direction.RIGHT))
        edges.add(DirectionalCoordinate(x, map.grid.height() - 1, Direction.UP))
    }

    for (y in 0..<map.grid.height()) {
        edges.add(DirectionalCoordinate(0, y, Direction.DOWN))
        edges.add(DirectionalCoordinate(0, y, Direction.LEFT))
        edges.add(DirectionalCoordinate(0, y, Direction.RIGHT))
        edges.add(DirectionalCoordinate(0, y, Direction.UP))

        edges.add(DirectionalCoordinate(0, map.grid.width() - 1, Direction.DOWN))
        edges.add(DirectionalCoordinate(0, map.grid.width() - 1, Direction.LEFT))
        edges.add(DirectionalCoordinate(0, map.grid.width() - 1, Direction.RIGHT))
        edges.add(DirectionalCoordinate(0, map.grid.width() - 1, Direction.UP))
    }
    
    var maxCount = Int.MIN_VALUE
    var maxStart = DirectionalCoordinate(-1, -1, Direction.DOWN)
    
    edges.forEach { edge ->
        val visited = map.traverse(edge)
        var uniqueCoords = mutableSetOf<Coordinate>()
        visited.forEach {
            uniqueCoords.add(Coordinate(it.x, it.y))
        }
        
        if (uniqueCoords.count() > maxCount) {
            maxCount = uniqueCoords.count()
            maxStart = edge
        }
        
        println("Start: $edge")
        println("\tVisited: ${visited.count()}")
        println("\tUnique: ${uniqueCoords.count()}")
    }
    
    println("")
    println("Best starting position: $maxStart with $maxCount")
}

class MirrorMap(input: List<String>) {
    val grid = Grid(input)

    fun traverse(start: DirectionalCoordinate): Set<Coordinate> {
        val seen = mutableSetOf<Coordinate>()
        val nextCoordinates = mutableListOf<DirectionalCoordinate>()
        nextCoordinates.add(start)

        while (nextCoordinates.isNotEmpty()) {
            val coordinate = nextCoordinates.removeFirst()
            
            // mark this one as seen
            seen.add(coordinate)
            
            // find next coordinates
            val candidates = getNextCoordinates(coordinate)
            
            candidates.forEach { candidate ->
                if (grid.isInBounds(candidate) && !seen.contains(candidate) ) {
                    nextCoordinates.add(candidate)
                }
            }
        }
        return seen
    }
    
    private fun getNextCoordinates(at: DirectionalCoordinate): List<DirectionalCoordinate> {
        val directions = mutableListOf<DirectionalCoordinate>()
        var x = at.x
        var y = at.y
        var direction = at.direction
        when (grid.get(at)) {
            '.'  -> {
                // empty tile - get next tile in current direction
                when (at.direction) {
                    Direction.UP -> { y -= 1 }
                    Direction.DOWN -> { y += 1 }
                    Direction.LEFT -> { x -= 1 }
                    Direction.RIGHT -> { x += 1 }
                }
            }
            '/'  -> {
                when (at.direction) {
                    // if direction is left, go down
                    Direction.LEFT -> {
                        y += 1
                        direction = Direction.DOWN
                    }
                    // if direction is right, go up
                    Direction.RIGHT -> {
                        y -= 1
                        direction = Direction.UP
                    }
                    // if direction is up, go right
                    Direction.UP -> {
                        x += 1
                        direction = Direction.RIGHT
                    }
                    Direction.DOWN -> {
                        // if direction is down, go left
                        x -= 1
                        direction = Direction.LEFT
                    }
                }
            }
            '\\' -> {
                when (at.direction) {
                    // if direction is up, go left
                    Direction.UP -> {
                        x -= 1
                        direction = Direction.LEFT
                    }
                    // if direction is down, go right
                    Direction.DOWN -> {
                        x += 1
                        direction = Direction.RIGHT
                    }
                    // if direction is left, go up
                    Direction.LEFT -> {
                        y -= 1
                        direction = Direction.UP
                    }
                    // if direction is right, go down
                    Direction.RIGHT -> {
                        y += 1
                        direction = Direction.DOWN
                    }
                }
            }
            '|'  -> {
                when (at.direction) {
                    // if direction is up or down, treat as empty
                    Direction.UP -> { y -= 1 }
                    Direction.DOWN -> { y += 1 }
                    // if direction is left or right, add a coordinate above and below
                    Direction.LEFT -> {
                        directions.add(DirectionalCoordinate(x, y+1, Direction.DOWN))

                        y -= 1
                        direction = Direction.UP
                    }
                    Direction.RIGHT -> {
                        directions.add(DirectionalCoordinate(x, y+1, Direction.DOWN))

                        y -= 1
                        direction = Direction.UP
                    }
                }
            }
            '-'  -> {


                when (at.direction) {
                    // if direction is up or down, add a coordinate above and below
                    Direction.UP -> {
                        directions.add(DirectionalCoordinate(x-1, y, Direction.LEFT))

                        x += 1
                        direction = Direction.RIGHT
                    }
                    Direction.DOWN -> {
                        directions.add(DirectionalCoordinate(x-1, y, Direction.LEFT))

                        x += 1
                        direction = Direction.RIGHT
                    }
                    // if direction is left or right, treat as empty
                    Direction.LEFT -> { x -= 1 }
                    Direction.RIGHT -> { x += 1 }
                }
            }
        }
        directions.add(DirectionalCoordinate(x, y, direction))

        return directions
    }
}

 

class DirectionalCoordinate(x: Int, y: Int, val direction: Direction) : Coordinate(x, y) {
    override fun equals(other: Any?): Boolean {
        return super.equals(other) && direction == (other as DirectionalCoordinate).direction
    }
}