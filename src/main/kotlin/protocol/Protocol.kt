package protocol

interface Protocol {
    fun commit(): Protocol
}