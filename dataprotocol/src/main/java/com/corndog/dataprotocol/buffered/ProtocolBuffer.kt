package dataprotocol.buffered

import dataprotocol.DataProtocol
import dataprotocol.Protocol
import java.lang.IllegalStateException
import java.nio.ByteBuffer
import java.nio.ByteOrder

class ProtocolBuffer(
    private val byteBuffer: ByteBuffer,
    private val protocol: Protocol
): Iterator<Any> {

    private lateinit var componentBuffer: ByteBuffer

    init {
        rewind()
        allocComponentBuffer()
    }

    fun rewind() {
        byteBuffer.rewind()
        protocol.headTo(0)
    }

    override fun hasNext(): Boolean {
        return (currentComponentRemaining() > 0) || ((currentComponentRemaining() <= 0) && (hasValidNextComponent()))
    }

    private fun hasValidNextComponent(): Boolean {
        val nextComponent = protocol.getNextComponent()
        return (nextComponent.size <= byteBuffer.remaining())
    }

    override fun next(): Any {
        if (currentComponentRemaining() <= 0) {
            headToNextComponent()
            allocComponentBuffer()
        }
        val value = protocol.getCurrentComponent().fetch(componentBuffer)!!
        byteBuffer.position(byteBuffer.position() + currentComponentPrimitiveSize())
        return value
    }

    private fun headToNextComponent() {
        protocol.headToNextComponent()

        if (currentComponentDataCount() == DataProtocol.DECLARE_LAZY_COUNT)
            throw IllegalStateException("Component's data count has not been lazily initialized.")
    }

    private fun allocComponentBuffer() {
        componentBuffer = byteBuffer.slice().limit(currentComponentSize()) as ByteBuffer
    }

    fun currentComponentRemaining(): Int {
        return componentBuffer.remaining()
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
        protocol.changeComponentNumber(componentIndex, count)
    }
}