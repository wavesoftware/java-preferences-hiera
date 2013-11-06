package pl.wavesoftware.util.preferences.impl.hiera;

import java.util.prefs.Preferences;
import java.util.prefs.PreferencesFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pupper hiera preferences factory implementation
 *
 * @author Krzysztof Suszyński <krzysztof.suszynski@wavesoftware.pl>
 */
public class HieraPreferencesFactory implements PreferencesFactory {

    private static final Logger LOG = LoggerFactory.getLogger(HieraPreferencesFactory.class);

    private static String orginal;

    private static boolean orginalSet = false;

    private transient Preferences rootPreferences;

    private static final String PROP = "java.util.prefs.PreferencesFactory";

    /**
     * Sets Hiera prefs as default for system
     *
     * @return orginal settings for Preference factory
     */
    public static String activate() {
        orginal = System.getProperty(PROP);
        orginalSet = true;
        System.setProperty(PROP, HieraPreferencesFactory.class.getName());
        return orginal;
    }

    /**
     * Restores orginal Preference factory
     */
    public static void restore() {
        if (orginalSet) {
            System.setProperty(PROP, orginal);
            orginalSet = false;
        }
    }

	@Override
	public Preferences systemRoot() {
        LOG.trace("systemRoot()");
		if (rootPreferences == null) {
			rootPreferences = new HieraPreferences();
		}
		return rootPreferences;
	}

	@Override
	public Preferences userRoot() {
        LOG.trace("userRoot()");
		return systemRoot();
	}

}
