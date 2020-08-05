package protocol.buffer

import protocol.data.Primitive
import java.lang.IllegalArgumentException
import java.nio.ByteOrder

sealed class BufferComponent(val num: Int,
                             val primitive: Class<*>,
                             val primitive_sz: Int,
                             var order: ByteOrder = ByteOrder.LITTLE_ENDIAN) {

    val sz = num * primitive_sz

    init {
        if (num <= 0)
            throw IllegalArgumentException()
    }

    fun order() = order

    fun order(order: ByteOrder) {
        this.order = order
    }

    override fun toString(): String {
        return """
            size: $sz
            primitive size: $primitive_sz
            quantity: $num
            byteorder: $order
        """.trimIndent()
    }

    class Chars(n: Int): BufferComponent(n,
        Primitive.Char, 1)

    class Bytes(n: Int): BufferComponent(n,
        Primitive.Byte, 1)

    class Shorts(n: Int): BufferComponent(n,
        Primitive.Short, 2)

    class Ints(n: Int): BufferComponent(n,
        Primitive.Int, 4)

    class Floats(n: Int): BufferComponent(n,
        Primitive.Float, 4)

    class Doubles(n: Int): BufferComponent(n,
        Primitive.Double, 8)
}