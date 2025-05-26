package implementations

import interfaces.Interface1
import interfaces.Interface3

class Class3Debug(val dependency: Interface1) : Interface3 {
    override fun doWork() {
        println("Class3Debug using:")
        dependency.doWork()
    }
}