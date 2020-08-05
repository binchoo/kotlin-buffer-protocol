package example

import protocol.BufferedProtocol
import protocol.buffer.BufferDescriptor
import protocol.typehandle.ByteHandler
import protocol.typehandle.ShortHandler
import java.nio.ByteBuffer

class MyProtocol(buffer: ByteBuffer, descriptor: BufferDescriptor)
    : BufferedProtocol(buffer, descriptor) {

    var changeSize = 3
    override fun setup() {
        super.setup()

        addByteHandler(object: ByteHandler {
            override fun handle(data: Byte, hintValue: Int) {
                if (hintValue == 0) {
                    println("this is first header")
                    bufferDescriptor.changeComponentNumber(1, changeSize++)
                    bufferDescriptor.commit()
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