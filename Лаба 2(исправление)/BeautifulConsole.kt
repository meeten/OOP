import java.io.File
import java.io.FileNotFoundException

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
            fontFile: String? = null
        ): Printer {
            val asciiArtMap = parseAsciiArt(fontFile ?: loadDefaultFont())
            return Printer(color, position, symbol, asciiArtMap)
        }

        private fun loadDefaultFont(): String {
            val resourceStream = Printer::class.java.classLoader.getResourceAsStream("font.txt")
            if (resourceStream != null) {
                return "font.txt"
            }

            val localFile = File("font.txt")
            if (localFile.exists()) {
                return localFile.path
            }

            throw FileNotFoundException("Font file not found in resources or local directory")
        }

        private fun parseAsciiArt(filePath: String): Map<Char, List<String>> {
            val asciiArtMap = mutableMapOf<Char, List<String>>()
            val lines: List<String> = try {
                val resourceStream = Printer::class.java.classLoader.getResourceAsStream(filePath)
                resourceStream?.bufferedReader()?.readLines() ?: File(filePath).readLines()
            } catch (e: Exception) {
                throw RuntimeException("Failed to load font file: ${e.message}")
            }

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

        fun print(
            text: String,
            color: Color,
            position: Pair<Int, Int>,
            scale: Int,
            symbol: String,
            fontFile: String? = null
        ) {
            val asciiArtMap = parseAsciiArt(fontFile ?: loadDefaultFont())
            Printer(color, position, symbol, asciiArtMap).use { printer ->
                printer.printText(text, scale)
            }
        }
    }

    fun printText(text: String, scale: Int = 1) {
        if (asciiArtMap.isEmpty()) return

        val height = asciiArtMap.values.first().size
        for (i in 0 until height * scale) {
            if (i == 0) print("\n".repeat(position.second.coerceAtLeast(0)))
            print(" ".repeat(position.first.coerceAtLeast(0)))
            print(color.code)

            text.uppercase().forEach { char ->
                asciiArtMap[char]?.getOrNull(i / scale)?.let { line ->
                    val scaledLine = line.map { c -> c.toString().repeat(scale) }.joinToString("")
                    print(scaledLine.replace('*', symbol.firstOrNull() ?: '*') + "  ")
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
        text = "HELLO",
        color = Color.YELLOW,
        position = Pair(2, 2),
        scale = 1,
        symbol = "k"
    )

    Printer.create().use { printer ->
        printer.printText("KOTLIN", scale = 3)
    }
}