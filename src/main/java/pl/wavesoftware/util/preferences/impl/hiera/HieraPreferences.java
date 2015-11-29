package pl.wavesoftware.util.preferences.impl.hiera;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 *
 * @author Krzysztof Suszyński <krzysztof.suszynski@wavesoftware.pl>
 */
public class HieraPreferences extends AbstractPreferences {

    /**
     * Logger for this class
     */
    private static final Logger LOG = LoggerFactory.getLogger(HieraPreferences.class);
    private static final String NOT_SUPPORTED = "Not supported by Hiera.";
    private transient BackingStoreException lastException;
    private HieraBackend backend = null;
    private Preferences defaultPrefs;

    /**
     * Default constructor
     */
    protected HieraPreferences() {
        this(null, "");
    }

    /**
     * Sup preferences constructor
     *
     * @param perent the parent of this preference
     * @param name name of the preference
     */
    protected HieraPreferences(final AbstractPreferences perent, final String name) {
        super(perent, name);
    }

    @Override
    protected void putSpi(final String key, final String value) {
        if (defaultPrefs == null) {
            throw new UnsupportedOperationException(NOT_SUPPORTED);
        }
        defaultPrefs.put(key, value);
    }

    @Override
    protected String getSpi(final String key) {
        if (defaultPrefs != null) {
            if (Order.HIERA_OVERWRITES.equals(HieraPreferencesFactory.getDefaultOrder())) {
                String val = getFromHiera(key);
                if (val == null) {
                    val = defaultPrefs.get(key, null);
                }
                return val;
            } else {
                String val = defaultPrefs.get(key, null);
                if (val == null) {
                    val = getFromHiera(key);
                }
                return val;
            }
        }
        return getFromHiera(key);
    }

    /**
     * Retrives value from Hiera backend
     *
     * @param key a key to be searched
     * @return a found value or null if not found
     */
    private String getFromHiera(final String key) {
        try {
            return getBackend().get(key);
        } catch (BackingStoreException e) {
            LOG.debug("20151129:232048", e);
        }
    }

    @Override
    protected void removeSpi(final String key) {
        if (defaultPrefs == null) {
            throw new UnsupportedOperationException(NOT_SUPPORTED);
        }
        defaultPrefs.remove(key);
    }

    @Override
    protected void removeNodeSpi() throws BackingStoreException {
        if (defaultPrefs == null) {
            throw new UnsupportedOperationException(NOT_SUPPORTED);
        }
        defaultPrefs.removeNode();
    }

    @Override
    protected String[] keysSpi() throws BackingStoreException {
        if (defaultPrefs == null) {
            throw new UnsupportedOperationException(NOT_SUPPORTED);
        }
        return defaultPrefs.keys();
    }

    @Override
    protected String[] childrenNamesSpi() throws BackingStoreException {
        if (defaultPrefs == null) {
            throw new UnsupportedOperationException(NOT_SUPPORTED);
        }
        return defaultPrefs.childrenNames();
    }

    @Override
    protected AbstractPreferences childSpi(final String name) {
        if (defaultPrefs == null) {
            throw new UnsupportedOperationException(NOT_SUPPORTED);
        }
        final Preferences sub = defaultPrefs.node(name);
        final HieraPreferences subHiera = new HieraPreferences(this, name);
        subHiera.setSystemDefaultPreferences(sub);
        return subHiera;
    }

    @Override
    protected void syncSpi() throws BackingStoreException {
        if (defaultPrefs != null) {
            defaultPrefs.sync();
        }
    }

    @Override
    protected void flushSpi() throws BackingStoreException {
        if (defaultPrefs != null) {
            defaultPrefs.flush();
        }
    }

    /**
     * Sets the system default preferences as backend.
     *
     * @param defaultPrefs system default preferences
     */
    public void setSystemDefaultPreferences(final Preferences defaultPrefs) {
        this.defaultPrefs = defaultPrefs;
    }

    /**
     * Retrives last exception
     *
     * @return last occured exception
     */
    protected BackingStoreException getLastException() {
        return lastException;
    }

    private HieraBackend getBackend() {
        if (backend == null) {
            backend = HieraBackend.instance();
        }
        return backend;
    }

    /**
     * An order of operation enum
     */
    public enum Order {

        HIERA_OVERWRITES, DEFAULT_PREFS_OVERWRITES
    }
}
