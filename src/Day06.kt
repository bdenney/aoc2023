fun main() {
    fun part01(input: List<String>) {
        val records = parse(input)
        var marginOfError = 1
        records.forEach { record ->
            val raceTime = record.first
            val distanceToBeat = record.second
            var wins = 0
            for (holdTime in 0..raceTime) {
                val travelDistance = (raceTime - holdTime) * holdTime
                if (travelDistance > distanceToBeat) {
                    wins++
                }
            }
            marginOfError *= wins
        }
        println(marginOfError)
    }
    
    fun part02(input: List<String>) {
        
    }
    
    val input = readInput("Day06Input")
    
    print("Part 1: ")
    part01(input)
    
    println("")
    
    print("Part 2: ")
    part02(input)
}

fun parse(input: List<String>): List<Pair<Long, Long>> {
    val times = mutableListOf<Long>()
    times.addAll(input[0].split(":")[1].split(" ").filter { it.isNotBlank() }.map { it.toLong() })
    
    val distances = mutableListOf<Long>()
    distances.addAll(input[1].split(":")[1].split(" ").filter { it.isNotBlank() }.map { it.toLong() })
    
    return times.zip(distances)
}