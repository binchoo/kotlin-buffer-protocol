package example

import protocol.buffered.ProtocolBuffer
import protocol.buffered.ProtocolBufferReader
import protocol.DataProtocolImpl
import protocol.typehandle.ByteHandler
import protocol.typehandle.IntHandler
import java.nio.ByteBuffer

class MyCapsulingBufferReader(rawBuffer: ByteBuffer)
    : ProtocolBufferReader(ProtocolBuffer(rawBuffer, MY_DATA_PROTOCOL)) {

    companion object {
        val MY_DATA_PROTOCOL = DataProtocolImpl()
            .bytes(1).bytes(2).ints(DataProtocolImpl.NUMBER_LAZILY_SET)
    }

    var changeSize = 3
    override fun onHandlerSetup() {
        addByteHandler(object: ByteHandler {
            override fun handle(data: Byte, handlingHint: Int) {
                if (handlingHint == 0) {
                    println("this is first header")
                    protocolBuffer.changeComponentDataCount(2, changeSize++)
                } else {
                    println("this is second header")
                }
            }
        })

        addIntHandler(object: IntHandler {
            override fun handle(data: Int, handlingHint: Int) {
                println("this is integer")
            }
        })
    }
}

class MyInjectedBufferReader(protocolBuffer: ProtocolBuffer):
        ProtocolBufferReader(protocolBuffer) {

    var changeSize = 3
    override fun onHandlerSetup() {
        addByteHandler(object: ByteHandler {
            override fun handle(data: Byte, handlingHint: Int) {
                if (handlingHint == 0) {
                    println("this is first header")
                    protocolBuffer.changeComponentDataCount(2, changeSize++)
                } else {
                    println("this is second header")
                }
            }
        })

        addIntHandler(object: IntHandler {
            override fun handle(data: Int, handlingHint: Int) {
                println("this is integer")
            }
        })
    }
}