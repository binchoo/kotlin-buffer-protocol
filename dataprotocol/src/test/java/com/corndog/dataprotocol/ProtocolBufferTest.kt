package com.corndog.dataprotocol

import dataprotocol.buffered.ProtocolBuffer
import dataprotocol.DataProtocol
import dataprotocol.Primitive
import org.junit.Test
import org.junit.Assert.*

import java.nio.ByteBuffer
import java.nio.ByteOrder

class ProtocolBufferTest {
    val protocol = DataProtocol.Builder().bytes(10).chars(10).ints(10).build()
    val rawBuff = ByteBufferCompat.allocate(protocol, 3)
    val protoBuff = ProtocolBuffer(rawBuff, protocol)
    val rawBuffLess = ByteBuffer.allocateDirect(50)
    val protoBuffLess = ProtocolBuffer(rawBuffLess, protocol)

    init {
        repeat (3) {
            var byte: Byte = 1
            repeat(10) {
                rawBuff.put(byte++)
            }

            var char = 'a'
            repeat(10) {
                rawBuff.putChar(char++)
            }

            var int = 1
            repeat(10) {
                rawBuff.putInt(int++)
            }
        }

        repeat(10) {
            rawBuffLess.put(0)
        }
        repeat(10) {
            rawBuffLess.putChar('a')
        }
        repeat(5) {
            rawBuffLess.putInt(0)
        }
    }

    @Test
    fun protoBuff_forEachCorrect() {
        var byteCnt = 0
        var charCnt = 0
        var intCnt = 0
        protoBuff.rewind()
        protoBuff.forEach {value: Any->
            val index = protoBuff.currentComponentIndex()
            when (index) {
                0-> {
                    assertEquals(Primitive.Byte, value::class.java)
                    byteCnt++
                }
                1-> {
                    assertEquals(Primitive.Char, value::class.java)
                    charCnt++
                }
                2-> {
                    assertEquals(Primitive.Int, value::class.java)
                    intCnt++
                }
            }
        }
        assertEquals(byteCnt, 30)
        assertEquals(charCnt, 30)
        assertEquals(intCnt, 30)
    }

    @Test
    fun protoBuff_valueCorrect() {
        protoBuff.rewind()

        var byte = 0
        var char = 0
        var int = 0

        protoBuff.forEach {value: Any->
            val index = protoBuff.currentComponentIndex()
            when (index) {
                0-> assertEquals((byte++ % 10 + 1).toByte(), value)
                1-> assertEquals((char++ % 10 + 'a'.toInt()).toChar(), value)
                2-> assertEquals(int++ % 10 + 1, value)
            }
        }
    }

    @Test
    fun protoBuff_buffLessForEachCorrect() {
        var byteCnt = 0
        var charCnt = 0
        var intCnt = 0

        protoBuffLess.rewind()
        protoBuffLess.forEach { value: Any ->
            val index = protoBuff.currentComponentIndex()
            when (index) {
                0 -> {
                    assertEquals(Primitive.Byte, value::class.java)
                    byteCnt++
                }
                1 -> {
                    assertEquals(Primitive.Char, value::class.java)
                    charCnt++
                }
                2 -> {
                    assertEquals(Primitive.Int, value::class.java)
                    intCnt++
                }
            }
        }

        assertEquals(byteCnt, 10)
        assertEquals(charCnt, 10)
        assertEquals(intCnt, 0)
    }
}