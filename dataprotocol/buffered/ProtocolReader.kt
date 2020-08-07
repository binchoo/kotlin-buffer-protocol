package protocol.buffered

interface ProtocolReader {
    fun read()
    fun readComponent()
    fun hasRemaining(): Boolean
}