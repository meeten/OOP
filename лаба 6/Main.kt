fun main() {
    val output = OutputManager()
    val keyboard = Keyboard(output)

    keyboard.bind("a", PrintCharCommand('a', output))
    keyboard.bind("b", PrintCharCommand('b', output))
    keyboard.bind("c", PrintCharCommand('c', output))
    keyboard.bind("ctrl++", VolumeUpCommand(output))
    keyboard.bind("ctrl+-", VolumeDownCommand(output))
    keyboard.bind("ctrl+p", MediaPlayerCommand(output))
    keyboard.bind("d", PrintCharCommand('d', output))

    keyboard.press("a")
    keyboard.press("b")
    keyboard.press("c")
    keyboard.undo()
    keyboard.undo()
    keyboard.redo()
    keyboard.press("ctrl++")
    keyboard.press("ctrl+-")
    keyboard.press("ctrl+p")
    keyboard.press("d")
    keyboard.undo()
    keyboard.undo()
}
