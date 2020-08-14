package com.corndog.androidprotocolbuffer

import android.content.Context
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.corndog.dataprotocol.ByteBufferCompat
import com.corndog.dataprotocol.allocate
import com.felhr.usbserial.UsbSerialDevice
import com.felhr.usbserial.UsbSerialInterface
import dataprotocol.DataProtocol
import dataprotocol.buffered.ProtocolBuffer
import dataprotocol.buffered.ProtocolBufferReader
import kotlinx.android.synthetic.main.activity_main.*
import java.nio.ByteBuffer

class MainActivity : AppCompatActivity() {

    private var serialPort: UsbSerialDevice? = null
    private lateinit var simpleArduinoProtocol: SimpleArduinoProtocol

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupGUI()
    }

    private fun setupGUI() {
        find_device.setOnClickListener {
            reallocSerialPort()
            checkSerialPortNull()
        }

        communicate.setOnClickListener {
            try {
                reallocProtocol()
                startCommunication()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "error: ${e.javaClass}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun reallocSerialPort() {
        if (serialPort != null)
            serialPort = null
        serialPort = findDevice()
    }

    private fun checkSerialPortNull() {
        if (serialPort == null)
            Toast.makeText(this, "device not found.", Toast.LENGTH_SHORT).show()
        else
            Toast.makeText(this, "device found.", Toast.LENGTH_SHORT).show()
    }

    private fun reallocProtocol() {
        if (simpleArduinoProtocol.isAlive)
            simpleArduinoProtocol.interrupt()

        simpleArduinoProtocol = SimpleArduinoProtocol(serialPort!!, SerialConfig.getDefaultConfig())
        simpleArduinoProtocol.setSignalHandler { data, handlingHint ->
                textViewAppend(logcat, data.toString())
        }
    }

    private fun textViewAppend(textView: TextView, text: String) {
        textView.setText("${textView.text}, $text")
    }

    private fun startCommunication() {
        simpleArduinoProtocol.start()
    }

    private fun findDevice(): UsbSerialDevice? {
        val manager = getSystemService(Context.USB_SERVICE) as UsbManager
        manager.deviceList.values.forEach {device->
            if (ARDUINO_VENDOR_ID == device.vendorId) {
                return wrapToSerialDevice(manager, device)
            }
        }
        return null
    }

    private fun wrapToSerialDevice(manager: UsbManager, usbDevice: UsbDevice): UsbSerialDevice {
        val connection = manager.openDevice(usbDevice)
        return UsbSerialDevice.createUsbSerialDevice(
            UsbSerialDevice.CDC,
            usbDevice,
            connection,
            ARDUINO_INTERFACE)
    }

    companion object {
        private val ARDUINO_VENDOR_ID = 0x2341
        private val ARDUINO_INTERFACE = 1
    }
}