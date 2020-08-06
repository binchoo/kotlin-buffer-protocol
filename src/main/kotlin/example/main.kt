package example

import protocol.buffered.ProtocolBuffer
import protocol.buffered.ProtocolBufferReader
import protocol.buffered.data.DataProtocol
import java.nio.ByteBuffer

fun main() {
    val rawBuffer = ByteBuffer.allocate(10000)
    val protocol = DataProtocol().bytes(1).bytes(2).shorts(512)

    val pbuffer = ProtocolBuffer(rawBuffer, protocol)
    val protocolBufferReader = MyProtocolBufferReader(pbuffer)

    while (protocolBufferReader.hasRemaining()) {
        protocolBufferReader.read()
    }
}

fun testBufferImage() {
    val image = DataProtocol().bytes(1).bytes(1).shorts(10).commit()

    assert(image.isCommitted)
    assert(image.head == 0)
    assert(image.components.size == 3)
    assert(image.size == 12)

    image.headTo(3)
    assert(image.head == 3)
    assert(image.getCurrentComponentIndex() == 2)

    image.headToNextComponent()
    assert(image.head == 0)
    assert(image.getCurrentComponentIndex() == 0)

    image.headToComponent(1)
    assert(image.head == 1)
    assert(image.getCurrentComponent().num == 1)
    assert(image.getCurrentComponent().primitive_sz == 1)
}