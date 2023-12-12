import kotlin.math.abs

open class Coordinate(val x: Int, val y: Int) {
    fun isAbove(other: Coordinate): Boolean {
        return y < other.y
    }

    fun isBelow(other: Coordinate): Boolean {
        return y > other.y
    }

    fun isLeftOf(other: Coordinate): Boolean {
        return x < other.x
    }

    fun isRightOf(other: Coordinate): Boolean {
        return x > other.x 
    }
    
    fun shortestPath(there: Coordinate): Int {
        return abs(there.y - y) + abs(there.x - x)
    }

    fun printDirection(other: Coordinate): String {
        if (isAbove(other)) {
            return "above"
        } else if (isBelow(other)) {
            return "below"
        } else if (isLeftOf(other)) {
            return "left of"
        } else if (isRightOf(other)) {
            return "right of"
        } else {
            return "error!"
        }
    }

    override fun equals(other: Any?): Boolean {
        return other is Coordinate && this.x == other.x && this.y == other.y
    }

    override fun toString(): String {
        return "($x, $y)"
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        return result
    }
}