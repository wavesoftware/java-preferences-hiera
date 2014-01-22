package pl.wavesoftware.util.preferences.impl.hiera;

import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Krzysztof Suszyński <krzysztof.suszynski@wavesoftware.pl>
 */
public class HieraPreferences extends AbstractPreferences {

    /**
     * Logger for this class
     */
    private static final Logger LOG = LoggerFactory.getLogger(HieraPreferences.class);

    private final HieraBackend backend;

    private static final String NOT_SUPPORTED = "Not supported by Hiera.";

    private Preferences defaultPrefs;

    private BackingStoreException lastException;

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
    protected HieraPreferences(AbstractPreferences perent, String name) {
        super(perent, name);
        backend = HieraBackend.instance();
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
            return backend.get(key, null);
        } catch (BackingStoreException ex) {
            lastException = ex;
            LOG.error("Hiera error", ex);
            return null;
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
        Preferences sub = defaultPrefs.node(name);
        HieraPreferences subHiera = new HieraPreferences(this, name);
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
    void setSystemDefaultPreferences(Preferences defaultPrefs) {
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

    /**
     * An order of operation enum
     */
    public enum Order {

        HIERA_OVERWRITES, DEFAULT_PREFS_OVERWRITES
    }
}
