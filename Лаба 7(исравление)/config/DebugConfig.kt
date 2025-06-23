package config

import di.Injector
import di.LifeStyle
import interfaces.*
import implementations.*

fun configureDebug(injector: Injector) {
    injector.register(
        interfaceType = Interface1::class,
        classType = Class1Debug::class,
        lifeStyle = LifeStyle.Singleton
    )

    injector.register(
        interfaceType = Interface2::class,
        classType = Class2Debug::class,
        lifeStyle = LifeStyle.PerRequest
    )

    injector.registerFactory(
        interfaceType = Interface3::class,
        factory = { Class3Debug() },
        lifeStyle = LifeStyle.Scoped
    )
}