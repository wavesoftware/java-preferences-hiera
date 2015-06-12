package pl.wavesoftware.util.preferences.impl.hiera;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
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

    private static final Field FACTORY_FIELD;

    private static final Field MODIFIERS_FIELD;

    static {
        FACTORY_FIELD = getField(Preferences.class, "factory");
        MODIFIERS_FIELD = getField(Field.class, "modifiers");
    }

    private static Order order = Order.HIERA_OVERWRITES;

    protected static Field getField(final Class<?> cls, final String name) {
        try {
            return cls.getDeclaredField(name);
        } catch (NoSuchFieldException ex) {
            throw new DeveloperError(ex);
        } catch (SecurityException ex) {
            throw new DeveloperError(ex);
        }
    }

    /**
     * Gets a default JAVA Preferences factory
     *
     * @return a default JAVA Preferences factory
     */
    public static PreferencesFactory getDefaultJavaFactory() {
        // Use platform-specific system-wide default
        final String osName = System.getProperty("os.name");
        
        try {
            final Class<?> cls = findPlatformFactoryClass(osName);
            final Constructor<?> constr = cls.getDeclaredConstructors()[0];
            constr.setAccessible(true);
            final Object instance = constr.newInstance();
            constr.setAccessible(false);
            return (PreferencesFactory) instance;
        } catch (IllegalAccessException e) {
            throw handleException(e, osName);
        } catch (IllegalArgumentException e) {
            throw handleException(e, osName);
        } catch (InstantiationException e) {
            throw handleException(e, osName);
        } catch (SecurityException e) {
            throw handleException(e, osName);
        } catch (InvocationTargetException e) {
            throw handleException(e, osName);
        }
    }

    private static InternalError handleException(final Exception exception, final String osName) {
        final InternalError error = new InternalError("Can't instantiate platform default Preferences factory "
                + osName);
        error.initCause(exception);
        return error;
    }

    protected static Class<?> findPlatformFactoryClass(final String osName) {
        try {
            String platformFactory;
            if (osName.startsWith("Windows")) {
                platformFactory = "java.util.prefs.WindowsPreferencesFactory";
            } else if (osName.contains("OS X")) {
                platformFactory = "java.util.prefs.MacOSXPreferencesFactory";
            } else {
                platformFactory = "java.util.prefs.FileSystemPreferencesFactory";
            }
            return Class.forName(platformFactory, false, null);
        } catch (ClassNotFoundException ex) {
            throw new DeveloperError(ex);
        }
    }

    /**
     * Sets Hiera prefs as default for system
     */
    public static void activate() {
        if (!isActivated()) {
            System.setProperty(PROP, HieraPreferencesFactory.class.getName());
            setSystemPreferencesFactory(new HieraPreferencesFactory());
        }
    }

    /**
     * Restores orginal Preference factory
     */
    public static void restore() {
        if (isActivated()) {
            System.clearProperty(PROP);
            final PreferencesFactory defaultFactory = HieraPreferencesFactory.getDefaultJavaFactory();
            setSystemPreferencesFactory(defaultFactory);
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
     * Force to set system's PreferencesFactory in Java
     *
     * @param newValue a new value of that field
     */
    private static void setSystemPreferencesFactory(final PreferencesFactory newValue) {
        try {
            FACTORY_FIELD.setAccessible(true);
            MODIFIERS_FIELD.setAccessible(true);
            MODIFIERS_FIELD.setInt(FACTORY_FIELD, FACTORY_FIELD.getModifiers() & ~Modifier.FINAL);

            FACTORY_FIELD.set(null, newValue);

            MODIFIERS_FIELD.setInt(FACTORY_FIELD, FACTORY_FIELD.getModifiers() & Modifier.FINAL);
            MODIFIERS_FIELD.setAccessible(false);
            FACTORY_FIELD.setAccessible(false);
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
