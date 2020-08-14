# Kotlin-DataProtocol

## Abstracts

> 무의미해 보여도 의미 있게 바라보는 시선만 있으면 됩니다.

###  Purpose

복잡한 데이터를 프로세스 사이에서  교환하려면, 데이터 프로토콜을 수립한 뒤 이에 맞게 데이터를 작성하고 해석해야 합니다.  `kotlin-dataprotocol` 은 **프로토콜 대로 데이터를 해석하는** 코드의 기반이 되려고 합니다.  작성해야 할 코드의 양을 줄이며 다양한 데이터 프로토콜을 쉽게 작성하도록 하는, 코틀린 프레임워크가 되는 것이 목적입니다.



### Principles

버퍼에 쌓인 바이트들(`ByteBuffer`)이 의미를 가지려면  해석할 규칙(`DataProtocol`)이 있어야 합니다. 그러므로 이 둘은 함께 묶여서 `ProtocolBuffer` 가 됩니다. 이제 `ProtocolBuffer`로부터 바이트 단위가 아닌 데이터로 단위로 값을 추출할 수 있습니다. 여기에 `ProtocolBufferReader`의 도움을 받는다면 **데이터 타입 별 콜백 호출에 기반**하여 풍부한 표현을 추가할 수 있을 것입니다.



## Usages

### Example1: Simple Data Protocol

| Data Component Name | Data Count | Data Type | Total Size | Byte Order |
| ------------------- | ---------- | --------- | ---------- | ---------- |
| header1             | 10         | Char      | 20 Bytes   | BIG ENDIAN |
| header2             | 1          | Byte      | 1 Bytes    | BIG ENDIAN |
| data                | 10         | Short     | 20 Bytes   | BIG ENDIAN |



#### 1. Define a DataProtocol

`DataProtocol.Builder` 객체를 사용하여 `DataProtocol`을 작성하세요.

```kotlin
val protocol = DataProtocol.Builder().chars(10).bytes(1).shorts(10).build()
```

⚠️ `DataProtocol.Builder` 객체는 **오직 한 번만** `DataProtocol` 객체를 생성할 수 있습니다.



⚠️ `ByteOrder` 기본값은 `ByteOrder.BIG_ENDIAN` 입니다. 프로토콜이 자료를 버퍼에 쓸 때 `ByteOrder.LITTEN_ENDIAN`을 사용한다면 `.chars(10, ByteOrder.LITTLE_ENDIAN)` 처럼 명시합니다.



#### 2. Prepare a ByteBuffer to Be Parsed

해석해야 할 바이트들을 담고 있는 `ByteBuffer`를 준비하세요.

```kotlin
val rawBuffer = ByteBufferCompat.allocate(protocol, num=10)
// 프로토콜 패킷을 10개 담을 수 있는 크기의 바이트 버퍼를 생성합니다.
rawBuffer.put(b) ...
// 통신 따위로 수신한 바이트들을 버퍼에 채워넣습니다.
```

⚠️ `kotlin-dataprotocol` 프레임워크는 데이터 프로토콜에 따라 버퍼를 해석하는 일을 담당합니다. 바이트의 수신과 버퍼를 채워넣는 일은 개별 구현 몫입니다.



#### 3. Create a ProtocolBuffer

`ByteBuffer`와 `DataProtocol` 을 묶어 `ProtocolBuffer`로 만듭니다.

```kotlin
val protoBuff = ProtocolBuffer(rawBuffer, protocol)
```



#### 4. Customize the ProtocolBufferReader Class

`ProtocolBufferReader` 를 상속하여 `onHandlerSetup()`을 구현하는 클래스를 작성합니다.

`onHandlerSetup()`  메서드는 `ProtocolBuffer`에서 취득한 데이터를 핸들링하는 `TypeHandler` 를 등록하는 곳입니다.

`TypeHandler`의 두 번째 인자 `handlingHint`에는 해당 데이터가 속한 **데이터 컴포넌트의 위치**가 입력됩니다.

예를 들어, header1에 속한 Char 자료들를 읽을 때 `handlingHint` 값은 0입니다.

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
                    header2.add(data)
            }

            addShortHandler { data, handlingHint ->
                if (handlingHint == 2)
                    shortData.add(data)
            }
        }
    }
}
```



#### 5. Instantiate a ProtocolBufferReader and Start Parsing

`ProtocolBufferReader.readByData()`를 호출하면, **프로토콜 버퍼의 모든 데이터를 취하면서 콜백을 실행**합니다.

```kotlin
val reader = MyReader(protoBuff)
reader.readByData()
```



### Example2: Simple but Lazy Data Protocol

| Data Component Name | Data Count                   | Data Type | Total Size  | Byte Order |
| ------------------- | ---------------------------- | --------- | ----------- | ---------- |
| header              | 1                            | Byte      | 1 Byte      | BIG ENDIAN |
| data                | cnt; the value of its header | Int       | 4*cnt Bytes | BIG ENDIAN |



#### 1. Define DataProtocol

이 프로토콜에 따르면, 데이터 부분의 정수 개수는 헤더 값에 의해 정해집니다. 이렇게 데이터 컴포넌트의 **데이터의 수가 뒤늦게 결정될 때** `DataProtocol.DECLARE_LAZY_COUNT`를 선언하세요.

```kotlin
val protocol = DataProtocol.Builder().bytes(1).ints(DataProtocol.DECLARE_LAZY_COUNT).build()
```



#### 4. Customize ProtocolBufferReader Class

결국 나중에 Lazy 하게 데이터 갯수를 결정해주어야 합니다. 이 때,  `ProtocolBuffer.changeComponentDataCount()` 함수를 이용하세요. `ProtocolBufferReader`의 멤버 변수인  `protocolBuffer`에게 부탁해주세요.

```kotlin
class SimpleButLazyDataProtocol {
    ...
    inner class MyReader(protoBuff: ProtocolBuffer): ProtocolBufferReader(protoBuff) {
        override fun onHandlerSetup() {
            addByteHandler { data, handlingHint ->
                if (handlingHint == 0)
                    protocolBuffer.changeComponentDataCount(1, data.toInt())
                    //Lazily Setting the Data Count of the DataComponent1.
            }

            addIntHandler { data, handlingHint ->
                if (handlingHint == 1)
                    println("this is int data $data")
            }
        }
    }
}
```

⚠️ `DataProtocol.DECLARE_LAZY_COUNT` 를 명시해 놓고, 데이터 갯수를 정의하지 않은 상태로 데이터 컴포넌트에 접근하면 `IllegalStateException`이 발생합니다.



⚠️ `DataProtocol.DECLARE_LAZY_COUNT` 가 명시되지 않았으나, 갯수를 수정하고자 하면 `IllegalStateException`이 발생합니다.



### Example Application: Simple Protocol Communication with Arduino

Go to [app_arduino_simple](https://github.com/binchoo/kotlin-dataprotocol/tree/master/app_arduino_simple).

## Class Diagram

[Class Diagram PDF](https://github.com/binchoo/kotlin-dataprotocol/blob/master/diagram/class_diagram.pdf)

 클래스 다이어그램에는 클래스의 모든 멤버가 표현되지 않으며 간혹 실제 코드와 불일치하는 부분과 오타 등을 포함할 수 있습니다.



## Import Library

- In project-level `build.gradle`

```groovy
allprojects {
    repositories {
        google()
        jcenter()
        maven { url "https://jitpack.io" }
    }
}
```

- In app-level `build.gradle`

```groovy
implementation 'com.github.binchoo:kotlin-dataprotocol:1.0.1'
```

- Sync Gradle!