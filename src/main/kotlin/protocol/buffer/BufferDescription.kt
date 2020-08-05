package protocol.buffer

import java.lang.IllegalStateException

class BufferDescription {

    private val __components = ArrayList<BufferComponent>()

    lateinit var components: ArrayList<BufferComponent>
        private set
    var size = 0
        private set
    var head = 0
        private set
    var isCommitted = false
        private set

    fun chars(n: Int): BufferDescription {
        __components.add(BufferComponent.Chars(n))
        return this
    }

    fun bytes(n: Int): BufferDescription {
        __components.add(BufferComponent.Bytes(n))
        return this
    }

    fun shorts(n: Int): BufferDescription {
        __components.add(BufferComponent.Shorts(n))
        return this
    }

    fun ints(n: Int): BufferDescription {
        __components.add(BufferComponent.Ints(n))
        return this
    }

    fun floats(n: Int): BufferDescription {
        __components.add(BufferComponent.Floats(n))
        return this
    }

    fun doubles(n: Int): BufferDescription {
        __components.add(BufferComponent.Doubles(n))
        return this
    }

    fun commit(): BufferDescription {
        if (isCommitted)
            throw IllegalStateException()

        components = __components.clone() as ArrayList<BufferComponent>

        __components.forEach {comp->
            size += comp.sz
        }

        head = 0
        isCommitted = true

        return this
    }

    fun getComponent(componentIndex: Int): BufferComponent {
        return components.get(componentIndex)
    }

    fun getCurrentComponent(): BufferComponent {
        return components.get(getCurrentComponentIndex())
    }

    fun getCurrentComponentIndex(): Int {
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
        head = bytePosition
    }

    fun headToComponent(componentIndex: Int) {
        if (componentIndex >= components.size)
            throw IndexOutOfBoundsException()

        var head_tmp = 0
        for (i in 0 until componentIndex) {
            head_tmp += components[i].sz
        }

        this.head = head_tmp
    }

    fun headToNextComponent() {
        val currentComponentIndex = getCurrentComponentIndex()
        val nextComponentIndex = (currentComponentIndex + 1) % components.size
        headToComponent(nextComponentIndex)
    }
}