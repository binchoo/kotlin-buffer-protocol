package protocol.buffer

import protocol.typehandle.Primitive
import java.lang.IllegalArgumentException
import java.nio.ByteOrder

sealed class BufferComponent(num: Int,
                           val primitive: Class<*>,
                           val primitive_sz: Int,
                           val order: ByteOrder) {
    var num: Int = num
        private set
    var sz = calcSize()
        private set

    fun changeNum(num: Int) {
        this.num = num
        this.sz = calcSize()
    }

    private fun calcSize() = this.num * this.primitive_sz

    class Chars(n: Int, order: ByteOrder)
        : BufferComponent(n, Primitive.Char, 1, order)

    class Bytes(n: Int, order: ByteOrder)
        : BufferComponent(n, Primitive.Byte, 1, order)

    class Shorts(n: Int, order: ByteOrder)
        : BufferComponent(n, Primitive.Short, 2, order)

    class Ints(n: Int, order: ByteOrder)
        : BufferComponent(n, Primitive.Int, 4, order)

    class Floats(n: Int, order: ByteOrder)
        : BufferComponent(n, Primitive.Float, 4, order)

    class Doubles(n: Int, order: ByteOrder)
        : BufferComponent(n, Primitive.Double, 8, order)

    init {
        if (num < NUM_LAZY)
            throw IllegalArgumentException()
    }

    companion object {
        val NUM_LAZY = -1
    }

    override fun toString(): String {
        return """
            size: $sz
            primitive size: $primitive_sz
            quantity: $num
            byteorder: $order
        """.trimIndent()
    }
}