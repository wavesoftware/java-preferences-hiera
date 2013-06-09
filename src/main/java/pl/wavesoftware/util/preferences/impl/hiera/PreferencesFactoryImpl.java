package pl.wavesoftware.util.preferences.impl.hiera;

import java.util.prefs.Preferences;
import java.util.prefs.PreferencesFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PreferencesFactoryImpl implements PreferencesFactory {

	private static final Logger logger = LoggerFactory.getLogger(PreferencesFactoryImpl.class);

	private Preferences rootPreferences;

	@Override
	public Preferences systemRoot() {
		logger.debug("systemRoot()");

		if (rootPreferences == null) {
			rootPreferences = new PreferencesImpl();
		}

		return rootPreferences;
	}

	@Override
	public Preferences userRoot() {
		logger.debug("userRoot()");
		return systemRoot();
	}

}
