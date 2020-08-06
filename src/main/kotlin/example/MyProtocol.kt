package example

import protocol.BufferedProtocol
import protocol.buffer.DataProtocol
import protocol.typehandle.ByteHandler
import protocol.typehandle.ShortHandler
import java.nio.ByteBuffer

class MyProtocol(buffer: ByteBuffer, descriptor: DataProtocol)
    : BufferedProtocol(buffer, descriptor) {

    var changeSize = 3
    override fun setup() {
        super.setup()

        addByteHandler(object: ByteHandler {
            override fun handle(data: Byte, handlingHint: Int) {
                if (handlingHint == 0) {
                    println("this is first header")
                } else {
                    println("this is second header")
                }
            }
        })

        addShortHandler(object: ShortHandler {
            override fun handle(data: Short, handlingHint: Int) {
                println("this is short")
            }
        })

    }
}