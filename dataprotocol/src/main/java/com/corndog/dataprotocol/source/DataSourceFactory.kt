package com.corndog.dataprotocol.source

import com.corndog.dataprotocol.DataSource
import java.io.InputStream
import java.nio.ByteBuffer

class DataSourceFactory {
    companion object {
        fun from(byteBuffer: ByteBuffer): DataSource {
            return DataSource.from(ByteBufferSource(byteBuffer))
        }

        fun from(inputStream: InputStream): DataSource {
            return DataSource.from(InputStreamSource(inputStream))
        }
    }
}