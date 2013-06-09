package pl.wavesoftware.util.preferences.impl.hiera;

import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PreferencesImpl extends AbstractPreferences {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(PreferencesImpl.class);

	private final HieraBackend backend;

	protected PreferencesImpl() {
		super(null, "");
		logger.debug("PreferencesImpl()");
		backend = HieraBackend.instance();
	}

	@Override
	protected void putSpi(String key, String value) {
		throw new UnsupportedOperationException("Not supported by Hiera.");
	}

	@Override
	protected String getSpi(String key) {
		try {
			return backend.get(key);
		} catch (IllegalArgumentException ex) {
			logger.trace("Key not found", ex);
			return null;
		} catch (BackingStoreException ex) {
			logger.error("Hiera error", ex);
			return null;
		}
	}

	@Override
	protected void removeSpi(String key) {
		throw new UnsupportedOperationException("Not supported by Hiera.");
	}

	@Override
	protected void removeNodeSpi() throws BackingStoreException {
		throw new UnsupportedOperationException("Not supported by Hiera.");
	}

	@Override
	protected String[] keysSpi() throws BackingStoreException {
		throw new UnsupportedOperationException("Not supported by Hiera.");
	}

	@Override
	protected String[] childrenNamesSpi() throws BackingStoreException {
		throw new UnsupportedOperationException("Not supported by Hiera.");
	}

	@Override
	protected AbstractPreferences childSpi(String name) {
		throw new UnsupportedOperationException("Not supported by Hiera.");
	}

	@Override
	protected void syncSpi() throws BackingStoreException {
		
	}

	@Override
	protected void flushSpi() throws BackingStoreException {
		
	}
}
