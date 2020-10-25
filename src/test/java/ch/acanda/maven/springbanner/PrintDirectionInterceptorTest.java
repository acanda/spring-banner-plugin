package ch.acanda.maven.springbanner;

import org.codehaus.plexus.util.IOUtil;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.assertj.core.api.Assertions.assertThat;

public class PrintDirectionInterceptorTest {

    @ParameterizedTest
    @ValueSource(strings = {"\n", "\r\n"})
    public void shouldKeepPrintDirection(final String lineSeparator) throws IOException {
        final byte[] bytes = createFontFile("flf2a$ 6 5 16 15 1 1 24463 229", lineSeparator).getBytes(US_ASCII);
        final PrintDirectionInterceptor interceptor = new PrintDirectionInterceptor(new ByteArrayInputStream(bytes));

        final String intercepted = IOUtil.toString(interceptor, US_ASCII.name());

        assertThat(intercepted).isEqualTo(createFontFile("flf2a$ 6 5 16 15 1 1 24463 229", lineSeparator));
    }

    @ParameterizedTest
    @ValueSource(strings = {"\n", "\r\n"})
    public void shouldInsertDefaultPrintDirectionIfMissing(final String lineSeparator) throws IOException {
        final byte[] bytes = createFontFile("flf2a$ 5 4 20 15 1", lineSeparator).getBytes(US_ASCII);
        final PrintDirectionInterceptor interceptor = new PrintDirectionInterceptor(new ByteArrayInputStream(bytes));

        final String intercepted = IOUtil.toString(interceptor, US_ASCII.name());

        assertThat(intercepted).isEqualTo(createFontFile("flf2a$ 5 4 20 15 1 0", lineSeparator));
    }

    private static String createFontFile(final String header, final String lineSeparator) {
        return header + lineSeparator + "Hi" + lineSeparator;
    }

}
