package com.corndog.dataprotocol

import dataprotocol.buffered.ProtocolBuffer
import dataprotocol.buffered.ProtocolBufferReader
import org.junit.Test
import org.junit.Assert.*


class ProtocolBufferReaderTest {

    inner class MyProtocolReader(probuf: ProtocolBuffer): ProtocolBufferReader(probuf) {
        override fun onHandlerSetup() {
            addByteHandler {

            }
        }

        private fun addByteHandler(handler: () -> Unit) {

        }
    }
}