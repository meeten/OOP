package config

import di.Injector
import di.LifeStyle
import interfaces.*
import implementations.*

fun configureRelease(injector: Injector) {
        injector.register(Interface1::class, Class1Release::class, LifeStyle.Singleton)
        injector.register(Interface2::class, Class2Release::class, LifeStyle.Singleton)
        injector.register(Interface3::class, Class3Release::class, LifeStyle.PerRequest)
}