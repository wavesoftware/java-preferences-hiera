/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.wavesoftware.util.preferences.impl.hiera;

import java.io.IOException;
import java.util.prefs.BackingStoreException;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 *
 * @author Krzysztof Suszyński <krzysztof.suszynski@wavesoftware.pl>
 */
public class HieraBackendTest {

    @Before
    public void beforeTest() {
        HieraBackend.clearInstance();
    }

    /**
     * Test of get method, of class HieraBackend.
     */
    @Test
    public void testGet_String_String() throws Exception {
        String key = "samplekey";
        String defaultValue = "val2";
        HieraBackend instance = HieraBackend.instance();
        String expResult = "val2";
        try {
            String result = instance.get(key, defaultValue);
            assertEquals(expResult, result);
            // if hiera is installed
        } catch (BackingStoreException bse) {
            if (bse.getCause() instanceof IOException) {
                assertThat(bse.getCause().getLocalizedMessage(), containsString("\"hiera\""));
            } else {
                assertThat(bse.getCause(), is(instanceOf(InterruptedException.class)));
            }
        }
    }

    /**
     * Test of get method, of class HieraBackend.
     */
    @Test
    public void testGet_String() throws Exception {
        String key = "production";
        HieraBackend instance = new HieraBackendMock();
        String expResult = "false";
        String result = instance.get(key);
        assertEquals(expResult, result);
    }

    private static final class HieraBackendMock extends HieraBackend {

        public HieraBackendMock() {
            runner = new CliRunner() {

                @Override
                public String run(String command) throws KeyNotFoundException, BackingStoreException {
                    return "false";
                }
            };
        }
    }
}
