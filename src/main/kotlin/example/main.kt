package example

import protocol.buffered.ProtocolBuffer
import protocol.buffered.data.DataProtocol
import java.nio.ByteBuffer

fun main() {
    val rawBuffer = ByteBuffer.allocate(51)
    val capsulingBufferReader = MyCapsulingBufferReader(rawBuffer)
    while (capsulingBufferReader.hasRemaining()) {
        capsulingBufferReader.read()
    }


    println("------------------")
    rawBuffer.rewind()

    val protocol = DataProtocol().bytes(1).bytes(2).ints(DataProtocol.NUMBER_LAZILY_SET)
    val injectedBufferReader = MyInjectedBufferReader(ProtocolBuffer(rawBuffer, protocol))
    while (injectedBufferReader.hasRemaining()) {
        injectedBufferReader.read()
    }
}

fun testBufferImage() {
    val image = DataProtocol().bytes(1).bytes(1).shorts(10).commit()

    assert(image.isCommitted)
    assert(image.components.size == 3)
    assert(image.size == 12)

    image.headTo(3)
    assert(image.getCurrentComponentIndex() == 2)

    image.headToNextComponent()
    assert(image.getCurrentComponentIndex() == 0)

    image.headToComponent(1)
    assert(image.getCurrentComponent().num == 1)
    assert(image.getCurrentComponent().primitive_sz == 1)
}