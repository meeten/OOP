interface IPropertyChangedListener {
    fun onPropertyChanged(obj: Any, propertyName: String)
}

interface INotifyDataChanged {
    fun addPropertyChangedListener(listener: IPropertyChangedListener)
    fun removePropertyChangedListener(listener: IPropertyChangedListener)
}

interface IPropertyChangingListener {
    fun onPropertyChanging(obj: Any, propertyName: String, oldValue: Any?, newValue: Any?): Boolean
}

interface INotifyDataChanging {
    fun addPropertyChangingListener(listener: IPropertyChangingListener)
    fun removePropertyChangingListener(listener: IPropertyChangingListener)
}

open class ObservableEntity : INotifyDataChanged, INotifyDataChanging {
    private val propertyChangedListeners = mutableListOf<IPropertyChangedListener>()
    private val propertyChangingListeners = mutableListOf<IPropertyChangingListener>()

    override fun addPropertyChangedListener(listener: IPropertyChangedListener) {
        propertyChangedListeners.add(listener)
    }

    override fun removePropertyChangedListener(listener: IPropertyChangedListener) {
        propertyChangedListeners.remove(listener)
    }

    override fun addPropertyChangingListener(listener: IPropertyChangingListener) {
        propertyChangingListeners.add(listener)
    }

    override fun removePropertyChangingListener(listener: IPropertyChangingListener) {
        propertyChangingListeners.remove(listener)
    }

    protected fun <T> setProperty(propertyName: String, oldValue: T, newValue: T, field: MutableProperty<T>) {
        if (oldValue == newValue) return

        val changeAllowed = propertyChangingListeners.all { listener ->
            listener.onPropertyChanging(this, propertyName, oldValue, newValue)
        }

        if (!changeAllowed) {
            println("Изменение свойства '$propertyName' отклонено")
            return
        }

        field.value = newValue

        propertyChangedListeners.forEach { listener ->
            listener.onPropertyChanged(this, propertyName)
        }
    }
}

class MutableProperty<T>(var value: T)

class Person() : ObservableEntity() {
    private val _name = MutableProperty("")
    private val _age = MutableProperty(0)

    var name: String
        get() = _name.value
        set(value) = setProperty("name", _name.value, value, _name)

    var age: Int
        get() = _age.value
        set(value) = setProperty("age", _age.value, value, _age)

    override fun toString(): String {
        return "Имя: $name, Возраст: $age"
    }

}

fun main() {
    val person = Person()

    val changeLogger = object : IPropertyChangedListener {
        override fun onPropertyChanged(obj: Any, propertyName: String) {
            println("Свойство $propertyName изменилось в объекте $obj")
        }
    }

    val nameValidator = object : IPropertyChangingListener {
        override fun onPropertyChanging(
            obj: Any,
            propertyName: String,
            oldValue: Any?,
            newValue: Any?
        ): Boolean {
            if (propertyName == "name" && newValue is String) {
                if (newValue.length > 50) {
                    println("Имя не должно превышать 50 символов")
                    return false
                }

                if (newValue.any { !it.isLetter() }) {
                    println("Имя не должно содержать цифр")
                    return false
                }
            }
            return true
        }
    }

    val ageValidator = object : IPropertyChangingListener {
        override fun onPropertyChanging(
            obj: Any,
            propertyName: String,
            oldValue: Any?,
            newValue: Any?
        ): Boolean {
            if (propertyName == "age" && newValue is Int) {
                if (newValue < 0) {
                    println("Возраст не должен быть отрицательным")
                    return false
                }

                if (newValue > 150) {
                    println("Возраст не может быть больше 150")
                    return false
                }
            }

            return true
        }
    }

    person.addPropertyChangedListener(changeLogger)
    person.addPropertyChangingListener(nameValidator)
    person.addPropertyChangingListener(ageValidator)

    person.name = "Илья"
    person.age = 20
    println(person)


    person.name = "Илья123"
    person.age = 21
    println(person)

    person.age = 2000
    println(person)
}