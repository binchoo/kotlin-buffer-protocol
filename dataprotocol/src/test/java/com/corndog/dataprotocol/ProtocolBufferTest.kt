package com.corndog.dataprotocol

import dataprotocol.buffered.ProtocolBuffer
import dataprotocol.DataProtocol
import dataprotocol.Primitive
import org.junit.Test
import org.junit.Assert.*

import java.nio.ByteBuffer

class ProtocolBufferTest {
    val rawBuff = ByteBuffer.allocateDirect(60 * 3)
    val protocol = DataProtocol().bytes(10).chars(10).ints(10)
    val protoBuff = ProtocolBuffer(rawBuff, protocol)

//    init {
//        var byte: Byte = 1
//        repeat(10) {
//            rawBuff.put(byte++)
//        }
//
//        var char: Char = 'a'
//        repeat(10) {
//            rawBuff.putChar(char++)
//        }
//
//        var int: Int = 1
//        repeat(10) {
//            rawBuff.putInt(int++)
//        }
//    }

    @Test
    fun rawBuff_putCorrect() {
        rawBuff.putChar('a')
        assertEquals(rawBuff.position(), 2)
    }

    @Test
    fun protoBuff_forEachCorrect() {
        protoBuff.rewind()
        protoBuff.forEach {value: Any->
            val index = protoBuff.currentComponentIndex()
            when (index) {
                0-> {
                    assertEquals(Primitive.Byte, value::class.java)
                }
                1-> {
                    assertEquals(Primitive.Char, value::class.java)
                }
                2-> {
                    assertEquals(Primitive.Int, value::class.java)
                }
            }
        }
    }

    @Test
    fun protoBuff_valueCorrect() {
        protoBuff.rewind()
        var byte: Byte = 1
        var char: Char = 'a'
        var int: Int = 1
        protoBuff.forEach {value: Any->
            val index = protoBuff.currentComponentIndex()
            when (index) {
                0-> {
                    assertEquals(byte++, value)
                }
                1-> {
                    assertEquals(char++, value)
                }
                2-> {
                    assertEquals(int++, value)
                }
            }
        }
    }
}