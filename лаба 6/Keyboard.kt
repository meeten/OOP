class Keyboard(private val output: OutputManager) {
    private val commandMap = mutableMapOf<String, Command>()
    private val undoStack = mutableListOf<Command>()
    private val redoStack = mutableListOf<Command>()
    private val stateSaver = KeyboardStateSaver("associations.json")

    init {
        val saved = stateSaver.load()
        saved.forEach { (key, commandStr) ->
            commandMap[key] = createCommandFromString(commandStr)
        }
    }

    fun press(key: String) {
        val command = commandMap[key]
        if (command != null) {
            command.execute()
            undoStack.add(command)
            redoStack.clear()
        } else {
            println("No command associated with key: $key")
        }
    }

    fun bind(key: String, command: Command) {
        commandMap[key] = command
        stateSaver.save(commandMap)
    }

    fun undo() {
        if (undoStack.isNotEmpty()) {
            val cmd = undoStack.removeLast()
            cmd.undo()
            redoStack.add(cmd)
        }
    }

    fun redo() {
        if (redoStack.isNotEmpty()) {
            val cmd = redoStack.removeLast()
            cmd.redo()
            undoStack.add(cmd)
        }
    }

    private fun createCommandFromString(str: String): Command {
        return when (str) {
            "VolumeUp" -> VolumeUpCommand(output)
            "VolumeDown" -> VolumeDownCommand(output)
            "MediaPlayer" -> MediaPlayerCommand(output)
            else -> PrintCharCommand(str.first(), output)
        }
    }
}
