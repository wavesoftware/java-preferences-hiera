/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.wavesoftware.util.preferences.impl.hiera;

import java.util.Arrays;
import java.util.UUID;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author Krzysztof Suszyński <krzysztof.suszynski@wavesoftware.pl>
 */
public class HieraPreferencesFactoryTest {

    /**
     * Test of systemRoot method, of class HieraPreferencesFactory.
     */
    @Test
    public void testSystemRoot() {
        final HieraPreferencesFactory instance = new HieraPreferencesFactory();
        final Preferences result = instance.systemRoot();
        assertNotNull("Result should be not null", result);
    }

    /**
     * Test of userRoot method, of class HieraPreferencesFactory.
     */
    @Test
    public void testUserRoot() {
        final HieraPreferencesFactory instance = new HieraPreferencesFactory();
        final Preferences result = instance.userRoot();
        assertNotNull("Result should be not null", result);
    }

    private transient int runned = 0;

    @Test
    public void testGlobal() {
        runned = 0;
        HieraBackend.clearInstance();
        HieraPreferencesFactory.activate();
        HieraBackend.instance().runner = new HieraBackend.CliRunner() {

            @Override
            public String run(final String command) throws HieraBackend.KeyNotFoundException, BackingStoreException {
                runned++;
                return "true";
            }
        };
        final Preferences prefs = Preferences.systemRoot();
        final boolean production = prefs.getBoolean("production", false);
        HieraBackend.clearInstance();
        assertTrue("Production setting shuld be true", production);
        assertEquals("internal runner shuld be runned once", 1, runned);
    }

    @Test
    public void testCaching() {
        runned = 0;
        HieraBackend.clearInstance();
        HieraBackend.instance().resetCache();
        HieraPreferencesFactory.activate();
        HieraBackend.instance().runner = new HieraBackend.CliRunner() {

            @Override
            public String run(final String command) throws HieraBackend.KeyNotFoundException, BackingStoreException {
                runned++;
                return "true";
            }
        };
        final Preferences prefs = Preferences.systemRoot();
        prefs.getBoolean("production", false);
        prefs.getBoolean("production", false);
        prefs.getBoolean("production", false);
        prefs.getBoolean("production", false);
        prefs.getBoolean("production", false);
        boolean production = prefs.getBoolean("production", false);

        HieraBackend.clearInstance();
        assertEquals("internal runner shuld be runned once", 1, runned);
        assertTrue("Production setting shuld be true", production);
    }

    @Test
    public void testDefaultProvider() throws BackingStoreException {
        testGlobal();
        HieraPreferencesFactory.restore();
        final Preferences prefs = Preferences.systemRoot();
        String key = "key-" + UUID.randomUUID().toString().replace("-", "");
        String expected = "SomeString";
        String fetched = prefs.get(key, expected);
        assertEquals(expected, fetched);
        String other = "OtherString";
        prefs.put(key, other);
        fetched = prefs.get(key, expected);
        assertEquals(other, fetched);
        prefs.remove(key);
        assertFalse(Arrays.asList(prefs.keys()).contains(key));
    }

    @After
    public void post() {
        HieraPreferencesFactory.restore();
    }
}
