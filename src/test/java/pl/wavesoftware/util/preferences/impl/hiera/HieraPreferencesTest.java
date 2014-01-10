/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.wavesoftware.util.preferences.impl.hiera;

import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author Krzysztof Suszyński <krzysztof.suszynski@wavesoftware.pl>
 */
public class HieraPreferencesTest {

    @Before
    public void before() {
        HieraBackend.clearInstance();
    }

    /**
     * Test of putSpi method, of class HieraPreferences.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testPutSpi() {
        String key = "";
        String value = "";
        HieraPreferences instance = new HieraPreferences();
        instance.putSpi(key, value);
    }

    /**
     * Test of getSpi method, of class HieraPreferences.
     */
    @Test
    public void testGetSpi() {
        String key = "samplekey";
        HieraPreferences instance = new HieraPreferences();
        String expResult = null;
        String result = instance.getSpi(key);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetSpiWithException2() {
        String key = "samplekey";
        String expResult = null;
        String oldExec = HieraBackend.instance().getExecutable();
        String command = "not-existent-command %s";
        HieraBackend.instance().setExecutable(command);
        HieraPreferences instance = new HieraPreferences();
        String result = instance.getSpi(key);
        HieraBackend.instance().setExecutable(oldExec);
        assertEquals(expResult, result);
        BackingStoreException ex = instance.getLastException();
        assertNull(ex);
    }

    /**
     * Test of removeSpi method, of class HieraPreferences.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testRemoveSpi() {
        String key = "";
        HieraPreferences instance = new HieraPreferences();
        instance.removeSpi(key);
    }

    /**
     * Test of removeNodeSpi method, of class HieraPreferences.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testRemoveNodeSpi() throws Exception {
        HieraPreferences instance = new HieraPreferences();
        instance.removeNodeSpi();
    }

    /**
     * Test of keysSpi method, of class HieraPreferences.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testKeysSpi() throws Exception {
        HieraPreferences instance = new HieraPreferences();
        String[] expResult = null;
        String[] result = instance.keysSpi();
        assertArrayEquals(expResult, result);
    }

    /**
     * Test of childrenNamesSpi method, of class HieraPreferences.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testChildrenNamesSpi() throws Exception {
        HieraPreferences instance = new HieraPreferences();
        String[] expResult = null;
        String[] result = instance.childrenNamesSpi();
        assertArrayEquals(expResult, result);
    }

    /**
     * Test of childSpi method, of class HieraPreferences.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testChildSpi() {
        String name = "";
        HieraPreferences instance = new HieraPreferences();
        AbstractPreferences expResult = null;
        AbstractPreferences result = instance.childSpi(name);
        assertEquals(expResult, result);
    }

    /**
     * Test of syncSpi method, of class HieraPreferences.
     */
    @Test
    public void testSyncSpi() throws Exception {
        HieraPreferences instance = new HieraPreferences();
        instance.syncSpi();
    }

    /**
     * Test of flushSpi method, of class HieraPreferences.
     */
    @Test
    public void testFlushSpi() throws Exception {
        HieraPreferences instance = new HieraPreferences();
        instance.flushSpi();
    }
}
