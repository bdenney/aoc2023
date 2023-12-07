import java.lang.Math.pow
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.sqrt

fun main() {
    fun part01(input: List<String>) {
        val records = parse(input)
        var marginOfError = 1
        
        records.forEach { record ->
            marginOfError *= marginForEntry(record)
        }
        println(marginOfError)
    }
    
    fun part02(input: List<String>) {
        val record = parsePart2(input)
        println(marginForEntry(record))
    }
    
    val input = readInput("Day06Input")
    
    print("Part 1: ")
    part01(input)
    
    println("")
    
    print("Part 2: ")
    part02(input)
}

fun marginForEntry(entry: Pair<Long, Long>): Int {
    val raceTime = entry.first
    val distanceToBeat = entry.second

    var topRange = (raceTime + sqrt(pow(raceTime.toDouble(), 2.0) - 4*(distanceToBeat))) / 2
    var bottomRange = (raceTime - sqrt(pow(raceTime.toDouble(), 2.0) - 4*(distanceToBeat))) / 2

    topRange = if (floor(topRange) == topRange) topRange - 1 else floor(topRange)
    bottomRange = if (ceil(bottomRange) == bottomRange) bottomRange + 1 else ceil(bottomRange)

    val winningTimeRange = Pair(bottomRange.toInt(), topRange.toInt())

    return (topRange - bottomRange + 1).toInt()
}

fun parse(input: List<String>): List<Pair<Long, Long>> {
    val times = mutableListOf<Long>()
    times.addAll(input[0].split(":")[1].split(" ").filter { it.isNotBlank() }.map { it.toLong() })
    
    val distances = mutableListOf<Long>()
    distances.addAll(input[1].split(":")[1].split(" ").filter { it.isNotBlank() }.map { it.toLong() })
    
    return times.zip(distances)
}

fun parsePart2(input: List<String>): Pair<Long, Long> {
    
    val time = input[0].split(":")[1].split(" ").filter { it.isNotBlank() }.joinToString("").toLong()
    val distance = input[1].split(":")[1].split(" ").filter { it.isNotBlank() }.joinToString("").toLong()

    return Pair(time, distance)
}