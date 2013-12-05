package pl.wavesoftware.util.preferences.impl.hiera;

import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;
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

    private final transient HieraBackend backend;

    private static final String NOT_SUPPORTED = "Not supported by Hiera.";

	private BackingStoreException lastException;

	/**     * Default constructor
     */
    protected HieraPreferences() {
        super(null, "");
        LOG.trace("PreferencesImpl()");
        backend = HieraBackend.instance();
    }

    @Override
    protected void putSpi(final String key, final String value) {
        throw new UnsupportedOperationException(NOT_SUPPORTED);
	}

	protected BackingStoreException getLastException() {
		return lastException;
	}

    @Override
    protected String getSpi(final String key) {
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
        throw new UnsupportedOperationException(NOT_SUPPORTED);
    }

    @Override
    protected void removeNodeSpi() throws BackingStoreException {
        throw new UnsupportedOperationException(NOT_SUPPORTED);
    }

    @Override
    protected String[] keysSpi() throws BackingStoreException {
        throw new UnsupportedOperationException(NOT_SUPPORTED);
    }

    @Override
    protected String[] childrenNamesSpi() throws BackingStoreException {
        throw new UnsupportedOperationException(NOT_SUPPORTED);
    }

    @Override
    protected AbstractPreferences childSpi(final String name) {
        throw new UnsupportedOperationException(NOT_SUPPORTED);
    }

    @Override
    protected void syncSpi() throws BackingStoreException {
        // empty
    }

    @Override
    protected void flushSpi() throws BackingStoreException {
        // empty
    }
}
