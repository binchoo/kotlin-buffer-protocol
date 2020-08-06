package protocol.buffered.data

import protocol.Protocol
import java.nio.ByteOrder

class DataProtocol: Protocol {

    private val components: ArrayList<DataComponent<*, *>> = ArrayList()
    private var componentHead = 0
    private var head = 0
    var totalSize = 0
        private set

    fun chars(n: Int, order: ByteOrder = ByteOrder.LITTLE_ENDIAN): DataProtocol {
        components.add(DataComponent.Chars(n, order))
        addSize(components.last().size)
        return this
    }

    fun bytes(n: Int, order: ByteOrder = ByteOrder.LITTLE_ENDIAN): DataProtocol {
        components.add(DataComponent.Bytes(n, order))
        addSize(components.last().size)
        return this
    }

    fun shorts(n: Int, order: ByteOrder = ByteOrder.LITTLE_ENDIAN): DataProtocol {
        components.add(DataComponent.Shorts(n, order))
        addSize(components.last().size)
        return this
    }

    fun ints(n: Int, order: ByteOrder = ByteOrder.LITTLE_ENDIAN): DataProtocol {
        components.add(DataComponent.Ints(n, order))
        addSize(components.last().size)
        return this
    }

    fun floats(n: Int, order: ByteOrder = ByteOrder.LITTLE_ENDIAN): DataProtocol {
        components.add(DataComponent.Floats(n, order))
        addSize(components.last().size)
        return this
    }

    fun doubles(n: Int, order: ByteOrder = ByteOrder.LITTLE_ENDIAN): DataProtocol {
        components.add(DataComponent.Doubles(n, order))
        addSize(components.last().size)
        return this
    }

    fun longs(n: Int, order: ByteOrder = ByteOrder.LITTLE_ENDIAN): DataProtocol {
        components.add(DataComponent.Longs(n, order))
        addSize(components.last().size)
        return this
    }

    private fun addSize(sizeToAdd: Int) {
        totalSize += sizeToAdd
    }

    fun getComponent(componentIndex: Int): DataComponent<*, *> {
        return components.get(componentIndex)
    }

    fun getCurrentComponent(): DataComponent<*, *> {
        return components.get(getCurrentComponentIndex())
    }

    fun getCurrentComponentIndex(): Int {
        return componentHead
    }

    fun changeComponentNumber(componentIndex: Int, num: Int) {
        components[componentIndex].changeNum(num)
    }

    fun headIncrease(bytePositionPlus: Int) {
        headTo(this.head + bytePositionPlus)
    }

    fun headTo(bytePosition: Int) {
        if (bytePosition >= totalSize)
            throw IndexOutOfBoundsException()
        this.head = bytePosition
        this.componentHead = calcComponentHead(bytePosition)
    }

    fun headToNextComponent() {
        headToComponent((componentHead + 1) % components.size)
    }

    fun headToComponent(componentIndex: Int) {
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

    companion object {
        val NUMBER_LAZILY_SET = -1
    }
}