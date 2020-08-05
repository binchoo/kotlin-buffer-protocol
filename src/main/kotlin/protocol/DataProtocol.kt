package protocol

import protocol.buffer.BufferDescriptor
import protocol.data.Primitive
import protocol.data.TypedExecutor
import java.nio.ByteBuffer

interface DataProtocol {
    fun setup()
    fun proceed()
}

open class BufferDataProtocol(protected val buffer: ByteBuffer,
                              protected val bufferDescriptor: BufferDescriptor)
    : DataProtocol, TypedExecutor() {

    override fun setup() {
        buffer.position(0)
    }

    override fun proceed() {
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
}