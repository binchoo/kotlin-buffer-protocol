package com.corndog.dataprotocol

import com.corndog.dataprotocol.source.ByteBufferSource
import com.corndog.dataprotocol.source.InputStreamSource
import java.io.InputStream
import java.nio.ByteBuffer

class DataSource private constructor(private val packetProvider: PacketProvider) {

    companion object {
        fun from(pp: PacketProvider): DataSource {
            return DataSource(pp)
        }
    }
}