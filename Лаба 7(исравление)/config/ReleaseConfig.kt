package config

import di.Injector
import di.LifeStyle
import interfaces.*
import implementations.*

fun configureRelease(injector: Injector) {
    injector.register(
        interfaceType = Interface1::class,
        classType = Class1Release::class,
        lifeStyle = LifeStyle.Singleton
    )

    injector.register(
        interfaceType = Interface2::class,
        classType = Class2Release::class,
        lifeStyle = LifeStyle.PerRequest
    )

    injector.register(
        interfaceType = Interface3::class,
        classType = Class3Release::class,
        lifeStyle = LifeStyle.Scoped,
        params = mapOf("config" to "production")
    )
}