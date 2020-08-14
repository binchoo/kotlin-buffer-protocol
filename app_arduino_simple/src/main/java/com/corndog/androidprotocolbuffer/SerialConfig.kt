package com.corndog.androidprotocolbuffer

import com.felhr.usbserial.UsbSerialInterface

open class SerialConfig private constructor(
    var dataBits: Int,
    var stopBits: Int, var parity: Int,
    var flowControl: Int, var baudRate: Int
) {

    class Builder {
        private var dataBits: Int = UsbSerialInterface.DATA_BITS_8
        private var stopBits: Int = UsbSerialInterface.STOP_BITS_1
        private var parity: Int = UsbSerialInterface.PARITY_NONE
        private var flowControl: Int = UsbSerialInterface.FLOW_CONTROL_OFF
        private var baudRate: Int = 9600

        fun dataBits(bits: Int): Builder {
            dataBits = bits
            return this
        }

        fun stopBits(bits: Int): Builder {
            stopBits = bits
            return this
        }

        fun parity(bits: Int): Builder {
            parity = bits
            return this
        }

        fun flowControl(flowControl: Int): Builder {
            this.flowControl = flowControl
            return this
        }

        fun baudRate(baudRate: Int): Builder {
            this.baudRate = baudRate
            return this
        }

        fun commit(): SerialConfig {
            return SerialConfig(dataBits, stopBits, parity, flowControl, baudRate)
        }
    }

    companion object {
        fun getDefaultConfig(): SerialConfig {
            return Builder().commit()
        }
    }
}