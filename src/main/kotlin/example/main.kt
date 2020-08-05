package example

import protocol.buffer.BufferComponent
import protocol.buffer.BufferDescriptor
import java.nio.ByteBuffer

fun main() {
    val rawBuffer = ByteBuffer.allocate(32)
    val bufferDescription = BufferDescriptor().bytes(5).bytes(1).shorts(BufferComponent.NUM_LAZY).commit()
    val protocol = MyDataProtocol(rawBuffer, bufferDescription)

    protocol.setup()
    while (protocol.hasRemaining()) {
        protocol.proceed()
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