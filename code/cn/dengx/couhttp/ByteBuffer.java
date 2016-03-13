package cn.dengx.couhttp;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.InvalidMarkException;

/**
 * Current project:CouHttp.
 * Created by dengx on 16/3/5,17:02.
 */
public class ByteBuffer {

    private int mark;
    private int position;
    private int limit;
    private int capacity;

    private final byte[] bytes;

    ByteBuffer(int cap) {
        this(-1, 0, cap, cap);
    }

    ByteBuffer(int mark, int pos, int lim, int cap) {
        if (cap < 0)
            throw new IllegalArgumentException("Negative capacity: " + cap);
        this.capacity = cap;
        this.mark = mark;
        this.position = pos;
        this.limit = lim;
        bytes = new byte[cap];
    }

    public int read(InputStream in) throws IOException {
        if (in == null)
            throw new NullPointerException();
        int length = in.read(bytes, position(), remaining());
        if (length >= 0)
            position(position() + length);
        return length;
    }

    public ByteBuffer write(OutputStream out) throws IOException {
        if (out == null)
            throw new NullPointerException();
        out.write(bytes, position(), remaining());
        position(position() + remaining());
        return this;
    }


    public final int capacity() {
        return capacity;
    }

    public final int position() {
        return position;
    }

    public final ByteBuffer position(int newPosition) {
        if ((newPosition > limit) || (newPosition < 0))
            throw new IllegalArgumentException();
        position = newPosition;
        if (mark > position) mark = -1;
        return this;
    }

    public final int limit() {
        return limit;
    }

    public final ByteBuffer limit(int newLimit) {
        if ((newLimit > capacity) || (newLimit < 0))
            throw new IllegalArgumentException();
        limit = newLimit;
        if (position > limit) position = limit;
        if (mark > limit) mark = -1;
        return this;
    }

    public final ByteBuffer mark() {
        mark = position;
        return this;
    }

    public final ByteBuffer reset() {
        int m = mark;
        if (m < 0)
            throw new InvalidMarkException();
        position = m;
        return this;
    }

    public final ByteBuffer clear() {
        position = 0;
        limit = capacity;
        mark = -1;
        return this;
    }

    public final ByteBuffer flip() {
        limit = position;
        position = 0;
        mark = -1;
        return this;
    }

    public final ByteBuffer rewind() {
        position = 0;
        mark = -1;
        return this;
    }

    public final int remaining() {
        return limit - position;
    }

    public final boolean hasRemaining() {
        return position < limit;
    }

    public byte get() {
        return bytes[nextGetIndex()];
    }

    public byte get(int index) {
        return bytes[nextGetIndex(index)];
    }

    public ByteBuffer put(byte b) {
        bytes[nextPutIndex()] = b;
        return this;
    }

    public ByteBuffer put(int index, byte b) {
        bytes[checkIndex(index)] = b;
        return this;
    }

    public ByteBuffer get(byte[] dst) {
        return get(dst, 0, dst.length);
    }

    public ByteBuffer get(byte[] dst, int offset, int length) {
        if (dst == null)
            throw new NullPointerException();
        checkBounds(offset, length, dst.length);
        if (length > remaining())
            throw new BufferUnderflowException();
        System.arraycopy(bytes, position(), dst, offset, length);
        position(position() + length);
        return this;
    }

    public ByteBuffer put(byte[] src) {
        return put(src, 0, src.length);
    }

    public ByteBuffer put(byte[] src, int offset, int length) {
        if (src == null)
            throw new NullPointerException();
        checkBounds(offset, length, src.length);
        if (length > remaining())
            throw new BufferOverflowException();
        System.arraycopy(src, offset, bytes, position(), length);
        position(position() + length);
        return this;
    }

    final int nextGetIndex() {
        if (position >= limit)
            throw new BufferUnderflowException();
        return position++;
    }

    final int nextGetIndex(int nb) {
        if (limit - position < nb)
            throw new BufferUnderflowException();
        int p = position;
        position += nb;
        return p;
    }

    final int nextPutIndex() {
        if (position >= limit)
            throw new BufferOverflowException();
        return position++;
    }

    final int checkIndex(int i) {
        if ((i < 0) || (i >= limit))
            throw new IndexOutOfBoundsException();
        return i;
    }

    static void checkBounds(int off, int len, int size) {
        if ((off | len | (off + len) | (size - (off + len))) < 0)
            throw new IndexOutOfBoundsException();
    }

    @Override
    public String toString() {
        return "ByteBuffer{" +
                "mark=" + mark +
                ", position=" + position +
                ", limit=" + limit +
                ", capacity=" + capacity +
                '}';
    }
}
