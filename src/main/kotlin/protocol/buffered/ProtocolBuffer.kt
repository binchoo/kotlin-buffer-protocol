package protocol.buffered

import protocol.Protocol
import java.lang.IllegalStateException
import java.nio.Buffer
import java.nio.ByteBuffer
import java.nio.ByteOrder

class ProtocolBuffer(private val byteBuffer: ByteBuffer, private val protocol: Protocol) {

    init {
        byteBuffer.rewind()
    }

    private lateinit var componentBuffer: Buffer

    fun allocComponentBuffer() {
        val componentByteBuffer = byteBuffer.slice()
            .order(currentComponentOrder())
            .limit(currentComponentSize())

        componentBuffer = protocol.getCurrentComponent()
            .typedBuffer(componentByteBuffer)
    }

    fun get(): Any {
        return protocol.getCurrentComponent().get(componentBuffer)
    }

    fun getBuffered(): Buffer {
        return componentBuffer.slice()
    }

    fun headToNextComponent() {
        protocol.headToNextComponent()
        if (byteBuffer.remaining() >= currentComponentSize())
            byteBuffer.position(byteBuffer.position() + currentComponentSize())
    }

    fun hasBytesRemaining(): Boolean {
        val bytesRemaining = byteBuffer.remaining()
        return (bytesRemaining > 0)
                && (bytesRemaining >= currentComponentSize())
    }

    fun hasComponentBytesRemaining(): Boolean {
        return componentBuffer.hasRemaining()
    }

    fun rewind() {
        byteBuffer.rewind()
        protocol.headToComponent(0)
    }

    fun currentComponentDataCount(): Int {
        return protocol.getCurrentComponent().count
    }

    fun currentComponentIndex(): Int {
        return protocol.getCurrentComponentIndex()
    }

    fun currentComponentPrimitiveSize(): Int {
        return protocol.getCurrentComponent().primitiveSize
    }

    fun currentComponentOrder(): ByteOrder {
        return protocol.getCurrentComponent().order
    }

    fun currentComponentSize(): Int {
        return protocol.getCurrentComponent().size
    }

    fun changeComponentDataCount(componentIndex: Int, count: Int) {
        val IsItLazy = protocol.getComponent(componentIndex).hasLazyCount

        if (!IsItLazy)
            throw IllegalStateException()

        protocol.changeComponentNumber(componentIndex, count)
    }
}