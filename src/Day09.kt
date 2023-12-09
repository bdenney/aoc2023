fun main() {
    fun part01(input: List<String>) {
        var total = 0
        input.forEach {
            val sequence = it.split(" ").map{it.toInt()}
            total += generateNextInSequence(sequence)
        }
        println(total)
    }
    
    fun part02(input: List<String>) {
        var total = 0
        input.forEach {
            val sequence = it.split(" ").map{it.toInt()}
            total += generatePreviousInSequence(sequence)
        }
        println(total)
    }
    
    val input = readInput("Day09Input")
    print("Part 1: ")
    part01(input)
    
    print("Part 2: ")
    part02(input)
}

fun generateNextInSequence(sequence: List<Int>): Int {
    val stack = calculateSuccessiveDifferences(sequence)
    var lastValue = 0
    while (stack.isNotEmpty()) {
        lastValue += stack.removeFirstOrNull()?.last() ?: 0
    }
    return lastValue
}

fun generatePreviousInSequence(sequence: List<Int>): Int {
    val stack = calculateSuccessiveDifferences(sequence)
    var previousFirst = 0
    // generate the next sequence by calculating (n-1) = n - (first of last sequence
    while (stack.isNotEmpty()) {
        previousFirst = (stack.removeFirstOrNull()?.first() ?: 0) - previousFirst
    }
    return previousFirst
}

fun calculateSuccessiveDifferences(input: List<Int>): ArrayDeque<List<Int>> {
    val stack = ArrayDeque<List<Int>>()
    var sequence = input
    while (sequence.any { it != 0 }) {
        // add our current sequence
        stack.addFirst(sequence)

        // generate the next sequence by calculating f(n) = (n+1) - (n)
        val nextSequence = mutableListOf<Int>()
        for (i in 0..<sequence.size-1) {
            nextSequence.add(i, sequence[i+1] - sequence[i])
        }

        sequence = nextSequence
    }
    return stack
}