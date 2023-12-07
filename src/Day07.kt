import kotlin.math.pow

fun main() {
    fun part01(input: List<String>) {
        val hands = mutableListOf<Hand>()
        val wild = false
        
        input.forEach {
            hands.add(Hand(it, wild))
        }

        hands.sortWith(compareBy<Hand> { it.score }
            .thenBy { Hand.getCardValue(it.cards[0], wild) }
            .thenBy { Hand.getCardValue(it.cards[1], wild) }
            .thenBy { Hand.getCardValue(it.cards[2], wild) }
            .thenBy { Hand.getCardValue(it.cards[3], wild) }
            .thenBy { Hand.getCardValue(it.cards[4], wild) })
        
        val winnings = hands.foldRightIndexed(0) { idx, element, sum ->
            sum + (idx + 1) * element.bid
        }
        
        println(winnings)
    }
    
    fun part02(input: List<String>) {
        val hands = mutableListOf<Hand>()
        input.forEach {
            hands.add(Hand(it))
        }

        hands.sortWith(compareBy<Hand> { it.score }
            .thenBy { Hand.getCardValue(it.cards[0]) }
            .thenBy { Hand.getCardValue(it.cards[1]) }
            .thenBy { Hand.getCardValue(it.cards[2]) }
            .thenBy { Hand.getCardValue(it.cards[3]) }
            .thenBy { Hand.getCardValue(it.cards[4]) })

        val winnings = hands.foldRightIndexed(0) { idx, element, sum ->
            sum + (idx + 1) * element.bid
        }

        println(winnings)
    }
    
    val input = readInput("Day07Input")
    print("Part 1: ")
    part01(input)
    
    print("Part 2: ")
    part02(input)
}

class Hand(str: String, wild: Boolean = true) {
    val cards: List<Char> 
    val bid: Int
    
    val score: Int
    
    init {
        val line = str.split(" ")
        cards = line[0].toList()
        bid = line[1].toInt()
        score = scoreHand(wild)
    }
    
    private fun scoreHand(wild: Boolean = true): Int {
        val group = cards.groupingBy { it }.eachCount().toList().sortedByDescending { it.second }.toMap(mutableMapOf())
        val wilds = if (wild) group['J'] ?: 0 else 0
        if (wild) { group.remove('J') }
        
        /*
            Scoring:
                5 of a kind = 25
                4 of a kind = 16
                Full house = 13
                3 of a kind = 9
                2 of a kind = 4
                1 of a kind = 1

        */
        
        var total = if (wild && wilds == 5) 25 else 0
        group.forEach {
            total += if (it.key == group.keys.first()) {
                (it.value + wilds).toDouble().pow(2.0).toInt()
            } else {
                it.value.toDouble().pow(2.0).toInt()
            }
        }
        
        return total
    }

    override fun toString(): String {
        return cards.joinToString()
    }
    
    companion object {
        fun getCardValue(card: Char, wild: Boolean = true): Int {
            val value = if (card.isDigit()) {
                card.digitToInt()
            } else {
                when (card) {
                    'T' -> 10
                    'J' -> if (wild) 0 else 11
                    'Q' -> 12
                    'K' -> 13
                    'A' -> 14
                    else -> {
                        println("ERROR - $card")
                        -1
                    } // error
                }
            }
            return value
        }
    }
}