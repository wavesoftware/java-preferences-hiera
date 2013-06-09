/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.wavesoftware.util.preferences.impl.hiera;

import java.util.prefs.Preferences;
import org.junit.Before;
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
}