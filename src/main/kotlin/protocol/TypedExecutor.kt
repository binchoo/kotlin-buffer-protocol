package protocol

import protocol.typehandle.TypeHandler
import java.util.*

interface TypedExecutor {
    val typedTypeHandlerTable: Hashtable<Class<*>, TypeHandler<*>?>

    fun <T: Any> execute(typedData: T, executionHint: Int)
    fun <K: Class<*>> addHandler(targetType: K, handler: TypeHandler<*>)
}

class TypedExecutorImpl: TypedExecutor {
    override val typedTypeHandlerTable = Hashtable<Class<*>, TypeHandler<*>?>()

    override fun <T: Any> execute(data: T, componentIndex: Int) {
        val itsClass = data::class.java
        val handler = typedTypeHandlerTable[itsClass]

        handler?.also {
            (it as TypeHandler<T>).handle(data, componentIndex)
        }
    }

    override fun <K: Class<*>> addHandler(targetClass: K, handler: TypeHandler<*>) {
        typedTypeHandlerTable[targetClass] = handler
    }
}