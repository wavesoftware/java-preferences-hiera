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

    private static final String PROP = "java.util.prefs.PreferencesFactory";

    private static Order order = Order.HIERA_OVERWRITES;

    /**
     * Gets a default JAVA Preferences factory
     *
     * @return a default JAVA Preferences factory
     */
    public static PreferencesFactory getDefaultJavaFactory() {
        // Use platform-specific system-wide default
        final String osName = System.getProperty("os.name");
        String platformFactory;
        if (osName.startsWith("Windows")) {
            platformFactory = "java.util.prefs.WindowsPreferencesFactory";
        } else if (osName.contains("OS X")) {
            platformFactory = "java.util.prefs.MacOSXPreferencesFactory";
        } else {
            platformFactory = "java.util.prefs.FileSystemPreferencesFactory";
        }
        try {
            final Class<?> cls = Class.forName(platformFactory, false, null);
            final Constructor<?> constr = cls.getDeclaredConstructors()[0];
            constr.setAccessible(true);
            final Object instance = constr.newInstance();
            constr.setAccessible(false);
            return (PreferencesFactory) instance;
        } catch (Exception e) {
            final InternalError error = new InternalError("Can't instantiate platform default Preferences factory "
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
            setFinalStaticField("factory", new HieraPreferencesFactory());
        }
    }

    /**
     * Restores orginal Preference factory
     */
    public static void restore() {
        if (isActivated()) {
            System.clearProperty(PROP);
            final PreferencesFactory defaultFactory = HieraPreferencesFactory.getDefaultJavaFactory();
            setFinalStaticField("factory", defaultFactory);
        }
    }

    /**
     * Creates a Hiera preferences object for user's root
     *
     * @return Hiera preferences object
     */
    public static Preferences createUserRoot() {
        final HieraPreferencesFactory factory = new HieraPreferencesFactory();
        return factory.userRoot();
    }

    /**
     * Creates a Hiera preferences object for system's root
     *
     * @return Hiera preferences object
     */
    public static Preferences createSystemRoot() {
        final HieraPreferencesFactory factory = new HieraPreferencesFactory();
        return factory.systemRoot();
    }

    /**
     * Force to set private final static field in Java
     *
     * @param fieldName name of a field to be set
     * @param newValue a new value of that field
     */
    private static void setFinalStaticField(final String fieldName, final Object newValue) {
        try {
            final Field factoryField = Preferences.class.getDeclaredField(fieldName);
            setFinalStaticField(factoryField, newValue);
        } catch (NoSuchFieldException ex) {
            throw new DeveloperError(ex);
        } catch (SecurityException ex) {
            throw new DeveloperError(ex);
        }
    }

    /**
     * Force to set private final static field in Java
     *
     * @param field field to be set
     * @param newValue a new value of that field
     */
    private static void setFinalStaticField(final Field field, final Object newValue) {
        try {
            field.setAccessible(true);

            final Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

            field.set(null, newValue);

            modifiersField.setInt(field, field.getModifiers() & Modifier.FINAL);
            modifiersField.setAccessible(false);
            field.setAccessible(false);
        } catch (NoSuchFieldException ex) {
            throw new DeveloperError(ex);
        } catch (SecurityException ex) {
            throw new DeveloperError(ex);
        } catch (IllegalArgumentException ex) {
            throw new DeveloperError(ex);
        } catch (IllegalAccessException ex) {
            throw new DeveloperError(ex);
        }
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
        return new HieraPreferences();
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
    public static void setDefaultOrder(final Order order) {
        HieraPreferencesFactory.order = order;
    }

    @Override
    public Preferences systemRoot() {
        final HieraPreferences prefs = getHieraPreferences();
        prefs.setSystemDefaultPreferences(getDefaultJavaFactory().systemRoot());
        return prefs;
    }

    @Override
    public Preferences userRoot() {
        final HieraPreferences prefs = getHieraPreferences();
        prefs.setSystemDefaultPreferences(getDefaultJavaFactory().userRoot());
        return prefs;
    }

}
