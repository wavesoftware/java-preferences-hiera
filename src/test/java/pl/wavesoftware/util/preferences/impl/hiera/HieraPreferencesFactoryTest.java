/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.wavesoftware.util.preferences.impl.hiera;

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

    private transient boolean runned = false;

	@Test
	public void testGlobal() {
		runned = false;
		HieraBackend.clearInstance();
        HieraPreferencesFactory.activate();
        HieraBackend.instance().runner = new HieraBackend.CliRunner() {

            @Override
            public String run(final String command) throws HieraBackend.KeyNotFoundException, BackingStoreException {
                runned = true;
                return "true";
            }
        };
        final Preferences prefs = Preferences.systemRoot();
        final boolean production = prefs.getBoolean("production", false);
		HieraBackend.clearInstance();
        assertTrue("Production setting shuld be true", production);
        assertTrue("internal runner shuld be runned", runned);
    }

    @After
    public void post() {
        HieraPreferencesFactory.restore();
    }
}