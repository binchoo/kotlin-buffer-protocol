package protocol

import protocol.typehandle.TypeHandler
import java.util.*

interface TypedExecutor {
    val typeHandlerTable: Hashtable<Class<*>, TypeHandler<*>?>

    fun <T: Any> execute(typedData: T, executionHint: Int)
    fun <K: Class<*>> addHandler(targetType: K, handler: TypeHandler<*>)
}

class TypedExecutorImpl: TypedExecutor {
    override val typeHandlerTable = Hashtable<Class<*>, TypeHandler<*>?>()

    override fun <T: Any> execute(typedData: T, executionHint: Int) {
        val itsClass = typedData::class.java
        val handler = typeHandlerTable[itsClass]

        handler?.also {
            (it as TypeHandler<T>).handle(typedData, executionHint)
        }
    }

    override fun <K: Class<*>> addHandler(targetType: K, handler: TypeHandler<*>) {
        typeHandlerTable[targetType] = handler
    }
}