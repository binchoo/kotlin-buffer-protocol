package com.corndog.dataprotocol

import dataprotocol.DataProtocol
import dataprotocol.buffered.ProtocolBuffer
import dataprotocol.buffered.ProtocolBufferReader
import org.junit.Test

class ProtocolBufferReaderTest {

    val protocol = DataProtocol.Builder().bytes(2).chars(2).shorts(2).ints(2).doubles(2).build()
    val rawBuffer = ByteBufferCompat.allocate(protocol)
    val protoBuff: ProtocolBuffer
    val reader: ProtocolBufferReader

    init {
        rawBuffer.put(1).put(2)
        rawBuffer.putChar('a').putChar('b')
        rawBuffer.putShort(3).putShort(4)
        rawBuffer.putInt(5).putInt(6)
        rawBuffer.putDouble(7.0).putDouble(8.0)

        protoBuff = ProtocolBuffer(rawBuffer, protocol)

        reader = MyProtocolReader(protoBuff)
    }

    @Test
    fun protoReader_readByData() {
        reader.readByData()
    }

    inner class MyProtocolReader(probuf: ProtocolBuffer): ProtocolBufferReader(probuf) {
        override fun onHandlerSetup() {
            addByteHandler { data, handlingHint ->
                println("$handlingHint this is byte! $data")
            }

            addCharHandler { data, handlingHint ->
                println("$handlingHint this is char! $data")
            }

            addShortHandler { data, handlingHint ->
                println("$handlingHint this is short! $data")
            }

            addIntHandler { data, handlingHint ->
                println("$handlingHint this is integer! $data")
            }

            addDoubleHandler { data, handlingHint ->
                println("$handlingHint this is double! $data")
            }
        }
    }
}