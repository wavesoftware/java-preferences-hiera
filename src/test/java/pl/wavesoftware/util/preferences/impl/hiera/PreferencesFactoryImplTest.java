/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.wavesoftware.util.preferences.impl.hiera;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Krzysztof Suszyński <krzysztof.suszynski@wavesoftware.pl>
 */
public class PreferencesFactoryImplTest {

	/**
	 * Test of systemRoot method, of class PreferencesFactoryImpl.
	 */
	@Test
	public void testSystemRoot() {
		PreferencesFactoryImpl instance = new PreferencesFactoryImpl();
		Preferences result = instance.systemRoot();
		assertNotNull(result);
	}

	/**
	 * Test of userRoot method, of class PreferencesFactoryImpl.
	 */
	@Test
	public void testUserRoot() {
		PreferencesFactoryImpl instance = new PreferencesFactoryImpl();
		Preferences result = instance.userRoot();
		assertNotNull(result);
	}

	boolean runned = false;

	@Test
	public void testGlobal() {
		runned = false;
		HieraBackend.clearInstance();
		HieraBackend.instance().runner = new HieraBackend.ProcessRunner() {
			public String run(String command) throws IllegalArgumentException, BackingStoreException {
				runned = true;
				return "true";
			}
		};
		System.setProperty("java.util.prefs.PreferencesFactory", PreferencesFactoryImpl.class.getName());
		Preferences prefs = Preferences.systemRoot();
		boolean production = prefs.getBoolean("production", false);
		HieraBackend.clearInstance();
		assertTrue(production);
		assertTrue(runned);
	}
}