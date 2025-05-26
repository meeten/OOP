import config.configureDebug
import di.Injector
import interfaces.*

fun main() {
    val injector = Injector()
    configureDebug(injector)

    val singleton1 = injector.getInstance(Interface1::class)
    val singleton2 = injector.getInstance(Interface1::class)
    println("Singleton equal: ${singleton1 === singleton2}")

    val perRequest1 = injector.getInstance(Interface2::class)
    val perRequest2 = injector.getInstance(Interface2::class)
    println("PerRequest different: ${perRequest1 !== perRequest2}")

    injector.scoped {
        val scoped1 = injector.getInstance(Interface3::class)
        val scoped2 = injector.getInstance(Interface3::class)
        println("Scoped same: ${scoped1 === scoped2}")
        scoped1.doWork()
    }
}
