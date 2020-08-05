## kotlin-data-protocol

 To make a series of bytes meaningful, we establish a protocol so that each part of the data must have the specified order, size, and values. They are written to the buffer, or feteched from the buffer both with compliance of the protocol.

Writing code that implements a protocol is very exhuastive and petty.

What if there exists a machine that applies the injected protocol to the injected data series? 

BufferedProtocol is a framework that fetches typed data from the given byte-buffer with iteration, and runs a registered callback (which applies protocol) according to the datatype to be handled. So it is a automatic buffer reading machine with protocol.

## Classes

[diagram.pdf](https://github.com/binchoo/kotlin-buffer-protocol/blob/master/diagram/diagram.pdf)

BufferDescriptor describes a buffer as series of BufferComponents.

BufferComponent is a data block that has a distinct meaning.

## What Kind of Protocol Can Be Implemented

## Usage


