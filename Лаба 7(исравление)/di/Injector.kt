package di

import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

typealias Factory<T> = () -> T

class Injector {
    private data class Registration<T : Any>(
        val factory: () -> T,
        val lifeStyle: LifeStyle
    )

    private val registrations = mutableMapOf<KClass<*>, Registration<*>>()
    private val singletons = mutableMapOf<KClass<*>, Any>()
    private val scopedInstances = ThreadLocal<MutableMap<KClass<*>, Any>>()

    fun <T : Any, U : T> register(
        interfaceType: KClass<T>,
        classType: KClass<U>,
        lifeStyle: LifeStyle,
        params: Map<String, Any> = emptyMap()
    ) {
        val factory = {
            val constructor = classType.primaryConstructor
                ?: throw IllegalArgumentException("Class ${classType.simpleName} must have a primary constructor")

            val args = constructor.parameters.associateWith { param ->
                params[param.name] ?: getInstance(param.type.classifier as KClass<*>)
            }

            constructor.callBy(args)
        }

        registrations[interfaceType] = Registration(factory, lifeStyle)
    }

    fun <T : Any> registerFactory(
        interfaceType: KClass<T>,
        factory: Factory<T>,
        lifeStyle: LifeStyle = LifeStyle.PerRequest
    ) {
        registrations[interfaceType] = Registration(factory, lifeStyle)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getInstance(interfaceType: KClass<T>): T {
        val registration = registrations[interfaceType] as? Registration<T>
            ?: throw IllegalArgumentException("No registration for ${interfaceType.simpleName}")

        return when (registration.lifeStyle) {
            LifeStyle.PerRequest -> registration.factory()
            LifeStyle.Singleton -> synchronized(singletons) {
                singletons.getOrPut(interfaceType) { registration.factory() } as T
            }
            LifeStyle.Scoped -> {
                val scope = scopedInstances.get() ?: throw IllegalStateException("No active scope")
                scope.getOrPut(interfaceType) { registration.factory() } as T
            }
        }
    }

    fun <T> scoped(block: Injector.() -> T): T {
        try {
            scopedInstances.set(mutableMapOf())
            return this.block()
        } finally {
            scopedInstances.remove()
        }
    }
}

