package dataprotocol

import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import java.nio.*

abstract class DataComponent<P, B: Buffer>(num: Int,
                                           val primitiveSize: Int,
                                           val order: ByteOrder) {
    var count: Int = num
        private set
    var size = calcSize()
        private set
    val hasLazyCount = (num == 0)

    init {
        if (num < 0)
            throw IllegalArgumentException()
    }

    private fun calcSize() = this.count * this.primitiveSize

    fun changeNum(num: Int) {
        if (!hasLazyCount)
            throw IllegalStateException("Its data count cannot be set lazily.")
        this.count = num
        this.size = calcSize()
    }

    abstract fun fetch(byteBuffer: ByteBuffer): P

    class Bytes(n: Int, order: ByteOrder)
        : DataComponent<Byte, ByteBuffer>(n, 1, order) {

        override fun fetch(byteBuffer: ByteBuffer): Byte {
            return byteBuffer.order(order).get()
        }
    }

    class Chars(n: Int, order: ByteOrder)
        : DataComponent<Char, CharBuffer>(n, 2, order) {

        override fun fetch(byteBuffer: ByteBuffer): Char {
            return byteBuffer.order(order).getChar()
        }
    }

    class Shorts(n: Int, order: ByteOrder)
        : DataComponent<Short, ShortBuffer>(n, 2, order) {

        override fun fetch(byteBuffer: ByteBuffer): Short {
            return byteBuffer.order(order).getShort()
        }
    }

    class Ints(n: Int, order: ByteOrder)
        : DataComponent<Int, IntBuffer>(n, 4, order) {

        override fun fetch(byteBuffer: ByteBuffer): Int {
            return byteBuffer.order(order).getInt()
        }
    }

    class Floats(n: Int, order: ByteOrder)
        : DataComponent<Float, FloatBuffer>(n, 4, order) {

        override fun fetch(byteBuffer: ByteBuffer): Float {
            return byteBuffer.order(order).getFloat()
        }
    }

    class Doubles(n: Int, order: ByteOrder)
        : DataComponent<Double, DoubleBuffer>(n, 8, order) {

        override fun fetch(byteBuffer: ByteBuffer): Double {
            return byteBuffer.order(order).getDouble()
        }
    }

    class Longs(n: Int, order: ByteOrder)
        : DataComponent<Long, LongBuffer>(n, 8, order) {

        override fun fetch(byteBuffer: ByteBuffer): Long {
            return byteBuffer.order(order).getLong()
        }
    }

    override fun toString(): String {
        return """
            size: $size
            primitive size: $primitiveSize
            class: ${this::class.java}
            quantity: $count
            byte order: $order
            lazy: $hasLazyCount
        """.trimIndent()
    }
}