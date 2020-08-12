package dataprotocol

import java.lang.IllegalStateException
import java.nio.ByteOrder

interface Protocol {
    fun headTo(bytePosition: Int)
    fun headToComponent(componentIndex: Int)
    fun headToNextComponent()
    fun getCurrentComponent(): DataComponent<out Any>
    fun getCurrentComponentIndex(): Int
    fun getNextComponent(): DataComponent<*>
    fun changeComponentNumber(componentIndex: Int, newNum: Int)
}

class DataProtocol private constructor(val components: ArrayList<DataComponent<out Any>>) : Protocol {

    private var componentHead = 0
    private var head = 0
    var totalSize = 0
        private set

    init {
        components.forEach {comp->
            totalSize += comp.size
        }
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

    override fun headToNextComponent() {
        headToComponent((componentHead + 1) % components.size)
    }

    override fun getCurrentComponent(): DataComponent<out Any> {
        return components.get(getCurrentComponentIndex())
    }

    override fun getCurrentComponentIndex(): Int {
        return componentHead
    }

    override fun getNextComponent(): DataComponent<out Any> {
        return components.get((componentHead + 1) % components.size)
    }

    override fun changeComponentNumber(componentIndex: Int, newNum: Int) {
        val comp = components[componentIndex]
        comp.changeNum(newNum)
        totalSize += comp.size
    }

    class Builder {
        private val components: ArrayList<DataComponent<out Any>> = ArrayList()
        var hasBuilt = false
            private set

        fun chars(n: Int, order: ByteOrder = ByteOrder.BIG_ENDIAN): Builder {
            components.add(DataComponent.Chars(n, order))
            return this
        }

        fun bytes(n: Int, order: ByteOrder = ByteOrder.BIG_ENDIAN): Builder {
            components.add(DataComponent.Bytes(n, order))
            return this
        }

        fun shorts(n: Int, order: ByteOrder = ByteOrder.BIG_ENDIAN): Builder {
            components.add(DataComponent.Shorts(n, order))
            return this
        }

        fun ints(n: Int, order: ByteOrder = ByteOrder.BIG_ENDIAN): Builder {
            components.add(DataComponent.Ints(n, order))
            return this
        }

        fun floats(n: Int, order: ByteOrder = ByteOrder.BIG_ENDIAN): Builder {
            components.add(DataComponent.Floats(n, order))
            return this
        }

        fun doubles(n: Int, order: ByteOrder = ByteOrder.BIG_ENDIAN): Builder {
            components.add(DataComponent.Doubles(n, order))
            return this
        }

        fun longs(n: Int, order: ByteOrder = ByteOrder.BIG_ENDIAN): Builder {
            components.add(DataComponent.Longs(n, order))
            return this
        }

        fun build(): DataProtocol {
            if (!hasBuilt) {
                hasBuilt = true
                return DataProtocol(components)
            } else throw IllegalStateException("A DataProtocol.Builder can only build " +
                    "one DataProtocol object.")
        }
    }

    companion object {
        val DECLARE_LAZY_COUNT = 0
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