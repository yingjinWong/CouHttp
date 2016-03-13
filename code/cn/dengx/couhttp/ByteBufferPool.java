package cn.dengx.couhttp;

import android.support.annotation.NonNull;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Current project:CouHttp.
 * Created by dengx on 16/3/5,23:23.
 */
public class ByteBufferPool {
    /**
     * default size is 8k
     */
    public static final int DEFAULT_SIZE = 1024 * 8;

    private int capacity;
    private int bufferSize;
    private volatile int lose;
    private LinkedBlockingQueue<ByteBuffer> buffers;

    public ByteBufferPool(int capacity) {
        this(capacity, DEFAULT_SIZE);
    }

    public ByteBufferPool(int capacity, int bufferSize) {
        this.capacity = capacity;
        this.bufferSize = bufferSize;
        lose = 0;
        buffers = new LinkedBlockingQueue<>(capacity);
    }

    public synchronized ByteBuffer borrow() {
        int size = buffers.size();
        int total = size + lose;
        if (total < capacity) {
            lose++;
            return new ByteBuffer(bufferSize);
        } else if (total == capacity) {
            try {
                return buffers.poll();
            } finally {
                lose++;
            }
        } else {
            throw new IndexOutOfBoundsException("byteBufferPool total is " + total
                    + " bigger than capacity " + capacity);
        }
    }

    public synchronized void restore(@NonNull ByteBuffer byteBuffer) {
        buffers.add(byteBuffer);
        lose--;
    }
}
