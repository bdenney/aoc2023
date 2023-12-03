fun main() {
    fun part01(input: List<String>) {
        // useful constants
        val height = input.size
        val width = input[0].length
        
        val engine = Array(height) { CharArray(width) } 
        input.forEachIndexed { index, element ->
            element.toCharArray().copyInto(engine[index])
        }
        
        val partPieces = getAdjacencies(engine)
        
        val parts = assemble(engine, partPieces)
        
        // keep a running sum
        var total = 0
        
        parts.forEach { part ->
            val partNumber = part.first
            val range = part.second
            
            total += partNumber
        }
        
        println(total)
    }
    
    fun part02(input: List<String>) {
        // useful constants
        val height = input.size
        val width = input[0].length

        val engine = Array(height) { CharArray(width) } 
        input.forEachIndexed { index, element ->
            element.toCharArray().copyInto(engine[index])
        }
        
        val partPieces = getAdjacencies(engine)

        val parts = assemble(engine, partPieces)
        
        val gearCandidates = getGearCandidates(engine)
        val gears = mutableListOf<Pair<Int,Int>>()
        
        // test if each gear has exactly two adjacent parts
        var totalRatio = 0
        gearCandidates.forEach { gear ->
            
            var ratio = 1
            val adjacentPoints = generateAdjacentPoints(gear)
            val uniqueParts = mutableSetOf<Pair<Int, Triple<Int,Int,Int>>>()
            adjacentPoints.forEach { adj ->
                val filtered = parts.filter { part ->
                    val xadj = adj.first
                    val yadj = adj.second
                    
                    val x1 = part.second.first
                    val x2 = part.second.second
                    val y = part.second.third
                    
                    (yadj == y) && (xadj in x1..<x2)
                }
                uniqueParts.addAll(filtered)
            }
            
            if (uniqueParts.count() == 2) {
                gears.add(gear)
                uniqueParts.forEach {
                    val value = it.first
                    ratio *= value
                }
                totalRatio += ratio
            }
        }
        
        println(totalRatio)
    }
    
    val input = readInput("Day03Input")
    print("Part 1: ")
    part01(input)
    
    print("Part 2: ")
    part02(input)
}

fun assemble(engine: Array<CharArray>, partPieces: List<Pair<Int,Int>>): List<Pair<Int, Triple<Int,Int,Int>>> {
    val assembledPieces = mutableListOf<Pair<Int, Triple<Int,Int, Int>>>()
    
    val width = engine[0].size
    
    // keep a list of dupes
    val dupes = mutableListOf<Pair<Int,Int>>()
    // assemble the full number for each part piece
    partPieces.forEach { piece ->
        val x = piece.first
        val y = piece.second

        if (!dupes.contains(piece)) {

            // crawl in each x direction until we encounter a non-digit
            var start = x
            var end = x
            var foundStart = false
            var foundEnd = false
            while (!foundStart || !foundEnd) {

                if (!foundStart) {
                    if (engine[y][start].isDigit()) {
                        start -= 1
                    } else {
                        start += 1
                        foundStart = true
                    }
                    if (start < 0) {
                        start = 0
                        foundStart = true
                    }
                }

                if (!foundEnd) {
                    if (engine[y][end].isDigit()) {
                        end += 1
                    } else {
                        foundEnd = true
                    }
                    if (end >= width) {
                        end = width
                        foundEnd = true
                    }
                }
            }
            val partNumber = String(engine[y]).substring(start,end).toInt() 
            assembledPieces.add(Pair(partNumber, Triple(start, end, y)))
            // dedupe anything in this range
            dupes.addAll(partPieces.filter { pair ->
                pair.second == y && (pair.first in start..end)
            })
        }
    }
    return assembledPieces
}

fun getGearCandidates(input: Array<CharArray>): List<Pair<Int,Int>> {
    val coords = mutableListOf<Pair<Int,Int>>()
    val height = input.size - 1
    for (y in 0..height) {
        val width = input[y].size - 1
        for (x in (0..width)) {
            val point = Pair(x, y)
            if (input[y][x] == '*') {
                val adjacentPoints = generateAdjacentPoints(point)
                var ptr = 0
                var count = 0
                while (ptr < adjacentPoints.size) {
                    val pair = adjacentPoints[ptr] 
                    val xadj = pair.first
                    val yadj = pair.second

                    // only check points that are in bounds of the array
                    if (xadj >= 0 && yadj >= 0 && xadj <= width && yadj <= height) {
                        if ((input[yadj][xadj]).isDigit()) {
                            count++
                        }
                    }
                    ptr++
                }
                coords.add(Pair(x,y))
            }
        }
    }
    return coords
}

fun generateAdjacentPoints(point: Pair<Int,Int>): List<Pair<Int,Int>> {
    val adjacentPoints = mutableListOf<Pair<Int,Int>>()
    for (x in -1..1) {
        for (y in -1..1) {
            adjacentPoints.add(Pair(point.first+x, point.second+y))
        }
    }
    return adjacentPoints
}

fun getAdjacencies(input: Array<CharArray>): List<Pair<Int,Int>> {
    val coords = mutableListOf<Pair<Int,Int>>()
    val height = input.size - 1
    
    for (y in 0..height) {
        val width = input[y].size - 1
        for (x in (0..width)) {
            val point = Pair(x, y)
            // we only care about part numbers
            if (input[y][x].isDigit()) {
                // get all possible adjacent point combinations
                val adjacentPoints = generateAdjacentPoints(point) 

                var ptr = 0
                while (ptr < adjacentPoints.size) {
                    val pair = adjacentPoints[ptr] 
                    val xadj = pair.first
                    val yadj = pair.second

                    // only check points that are in bounds of the array
                    if (xadj >= 0 && yadj >= 0 && xadj <= width && yadj <= height) {
                        if (isSymbol(input[yadj][xadj])) {
                            coords.add(Pair(x,y))
                            break
                        }
                    }
                    ptr++
                }
            }
        }
    }
    
    return coords
}

fun isSymbol(char: Char): Boolean {
    return !char.isDigit() && !char.isLetter() && char != '.'
}