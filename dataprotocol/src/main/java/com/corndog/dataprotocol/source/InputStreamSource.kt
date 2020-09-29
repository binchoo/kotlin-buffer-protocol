package com.corndog.dataprotocol.source

import com.corndog.dataprotocol.PacketProvider
import com.corndog.dataprotocol.PacketProvider.Companion.READ_ALL
import java.io.InputStream
import java.nio.ByteBuffer

class InputStreamSource(val inputStream: InputStream): PacketProvider {
    override fun provide(size: Int): ByteBuffer {
        return ByteBuffer.wrap(
            if (READ_ALL == size)
                inputStream.readBytes()
            else
                ByteArray(size).also { inputStream.read(it) }
        ).asReadOnlyBuffer()
    }

    override fun available(size: Int): Boolean {
        return inputStream.available() >= if (READ_ALL == size) 1 else size
    }
}