package protocol

import java.nio.Buffer
import java.nio.ByteOrder

interface DataProtocol {
    fun headTo(bytePosition: Int)
    fun headToComponent(componentIndex: Int)
    fun getCurrentComponent(): DataComponent<*, *>
    fun getCurrentComponentIndex(): Int
}

class DataProtocolImpl: DataProtocol {

    private val components: ArrayList<DataComponent<*, *>> = ArrayList()
    private var componentHead = 0
    private var head = 0
    var totalSize = 0
        private set

    fun getComponent(componentIndex: Int): DataComponent<out Any, out Buffer> {
        return components.get(componentIndex)
                as DataComponent<out Any, out Buffer>
    }

    override fun getCurrentComponent(): DataComponent<out Any, out Buffer> {
        return components.get(getCurrentComponentIndex())
                as DataComponent<out Any, out Buffer>
    }

    override fun getCurrentComponentIndex(): Int {
        return componentHead
    }

    fun changeComponentNumber(componentIndex: Int, num: Int) {
        components[componentIndex].changeNum(num)
    }

    fun headIncrease(bytePositionPlus: Int) {
        headTo(this.head + bytePositionPlus)
    }

    override fun headTo(bytePosition: Int) {
        if (bytePosition >= totalSize)
            throw IndexOutOfBoundsException()
        this.head = bytePosition
        this.componentHead = calcComponentHead(bytePosition)
    }

    private fun calcComponentHead(currentHead: Int): Int {
        var head_tmp = currentHead
        var comp_head_tmp = 0

        components.forEachIndexed { componentIndex, comp ->
            head_tmp -= comp.size
            if (head_tmp < 0) {
                comp_head_tmp = componentIndex
                return comp_head_tmp
            }
        }
        return comp_head_tmp
    }

    fun headToNextComponent() {
        headToComponent((componentHead + 1) % components.size)
    }

    override fun headToComponent(componentIndex: Int) {
        if (componentIndex >= components.size)
            throw IndexOutOfBoundsException()
        this.componentHead = componentIndex
        this.head = calcHead(componentIndex)
    }

    private fun calcHead(currentComponentHead: Int): Int {
        var head_tmp = 0
        for (i in 0 until currentComponentHead)
            head_tmp += components[i].size
        return head_tmp
    }

    fun chars(n: Int, order: ByteOrder = ByteOrder.LITTLE_ENDIAN): DataProtocolImpl {
        components.add(DataComponent.Chars(n, order))
        addSize(components.last().size)
        return this
    }

    fun bytes(n: Int, order: ByteOrder = ByteOrder.LITTLE_ENDIAN): DataProtocolImpl {
        components.add(DataComponent.Bytes(n, order))
        addSize(components.last().size)
        return this
    }

    fun shorts(n: Int, order: ByteOrder = ByteOrder.LITTLE_ENDIAN): DataProtocolImpl {
        components.add(DataComponent.Shorts(n, order))
        addSize(components.last().size)
        return this
    }

    fun ints(n: Int, order: ByteOrder = ByteOrder.LITTLE_ENDIAN): DataProtocolImpl {
        components.add(DataComponent.Ints(n, order))
        addSize(components.last().size)
        return this
    }

    fun floats(n: Int, order: ByteOrder = ByteOrder.LITTLE_ENDIAN): DataProtocolImpl {
        components.add(DataComponent.Floats(n, order))
        addSize(components.last().size)
        return this
    }

    fun doubles(n: Int, order: ByteOrder = ByteOrder.LITTLE_ENDIAN): DataProtocolImpl {
        components.add(DataComponent.Doubles(n, order))
        addSize(components.last().size)
        return this
    }

    fun longs(n: Int, order: ByteOrder = ByteOrder.LITTLE_ENDIAN): DataProtocolImpl {
        components.add(DataComponent.Longs(n, order))
        addSize(components.last().size)
        return this
    }

    private fun addSize(sizeToAdd: Int) {
        totalSize += sizeToAdd
    }

    companion object {
        val NUMBER_LAZILY_SET = -1
    }

    override fun toString(): String {
        return """
            data size: $totalSize
            components: ${components.size}
            head: @$head bytes
            component head: @$componentHead
        """.trimIndent()
    }
}