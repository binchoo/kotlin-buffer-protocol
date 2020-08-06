package protocol.buffered.data

import protocol.Protocol
import java.lang.IllegalStateException
import java.nio.ByteOrder

class DataProtocol: Protocol {

    private val __components = ArrayList<DataComponent>()

    lateinit var components: ArrayList<DataComponent>
        private set

    var size = 0
        private set
    var head = 0
        private set
    var isCommitted = false
        private set

    fun chars(n: Int, order: ByteOrder = ByteOrder.LITTLE_ENDIAN): DataProtocol {
        assertUncommitted()
        __components.add(DataComponent.Chars(n, order))
        return this
    }

    fun bytes(n: Int, order: ByteOrder = ByteOrder.LITTLE_ENDIAN): DataProtocol {
        assertUncommitted()
        __components.add(DataComponent.Bytes(n, order))
        return this
    }

    fun shorts(n: Int, order: ByteOrder = ByteOrder.LITTLE_ENDIAN): DataProtocol {
        assertUncommitted()
        __components.add(DataComponent.Shorts(n, order))
        return this
    }

    fun ints(n: Int, order: ByteOrder = ByteOrder.LITTLE_ENDIAN): DataProtocol {
        assertUncommitted()
        __components.add(DataComponent.Ints(n, order))
        return this
    }

    fun floats(n: Int, order: ByteOrder = ByteOrder.LITTLE_ENDIAN): DataProtocol {
        assertUncommitted()
        __components.add(DataComponent.Floats(n, order))
        return this
    }

    fun doubles(n: Int, order: ByteOrder = ByteOrder.LITTLE_ENDIAN): DataProtocol {
        assertUncommitted()
        __components.add(DataComponent.Doubles(n, order))
        return this
    }

    fun changeComponentNumber(componentIndex: Int, num: Int) {
        assertCommitted()
        __components[componentIndex].changeNum(num)
        isCommitted = false
    }

    override fun commit(): DataProtocol {
        assertUncommitted()

        components = __components.clone() as ArrayList<DataComponent>

        __components.forEach {comp->
            size += comp.sz
        }

        isCommitted = true

        return this
    }

    fun getComponent(componentIndex: Int): DataComponent {
        assertCommitted()
        return components.get(componentIndex)
    }

    fun getCurrentComponent(): DataComponent {
        assertCommitted()
        return components.get(getCurrentComponentIndex())
    }

    fun getCurrentComponentIndex(): Int {
        assertCommitted()

        var currentHead = head
        var rtn = 0

        components.forEachIndexed { componentCursor, comp ->
            currentHead -= comp.sz
            if (currentHead < 0) {
                rtn = componentCursor
                return rtn
            }
        }
        return rtn
    }

    fun headTo(bytePosition: Int) {
        assertCommitted()
        head = bytePosition
    }

    fun headToComponent(componentIndex: Int) {
        assertCommitted()
        if (componentIndex >= components.size)
            throw IndexOutOfBoundsException()

        var head_tmp = 0
        for (i in 0 until componentIndex) {
            head_tmp += components[i].sz
        }

        this.head = head_tmp
    }

    fun headToNextComponent() {
        assertCommitted()
        val currentComponentIndex = getCurrentComponentIndex()
        val nextComponentIndex = (currentComponentIndex + 1) % components.size
        headToComponent(nextComponentIndex)
    }

    private fun assertCommitted() {
        if (!isCommitted) throw IllegalStateException()
    }

    private fun assertUncommitted() {
        if (isCommitted) throw IllegalStateException()
    }

    companion object {
        val NUM_LAZY = -1
    }
}