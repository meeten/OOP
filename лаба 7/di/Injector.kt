package di

import implementations.Class1Debug
import kotlin.reflect.KClass

typealias Factory<T> = () -> T

class Injector {
    private data class Registration(
        val factory: () -> Any,
        val lifeStyle: LifeStyle
    )

    private val registrations = mutableMapOf<KClass<*>, Registration>()
    private val singletons = mutableMapOf<KClass<*>, Any>()
    private val scopedInstances = ThreadLocal<MutableMap<KClass<*>, Any>?>()

   fun <T : Any, U : T> register(
        interfaceType: KClass<T>,
            classType: KClass<U>,
        lifeStyle: LifeStyle,
        params: List<Any> = emptyList()
    ) {
        val factory = {
            val constructor = classType.constructors.first()
            val args = constructor.parameters.map { param ->
                val paramType = param.type.classifier as? KClass<*>
                params.find { paramType?.isInstance(it) == true }
                    ?: getInstance(paramType!!)
            }
            constructor.call(*args.toTypedArray())
        }
        registrations[interfaceType] = Registration(factory, lifeStyle)
    }

    fun <T : Any> getInstance(interfaceType: KClass<T>): T {
        val registration = registrations[interfaceType]
            ?: throw IllegalArgumentException("No registration for ${interfaceType.simpleName}")

        return when (registration.lifeStyle) {
            LifeStyle.PerRequest -> registration.factory() as T
            LifeStyle.Singleton -> synchronized(singletons) {
                singletons.getOrPut(interfaceType) { registration.factory() } as T
            }
            LifeStyle.Scoped -> {
                val scope = scopedInstances.get() ?: throw IllegalStateException("No active scope")
                @Suppress("UNCHECKED_CAST")
                scope.getOrPut(interfaceType) { registration.factory() } as T
            }
        }
    }

    fun <T> scoped(block: () -> T): T {
        scopedInstances.set(mutableMapOf())
        return try {
            block()
        } finally {
            scopedInstances.remove()
        }
    }
}
