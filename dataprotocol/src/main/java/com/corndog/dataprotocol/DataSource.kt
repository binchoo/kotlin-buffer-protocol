package com.corndog.dataprotocol

class DataSource private constructor(private val packetProvider: PacketProvider) {
    companion object {
        fun from(pp: PacketProvider): DataSource {
            return DataSource(pp)
        }
    }
}