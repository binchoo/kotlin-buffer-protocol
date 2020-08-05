package protocol

import protocol.buffer.BufferDescription
import protocol.data.Primitive
import protocol.data.TypedExecutor
import java.nio.ByteBuffer

interface DataProtocol {
    fun setup()
    fun proceed()
}

open class BufferDataProtocol(private val buffer: ByteBuffer,
                              private val bufferDescription: BufferDescription)
    : DataProtocol, TypedExecutor() {

    override fun setup() {
        buffer.position(0)
    }

    override fun proceed() {
        val comp = bufferDescription.getCurrentComponent()
        val compBuffer = buffer.slice()
                .order(comp.order).limit(comp.sz)

        when(comp.primitive) {
            Primitive.Char-> {
                val typedBuffer = compBuffer.asCharBuffer()
                while (typedBuffer.hasRemaining())
                    execute(typedBuffer.get(), bufferDescription.getCurrentComponentIndex())
            }
            Primitive.Short-> {
                val typedBuffer = compBuffer.asShortBuffer()
                while (typedBuffer.hasRemaining())
                    execute(typedBuffer.get(), bufferDescription.getCurrentComponentIndex())
            }
            Primitive.Float-> {
                val typedBuffer = compBuffer.asFloatBuffer()
                while (typedBuffer.hasRemaining())
                    execute(typedBuffer.get(), bufferDescription.getCurrentComponentIndex())
            }
            Primitive.Double-> {
                val typedBuffer = compBuffer.asDoubleBuffer()
                while (typedBuffer.hasRemaining())
                    execute(typedBuffer.get(), bufferDescription.getCurrentComponentIndex())
            }
            else-> {
                val typedBuffer = compBuffer
                while (typedBuffer.hasRemaining())
                    execute(typedBuffer.get(), bufferDescription.getCurrentComponentIndex())
            }
        }

        buffer.position(buffer.position() + comp.sz)
        bufferDescription.headToNextComponent()
    }

    fun hasRemaining(): Boolean = buffer.hasRemaining()
}