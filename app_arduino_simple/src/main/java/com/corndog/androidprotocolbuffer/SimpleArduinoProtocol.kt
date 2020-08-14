package com.corndog.androidprotocolbuffer

import android.util.Log
import com.felhr.usbserial.UsbSerialDevice
import dataprotocol.DataProtocol
import dataprotocol.buffered.ProtocolBuffer
import dataprotocol.buffered.ProtocolBufferReader
import dataprotocol.typehandle.TypeHandler
import java.io.*
import java.nio.ByteBuffer

class SimpleArduinoProtocol(private val serialPort: UsbSerialDevice,
                            private val serialConfig: SerialConfig,
                            val signalAmount: Int = 50)
    : Thread(), Closeable {

    private lateinit var instream: InputStream
    private lateinit var outstream: OutputStream
    private lateinit var readBytes: ByteArray

    private val signalProtocol = DataProtocol.Builder().shorts(signalAmount).build()
    private var signalHandler: TypeHandler<Short>? = null

    init {
        openSerialPort()

        setupIOStream()

        waitDeviceReady()

        setupSerialConfiguration()
    }

    private fun openSerialPort() {
        if (!serialPort.isOpen)
            serialPort.syncOpen()
    }

    private fun setupIOStream() {
        instream = serialPort.inputStream
        outstream = serialPort.outputStream
    }

    private fun waitDeviceReady() {
        while (instream.available() == 0);
        instream.readBytes()
    }

    private fun setupSerialConfiguration() {
        serialPort.setBaudRate(serialConfig.baudRate)
        serialPort.setDataBits(serialConfig.dataBits)
        serialPort.setStopBits(serialConfig.stopBits)
        serialPort.setFlowControl(serialConfig.flowControl)
        serialPort.setParity(serialConfig.parity)
    }

    override fun run() {
        try {
            while (true)
                communicate()
        } catch (e: InterruptedException) {
            Log.d("SimpleArduinoProtocol", "Thread Interrupted.")
        } finally {
            close()
        }
    }

    private fun communicate() {
        sendSignalAmount(signalAmount)
        receiveSignal(signalAmount)
        handleSignal()
    }

    private fun sendSignalAmount(amount: Int) {
        outstream.write(amount)
    }

    private fun receiveSignal(amount: Int) {
        val expectedLength = amount * SIGNAL_UNIT_SIZE
        var readLength = 0
        readBytes = ByteArray(expectedLength)

        do {
            readLength += instream.read(readBytes, readLength, expectedLength - readLength)
        } while (readLength < expectedLength)
    }

    private fun handleSignal() {
        val protoBuff = ProtocolBuffer(ByteBuffer.wrap(readBytes), signalProtocol)
        val signalReader = SignalReader(protoBuff)
        signalReader.readByData()
    }

    private inner class SignalReader(protoBuff: ProtocolBuffer): ProtocolBufferReader(protoBuff) {
        override fun onHandlerSetup() {
            signalHandler?.let {
                addShortHandler(it)
            }
        }
    }

    fun setSignalHandler(shortHandler: TypeHandler<Short>) {
        signalHandler = shortHandler
    }

    override fun close() {
        if (serialPort.isOpen) {
            serialPort.syncClose()
            instream.close()
            outstream.close()
        }
    }

    companion object {
        private val SIGNAL_UNIT_SIZE = 2
    }
}
