package example

import protocol.buffered.ProtocolBuffer
import protocol.DataProtocol
import java.nio.ByteBuffer

fun main() {
    val rawBuffer = ByteBuffer.allocate(10000)
    val capsulingBufferReader = MyCapsulingBufferReader(rawBuffer)
    while (capsulingBufferReader.hasRemaining()) {
        capsulingBufferReader.read()
    }

    println("------------------")
    rawBuffer.rewind()

    val protocol = DataProtocol()
        .bytes(1).bytes(2).ints(DataProtocol.DECLARE_LAZY_COUNT)
    val injectedBufferReader = MyInjectedBufferReader(ProtocolBuffer(rawBuffer, protocol))
    while (injectedBufferReader.hasRemaining()) {
        injectedBufferReader.readComponent()
    }
}