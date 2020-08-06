package protocol.buffered.data

import protocol.Primitive
import java.lang.IllegalArgumentException
import java.nio.ByteOrder

sealed class DataComponent(num: Int,
                           val primitive: Class<*>,
                           val primitive_sz: Int,
                           val order: ByteOrder) {
    var num: Int = num
        private set
    var sz = calcSize()
        private set
    val num_is_lazy = (num == -1)

    fun changeNum(num: Int) {
        this.num = num
        this.sz = calcSize()
    }

    private fun calcSize() = this.num * this.primitive_sz

    class Chars(n: Int, order: ByteOrder)
        : DataComponent(n, Primitive.Char, 1, order)

    class Bytes(n: Int, order: ByteOrder)
        : DataComponent(n, Primitive.Byte, 1, order)

    class Shorts(n: Int, order: ByteOrder)
        : DataComponent(n, Primitive.Short, 2, order)

    class Ints(n: Int, order: ByteOrder)
        : DataComponent(n, Primitive.Int, 4, order)

    class Floats(n: Int, order: ByteOrder)
        : DataComponent(n, Primitive.Float, 4, order)

    class Doubles(n: Int, order: ByteOrder)
        : DataComponent(n, Primitive.Double, 8, order)

    init {
        if (num < -1)
            throw IllegalArgumentException()
    }

    override fun toString(): String {
        return """
            size: $sz
            primitive size: $primitive_sz
            quantity: $num
            byteorder: $order
            lazy: $num_is_lazy
        """.trimIndent()
    }
}