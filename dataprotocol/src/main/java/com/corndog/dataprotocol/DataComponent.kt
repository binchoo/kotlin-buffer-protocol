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
    val hasLazyCount = (num == -1)

    init {
        if (num < -1)
            throw IllegalArgumentException()
    }

    private fun calcSize() = this.count * this.primitiveSize

    fun changeNum(num: Int) {
        if (!hasLazyCount)
            throw IllegalStateException("Its data count cannot be set lazily.")
        this.count = num
        this.size = calcSize()
    }

    fun typedBuffer(bytes: ByteArray): B {
        return typedBuffer(ByteBuffer.wrap(bytes))
    }

    abstract fun typedBuffer(byteBuffer: ByteBuffer): B

    abstract fun get(typedBuffer: Buffer): P

    class Chars(n: Int, order: ByteOrder)
        : DataComponent<Char, CharBuffer>(n, 1, order) {
        override fun typedBuffer(byteBuffer: ByteBuffer): CharBuffer {
            return byteBuffer.order(order).asCharBuffer()
        }

        override fun get(typedBuffer: Buffer): Char {
            return (typedBuffer as CharBuffer).get()
        }
    }

    class Bytes(n: Int, order: ByteOrder)
        : DataComponent<Byte, ByteBuffer>(n, 1, order) {
        override fun typedBuffer(byteBuffer: ByteBuffer): ByteBuffer {
            return byteBuffer
        }

        override fun get(typedBuffer: Buffer): Byte {
            return (typedBuffer as ByteBuffer).get()
        }
    }

    class Shorts(n: Int, order: ByteOrder)
        : DataComponent<Short, ShortBuffer>(n, 2, order) {
        override fun typedBuffer(byteBuffer: ByteBuffer): ShortBuffer {
            return byteBuffer.order(order).asShortBuffer()
        }

        override fun get(typedBuffer: Buffer): Short {
            return (typedBuffer as ShortBuffer).get()
        }
    }

    class Ints(n: Int, order: ByteOrder)
        : DataComponent<Int, IntBuffer>(n, 4, order) {
        override fun typedBuffer(byteBuffer: ByteBuffer): IntBuffer {
            return byteBuffer.order(order).asIntBuffer()
        }

        override fun get(typedBuffer: Buffer): Int {
            return (typedBuffer as IntBuffer).get()
        }
    }

    class Floats(n: Int, order: ByteOrder)
        : DataComponent<Float, FloatBuffer>(n, 4, order) {
        override fun typedBuffer(byteBuffer: ByteBuffer): FloatBuffer {
            return byteBuffer.order(order).asFloatBuffer()
        }

        override fun get(typedBuffer: Buffer): Float {
            return (typedBuffer as FloatBuffer).get()
        }
    }

    class Doubles(n: Int, order: ByteOrder)
        : DataComponent<Double, DoubleBuffer>(n, 8, order) {
        override fun typedBuffer(byteBuffer: ByteBuffer): DoubleBuffer {
            return byteBuffer.order(order).asDoubleBuffer()
        }

        override fun get(typedBuffer: Buffer): Double {
            return (typedBuffer as DoubleBuffer).get()
        }
    }

    class Longs(n: Int, order: ByteOrder)
        : DataComponent<Long, LongBuffer>(n, 8, order) {
        override fun typedBuffer(byteBuffer: ByteBuffer): LongBuffer {
            return byteBuffer.order(order).asLongBuffer()
        }

        override fun get(typedBuffer: Buffer): Long {
            return (typedBuffer as LongBuffer).get()
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