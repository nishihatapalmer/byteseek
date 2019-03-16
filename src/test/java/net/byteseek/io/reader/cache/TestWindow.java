package net.byteseek.io.reader.cache;

import net.byteseek.io.reader.windows.Window;
import net.byteseek.io.reader.windows.WindowFactory;

import java.io.IOException;

public class TestWindow implements Window {

    public static final WindowFactory FACTORY = new WindowFactory() {

        @Override
        public Window createWindow(byte[] arrayToWrap, long position, int length) {
            return new TestWindow(arrayToWrap, position);
        }
    };

    private final byte[] array;
    private final long winPos;

    public TestWindow() {
        this(new byte[1024], 0);
    }

    public TestWindow(final long position) {
        this(new byte[1024], position);
    }

    public TestWindow(final byte[] array, final long position) {
        this.array = array;
        this.winPos = position;
    }

    @Override
    public byte getByte(int position) throws IOException {
        return array[position];
    }

    @Override
    public byte[] getArray() throws IOException {
        return array;
    }

    @Override
    public long getWindowPosition() {
        return winPos;
    }

    @Override
    public long getWindowEndPosition() {
        return winPos + array.length - 1;
    }

    @Override
    public long getNextWindowPosition() {
        return winPos + array.length;
    }

    @Override
    public int length() {
        return array.length;
    }
}
