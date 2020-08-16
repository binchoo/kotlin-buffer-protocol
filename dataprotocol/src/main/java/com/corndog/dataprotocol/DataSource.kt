package com.corndog.dataprotocol

import com.corndog.dataprotocol.source.ByteBufferSource
import com.corndog.dataprotocol.source.InputStreamSource
import java.io.InputStream
import java.nio.ByteBuffer

class DataSource private constructor(private val packetProvider: PacketProvider) {

    companion object {
        fun fromPacket(pp: PacketProvider): DataSource {
            return DataSource(pp)
        }

        fun from(inputStream: InputStream): DataSource {
            return fromPacket(InputStreamSource(inputStream))
        }

        fun from(byteBuffer: ByteBuffer): DataSource {
            return fromPacket(ByteBufferSource(byteBuffer))
        }
    }
}