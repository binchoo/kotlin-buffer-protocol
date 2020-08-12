package dataprotocol.typehandle

typealias TypeHandler<T> = ((data: T, handlingHint: Int)-> Unit)