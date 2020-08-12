package dataprotocol.buffered

import dataprotocol.typehandle.TypeHandler
import dataprotocol.Primitive
import dataprotocol.typehandle.TypeHandleCaller
import dataprotocol.typehandle.TypeHandleCallerImpl
import java.util.*

open class ProtocolBufferReader(var protocolBuffer: ProtocolBuffer)
    : ProtocolReader, TypeHandleCaller {

    val typeHandleDelegator: TypeHandleCaller =
        TypeHandleCallerImpl()

    override val typeHandlerTable: Hashtable<Class<*>, TypeHandler<*>?>
        get() = typeHandleDelegator.typeHandlerTable

    override fun read() {
        val pbuffer = protocolBuffer
        pbuffer.forEach {
            callHandler(it, pbuffer.currentComponentIndex())
        }
    }

    override fun <T : Any> callHandler(typedData: T, handlingHint: Int) {
        typeHandleDelegator.callHandler(typedData, handlingHint)
    }

    override fun <K : Class<*>> addHandler(targetType: K, handler: TypeHandler<*>) {
        typeHandleDelegator.addHandler(targetType, handler)
    }

    protected fun addByteHandler(handler: TypeHandler<Byte>) {
        addHandler(Primitive.Byte, handler)
    }

    protected fun addCharHandler(handler: TypeHandler<Char>) {
        addHandler(Primitive.Char, handler)
    }

    protected fun addShortHandler(handler: TypeHandler<Short>) {
        addHandler(Primitive.Short, handler)
    }

    protected fun addIntHandler(handler: TypeHandler<Int>) {
        addHandler(Primitive.Int, handler)
    }

    protected fun addFloatHandler(handler: TypeHandler<Float>) {
        addHandler(Primitive.Float, handler)
    }

    protected fun addDoubleHandler(handler: TypeHandler<Double>) {
        addHandler(Primitive.Double, handler)
    }

    protected fun addLongHandler(handler: TypeHandler<Double>) {
        addHandler(Primitive.Long, handler)
    }
}