package protocol.data

import java.util.*

open class TypedExecutor {

    val typedDataHandlerTable = Hashtable<Class<*>, DataHandler<*>?>()

    fun <T: Any> execute(data: T, componentIndex: Int) {
        val itsClass = data::class.java
        val handler = typedDataHandlerTable[itsClass]

        handler?.also {
            (it as DataHandler<T>).handle(data, componentIndex)
        }
    }

    fun addByteHandler(handler: DataHandler<Byte>) {
        typedDataHandlerTable[Primitive.Byte] = handler
    }

    fun addCharHandler(handler: DataHandler<Char>) {
        typedDataHandlerTable[Primitive.Char] = handler
    }

    fun addShortHandler(handler: DataHandler<Short>) {
        typedDataHandlerTable[Primitive.Short] = handler
    }

    fun addIntHandler(handler: DataHandler<Int>) {
        typedDataHandlerTable[Primitive.Int] = handler
    }

    fun addFloatHandler(handler: DataHandler<Float>) {
        typedDataHandlerTable[Primitive.Float] = handler
    }

    fun addDoubleHandler(handler: DataHandler<Double>) {
        typedDataHandlerTable[Primitive.Double] = handler
    }
}