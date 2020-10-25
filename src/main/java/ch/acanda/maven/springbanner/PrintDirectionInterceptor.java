package ch.acanda.maven.springbanner;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

/**
 * This works around a bug in dtmo-jfiglet 1.0.0 that renders text by default
 * right-to-left instead of left-to-right.
 * See GitHub issue <a href="https://github.com/dtmo/jfiglet/issues/1">Default print direction</a>.
 * This class can be removed when the bug is fixed.
 * <p>
 * If the font does not define a print direction, this interceptor inserts
 * the default left-to-right print direction.
 */
public class PrintDirectionInterceptor extends InputStream {

    private static final int POS_PRINT_DIR = 6;

    private final PushbackInputStream stream;
    private final boolean isFigFont;
    private boolean isHeader = true;
    private int position;

    public PrintDirectionInterceptor(final InputStream stream) throws IOException {
        this.stream = new PushbackInputStream(stream, 5);
        final byte[] signature = new byte[5];
        final int bytesRead = this.stream.read(signature, 0, 5);
        this.stream.unread(signature, 0, bytesRead);
        isFigFont = bytesRead == 5
                    && signature[0] == 'f'
                    && signature[1] == 'l'
                    && signature[2] == 'f'
                    && signature[3] == '2'
                    && signature[4] == 'a';
    }

    @Override
    public int read() throws IOException {
        if (!isFigFont || !isHeader) {
            return stream.read();
        }
        int b = stream.read();
        if (b == ' ') {
            position++;
        } else if (b == '\r' || b == '\n') {
            isHeader = false;
            if (position == POS_PRINT_DIR - 1) {
                // insert " 0" before line break
                stream.unread(b);
                stream.unread('0');
                b = ' ';
            }
        }
        return b;
    }

}
