package protocol.data

interface DataHandler<T> {
    fun handle(data: T, hintValue: Int)
}

interface ByteHandler: DataHandler<Byte>
interface CharHandler: DataHandler<Char>
interface ShortHandler: DataHandler<Short>
interface IntHandler: DataHandler<Int>
interface FloatHandler: DataHandler<Float>
interface DoubleHandler: DataHandler<Double>