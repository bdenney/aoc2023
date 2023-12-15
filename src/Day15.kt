fun main() {
    val input = readInput("Day15Input")
    
    print("Part 1: ")
    println(hash(input[0]))
    
    print("Part 2: ")
    println(calculateFocusPower(hashmap(input[0])))
}

fun calculateFocusPower(map: Map<Int, List<String>>): Int {
    /*
        The focusing power of a single lens is the result of multiplying together:
    
            One plus the box number of the lens in question.
            The slot number of the lens within the box: 1 for the first lens, 2 for the second lens, and so on.
            The focal length of the lens.
     */
    
    var totalPower = 0
    map.forEach { entry ->
        entry.value.forEachIndexed { index, lens ->
            val parts = lens.split(" ")
            var focalLength = parts[1].toInt()
            val boxNumber = entry.key
            val slotNumber = index + 1
            
            val focusingPower = (1 + boxNumber) * (slotNumber) * focalLength
            totalPower += focusingPower
        }
    }
    
    return totalPower
}

fun hashmap(value: String): Map<Int, List<String>> {
    val boxes = mutableMapOf<Int, MutableList<String>>()
    
    var ptr = 0
    var currentValue = 0
    var label = StringBuilder()
    
    while (ptr < value.length) {
        var char = value[ptr]
        when (char) {
            ',' -> {
                currentValue = 0
            }
            '=' -> {
                var box = boxes[currentValue]
                if (box == null) {
                    ptr++
                    char = value[ptr]
                    label.append(" ").append(char)
                    
                    boxes[currentValue] = mutableListOf(label.toString())
                } else {
                    val list = boxes[currentValue]
                    val index = list!!.indexOfFirst {it.contains(label)} 
                    if (index > -1) {
                        ptr++
                        char = value[ptr]
                        label.append(" ").append(char)
                        
                        list[index] = label.toString()
                    } else {
                        ptr++
                        char = value[ptr]
                        label.append(" ").append(char)

                        boxes[currentValue]!!.add(label.toString())
                    }
                }
                
                label.clear()
            }
            '-' -> {
                boxes[currentValue]?.removeAll { it.contains(label)}
                
                label.clear()
            }
            else -> {
                label.append(char)
                
                currentValue += char.code
                currentValue *= 17
                currentValue %= 256
            }
        }
        ptr++
    }

    return boxes
}

fun hash(value: String): Int {
    /*
        Determine the ASCII code for the current character of the string.
        Increase the current value by the ASCII code you just determined.
        Set the current value to itself multiplied by 17.
        Set the current value to the remainder of dividing itself by 256.
     */
    
    var sum = 0
    var ptr = 0
    var currentValue = 0
    
    while (ptr < value.length) {
        var char = value[ptr]
        when (char) {
            ',' -> {
                sum += currentValue
                currentValue = 0
            }
            else -> {
                currentValue += char.code
                currentValue *= 17
                currentValue %= 256
            }
        }
        ptr++
    }
    sum += currentValue
    
    return sum
}