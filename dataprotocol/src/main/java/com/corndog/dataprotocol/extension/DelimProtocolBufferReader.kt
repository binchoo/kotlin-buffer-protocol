package com.corndog.dataprotocol.extension

import dataprotocol.DataProtocol
import dataprotocol.buffered.ProtocolBuffer
import dataprotocol.buffered.ProtocolBufferReader
import dataprotocol.typehandle.TypeHandler
import java.nio.ByteBuffer
import java.nio.ByteOrder

class DelimProtocolBufferReader(
    val delim: String,
    buffer: ByteBuffer,
    byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN
) : ProtocolBufferReader(
    ProtocolBuffer(
        buffer,
        if (byteOrder == ByteOrder.LITTLE_ENDIAN) protocolOnlyCharacterLittleEndian
        else protocolOnlyCharacterBigEndian
    )
) {

    var stringChunkHandler: TypeHandler<String>? = null

    val stringBuilder = StringBuilder()

    override fun onHandlerSetup() {
        addCharHandler { data, handlingHint ->
            stringBuilder.append(data)
            if (!protocolBuffer.hasNext()) {
                chunkString(stringBuilder.toString()).forEach {
                    stringChunkHandler?.invoke(it, handlingHint)
                    stringBuilder.clear()
                }
            }
        }
    }

    fun addStringChunkHandler(stringHandler: TypeHandler<String>) {
        stringChunkHandler = stringHandler
    }

    private fun chunkString(str: String): List<String>{
        return str.split(delim)
    }

    companion object {
        private val protocolOnlyCharacterBigEndian =
            DataProtocol.Builder().chars(1).build()

        private val protocolOnlyCharacterLittleEndian =
            DataProtocol.Builder().chars(1, ByteOrder.LITTLE_ENDIAN).build()
    }
}