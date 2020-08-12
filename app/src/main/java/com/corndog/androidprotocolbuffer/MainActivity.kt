package com.corndog.androidprotocolbuffer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import dataprotocol.DataProtocol
import dataprotocol.buffered.ProtocolBuffer
import dataprotocol.buffered.ProtocolBufferReader
import dataprotocol.typehandle.ByteHandler
import dataprotocol.typehandle.IntHandler
import dataprotocol.typehandle.ShortHandler
import java.nio.ByteBuffer

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rawBuffer = ByteBuffer.allocate(150)
        val protocol = DataProtocol().bytes(10).shorts(20)
        val protoBuf = ProtocolBuffer(rawBuffer, protocol)
        val reader = MyReader(protoBuf)

        reader.read()
    }

    class MyReader(protoBuf: ProtocolBuffer): ProtocolBufferReader(protoBuf) {
        var count = 0
        init {
            addByteHandler(object : ByteHandler {
                override fun handle(data: Byte, handlingHint: Int) {
                    Log.w("handler", "this is byte! $handlingHint ${count++}")
                }
            })

            addShortHandler(object: ShortHandler {
                override fun handle(data: Short, handlingHint: Int) {
                    Log.w("handler", "this is short! $handlingHint ${count++}")
                }
            })
        }
    }
}
