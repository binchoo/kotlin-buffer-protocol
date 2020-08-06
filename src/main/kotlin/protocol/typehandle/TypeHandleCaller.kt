package protocol.typehandle

import java.util.*

interface TypeHandleCaller {
    val typeHandlerTable: Hashtable<Class<*>, TypeHandler<*>?>

    fun <T: Any> execute(typedData: T, handlingHint: Int)
    fun <K: Class<*>> addHandler(targetType: K, handler: TypeHandler<*>)
}

class TypeHandleCallerImpl: TypeHandleCaller {
    override val typeHandlerTable = Hashtable<Class<*>, TypeHandler<*>?>()

    override fun <T: Any> execute(typedData: T, handlingHint: Int) {
        val itsClass = typedData::class.java
        val handler = typeHandlerTable[itsClass]

        handler?.also {
            (it as TypeHandler<T>).handle(typedData, handlingHint)
        }
    }

    override fun <K: Class<*>> addHandler(targetType: K, handler: TypeHandler<*>) {
        typeHandlerTable[targetType] = handler
    }
}