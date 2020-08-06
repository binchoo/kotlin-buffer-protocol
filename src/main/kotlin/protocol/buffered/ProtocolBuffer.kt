package protocol.buffered

import protocol.buffered.data.DataProtocol
import java.lang.IllegalStateException
import java.nio.ByteBuffer
import java.nio.ByteOrder

class ProtocolBuffer(private val byteBuffer: ByteBuffer, private val dataProtocol: DataProtocol) {

    init {
        byteBuffer.rewind()
        dataProtocol.commit()
    }

    fun currentComponentDataCount(): Int {
        return dataProtocol.getCurrentComponent().num
    }

    fun currentComponentIndex(): Int {
        return dataProtocol.getCurrentComponentIndex()
    }

    fun currentComponentPrimitive(): Class<*> {
        return dataProtocol.getCurrentComponent().primitive
    }

    fun currentComponentPrimitiveSize(): Int {
        return dataProtocol.getCurrentComponent().primitive_sz
    }

    fun currentComponentOrder(): ByteOrder {
        return dataProtocol.getCurrentComponent().order
    }

    fun currentComponentSize(): Int {
        return dataProtocol.getCurrentComponent().sz
    }

    fun currentComponentBuffer(): ByteBuffer {
        return byteBuffer.slice()
            .order(currentComponentOrder())
            .limit(currentComponentSize())
    }

    fun changeComponentDataCount(componentIndex: Int, count: Int) {
        val IsItLazy = dataProtocol.getComponent(componentIndex).num_is_lazy

        if (!IsItLazy)
            throw IllegalStateException()

        dataProtocol.changeComponentNumber(componentIndex, count)
        dataProtocol.commit()
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

    fun rewind() {
        byteBuffer.rewind()
        dataProtocol.headToComponent(0)
    }
}