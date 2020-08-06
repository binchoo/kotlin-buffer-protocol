package protocol.buffered

import protocol.buffered.data.DataProtocol
import java.lang.IllegalStateException
import java.nio.Buffer
import java.nio.ByteBuffer
import java.nio.ByteOrder

class ProtocolBuffer(private val byteBuffer: ByteBuffer, private val dataProtocol: DataProtocol) {

    init {
        byteBuffer.rewind()
    }

    private lateinit var componentBuffer: Buffer

    fun currentComponentDataCount(): Int {
        return dataProtocol.getCurrentComponent().count
    }

    fun currentComponentIndex(): Int {
        return dataProtocol.getCurrentComponentIndex()
    }

    fun currentComponentPrimitiveSize(): Int {
        return dataProtocol.getCurrentComponent().primitiveSize
    }

    fun currentComponentOrder(): ByteOrder {
        return dataProtocol.getCurrentComponent().order
    }

    fun currentComponentSize(): Int {
        return dataProtocol.getCurrentComponent().size
    }

    fun allocateComponentBuffer() {
        val componentByteBuffer = byteBuffer.slice()
            .order(currentComponentOrder())
            .limit(currentComponentSize())

        componentBuffer = dataProtocol.getCurrentComponent()
            .typedBuffer(componentByteBuffer)
    }
    fun get(): Any {
        return dataProtocol.getCurrentComponent().get(componentBuffer)!!
    }

    fun changeComponentDataCount(componentIndex: Int, count: Int) {
        val IsItLazy = dataProtocol.getComponent(componentIndex).hasLazyCount

        if (!IsItLazy)
            throw IllegalStateException()

        dataProtocol.changeComponentNumber(componentIndex, count)
    }

    fun headToNextComponent() {
        dataProtocol.headToNextComponent()
        if (byteBuffer.remaining() >= currentComponentSize())
            byteBuffer.position(byteBuffer.position() + currentComponentSize())
    }

    fun hasRemaining(): Boolean {
        val bytesRemaining = byteBuffer.remaining()
        return (bytesRemaining > 0)
                && (bytesRemaining >= currentComponentSize())
    }

    fun hasComponentRemaining(): Boolean {
        return componentBuffer.hasRemaining()
    }

    fun rewind() {
        byteBuffer.rewind()
        dataProtocol.headToComponent(0)
    }
}