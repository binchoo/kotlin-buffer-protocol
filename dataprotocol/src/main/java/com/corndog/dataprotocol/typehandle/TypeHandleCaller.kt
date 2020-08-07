package dataprotocol.typehandle

import java.util.*

interface TypeHandleCaller {
    val typeHandlerTable: Hashtable<Class<*>, TypeHandler<*>?>

    fun <T: Any> callHandler(typedData: T, handlingHint: Int)
    fun <K: Class<*>> addHandler(targetType: K, handler: TypeHandler<*>)
}

class TypeHandleCallerImpl: TypeHandleCaller {
    override val typeHandlerTable = Hashtable<Class<*>, TypeHandler<*>?>()

    override fun <T: Any> callHandler(typedData: T, handlingHint: Int) {
        val itsClass = typedData::class.java
        val superClass = itsClass.superclass
        val handler = typeHandlerTable[itsClass]
            ?: typeHandlerTable[superClass]
        //TODO: Avoid type conversion from parent to child type.
        handler?.also {
            (it as TypeHandler<T>).handle(typedData, handlingHint)
        }
    }

    override fun <K: Class<*>> addHandler(targetType: K, handler: TypeHandler<*>) {
        typeHandlerTable[targetType] = handler
    }
}