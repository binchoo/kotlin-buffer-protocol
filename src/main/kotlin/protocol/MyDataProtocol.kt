package protocol

import protocol.buffer.BufferDescription
import protocol.data.ByteHandler
import protocol.data.ShortHandler
import java.nio.ByteBuffer

class MyDataProtocol(buffer: ByteBuffer, description: BufferDescription)
    : BufferDataProtocol(buffer, description) {

    override fun setup() {
        super.setup()

        addByteHandler(object: ByteHandler {
            override fun handle(data: Byte, hintValue: Int) {
                if (hintValue == 0) {
                    println("this is first header")
                } else {
                    println("this is second header")
                }
            }
        })

        addShortHandler(object: ShortHandler {
            override fun handle(data: Short, hintValue: Int) {
                println("this is short")
            }
        })
    }
}