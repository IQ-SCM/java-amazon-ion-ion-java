package com.amazon.ion.impl;

import com.amazon.ion.BufferConfiguration;

import java.io.IOException;
import java.nio.ByteBuffer;

// TODO try making LexerRefillable extend this? Performance experiment.
final class IonBinaryLexerFixedFromByteArray extends IonBinaryLexerBase {

    private final byte[] buffer;

    IonBinaryLexerFixedFromByteArray(BufferConfiguration<?> configuration, byte[] buffer, int offset, int length) {
        super(offset, configuration == null ? null : configuration.getDataHandler());
        this.buffer = buffer;
        this.offset = offset;
        this.limit = offset + length;
        this.capacity = limit;
        byteBuffer = ByteBuffer.wrap(buffer, offset, length);
    }

    @Override
    int peek(long index) {
        return buffer[(int) index] & SINGLE_BYTE_MASK;
    }

    @Override
    void copyBytes(long position, byte[] destination, int destinationOffset, int length) {
        System.arraycopy(buffer, (int) position, destination, destinationOffset, length);
    }

    @Override
    protected boolean carefulFillAt(long index, long numberOfBytes) {
        if (numberOfBytes > availableAt(index)) {
            // TODO? throw or notify user?
            return false;
        }
        return true;
    }

    @Override
    protected boolean seek(long numberOfBytes) {
        if (numberOfBytes > available()) {
            offset = limit;
            return false;
        }
        offset += numberOfBytes;
        return true;
    }

    @Override
    public void close() throws IOException {
        // Nothing to do.
    }
}
