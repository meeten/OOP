import kotlin.math.sqrt

class Point2d(private var _x: Int, private var _y: Int) {
    companion object {
        const val WIDTH = 800
        const val HEIGHT = 600
    }

    var x: Int = _x
        set(value) {
            field = if (value in 0..WIDTH) value else 0
        }

    var y: Int = _y
        set(value) {
            field = if (value in 0..HEIGHT) value else 0
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Point2d) return false
        return x == other.x && y == other.y
    }

    override fun toString(): String {
        return "Point2d(x=$x, y=$y)"
    }
}

class Vector2d(var x: Int, var y: Int) {
    constructor(start: Point2d, end: Point2d) : this(end.x - start.x, end.y - start.y)

    fun getItem(index: Int): Int {
        return when (index) {
            0 -> x
            1 -> y
            else -> throw IndexOutOfBoundsException("Индекс должен быть 0 или 1")
        }
    }

    fun setItem(index: Int, value: Int) {
        when (index) {
            0 -> x = value
            1 -> y = value
            else -> throw IndexOutOfBoundsException("Индекс должен быть 0 или 1")
        }
    }

    operator fun iterator(): Iterator<Int> {
        return listOf(x, y).iterator()
    }

    fun size(): Int {
        return 2
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Vector2d) return false
        return x == other.x && y == other.y
    }

    override fun toString(): String {
        return "Point2d(x=$x, y=$y)"
    }

    fun abs(): Double {
        return sqrt((x * x + y * y).toDouble())
    }

    fun plus(other: Vector2d): Vector2d {
        return Vector2d(x + other.x, y + other.y)
    }

    fun minus(other: Vector2d): Vector2d {
        return Vector2d(x - other.x, y - other.y)
    }

    fun times(scalar: Int): Vector2d {
        return Vector2d(x * scalar, y * scalar)
    }

    fun div(scalar: Int): Vector2d {
        require(scalar != 0) { "Деление на 0 невозможно" }
        return Vector2d(x / scalar, y / scalar)
    }

    fun dot(other: Vector2d): Int {
        return x * other.x + y * other.y
    }

    fun cross(other: Vector2d): Int {
        return x * other.y - y * other.x
    }

    companion object {
        fun dot(v1: Vector2d, v2: Vector2d): Int {
            return v1.x * v2.x + v1.y * v2.y
        }

        fun cross(v1: Vector2d, v2: Vector2d): Int {
            return v1.x * v2.y - v1.y * v2.x
        }
    }

    fun mixedProduct(b: Vector2d, c: Vector2d): Int {
        return 0
    }
}

fun main() {
    val vector1 = Vector2d(20, 30)
    val vector2 = Vector2d(Point2d(1, 1), Point2d(30, 40))

    println(vector2.mixedProduct(vector1, Vector2d(1, 1)))
}