package pl.wavesoftware.util.preferences.impl.hiera;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.prefs.Preferences;
import java.util.prefs.PreferencesFactory;
import pl.wavesoftware.util.preferences.impl.hiera.HieraPreferences.Order;

/**
 * Pupper hiera preferences factory implementation
 *
 * @author Krzysztof Suszyński <krzysztof.suszynski@wavesoftware.pl>
 */
public class HieraPreferencesFactory implements PreferencesFactory {

    private HieraPreferences hieraPrefs;

    private static final String PROP = "java.util.prefs.PreferencesFactory";

    private static Order order = Order.HIERA_OVERWRITES;

    /**
     * Gets a default JAVA Preferences factory
     *
     * @return a default JAVA Preferences factory
     */
    public static PreferencesFactory getDefaultJavaFactory() {
        // Use platform-specific system-wide default
        String osName = System.getProperty("os.name");
        String platformFactory;
        if (osName.startsWith("Windows")) {
            platformFactory = "java.util.prefs.WindowsPreferencesFactory";
        } else if (osName.contains("OS X")) {
            platformFactory = "java.util.prefs.MacOSXPreferencesFactory";
        } else {
            platformFactory = "java.util.prefs.FileSystemPreferencesFactory";
        }
        try {
            Class<?> cls = Class.forName(platformFactory, false, null);
            Constructor<?> constr = cls.getDeclaredConstructors()[0];
            constr.setAccessible(true);
            Object instance = constr.newInstance();
            constr.setAccessible(false);
            return (PreferencesFactory) instance;
        } catch (Exception e) {
            InternalError error = new InternalError(
                    "Can't instantiate platform default Preferences factory "
                    + platformFactory);
            error.initCause(e);
            throw error;
        }
    }

    /**
     * Sets Hiera prefs as default for system
     */
    public static void activate() {
        if (!isActivated()) {
            System.setProperty(PROP, HieraPreferencesFactory.class.getName());
            try {
                Field factoryField = Preferences.class.getDeclaredField("factory");
                setFinalStatic(factoryField, new HieraPreferencesFactory());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    /**
     * Restores orginal Preference factory
     */
    public static void restore() {
        if (isActivated()) {
            try {
                System.clearProperty(PROP);
                PreferencesFactory defaultFactory = HieraPreferencesFactory.getDefaultJavaFactory();
                Field factoryField = Preferences.class.getDeclaredField("factory");
                setFinalStatic(factoryField, defaultFactory);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    /**
     * Force to set private final static field in Java
     *
     * @param field field to be set
     * @param newValue a new value of that field
     * @throws Exception if something is wrong
     */
    private static void setFinalStatic(Field field, Object newValue) throws Exception {
        field.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(null, newValue);

        modifiersField.setInt(field, field.getModifiers() & Modifier.FINAL);
        modifiersField.setAccessible(false);
        field.setAccessible(false);
    }

    /**
     * Checks is Hiera preferences is set as current default preferences factory
     *
     * @return true if is set as current
     */
    public static boolean isActivated() {
        return HieraPreferencesFactory.class.getName().equals(System.getProperty(PROP));
    }

    /**
     * Gets Hiera preferences
     *
     * @return Hiera preferences
     */
    private HieraPreferences getHieraPreferences() {
        if (hieraPrefs == null) {
            hieraPrefs = new HieraPreferences();
        }
        return hieraPrefs;
    }

    /**
     * Gets current order of operation
     *
     * @return an order
     */
    public static Order getDefaultOrder() {
        return order;
    }

    /**
     * Sets order of operations
     *
     * @param order desired order
     */
    public static void setDefaultOrder(Order order) {
        HieraPreferencesFactory.order = order;
    }

    @Override
    public Preferences systemRoot() {
        HieraPreferences prefs = getHieraPreferences();
        prefs.setSystemDefaultPreferences(getDefaultJavaFactory().systemRoot());
        return prefs;
    }

    @Override
    public Preferences userRoot() {
        HieraPreferences prefs = getHieraPreferences();
        prefs.setSystemDefaultPreferences(getDefaultJavaFactory().userRoot());
        return prefs;
    }

}
