package com.corndog.dataprotocol

import com.corndog.dataprotocol.extension.DelimProtocolBufferReader
import org.junit.Assert.*
import org.junit.Test
import java.nio.ByteBuffer

class DelimProtocolBufferReaderTest {

    val strings = arrayOf("hello", "world", "I", "love", "you")
    val string = strings.joinToString("\n")
    val stringResult = ArrayList<String>()

    val buffer = ByteBuffer.allocate(2 * string.length).also {buffer->
        string.toCharArray().forEach {character->
            buffer.putChar(character)
        }
    }

    val reader = DelimProtocolBufferReader("\n", buffer).also {
        it.addStringChunkHandler { data, handlingHint ->
            println(data)
            stringResult.add(data)
        }
    }

    @Test
    fun delimProtocolReader_printCorrect() {
        reader.readByData()
        stringResult.forEachIndexed {index, str->
            assertEquals(strings[index], str)
        }
    }
}