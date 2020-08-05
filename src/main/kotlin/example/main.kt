package example

import protocol.BufferedProtocol
import protocol.Protocol
import protocol.TypedProtocol
import protocol.buffer.BufferComponent
import protocol.buffer.BufferDescriptor
import java.nio.ByteBuffer

fun main() {
    val rawBuffer = ByteBuffer.allocate(10000)
    val bufferDescription = BufferDescriptor().bytes(1).bytes(2).shorts(512).commit()

    val protocol: BufferedProtocol = MyProtocol(rawBuffer, bufferDescription)

    protocol.setup()
    while (protocol.hasRemaining()) {
        protocol.apply()
    }
}

fun testBufferImage() {
    val image = BufferDescriptor().bytes(1).bytes(1).shorts(10).commit()

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