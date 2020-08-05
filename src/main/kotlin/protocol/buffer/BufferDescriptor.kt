package protocol.buffer

import java.nio.ByteOrder

class BufferDescriptor {

    private val __components = ArrayList<BufferComponent>()

    lateinit var components: ArrayList<BufferComponent>
        private set

    var size = 0
        private set
    var head = 0
        private set
    var isCommitted = false
        private set

    fun chars(n: Int, order: ByteOrder = ByteOrder.LITTLE_ENDIAN): BufferDescriptor {
        checkUnCommitted()
        __components.add(BufferComponent.Chars(n, order))
        return this
    }

    fun bytes(n: Int, order: ByteOrder = ByteOrder.LITTLE_ENDIAN): BufferDescriptor {
        checkUnCommitted()
        __components.add(BufferComponent.Bytes(n, order))
        return this
    }

    fun shorts(n: Int, order: ByteOrder = ByteOrder.LITTLE_ENDIAN): BufferDescriptor {
        checkUnCommitted()
        __components.add(BufferComponent.Shorts(n, order))
        return this
    }

    fun ints(n: Int, order: ByteOrder = ByteOrder.LITTLE_ENDIAN): BufferDescriptor {
        checkUnCommitted()
        __components.add(BufferComponent.Ints(n, order))
        return this
    }

    fun floats(n: Int, order: ByteOrder = ByteOrder.LITTLE_ENDIAN): BufferDescriptor {
        checkUnCommitted()
        __components.add(BufferComponent.Floats(n, order))
        return this
    }

    fun doubles(n: Int, order: ByteOrder = ByteOrder.LITTLE_ENDIAN): BufferDescriptor {
        checkUnCommitted()
        __components.add(BufferComponent.Doubles(n, order))
        return this
    }

    fun changeComponentNumber(componentIndex: Int, num: Int) {
        checkCommitted()
        __components[componentIndex].changeNum(num)
        isCommitted = false
    }

    fun commit(): BufferDescriptor {
        checkUnCommitted()

        components = __components.clone() as ArrayList<BufferComponent>

        __components.forEach {comp->
            size += comp.sz
        }

        isCommitted = true

        return this
    }

    fun getComponent(componentIndex: Int): BufferComponent {
        checkCommitted()
        return components.get(componentIndex)
    }

    fun getCurrentComponent(): BufferComponent {
        checkCommitted()
        return components.get(getCurrentComponentIndex())
    }

    fun getCurrentComponentIndex(): Int {
        checkCommitted()

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
        checkCommitted()
        head = bytePosition
    }

    fun headToComponent(componentIndex: Int) {
        checkCommitted()
        if (componentIndex >= components.size)
            throw IndexOutOfBoundsException()

        var head_tmp = 0
        for (i in 0 until componentIndex) {
            head_tmp += components[i].sz
        }

        this.head = head_tmp
    }

    fun headToNextComponent() {
        checkCommitted()
        val currentComponentIndex = getCurrentComponentIndex()
        val nextComponentIndex = (currentComponentIndex + 1) % components.size
        headToComponent(nextComponentIndex)
    }

    private fun checkCommitted() = assert(isCommitted)
    private fun checkUnCommitted() = assert(!isCommitted)
}