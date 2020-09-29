package com.corndog.dataprotocol.source

import com.corndog.dataprotocol.PacketProvider
import com.corndog.dataprotocol.PacketProvider.Companion.READ_ALL
import java.nio.ByteBuffer

class ByteBufferSource(val byteBuffer: ByteBuffer): PacketProvider {
    override fun provide(size: Int): ByteBuffer {
        return if (READ_ALL == size) {
            byteBuffer
        } else {
            byteBuffer.limit(byteBuffer.position() + size) as ByteBuffer
        }.asReadOnlyBuffer()
    }

    override fun available(size: Int): Boolean {
        return byteBuffer.remaining() >= if (READ_ALL == size) 1 else size
    }
}