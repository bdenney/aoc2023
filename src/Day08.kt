fun main() {
    fun part01(input: List<String>) {
        val documents = parseDocuments(input)
        val directions = documents.first
        val map = documents.second
        
        println(traversePath("AAA", map, directions) { node -> node == "ZZZ" })
    }
    
    fun part02(input: List<String>) {
        val documents = parseDocuments(input)
        val directions = documents.first
        val map = documents.second
        
        // get all starts
        val startNodes = map.keys.filter { it.last() == 'A' }.toMutableList()

        val paths = mutableListOf<Long>()
        startNodes.forEach {
            paths.add(traversePath(it, map, directions) { node -> node.endsWith("Z") }.toLong())
        }

        print(lcm(paths))
    }
    
    val input = readInput("Day08Input")
    print("Part 1: ")
    part01(input)
    
    print("Part 2: ")
    part02(input)
}

fun traversePath(start: String, map: Map<String, Node>, directions: String, stop: (String) -> Boolean): Int {
    var currentNode = start
    // count our steps
    var steps = 0
    
    var iter = directions.iterator()
    
    while (!stop(currentNode) || steps == 0) {
        val direction = if (!iter.hasNext()) {
            iter = directions.iterator()
            iter.next()
        } else {
            iter.next()
        }

        when (direction) {
            'L' -> currentNode = map[currentNode]!!.left
            'R' -> currentNode = map[currentNode]!!.right
            else -> println("Invalid direction!")
        }
        
        steps++
    }
    
    return steps
}

fun parseDocuments(input: List<String>): Pair<String, Map<String, Node>> {
    // first document is our directions
    val directions = input[0]
    
    // the rest give us the graph
    val map = mutableMapOf<String, Node>()
    for (idx in 2..<input.size) {
        // format of Node.value = (Node.left, Node.right)
        val parts = input[idx].split(" = ")
        val nodeValue = parts[0]
        
        val nodeDirections = parts[1].split(", ")
            .toMutableList()
            .also {
                it[0] = it[0].removePrefix("(")
                it[1] = it[1].removeSuffix(")")
            }
         
        val leftValue = nodeDirections[0]
        val rightValue = nodeDirections[1]
        
        map[nodeValue] = Node(nodeValue, leftValue, rightValue)
    }
    
    return Pair(directions, map)
}

class Node(val value: String, val left: String, val right: String) {
    override fun toString(): String {
        return "[$value, (${left}, ${right})]"
    }
}

fun gcd(first: Long, second: Long): Long {
    var a = first
    var b = second
    while (b > 0) {
        val temp = b
        b = a % b // % is remainder
        a = temp
    }
    return a
}

fun lcm(a: Long, b: Long): Long {
    return a * (b / gcd(a, b))
}

fun lcm(input: List<Long>): Long {
    var result = input[0]
    for(i in 1..<input.size) result = lcm(result, input[i])
    return result
}