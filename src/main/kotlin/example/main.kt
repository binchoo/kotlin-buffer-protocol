package example

import protocol.buffered.ProtocolBuffer
import protocol.DataProtocolImpl
import java.nio.ByteBuffer

fun main() {
    val rawBuffer = ByteBuffer.allocate(51)
    val capsulingBufferReader = MyCapsulingBufferReader(rawBuffer)
    while (capsulingBufferReader.hasRemaining()) {
        capsulingBufferReader.read()
    }

    println("------------------")
    rawBuffer.rewind()

    val protocol = DataProtocolImpl()
        .bytes(1).bytes(2).ints(DataProtocolImpl.NUMBER_LAZILY_SET)
    val injectedBufferReader = MyInjectedBufferReader(ProtocolBuffer(rawBuffer, protocol))
    while (injectedBufferReader.hasRemaining()) {
        injectedBufferReader.read()
    }
}