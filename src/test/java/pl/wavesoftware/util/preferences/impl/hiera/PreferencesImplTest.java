/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.wavesoftware.util.preferences.impl.hiera;

import java.util.prefs.AbstractPreferences;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Krzysztof Suszyński <krzysztof.suszynski@wavesoftware.pl>
 */
public class PreferencesImplTest {

	/**
	 * Test of putSpi method, of class PreferencesImpl.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testPutSpi() {
		String key = "";
		String value = "";
		PreferencesImpl instance = new PreferencesImpl();
		instance.putSpi(key, value);
	}

	/**
	 * Test of getSpi method, of class PreferencesImpl.
	 */
	@Test
	public void testGetSpi() {
		String key = "samplekey";
		PreferencesImpl instance = new PreferencesImpl();
		String expResult = null;
		String result = instance.getSpi(key);
		assertEquals(expResult, result);
	}

	/**
	 * Test of removeSpi method, of class PreferencesImpl.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testRemoveSpi() {
		String key = "";
		PreferencesImpl instance = new PreferencesImpl();
		instance.removeSpi(key);
	}

	/**
	 * Test of removeNodeSpi method, of class PreferencesImpl.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testRemoveNodeSpi() throws Exception {
		PreferencesImpl instance = new PreferencesImpl();
		instance.removeNodeSpi();
	}

	/**
	 * Test of keysSpi method, of class PreferencesImpl.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testKeysSpi() throws Exception {
		PreferencesImpl instance = new PreferencesImpl();
		String[] expResult = null;
		String[] result = instance.keysSpi();
		assertArrayEquals(expResult, result);
	}

	/**
	 * Test of childrenNamesSpi method, of class PreferencesImpl.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testChildrenNamesSpi() throws Exception {
		PreferencesImpl instance = new PreferencesImpl();
		String[] expResult = null;
		String[] result = instance.childrenNamesSpi();
		assertArrayEquals(expResult, result);
	}

	/**
	 * Test of childSpi method, of class PreferencesImpl.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testChildSpi() {
		String name = "";
		PreferencesImpl instance = new PreferencesImpl();
		AbstractPreferences expResult = null;
		AbstractPreferences result = instance.childSpi(name);
		assertEquals(expResult, result);
	}

	/**
	 * Test of syncSpi method, of class PreferencesImpl.
	 */
	@Test
	public void testSyncSpi() throws Exception {
		PreferencesImpl instance = new PreferencesImpl();
		instance.syncSpi();
	}

	/**
	 * Test of flushSpi method, of class PreferencesImpl.
	 */
	@Test
	public void testFlushSpi() throws Exception {
		PreferencesImpl instance = new PreferencesImpl();
		instance.flushSpi();
	}
}