import kotlin.math.max

class Game(val str: String) {
    
    val number: Int
    val dice = mutableListOf<Triple<Int, Int, Int>>()
    
    val maxColors: Triple<Int,Int,Int>
    
    init {
        val gameString = str.split(":")
        number = gameString[0].replace("Game ", "").toInt()
        
        val games = gameString[1].split(";")
        var maxRed = 0
        var maxBlue = 0
        var maxGreen = 0
        games.forEach { game ->
            val colors = game.split(",")
            var red = 0
            var green = 0
            var blue = 0
            colors.forEach { color->
                when {
                    color.contains("red") -> {
                        red = parseColor(color)
                        maxRed = max(red, maxRed)
                    }
                    color.contains("green") -> {
                        green = parseColor(color)
                        maxGreen = max(green, maxGreen)
                    }
                    color.contains("blue") -> {
                        blue = parseColor(color)
                        maxBlue = max(blue, maxBlue)
                    }
                }
            }
            dice.add(Triple(red, green, blue))
        }
        maxColors = Triple(maxRed, maxBlue, maxGreen)
    }
    
    fun power(): Int {
        return maxColors.first * maxColors.second * maxColors.third
    }
    
    fun isValid(): Boolean {
        var valid = true
        dice.forEach { die ->
             if (die.first > 12 || die.second > 13 || die.third > 14) {
                 valid = false
             } 
        }
        return valid
    }
    
    fun parseColor(colorString: String): Int {
        return colorString.filter { c-> c.isDigit() }.toInt()
    }

    override fun toString(): String {
        return str
    }
}

fun main() {
    
    fun part01(input: List<String>) {
        var sum = 0
        input.forEach {
            val game = Game(it)
            if (game.isValid()) {
                sum += game.number
            }
        }
        println(sum)
    }
    
    fun part02(input: List<String>) {
        var sum = 0
        input.forEach {
            val game = Game(it)
            sum += game.power()
        }
        println(sum)
    }
    
    val input = readInput("Day02Input")
    part01(input)
    part02(input)
}