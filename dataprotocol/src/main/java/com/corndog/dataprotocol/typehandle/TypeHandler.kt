package dataprotocol.typehandle

interface TypeHandler<T> {
    fun handle(data: T, handlingHint: Int)
}

interface ByteHandler: TypeHandler<Byte>
interface CharHandler: TypeHandler<Char>
interface ShortHandler: TypeHandler<Short>
interface IntHandler: TypeHandler<Int>
interface FloatHandler: TypeHandler<Float>
interface DoubleHandler: TypeHandler<Double>