package protocol.buffered

import protocol.DataProtocolImpl
import java.lang.IllegalStateException
import java.nio.Buffer
import java.nio.ByteBuffer
import java.nio.ByteOrder

class ProtocolBuffer(private val byteBuffer: ByteBuffer, private val dataProtocolImpl: DataProtocolImpl) {

    init {
        byteBuffer.rewind()
    }

    private lateinit var componentBuffer: Buffer

    fun currentComponentDataCount(): Int {
        return dataProtocolImpl.getCurrentComponent().count
    }

    fun currentComponentIndex(): Int {
        return dataProtocolImpl.getCurrentComponentIndex()
    }

    fun currentComponentPrimitiveSize(): Int {
        return dataProtocolImpl.getCurrentComponent().primitiveSize
    }

    fun currentComponentOrder(): ByteOrder {
        return dataProtocolImpl.getCurrentComponent().order
    }

    fun currentComponentSize(): Int {
        return dataProtocolImpl.getCurrentComponent().size
    }

    fun allocateComponentBuffer() {
        val componentByteBuffer = byteBuffer.slice()
            .order(currentComponentOrder())
            .limit(currentComponentSize())

        componentBuffer = dataProtocolImpl.getCurrentComponent()
            .typedBuffer(componentByteBuffer)
    }

    fun get(): Any {
        return dataProtocolImpl.getCurrentComponent().get(componentBuffer)
    }

    fun changeComponentDataCount(componentIndex: Int, count: Int) {
        val IsItLazy = dataProtocolImpl.getComponent(componentIndex).hasLazyCount

        if (!IsItLazy)
            throw IllegalStateException()

        dataProtocolImpl.changeComponentNumber(componentIndex, count)
    }

    fun headToNextComponent() {
        dataProtocolImpl.headToNextComponent()
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
        dataProtocolImpl.headToComponent(0)
    }
}