import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.collections.sorted

@Serializable
data class User(
    var id: Int,
    var name: String,
    var login: String,
    @Transient val password: String = "",
    var email: String? = null,
    val address: String? = null
) : Comparable<User> {
    override fun compareTo(other: User): Int = this.name.compareTo(other.name)

    fun checkPassword(inputPassword: String): Boolean = password == inputPassword

    override fun toString(): String {
        return "User(id=$id, name='$name', login='$login', email=$email, address=$address)"
    }
}

interface IDataRepository<T> {
    fun getAll(): List<T>
    fun getById(id: Int): T?
    fun add(item: T)
    fun update(item: T)
    fun delete(item: T)
}

interface IUserRepository : IDataRepository<User> {
    fun getByLogin(login: String): User?
}

class DataRepository<T : Any>(
    private val filePath: String,
    private val serializer: KSerializer<List<T>>
) : IDataRepository<T> {
    private val file = File(filePath)

    private fun readData(): MutableList<T> {
        return if (file.exists()) {
            val json = file.readText()
            Json.decodeFromString(serializer, json).toMutableList()
        } else {
            mutableListOf()
        }
    }

    private fun writeData(data: List<T>) {
        val json = Json.encodeToString(serializer, data)
        file.writeText(json)
    }

    override fun getAll(): List<T> = readData()

    override fun getById(id: Int): T? {
        val data = readData()
        return data.find { it is User && it.id == id }
    }

    override fun add(item: T) {
        val data = readData()
        data.add(item)
        writeData(data)
    }

    override fun update(item: T) {
        val data = readData()
        if (item is User) {
            val index = data.indexOfFirst { (it as User).id == item.id }
            if (index != -1) {
                data[index] = item
                writeData(data)
            }
        } else {
            throw UnsupportedOperationException("Unsupported type")
        }
    }

    override fun delete(item: T) {
        val data = readData()
        data.remove(item)
        writeData(data)
    }
}

class UserRepository(filePath: String) : IUserRepository {
    private val dataRepository = DataRepository(filePath, ListSerializer(User.serializer()))

    override fun getAll(): List<User> = dataRepository.getAll().sorted()

    override fun getById(id: Int): User? = dataRepository.getById(id)

    override fun getByLogin(login: String): User? {
        return dataRepository.getAll().find { it.login == login }
    }

    override fun add(item: User) = dataRepository.add(item)

    override fun update(item: User) = dataRepository.update(item)

    override fun delete(item: User) = dataRepository.delete(item)
}

interface IAuthService {
    fun signIn(user: User) // вход
    fun signOut() // выход
    val isAuthorized: Boolean // авторизован?
    val currentUser: User? // текущие данные пользователя
}

class AuthService(
    private val userRepository: IUserRepository,
    private val authFilePath: String = "auth_data.json"
) : IAuthService {
    private val authFile = File(authFilePath)
    override var currentUser: User? = null
        private set

    init {
        autoSignIn()
    }

    override val isAuthorized: Boolean
        get() = currentUser != null

    private fun autoSignIn() {
        if (authFile.exists()) {
            val json = authFile.readText()
            val login = Json.decodeFromString<String>(json)
            currentUser = userRepository.getByLogin(login)
        }
    }

    override fun signIn(user: User) {
        currentUser = user
        val json = Json.encodeToString(user.login)
        authFile.writeText(json)
    }

    override fun signOut() {
        currentUser = null
        authFile.delete()
    }
}

fun main() {
    val userRepository = UserRepository("users.json")

    val authService = AuthService(userRepository)

    val user1 = User(1, "Ilya", "ilya", "qwerty", "ilyamitin2005@gmail.com")
    var user2 = User(2, "Bob", "bob", "12345", "bob@gmail.com")

    userRepository.add(user1)
    userRepository.add(user2)

    user2 = user2.copy(name = "bob")
    userRepository.update(user2)

    authService.signIn(user1)
    println("авторизован: ${authService.isAuthorized}")
    println("данные текущего пользователя: ${authService.currentUser}")

    authService.signIn(user2)
    println("авторизован: ${authService.isAuthorized}")
    println("данные текущего пользователя: ${authService.currentUser}")

    val newAuthService = AuthService(userRepository)
    println("авторизован: ${authService.isAuthorized}")
    println("данные текущего пользователя: ${authService.currentUser}")
}