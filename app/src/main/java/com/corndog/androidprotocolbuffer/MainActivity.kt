package com.corndog.androidprotocolbuffer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import dataprotocol.DataProtocol
import dataprotocol.buffered.ProtocolBuffer
import dataprotocol.buffered.ProtocolBufferReader
import java.nio.ByteBuffer

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rawBuffer = ByteBuffer.allocate(150)
        val protocol = DataProtocol.Builder().bytes(10).shorts(20).build()
        val protoBuf = ProtocolBuffer(rawBuffer, protocol)
        val reader = MyReader(protoBuf)

        reader.readByData()
    }

    class MyReader(protoBuf: ProtocolBuffer): ProtocolBufferReader(protoBuf) {
        var count = 0
        override fun onHandlerSetup() {
            addByteHandler { data, handlingHint ->
                Log.w("handler", "this is byte! $handlingHint ${count++}")
            }

            addShortHandler { data, handlingHint ->
                Log.w("handler", "this is short! $handlingHint ${count++}")
            }
        }
    }
}
