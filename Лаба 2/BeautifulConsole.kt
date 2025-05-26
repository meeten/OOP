import java.io.File

enum class Color(val code: String) {
    RED("\u001B[31m"),
    GREEN("\u001B[32m"),
    YELLOW("\u001B[33m"),
    BLUE("\u001B[34m"),
    WHITE("\u001B[37m"),
    RESET("\u001B[0m");
}

class Printer private constructor(
    private val color: Color,
    private val position: Pair<Int, Int>,
    private val symbol: String,
    private val asciiArtMap: Map<Char, List<String>>
) : AutoCloseable {
    companion object {
        fun create(
            color: Color = Color.RESET,
            position: Pair<Int, Int> = Pair(0, 0),
            symbol: String = "*",
            fontFile: String = "C:\\Users\\MSI\\IdeaProjects\\OOP_Labs\\src\\font.txt"
        ): Printer {
            val asciiArtMap = parseAsciiArt(fontFile)
            return Printer(color, position, symbol, asciiArtMap)
        }

        private fun parseAsciiArt(filePath: String): Map<Char, List<String>> {
            val asciiArtMap = mutableMapOf<Char, List<String>>()
            val lines = File(filePath).readLines()

            var currentChar: Char? = null
            val currentArt = mutableListOf<String>()

            for (line in lines) {
                if (line.isNotEmpty() && line.length == 1 && line[0].isLetter()) {
                    currentChar?.let {
                        val maxLength = currentArt.maxOfOrNull { it.length } ?: 0
                        val alignedArt = currentArt.map { it.padEnd(maxLength, ' ') }
                        asciiArtMap[it] = alignedArt
                    }
                    currentChar = line[0]
                    currentArt.clear()
                } else if (currentChar != null && line.isNotBlank()) {
                    currentArt.add(line)
                }
            }

            currentChar?.let {
                val maxLength = currentArt.maxOfOrNull { it.length } ?: 0
                val alignedArt = currentArt.map { it.padEnd(maxLength, ' ') }
                asciiArtMap[it] = alignedArt
            }

            return asciiArtMap
        }

        fun print(text: String, color: Color, position: Pair<Int, Int>, symbol: String, fontFile: String) {
            val asciiArtMap = parseAsciiArt(fontFile)
            Printer(color, position, symbol, asciiArtMap).use { printer ->
                printer.printText(text)
            }
        }
    }

    fun printText(text: String) {
        if (asciiArtMap.isEmpty()) return

        val height = asciiArtMap.values.first().size
        for (i in 0 until height) {
            if (i == 0) print("\n".repeat(position.second.coerceAtLeast(0)))
            print(" ".repeat(position.first.coerceAtLeast(0)))
            print(color.code)

            text.uppercase().forEach { char ->
                asciiArtMap[char]?.getOrNull(i)?.let { line ->
                    print(line.replace('*', symbol.firstOrNull() ?: '*') + "  ")
                }
            }
            println()
        }
    }

    override fun close() {
        print(Color.RESET.code)
    }
}

fun main() {
    Printer.print(
        text = "TEST",
        color = Color.RED,
        position = Pair(5, 5),
        symbol = "@",
        fontFile = "C:\\Users\\MSI\\IdeaProjects\\OOP_Labs\\src\\font.txt"
    )

    Printer.print(
        text = "HELLO",
        color = Color.YELLOW,
        position = Pair(2, 2),
        symbol = "k",
        fontFile = "C:\\Users\\MSI\\IdeaProjects\\OOP_Labs\\src\\font.txt"
    )

    Printer.create().use { printer -> printer.printText("WORLD") }
}
