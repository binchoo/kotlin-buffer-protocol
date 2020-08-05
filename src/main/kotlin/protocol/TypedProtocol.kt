package protocol

import protocol.buffer.BufferDescriptor
import protocol.typehandle.TypeHandler
import protocol.typehandle.Primitive
import java.nio.ByteBuffer
import java.util.*

interface TypedProtocol: Protocol, TypedExecutor

open class BufferedProtocol(protected val buffer: ByteBuffer,
                            protected val bufferDescriptor: BufferDescriptor)
    : TypedProtocol {

    val delegateExecutor: TypedExecutor = TypedExecutorImpl()

    override fun setup() {
        buffer.position(0)
    }

    override fun apply() {
        val comp = bufferDescriptor.getCurrentComponent()
        val compBuffer = buffer.slice()
            .order(comp.order).limit(comp.sz)

        when(comp.primitive) {
            Primitive.Char-> {
                val typedBuffer = compBuffer.asCharBuffer()
                while (typedBuffer.hasRemaining())
                    execute(typedBuffer.get(), bufferDescriptor.getCurrentComponentIndex())
            }
            Primitive.Short-> {
                val typedBuffer = compBuffer.asShortBuffer()
                while (typedBuffer.hasRemaining())
                    execute(typedBuffer.get(), bufferDescriptor.getCurrentComponentIndex())
            }
            Primitive.Float-> {
                val typedBuffer = compBuffer.asFloatBuffer()
                while (typedBuffer.hasRemaining())
                    execute(typedBuffer.get(), bufferDescriptor.getCurrentComponentIndex())
            }
            Primitive.Double-> {
                val typedBuffer = compBuffer.asDoubleBuffer()
                while (typedBuffer.hasRemaining())
                    execute(typedBuffer.get(), bufferDescriptor.getCurrentComponentIndex())
            }
            else-> {
                val typedBuffer = compBuffer
                while (typedBuffer.hasRemaining())
                    execute(typedBuffer.get(), bufferDescriptor.getCurrentComponentIndex())
            }
        }

        buffer.position(buffer.position() + comp.sz)
        bufferDescriptor.headToNextComponent()
    }

    fun hasRemaining(): Boolean {
        val bytesRemaining = buffer.remaining()
        return (bytesRemaining > 0)
                && (bytesRemaining >= bufferDescriptor.getCurrentComponent().sz)
    }

    override val typeHandlerTable: Hashtable<Class<*>, TypeHandler<*>?>
        get() = delegateExecutor.typeHandlerTable

    override fun <T : Any> execute(typedData: T, executionHint: Int) {
        delegateExecutor.execute(typedData, executionHint)
    }

    override fun <K : Class<*>> addHandler(targetType: K, handler: TypeHandler<*>) {
        delegateExecutor.addHandler(targetType, handler)
    }

    fun addByteHandler(handler: TypeHandler<Byte>) {
        addHandler(Primitive.Byte, handler)
    }

    fun addCharHandler(handler: TypeHandler<Char>) {
        addHandler(Primitive.Char, handler)
    }

    fun addShortHandler(handler: TypeHandler<Short>) {
        addHandler(Primitive.Short, handler)
    }

    fun addIntHandler(handler: TypeHandler<Int>) {
        addHandler(Primitive.Int, handler)
    }

    fun addFloatHandler(handler: TypeHandler<Float>) {
        addHandler(Primitive.Float, handler)
    }

    fun addDoubleHandler(handler: TypeHandler<Double>) {
        addHandler(Primitive.Double, handler)
    }
}