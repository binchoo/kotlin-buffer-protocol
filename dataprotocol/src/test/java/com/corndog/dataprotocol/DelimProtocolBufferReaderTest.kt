package com.corndog.dataprotocol

import com.corndog.dataprotocol.extension.DelimProtocolBufferReader
import dataprotocol.buffered.ProtocolReader
import org.junit.Assert.*
import org.junit.Test
import java.nio.ByteBuffer
import java.nio.ByteOrder

class DelimProtocolBufferReaderTest {

    companion object {
        private val STRINGS = arrayOf("hello", "world", "I", "love", "you")
        private val STRINGS_CONCAT = arrayOf("hello", "world", "I", "love", "you").joinToString("\n")
    }

    private val stringResult = ArrayList<String>()

    val bufferBig = stringToByteBuffer(ByteOrder.BIG_ENDIAN)
    val bufferLittle = stringToByteBuffer(ByteOrder.LITTLE_ENDIAN)

    lateinit var reader: ProtocolReader

    @Test
    fun delimProtocolReader_bigCorrect() {
        reader = readerForOrder(ByteOrder.BIG_ENDIAN, bufferBig)
        reader.readByData()

        stringResult.forEachIndexed {index, str->
            assertEquals(STRINGS[index], str)
        }

        stringResult.clear()
    }

    @Test
    fun delimProtocolReader_littleCorrect() {
        reader = readerForOrder(ByteOrder.LITTLE_ENDIAN, bufferLittle)
        reader.readByData()

        stringResult.forEachIndexed {index, str->
            assertEquals(STRINGS[index], str)
        }

        stringResult.clear()
    }

    private fun stringToByteBuffer(order: ByteOrder): ByteBuffer {
        return ByteBuffer.allocate(2 * STRINGS_CONCAT.length).also { buffer->
            STRINGS_CONCAT.toCharArray().forEach { character->
                buffer.order(order).putChar(character)
            }
        }
    }

    private fun readerForOrder(order: ByteOrder, buffer: ByteBuffer): ProtocolReader {
        return DelimProtocolBufferReader("\n", buffer, order).also {
            it.addStringChunkHandler { data, handlingHint ->
                println(data)
                stringResult.add(data)
            }
        }
    }
}