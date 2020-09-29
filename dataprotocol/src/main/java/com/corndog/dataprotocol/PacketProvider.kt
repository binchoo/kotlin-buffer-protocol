package com.corndog.dataprotocol

import java.io.InputStream
import java.nio.ByteBuffer

interface PacketProvider {
    fun provide(size: Int = -1): ByteBuffer

    fun available(size: Int = -1): Boolean
}