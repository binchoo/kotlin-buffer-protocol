package protocol.buffered

interface ProtocolReader {
    fun read()
    fun hasRemaining(): Boolean
}