package implementations

import interfaces.Interface1
import interfaces.Interface2

class Class2Release(private val dependency: Interface1) : Interface2 {
    override fun operation2(): Int {
        println("Release Operation2 with ${dependency.operation1()}")
        return 100
    }
}