import kotlin.math.max
import kotlin.math.min

fun main() {
    fun part02(input: List<String>) {
        val almanac = Almanac(input)
        
        val ranges = mutableListOf<LongRange>()
        almanac.seedRanges.forEach {
            ranges.addAll(almanac.getLocation(it))
        }
        println(ranges.minBy { it.start }.first)
    }
    
    val input = readInput("Day05Input")
    
    print("Part 2: ")
    part02(input)
}

class Almanac(input: List<String>) {
    val seeds = mutableListOf<Long>()
    
    val seedRanges = mutableListOf<LongRange>()
    val seedToSoil = mutableMapOf<LongRange, LongRange>()
    val soilToFertilizer = mutableMapOf<LongRange, LongRange>()
    val fertilizerToWater = mutableMapOf<LongRange, LongRange>()
    val waterToLight = mutableMapOf<LongRange, LongRange>()
    val lightToTemperature = mutableMapOf<LongRange, LongRange>()
    val temperatureToHumidity = mutableMapOf<LongRange, LongRange>()
    val humidityToLocation = mutableMapOf<LongRange, LongRange>()
    
    val maps = mutableMapOf(
        Pair("seed-to-soil map:", seedToSoil),
        Pair("soil-to-fertilizer map:", soilToFertilizer),
        Pair("fertilizer-to-water map:", fertilizerToWater),
        Pair("water-to-light map:", waterToLight),
        Pair("light-to-temperature map:", lightToTemperature),
        Pair("temperature-to-humidity map:", temperatureToHumidity),
        Pair("humidity-to-location map:", humidityToLocation)
    )
    
    init {
        parse(input)
    }
    
    fun getLocation(range: LongRange): List<LongRange> {
        val soils = seedToSoil.allPossibleRanges(range)
        
        var fertilizers = mutableListOf<LongRange>()
        soils.forEach {
            fertilizers.addAll(soilToFertilizer.allPossibleRanges(it))
        }
        
        var waters = mutableListOf<LongRange>()
        fertilizers.forEach {
            waters.addAll(fertilizerToWater.allPossibleRanges(it))
        }
        
        var lights = mutableListOf<LongRange>()
        waters.forEach {
            lights.addAll(waterToLight.allPossibleRanges(it))
        }
        
        var temperatures = mutableListOf<LongRange>()
        lights.forEach {
            temperatures.addAll(lightToTemperature.allPossibleRanges(it))
        }
        
        var humidities = mutableListOf<LongRange>()
        temperatures.forEach {
            humidities.addAll(temperatureToHumidity.allPossibleRanges(it))
        }
        
        var locations = mutableListOf<LongRange>()
        humidities.forEach {
            locations.addAll(humidityToLocation.allPossibleRanges(it))
        }
        
        return locations
    }
    
    private fun MutableMap<LongRange, LongRange>.allPossibleRanges(input: LongRange): List<LongRange> {
        val output = mutableListOf<LongRange>()
        
        val sortedCandidates = this.keys.sortedBy { it.start }
        
        val startRange = this.keys.firstOrNull { it.contains(input.start) }
        val endRange = this.keys.firstOrNull { it.contains(input.endInclusive) }
        
        if (startRange != null && startRange.equals(endRange)) {
            // incoming range is fully less than one of our ranges
            // get output of the bounds input
            val outputStartRange = this[startRange]!!
            
            val offset = input.start - startRange.start 
            val length = input.endInclusive - input.start
            
            // adjust to new length
            output.add(LongRange(outputStartRange.start + offset, outputStartRange.start + offset + length))
        } else if (startRange == null && endRange == null) {
            // incoming range is fully outside of any bounds we have
            
            if (sortedCandidates.first().start > input.endInclusive) {
                output.add(input)
            } else if (sortedCandidates.last().endInclusive < input.start) {
                output.add(input)
            } else {
                output.add(input)
            }
            
        } else if (startRange != null && endRange == null) {
            // input starts within our known ranges, but extends past it
            val outputStartRange = this[startRange]!!
            val offset = input.start - startRange.start
            output.add(LongRange(outputStartRange.start + offset, outputStartRange.endInclusive))
            output.add(LongRange(input.start + offset + 1, input.endInclusive))
        } else if (startRange == null && endRange != null) {
            // input starts before our known ranges, but ends within it
            val outputEndRange = this[endRange]!!
            val offset = input.endInclusive - (endRange?.start ?: Long.MIN_VALUE)
            output.add(LongRange(input.start, outputEndRange.start + offset))
        } else if (startRange != null && endRange != null) {
            // input crosses a boundary
            val outputStartRange = this[startRange]!!
            val outputEndRange = this[endRange]!!
            if (startRange.endInclusive + 1 == endRange.start) {
                val offset = input.start - (startRange?.start ?: Long.MIN_VALUE) 
                val length = input.endInclusive - input.start
                val endLength = input.endInclusive - endRange.start 
                output.add(LongRange(outputStartRange.start + offset, outputStartRange.endInclusive))
                output.add(LongRange(outputEndRange.start, outputEndRange.start + endLength))
            } else {
                // get start and end ranges
                val offset = input.start - (startRange?.start ?: Long.MIN_VALUE) 
                val length = input.endInclusive - input.start
                val endLength = input.endInclusive - endRange.start 
                output.add(LongRange(outputStartRange.start + offset, outputStartRange.endInclusive))
                output.add(LongRange(outputEndRange.start, outputEndRange.start + endLength))
                
                // get any in-between
                val startIndex = sortedCandidates.indexOf(startRange)
                val endIndex = sortedCandidates.indexOf(endRange)
                for (i in (startIndex+1)..(endIndex-1)) {
                    output.add(this[sortedCandidates[i]]!!)
                }
            }
        } else {
            println("!!! UNHANDLED UNKNOWN CASE $startRange, $endRange !!!")
        }
        
        return output
    }
    
    fun parse(input: List<String>) {
        val iter = input.iterator()
        while (iter.hasNext()) {
            val line = iter.next()
            when {
                line.contains("seeds:") -> parseSeeds(line)
                line.contains("map:") -> parseMap(line, iter)
                else -> continue
            }
        }
    }

    fun parseSeeds(seedStr: String) {
        parseSeedRanges(seedStr)
        seeds.addAll(seedStr.split(":")[1].split(" ").filter{ it.isNotBlank() }.map { it.toLong() })
    }
    
    fun parseSeedRanges(seedStr: String) {
        var strs = seedStr.split(":")[1].split(" ").filter{ it.isNotBlank() }.map { it.toLong() }
        var ptr = 0
        while (ptr < (strs.size - 1)) {
            seedRanges.add(LongRange(strs[ptr], strs[ptr] + strs[ptr+1] - 1))
            ptr += 2
        }
    }

    fun parseMap(mapVal: String, iter: Iterator<String>) {
        val map = maps[mapVal]!!
        var done = false
        while (!done && iter.hasNext()) {
            val line = iter.next()
            
            if (line.isBlank() || line.isEmpty()) {
                done = true
                break
            }
            
            // get the metadata
            val values = line.split(" ").filter{ it.isNotBlank() }
            val dest = values[0].toLong()
            val src = values[1].toLong()
            val len = values[2].toLong()
            
            map[LongRange(src, src + len - 1)] = LongRange(dest, dest + len - 1)
        }
    }
}