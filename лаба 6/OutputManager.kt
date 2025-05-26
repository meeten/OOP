import java.io.File

class OutputManager {
    private val text = StringBuilder()
    private val file = File("output.txt")

    init {
        file.writeText("")
    }

    fun append(str: String) {
        println(str)

        if (str.length == 1) {
            text.append(str)
            file.appendText(text.toString() + "\n")
        } else {
            file.appendText("$str\n")
        }
    }

    fun removeLastChar() {
        println("undo")

        if (text.isNotEmpty()) {
            text.deleteCharAt(text.lastIndex)
            file.appendText(text.toString() + "\n")
        }
    }

    fun redoLastChar(c:String) {
        println("redo")

        if (text.isNotEmpty()){
            text.append(c)
            file.appendText(text.toString() + "\n")
        }
    }
}
