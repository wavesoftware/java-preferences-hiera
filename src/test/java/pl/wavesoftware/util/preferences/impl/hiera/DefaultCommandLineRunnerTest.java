package pl.wavesoftware.util.preferences.impl.hiera;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.prefs.BackingStoreException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.core.Is.is;
import static org.junit.internal.matchers.ThrowableMessageMatcher.hasMessage;
import static org.mockito.Mockito.when;

/**
 * @author Krzysztof Suszyński <krzysztof.suszynski@wavesoftware.pl>
 * @since 2015-11-28
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultCommandLineRunnerTest {

    @Mock
    private Runtime runtime;

    @Mock
    private Process process;

    @InjectMocks
    private DefaultCommandLineRunner runner;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testDoubleCheckTheSetup() throws Exception {
        Field field = DefaultCommandLineRunner.class.getDeclaredField("runtime");
        assertThat(runner).isNotNull();
        try {
            field.setAccessible(true);
            Object value = field.get(runner);
            assertThat(value).isNotNull();
            assertThat(value).isSameAs(runtime);
        } finally {
            field.setAccessible(false);
        }
    }

    @Test
    public void testRun() throws Exception {
        // given
        String[] command = new String[]{ "dir" };
        String stdout = "some files output";
        String stderr = "";
        int exitCode = 0;
        when(runtime.exec(command)).thenReturn(process);
        stubProcess(exitCode, stdout, stderr);

        // when
        String resolved = runner.run(command);

        // then
        assertThat(resolved).isEqualTo(stdout);
    }

    @Test
    public void testRun_IllegalCommand() throws Exception {
        // given
        String[] command = new String[]{ "non-existing-command" };
        String stdout = "";
        String stderr = "command do not exists";
        int exitCode = 127;
        when(runtime.exec(command)).thenReturn(process);
        stubProcess(exitCode, stdout, stderr);

        // then
        thrown.expect(BackingStoreException.class);
        thrown.expectMessage("[127] command do not exists");

        // when
        runner.run(command);
    }

    @Test
    public void testRun_Failure() throws Exception {
        // given
        String[] command = new String[]{ "hiera", "--non-existing" };
        String stderr = "invalid option: --non-existing (OptionParser::InvalidOption)";
        when(runtime.exec(command)).thenThrow(new IOException(stderr));

        // then
        thrown.expect(BackingStoreException.class);
        thrown.expectMessage(containsString(IOException.class.getName()));
        thrown.expectMessage(containsString(stderr));
        thrown.expectCause(isA(IOException.class));
        thrown.expectCause(hasMessage(is(stderr)));

        // when
        runner.run(command);
    }

    private void stubProcess(int exitCode, String stdout, String stderr) {
        when(process.exitValue()).thenReturn(exitCode);
        when(process.getInputStream()).thenReturn(new ByteArrayInputStream(stdout.getBytes()));
        when(process.getErrorStream()).thenReturn(new ByteArrayInputStream(stderr.getBytes()));
    }
}