# Kotlin-DataProtocol

## Abstracts

> 무의미해 보여도 의미 있게 바라보는 시선만 있으면 됩니다.

###  Purpose

프로세스 간 복잡한 데이터를 교환할 땐 공통된 데이터 프로토콜을 수립하고 여기에 맞추어 데이터를 작성 및 해석해야 합니다.  `kotlin-dataprotocol` 은 **프로토콜에 따라 데이터를 해석하는** 코드의 기반을 제공하여,  프로그래머가 작성해야 할 코드의 양을 줄이고,  데이터 프로토콜을 쉽게 구현하여 사용하는 프레임워크가 되는 것이 목표로 합니다.



### Principles

버퍼에 쌓인 바이트들은(`ByteBuffer`) 그걸 해석할 규칙(`DataProtocol`)이 있어야 의미를 가질 수 있습니다. 그러므로 이 둘은 함께 묶여서 `ProtocolBuffer` 가 됩니다. 이제 버퍼에서 바이트 단위가 아닌 데이터로 단위로 값을 추출하게 됩니다. `ProtocolBufferReader`의 도움을 받아 **데이터 타입 별 콜백 호출에 기반**하여 프로토콜을 구현할 수 있게 됩니다.



## Usages

### Example1: Simple Data Protocol

- header1 / 20 bytes / Char Data * 10 / BIG_ENDIAN
- header2 / 1 byte / Byte Data * 1 / BIG_ENDIAN
- data / 20 bytes / Short Data * 10 / BIG_ENDIAN



#### 1. Define a DataProtocol

`DataProtocol.Builder`를 사용하여 `DataProtocol`을 작성합니다.

```kotlin
val protocol = DataProtocol.Builder().chars(10).bytes(1).shorts(10).build()
```

⚠️ `DataProtocol.Builder` 객체는 오직 한 번만 `DataProtocol` 객체를 생성할 수 있습니다.



⚠️ `ByteOrder` 기본값은 `ByteOrder.BIG_ENDIAN` 입니다. 프로토콜이 자료를 버퍼에 쓸 때 `ByteOrder.LITTEN_ENDIAN`을 사용한다면 `.chars(10, ByteOrder.LITTLE_ENDIAN)` 처럼 명시할 수 있습니다.



#### 2. Prepare a ByteBuffer to Be Parsed

```kotlin
val rawBuffer = ByteBufferCompat.allocate(protocol, num=10)
// 프로토콜 패킷을 10개 담을 수 있는 크기의 바이트 버퍼를 생성합니다.
rawBuffer.put(b) ...
// 통신 따위로 수신한 바이트들을 버퍼에 채워넣습니다.
```

⚠️ `kotlin-dataprotocol` 프레임워크는 버퍼를 데이터 프로토콜에 따라 해석하는 부분을 담당합니다. 바이트 수신과 버퍼를 채우는 일은 개별 구현 몫입니다.



#### 3. Create a ProtocolBuffer

`ByteBuffer`와 `DataProtocol` 을 묶어 `ProtocolBuffer`로 만듭니다.

```kotlin
val protoBuff = ProtocolBuffer(rawBuffer, protocol)
```



#### 4. Customize the ProtocolBufferReader Class

`ProtocolBufferReader` 를 상속하고 `onHandlerSetup()`을 구현하는 클래스를 작성합니다.

`onHandlerSetup()` 에서는 프로토콜 버퍼에서 취한 데이터를 핸들링하는 `TypeHandler`을 등록해 줍니다.

`handlingHint`에는 데이터 프로토콜에서 해당 데이터 컴포넌트가 자리한 순번이 전달됩니다.

```kotlin
class SimpleDataProtocol {
    var header1 = ArrayList<Char>(100)
    var header2 = ArrayList<Byte>(10)
    val shortData = ArrayList<Short>(100)
		...
    inner class MyReader(protoBuff: ProtocolBuffer): ProtocolBufferReader(protoBuff) {
        override fun onHandlerSetup() {
            addCharHandler { data, handlingHint ->
                if (handlingHint == 0)
                    header1.add(data)
            }

            addByteHandler { data, handlingHint ->
                if (handlingHint == 1)
                    header2 = data
            }

            addShortHandler { data, handlingHint ->
                if (handlingHint == 2)
                    shortData.add(data)
            }
        }
    }
}
```



#### 5. Instantiate ProtocolBufferReader and Start Parsing

`readByData()`를 호출하면 프로토콜 버퍼의 모든 데이터를 취하면서 콜백을 실행합니다.

```kotlin
val reader = MyReader(protoBuff)
reader.readByData()
```



### Example2: Simple but Lazy Data Protocol

- header / 1 byte / Byte Data * 1 / BIG_ENDIAN

- data / 4 * (value of the header) bytes / Int Data * (value of the header) / BIG_ENDIAN



#### 1. Define DataProtocol

프로토콜에 따르면 데이터 부분의 정수 개수는 헤더 값에 의해 정해집니다. 이렇게 데이터의 수가 뒤늦게 결정될 때에는 `DataProtocol.DECLARE_LAZY_COUNT`를 사용합니다.

```kotlin
val protocol = DataProtocol().bytes(1).ints(DataProtocol.DECLARE_LAZY_COUNT).build()
```



#### 4. Customize ProtocolBufferReader Class

Lazy 하게 데이터 갯수를 조정하려면 `changeComponentDataCount()` 함수를 이용하도록 합니다.   `ProtocolBufferReader`의 멤버 변수 `protocolBuffer`를 이용합니다.

```kotlin
class SimpleButLazyDataProtocol {
    inner class MyReader(protoBuff: ProtocolBuffer): ProtocolBufferReader(protoBuff) {
        override fun onHandlerSetup() {
            addByteHandler { data, handlingHint ->
                if (handlingHint == 0)
                    protocolBuffer.changeComponentDataCount(1, data.toInt())
            }

            addShortHandler { data, handlingHint ->
                if (handlingHint == 1)
                    println("this is int data $data")
            }
        }
    }
}
```

⚠️ `DataProtocol.DECLARE_LAZY_COUNT` 를 명시하였으나 그 갯수를 아직 정의하지 않은 상태로 접근한다면 해당 컴포넌트에서 `IllegalStateException`을 발생시킵니다.



⚠️ `DataProtocol.DECLARE_LAZY_COUNT` 를 명시되지 않았으나 그 갯수를 수정하고자 하면 `IllegalStateException`이 발생합니다.



### Class Diagram

[Class Diagram PDF](https://github.com/binchoo/kotlin-dataprotocol/blob/master/diagram/class_diagram.pdf)

 클래스 다이어그램에는 클래스의 모든 멤버가 표현되지 않으며 간혹 실제 코드와 불일치하는 부분과 오타 등을 포함할 수 있습니다.



## APIs


