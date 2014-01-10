/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.wavesoftware.util.preferences.impl.hiera;

import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;
import java.util.prefs.PreferencesFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author Krzysztof Suszyński <krzysztof.suszynski@wavesoftware.pl>
 */
public class HieraPreferencesTest {

    private static final String UNIT_TESTS_NODE = "hiera-preferences-unittests";

    @Before
    public void before() {
        HieraBackend.clearInstance();
    }

    @After
    public void after() throws BackingStoreException {
        HieraPreferences instance = newInstance();
        instance.removeNodeSpi();
    }

    /**
     * New instance of hiera prefs.
     *
     * @return
     */
    private HieraPreferences newInstance() {
        try {
            HieraPreferences instance = new HieraPreferences();
            PreferencesFactory factory = HieraPreferencesFactory.getDefaultJavaFactory();
            instance.setSystemDefaultPreferences(factory.userRoot());
            instance = (HieraPreferences) instance.childSpi(UNIT_TESTS_NODE);
            instance.clear();
            return instance;
        } catch (BackingStoreException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testPutSpi() {
        String key = "some-key-to-put";
        String value = "some-value of the KEY";
        HieraPreferences instance = new HieraPreferences();
        instance.putSpi(key, value);
    }

    /**
     * Test of putSpi method, of class HieraPreferences.
     */
    @Test
    public void testPutSpi2() {
        String key = "some-key-to-put";
        String value = "some-value of the KEY";
        HieraPreferences instance = newInstance();
        try {
            instance.putSpi(key, value);
            String result = instance.getSpi(key);
            assertEquals(value, result);
        } finally {
            instance.remove(key);
        }
    }

    /**
     * Test of getSpi method, of class HieraPreferences.
     */
    @Test
    public void testGetSpi() {
        String key = "samplekey";
        HieraPreferences instance = newInstance();
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
        HieraPreferences instance = newInstance();
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
        String key = "some-key-to-put2";
        String value = "some-value of the KEY2";
        HieraPreferences instance = new HieraPreferences();
        instance.putSpi(key, value);
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

    /**
     * Test of removeSpi method, of class HieraPreferences.
     */
    @Test
    public void testRemoveSpi2() {
        String key = "some-key-to-put2";
        String value = "some-value of the KEY2";
        HieraPreferences instance = newInstance();
        instance.putSpi(key, value);
        instance.removeSpi(key);
    }

    /**
     * Test of removeNodeSpi method, of class HieraPreferences.
     */
    @Test
    public void testRemoveNodeSpi2() throws Exception {
        HieraPreferences instance = newInstance();
        String key = "testRemoveNodeSpi2";
        HieraPreferences prefs = (HieraPreferences) instance.childSpi(key);
        prefs.putSpi(key, "val1");
        assertEquals("val1", prefs.getSpi(key));
        prefs.removeNodeSpi();
        instance = newInstance();
        prefs = (HieraPreferences) instance.childSpi(key);
        assertNull(instance.getSpi(key));
        assertNull(prefs.getSpi(key));
    }

    /**
     * Test of keysSpi method, of class HieraPreferences.
     */
    @Test
    public void testKeysSpi2() throws Exception {
        HieraPreferences instance = newInstance();
        String[] expResult = new String[]{ "some-key1", "some-key2" };
        for (String key : expResult) {
            instance.putSpi(key, "value for " + key);
        }
        String[] result = instance.keysSpi();
        assertArrayEquals(expResult, result);
    }

    /**
     * Test of childrenNamesSpi method, of class HieraPreferences.
     */
    @Test
    public void testChildrenNamesSpi2() throws Exception {
        HieraPreferences instance = newInstance();
        String[] expResult = new String[]{ "some-node1", "some-node2" };
        for (String key : expResult) {
            instance = newInstance();
            instance.childSpi(key);
        }
        instance = newInstance();
        String[] result = instance.childrenNamesSpi();
        assertArrayEquals(expResult, result);
    }

    /**
     * Test of childSpi method, of class HieraPreferences.
     */
    @Test
    public void testChildSpi2() {
        String name = "subchild";
        HieraPreferences instance = newInstance();
        AbstractPreferences result = instance.childSpi(name);
        assertEquals(String.format("/%s/%s", UNIT_TESTS_NODE, name), result.absolutePath());
        assertEquals(HieraPreferences.class, result.getClass());
    }

    /**
     * Test of syncSpi method, of class HieraPreferences.
     */
    @Test
    public void testSyncSpi2() throws Exception {
        HieraPreferences instance = newInstance();
        instance.syncSpi();
    }

    /**
     * Test of flushSpi method, of class HieraPreferences.
     */
    @Test
    public void testFlushSpi2() throws Exception {
        HieraPreferences instance = newInstance();
        instance.flushSpi();
    }
}
