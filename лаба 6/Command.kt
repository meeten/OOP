interface Command {
    fun execute()
    fun undo()
    fun redo()
}

class PrintCharCommand(private val char: Char, private val output: OutputManager) : Command {
    override fun execute() {
        output.append(char.toString())
    }

    override fun undo() {
        output.removeLastChar()
    }

    override fun redo() {
        output.redoLastChar(char.toString())
    }
}


class VolumeUpCommand(private val output: OutputManager) : Command {
    override fun execute() {
        output.append("volume increased +20%")
    }

    override fun undo() {
        output.append("volume decreased -20%")
    }

    override fun redo() {
        output.redoLastChar("")
        execute()
    }
}


class VolumeDownCommand(private val output: OutputManager) : Command {
    override fun execute() {
        output.append("volume decreased +20%")
    }

    override fun undo() {
        output.append("volume increased +20%")
    }

    override fun redo() {
        println("ctrl+-")
        execute()
    }
}


class MediaPlayerCommand(private val output: OutputManager) : Command {
    private var launched = false

    override fun execute() {
        launched = true
        output.append("media player launched")
    }

    override fun undo() {
        if (launched) {
            output.append("media player closed")
            launched = false
        }
    }

    override fun redo() {
        output.redoLastChar("")
        execute()
    }
}
