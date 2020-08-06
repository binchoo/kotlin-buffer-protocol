package protocol.buffered

import protocol.typehandle.TypeHandler
import protocol.Primitive
import protocol.typehandle.TypeHandleCaller
import protocol.typehandle.TypeHandleCallerImpl
import java.lang.NullPointerException
import java.util.*

open class ProtocolBufferReader(var protocolBuffer: ProtocolBuffer)
    : ProtocolReader, TypeHandleCaller {

    val typeHandleDelegator: TypeHandleCaller =
        TypeHandleCallerImpl()

    override val typeHandlerTable: Hashtable<Class<*>, TypeHandler<*>?>
        get() = typeHandleDelegator.typeHandlerTable

    init {
        onHandlerSetup()
    }

    open fun onHandlerSetup() {}

    override fun read() {
        assertBufferNonNull()
        val pbuffer = protocolBuffer!!
        val cbuffer = pbuffer.currentComponentBuffer()
        val cIndex = pbuffer.currentComponentIndex()

        when(pbuffer.currentComponentPrimitive()) {
            Primitive.Char-> {
                val typedBuffer = cbuffer.asCharBuffer()
                while (typedBuffer.hasRemaining())
                    execute(typedBuffer.get(), cIndex)
            }
            Primitive.Short-> {
                val typedBuffer = cbuffer.asShortBuffer()
                while (typedBuffer.hasRemaining())
                    execute(typedBuffer.get(), cIndex)
            }
            Primitive.Int-> {
                val typedBuffer = cbuffer.asIntBuffer()
                while (typedBuffer.hasRemaining())
                    execute(typedBuffer.get(), cIndex)
            }
            Primitive.Float-> {
                val typedBuffer = cbuffer.asFloatBuffer()
                while (typedBuffer.hasRemaining())
                    execute(typedBuffer.get(), cIndex)
            }
            Primitive.Double-> {
                val typedBuffer = cbuffer.asDoubleBuffer()
                while (typedBuffer.hasRemaining())
                    execute(typedBuffer.get(), cIndex)
            }
            else-> {
                val typedBuffer = cbuffer
                while (typedBuffer.hasRemaining())
                    execute(typedBuffer.get(), cIndex)
            }
        }
        pbuffer.headToNextComponent()
    }

    fun hasRemaining(): Boolean {
        return protocolBuffer?.hasRemaining() ?: false
    }

    private fun assertBufferNonNull() {
        if (protocolBuffer == null)
            throw NullPointerException()
    }

    override fun <T : Any> execute(typedData: T, executionHint: Int) {
        typeHandleDelegator.execute(typedData, executionHint)
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
}