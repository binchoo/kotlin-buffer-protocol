package com.corndog.dataprotocol

import dataprotocol.Protocol
import java.nio.ByteBuffer

fun ByteBuffer?.allocate(protocol: Protocol, num: Int = 1): ByteBuffer {
    return ByteBuffer.allocate(protocol.getSize() * num)
}

val ByteBufferCompat: ByteBuffer? = null
