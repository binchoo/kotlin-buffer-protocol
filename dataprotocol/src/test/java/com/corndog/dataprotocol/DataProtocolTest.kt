package com.corndog.dataprotocol

import dataprotocol.DataProtocol
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class DataProtocolTest {

    val protocol = DataProtocol()
        .bytes(1).chars(1)
        .shorts(1).ints(1)
        .floats(1).doubles(1)

    val protocolLastLazy = DataProtocol()
        .bytes(1).chars(1)
        .shorts(1).ints(1)
        .floats(1).doubles(1).bytes(DataProtocol.DECLARE_LAZY_COUNT)

    @Test
    fun protocol_sizeCorrect() {
        val sizeExpected = 1 + 1 + 2 + 4 + 4 + 8
        assertEquals(sizeExpected, protocol.totalSize)
        assertEquals(sizeExpected, protocolLastLazy.totalSize)
    }

    @Test
    fun protocol_headToNextCorrect() {
        protocol.headTo(0)

        val iters = 100
        repeat(iters) {
            val componentExpected = protocol.getNextComponent()
            protocol.headToNextComponent()
            assertEquals(componentExpected, protocol.getCurrentComponent())
        }
    }

    @Test
    fun protocol_toString() {
        protocol.headTo(0)
        repeat(6) {
            val comp = protocol.getCurrentComponent()
            println(comp)
            println()
            protocol.headToNextComponent()
        }
    }
}
