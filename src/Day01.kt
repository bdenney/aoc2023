import java.lang.StringBuilder

// list of number words with the word at the correct numerical index
val numbers = arrayOf("zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine")

fun main() {
    fun partOne(input: List<String>) {
        var sum = 0
        input.forEach {
            // for each string filter out any non-digit characters
            val digits = it.filter { c -> c.isDigit() }
            // we get the first and last character in our digit string
            val firstAndLast = digits[0].toString() + digits[digits.length - 1]
            // add it to our running tally
            sum += firstAndLast.toInt()
        }
        println(sum)
    }

    fun partTwo(input: List<String>) {
        var sum = 0
        input.forEach {
            // we want to go through and replace _every possible number word_ with its number equivalent
            var replaced = replaceNumberWords(it)
            // once we have the new string, we can filter out any non-digits
            val digits = replaced.filter { c -> c.isDigit() }
            // grab the first and last number
            val firstAndLast = digits[0].toString() + digits[digits.length - 1]
            // and add it to our running tally
            sum += firstAndLast.toInt()
        }
        println(sum)
    }

    val input = readInput("Day01Input")

    println("Part One:")
    partOne(input)

    println("Part Two:")
    partTwo(input)
}

fun replaceNumberWords(s: String): String {
    var result = s

    // oPtr tracks where we are in our replacement solution
    var oPtr = 0
    while (oPtr <= s.length) {
        val chars = result.toCharArray()
        val search = StringBuilder()

        // iPtr tracks where we are for this particular iteration. we want to initialize at where we're starting in the outer pointer
        var iPtr = oPtr
        while (iPtr <= chars.size - 1) {
            // if we've encountered something that isn't a letter, we know we can't make a word from it
            if (!chars[iPtr].isLetter()) break

            // append the next letter to our search string
            search.append(chars[iPtr])
            // check if the string is in our list of number words
            val index = numbers.indexOf(search.toString())
            if (index >= 0) {
                // we found a number! replace our input with the number and continue the search at the next index
                result = result.replaceRange(oPtr, oPtr + 1, index.toString())
                // cheap way to end this iteration
                iPtr = Int.MAX_VALUE
            } else {
                iPtr++
            }
        }
        oPtr++
    }
    return result
}
