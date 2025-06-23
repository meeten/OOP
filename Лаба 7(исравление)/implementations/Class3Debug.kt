package implementations

import interfaces.Interface1
import interfaces.Interface3

class Class3Debug : Interface3 {
    override fun operation3(data: String) {
        println("Debug Operation3 with data: $data")
    }
}