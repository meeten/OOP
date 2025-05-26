import java.io.File
import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
data class CommandAssociation(val key: String, val commandName: String)

class KeyboardStateSaver(private val path: String) {
    fun save(map: Map<String, Command>) {
        val list = map.map { (key, cmd) ->
            val name = when (cmd) {
                is VolumeUpCommand -> "VolumeUp"
                is VolumeDownCommand -> "VolumeDown"
                is MediaPlayerCommand -> "MediaPlayer"
                is PrintCharCommand -> cmd.toString().last().toString()
                else -> "Unknown"
            }
            CommandAssociation(key, name)
        }
        val json = Json.encodeToString(list)
        File(path).writeText(json)
    }

    fun load(): Map<String, String> {
        val file = File(path)
        if (!file.exists()) return emptyMap()

        val json = file.readText()
        val list = Json.decodeFromString<List<CommandAssociation>>(json)
        return list.associate { it.key to it.commandName }
    }
}
