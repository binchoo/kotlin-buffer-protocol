package dataprotocol.buffered

interface ProtocolReader {
    fun read()
    fun readComponent()
    fun hasRemaining(): Boolean
}