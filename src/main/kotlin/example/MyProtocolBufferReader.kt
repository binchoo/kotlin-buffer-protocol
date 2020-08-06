package example

import protocol.buffered.ProtocolBuffer
import protocol.buffered.ProtocolBufferReader
import protocol.buffered.data.DataProtocol
import protocol.typehandle.ByteHandler
import protocol.typehandle.ShortHandler
import java.nio.ByteBuffer

class MyProtocolBufferReader(pbuffer: ProtocolBuffer)
    : ProtocolBufferReader(pbuffer) {

    var changeSize = 3
    override fun onHandlerSetup() {
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