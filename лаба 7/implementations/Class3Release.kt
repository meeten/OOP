package implementations

import interfaces.Interface1
import interfaces.Interface3

class Class3Release(val dependency: Interface1) : Interface3 {
    override fun doWork() {
        println("Class3Release using:")
        dependency.doWork()
    }
}