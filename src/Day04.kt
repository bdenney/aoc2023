import kotlin.math.pow

fun main() {
    fun part01(input: List<String>) {
        var total = 0
        input.forEach {
            val card = it.split(':')[1].split('|')
            val winners = card[0].split(" ").filter { it.isNotBlank() }.map { it.toInt() }
            val numbers = card[1].split(" ").filter { it.isNotBlank() }.map { it.toInt() }
            val matches = numbers.count { winners.contains(it) }
            total += 2.toDouble().pow(matches-1).toInt()
        }
        println(total)
    }
    
    fun part02(input: List<String>) {
        val cards = input.map{ Card(it) }
        var queue = ArrayDeque<Card>()
        queue.addAll(cards)
        
        val map = mutableMapOf<Card, Int>()
        while (queue.isNotEmpty()) {
            val card = queue.removeFirst()
            map.put(card, map.getOrDefault(card, 0) + 1)
            if (card.isWinner()) {
                for (i in 0..<card.matches) {
                    queue.add(cards[card.number + i])
                }
            }
        }
        println(map.values.sum())
    }
    
    val input = readInput("Day04Input")
    print("Part 1: ")
    part01(input)
    
    print("Part 2: ")
    part02(input)
}

class Card(str: String) {
    val number: Int
    val winners: List<Int>
    val played: List<Int>
    
    val matches: Int
    
    init {
        val cardText = str.split(":")
        number = cardText[0].split(" ").filter { it.isNotBlank() }[1].toInt()
        
        val gameField = cardText[1].split("|")
        winners = gameField[0].split(" ").filter { it.isNotBlank() }.map { it.toInt() }
        played = gameField[1].split(" ").filter { it.isNotBlank() }.map { it.toInt() }
        
        matches = played.count { winners.contains(it) }
    }
    
    fun isWinner(): Boolean {
        return matches > 0
    }

    override fun toString(): String {
        return "Card $number"
    }
}