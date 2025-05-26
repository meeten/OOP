package config

import di.Injector
import di.LifeStyle
import interfaces.*
import implementations.*

fun configureDebug(injector: Injector) {
    injector.register(Interface1::class, Class1Debug::class, LifeStyle.Singleton)
    injector.register(Interface2::class, Class2Debug::class, LifeStyle.PerRequest)
    injector.register(Interface3::class, Class3Debug::class, LifeStyle.Scoped)
}
