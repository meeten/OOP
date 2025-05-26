import kotlin.math.log

interface ILogFilter {
    fun match(text: String): Boolean
}

class SimpleLogFilter(private val pattern: String) : ILogFilter {
    override fun match(text: String): Boolean = text.contains(pattern)
}

class ReLogFilter(private val regex: Regex) : ILogFilter {
    override fun match(text: String): Boolean = regex.containsMatchIn(text)
}

interface ILogHandler {
    fun handle(text: String)
}

class FileHandler(private val filename: String) : ILogHandler {
    override fun handle(text: String) {
        try {
            java.io.File(filename).appendText("$text\n")
        } catch (e: Exception) {
            System.err.println("FileHandler error: ${e.message}")
        }
    }
}

class SocketHandler(
    private val host: String,
    private val port: Int
) : ILogHandler {
    override fun handle(text: String) {
        try {
            java.net.Socket(host, port).use { socket ->
                socket.getOutputStream().write("$text\n".toByteArray())
            }
        } catch (e: Exception) {
        }
    }
}

class ConsoleHandler : ILogHandler {
    override fun handle(text: String) {
        println(text)
    }
}

class SyslogHandler(
    private val host: String = "localhost",
    private val port: Int = 514,
    private val facility: Int = 1
) : ILogHandler {
    override fun handle(text: String) {
        try {
            java.net.DatagramSocket().use { socket ->
                val message = "<${facility * 8 + 6}>$text"
                val bytes = message.toByteArray()
                val address = java.net.InetAddress.getByName(host)
                val packet = java.net.DatagramPacket(bytes, bytes.size, address, port)
                socket.send(packet)
            }
        } catch (e: Exception) {
            System.err.println("SyslogHandler error: ${e.message}")
        }
    }
}

class Logger(
    private val filters: List<ILogFilter>,
    private val handlers: List<ILogHandler>
) {
    fun log(text: String) {
        if (filters.all { it.match(text) }) {
            handlers.forEach { it.handle(text) }
        }
    }
}

fun main() {
    val levelFilter = SimpleLogFilter("2")
    val regFilter = ReLogFilter(Regex(""".*\d.*"""))

    val file = FileHandler("log.txt")
    val console = ConsoleHandler()
    val socket = SocketHandler("localhost", 10514)
    val syslog = SyslogHandler()

    val logger = Logger(listOf(levelFilter, regFilter), listOf(file, console, socket, syslog))
    logger.log("2")
    logger.log("Jetpack Compose")
    logger.log("Android 8")
}