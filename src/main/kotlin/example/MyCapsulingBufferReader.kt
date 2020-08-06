package example

import protocol.buffered.ProtocolBuffer
import protocol.buffered.ProtocolBufferReader
import protocol.DataProtocol
import protocol.typehandle.ByteHandler
import protocol.typehandle.IntHandler
import protocol.typehandle.TypeHandler
import java.nio.ByteBuffer
import java.nio.IntBuffer

class MyCapsulingBufferReader(rawBuffer: ByteBuffer)
    : ProtocolBufferReader(ProtocolBuffer(rawBuffer, MY_DATA_PROTOCOL)) {

    companion object {
        val MY_DATA_PROTOCOL = DataProtocol()
            .bytes(1).bytes(2).ints(DataProtocol.DECLARE_LAZY_COUNT)
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
        addHandler(Class.forName("java.nio.IntBuffer"), object: TypeHandler<IntBuffer> {
            override fun handle(data: IntBuffer, handlingHint: Int) {
                println("this is int buffer! ${data.remaining()}")
            }
        })

        addHandler(Class.forName("java.nio.ByteBuffer"), object: TypeHandler<ByteBuffer> {
            override fun handle(data: ByteBuffer, handlingHint: Int) {
                println("this is byte buffer! ${data.remaining()}")
            }
        })
    }
}