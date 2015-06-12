
package pl.wavesoftware.util.preferences.impl.hiera;

import java.io.Serializable;
import java.util.Arrays;
import java.util.UUID;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import static org.hamcrest.Matchers.*;

import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 *
 * @author Krzysztof Suszyński <krzysztof.suszynski@wavesoftware.pl>
 */
public class HieraPreferencesFactoryTest implements Serializable {

    private static final long serialVersionUID = 1L;

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

    private static final String PRODUCTION = "production";

    @Test
    public void testGlobal() {
        runned = 0;
        HieraBackend.clearInstance();
        HieraPreferencesFactory.activate();
        HieraBackend.instance().runner = new CliRunner() {

            @Override
            public String run(final String command) throws KeyNotFoundException, BackingStoreException {
                runned++;
                return "true";
            }
        };
        final Preferences prefs = Preferences.userRoot();
        final boolean production = prefs.getBoolean(PRODUCTION, false);
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
        HieraBackend.instance().runner = new CliRunner() {

            @Override
            public String run(final String command) throws KeyNotFoundException, BackingStoreException {
                runned++;
                return "true";
            }
        };
        final Preferences prefs = Preferences.userRoot();
        prefs.getBoolean(PRODUCTION, false);
        prefs.getBoolean(PRODUCTION, false);
        prefs.getBoolean(PRODUCTION, false);
        prefs.getBoolean(PRODUCTION, false);
        prefs.getBoolean(PRODUCTION, false);
        boolean production = prefs.getBoolean(PRODUCTION, false);
        assertEquals("internal runner shuld be runned once", 1, runned);
        assertTrue("Production setting shuld be true", production);

        System.gc();

        prefs.getBoolean(PRODUCTION, false);
        production = prefs.getBoolean(PRODUCTION, false);
        assertEquals("internal runner shuld be runned once", 1, runned);
        assertTrue("Production setting shuld be true", production);

        HieraBackend.clearInstance();
    }

    @Test
    public void testDefaultProvider() throws BackingStoreException {
        testGlobal();
        HieraPreferencesFactory.restore();
        final Preferences prefs = Preferences.userRoot();
        final String key = "key-" + UUID.randomUUID().toString().replace("-", "");
        final String expected = "SomeString";
        String fetched = prefs.get(key, expected);
        assertEquals(expected, fetched);
        final String other = "OtherString";
        prefs.put(key, other);
        fetched = prefs.get(key, expected);
        assertEquals(other, fetched);
        prefs.remove(key);
        assertFalse(Arrays.asList(prefs.keys()).contains(key));
    }

    @Test
    public void testStaticCreateUserRoot() {
        Preferences prefs = HieraPreferencesFactory.createUserRoot();
        assertNotNull(prefs);
    }

    @Test
    public void testStaticCreateSystemRoot() {
        Preferences prefs = HieraPreferencesFactory.createSystemRoot();
        assertNotNull(prefs);
    }

    @Test
    public void testInvalidGetField() {

    }

    @Test
    public void testInvalidFindPlatformFactoryClass() {
        Class<?> defaultValue = HieraPreferencesFactory.findPlatformFactoryClass("UnknownOS Invalid");
        assertNotNull(defaultValue);
        assertEquals("java.util.prefs.FileSystemPreferencesFactory", defaultValue.getName());

        final String osName = System.getProperty("os.name");
        if (osName.startsWith("Windows")) {
            Class<?> windowsClass = HieraPreferencesFactory.findPlatformFactoryClass("Windows 7");
            assertEquals("java.util.prefs.WindowsPreferencesFactory", windowsClass.getName());
        } else {
            try {
                HieraPreferencesFactory.findPlatformFactoryClass("Windows 8");
                fail("Expected to throw DeveloperError(ClassNotFoundException) on non-windows machine");
            } catch (DeveloperError de) {
                assertThat(de, is(instanceOf(DeveloperError.class)));
                ClassNotFoundException cnfe = ClassNotFoundException.class.cast(de.getCause());
                assertThat(cnfe.getLocalizedMessage(), containsString("java/util/prefs/WindowsPreferencesFactory"));
            }
        }

        if (osName.contains("OS X")) {
            Class<?> macClass = HieraPreferencesFactory.findPlatformFactoryClass("MacOS X Lion");
            assertEquals("java.util.prefs.MacOSXPreferencesFactory", macClass.getName());
        } else {
            try {
                HieraPreferencesFactory.findPlatformFactoryClass("MacOS X");
                fail("Expected to throw DeveloperError(ClassNotFoundException) on non-mac machine");
            } catch (DeveloperError de) {
                assertThat(de, is(instanceOf(DeveloperError.class)));
                ClassNotFoundException cnfe = ClassNotFoundException.class.cast(de.getCause());
                assertThat(cnfe.getLocalizedMessage(), containsString("java/util/prefs/MacOSXPreferencesFactory"));
            }
        }
    }

    @After
    public void post() {
        HieraPreferencesFactory.restore();
    }
}
