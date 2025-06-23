package implementations

import interfaces.Interface1
import interfaces.Interface3

class Class3Release(private val config: String) : Interface3 {
    override fun operation3(data: String) {
        println("Release Operation3 with config: $config and data: $data")
    }
}