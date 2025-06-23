import config.configureDebug
import config.configureRelease
import di.Injector
import interfaces.*
import java.sql.DriverManager.println

fun main() {
    println("=== DEBUG CONFIGURATION ===")
    val debugInjector = Injector()
    configureDebug(debugInjector)
    demonstrate(debugInjector)

    println("\n=== RELEASE CONFIGURATION ===")
    val releaseInjector = Injector()
    configureRelease(releaseInjector)
    demonstrate(releaseInjector)
}

fun demonstrate(injector: Injector) {
    val singleton1 = injector.getInstance(Interface1::class)
    val singleton2 = injector.getInstance(Interface1::class)
    println("Singleton same instance: ${singleton1 === singleton2}")
    println(singleton1.operation1())

    val perRequest1 = injector.getInstance(Interface2::class)
    val perRequest2 = injector.getInstance(Interface2::class)
    println("PerRequest different instances: ${perRequest1 !== perRequest2}")
    println(perRequest1.operation2())


    injector.scoped {
        val scoped1 = getInstance(Interface3::class)
        val scoped2 = getInstance(Interface3::class)
        println("Scoped same instance: ${scoped1 === scoped2}")
        scoped1.operation3("scoped-data")

        val scopedService = getInstance(Interface2::class)
        scopedService.operation2()
    }

    try {
        val factoryCreated = injector.getInstance(Interface3::class)
        factoryCreated.operation3("factory-data")
    } catch (e: IllegalStateException) {
        println("Cannot get scoped instance outside of scope block")
    }

    injector.scoped {
        val factoryCreated = getInstance(Interface3::class)
        factoryCreated.operation3("factory-data-in-scope")
    }
}
