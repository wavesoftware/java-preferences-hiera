package pl.wavesoftware.util.preferences.impl.hiera;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.io.Serializable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.prefs.BackingStoreException;

/**
 *
 * @author Krzysztof Suszyński <krzysztof.suszynski@wavesoftware.pl>
 */
public class HieraBackend implements Serializable {

    private static final long serialVersionUID = 1L;

    private static HieraBackend inst;

    private static final Object LOCK = new Object();

    private static final int CACHE_TIME = 120;

    private static final TimeUnit CACHE_TIME_UNIT = TimeUnit.SECONDS;

    protected CliRunner runner;

    private boolean disabled = false;

    private String executable = "hiera %s";

    private LoadingCache<String, String> cache;

    protected boolean isDisabled() {
        return disabled;
    }

    protected void setDisabled(final boolean disabled) {
        this.disabled = disabled;
    }

    protected CliRunner getRunner() {
        return runner;
    }

    protected void setRunner(final CliRunner runner) {
        this.runner = runner;
    }

    protected final void setCache(final LoadingCache<String, String> cache) {
        this.cache = cache;
    }

    private void cleanCache() {
        if (cache != null) {
            cache.invalidateAll();
        }
        setCache(null);
    }

    /**
     * Resets cache with default of 120sec. cache time
     */
    public final void resetCache() {
        resetCache(CACHE_TIME, CACHE_TIME_UNIT);
    }

    /**
     * Resets cache with given cache time
     *
     * @param cacheTime a new cache time
     * @param cacheTimeUnit a cache time unit
     */
    public final void resetCache(final int cacheTime, final TimeUnit cacheTimeUnit) {
        cleanCache();
        cache = CacheBuilder.newBuilder().expireAfterAccess(cacheTime, cacheTimeUnit).build(
                new CacheLoader<String, String>() {

                    @Override
                    public String load(final String key) throws KeyNotFoundException, BackingStoreException {
                        return runRunnerForKey(key);
                    }
                });
    }

    private LoadingCache<String, String> getCache() {
        if (cache == null) {
            resetCache();
        }
        return cache;
    }

    /**
     * Instance method
     *
     * @return singleton of {@link HieraBackend}
     */
    public static HieraBackend instance() {
        synchronized (LOCK) {
            if (inst == null || inst.disabled) {
                inst = new HieraBackend();
            }
            return inst;
        }
    }

    /**
     * Gets executable name
     *
     * @return executable name
     */
    public String getExecutable() {
        return executable;
    }

    /**
     * Sets executable name
     *
     * @param executable name of the executable
     */
    public void setExecutable(final String executable) {
        this.executable = executable;
    }

    /**
     * Clears instance
     */
    public static void clearInstance() {
        if (inst != null) {
            inst.runner = new DefaultCommandLineRunner();
            inst.disabled = false;
            inst.cleanCache();
        }
    }

    protected HieraBackend() {
        runner = new DefaultCommandLineRunner();
        cleanCache();
    }

    /**
     * Gets value from hiera but returns default value if not found
     *
     * @param key the key to search for
     * @param defaultValue the value to return if not found
     * @return founded value
     * @throws BackingStoreException thrown if error occurd
     */
    public String get(final String key, final String defaultValue) throws BackingStoreException {
        try {
            return get(key);
        } catch (KeyNotFoundException ex) {
            return defaultValue;
        }
    }

    private String runRunnerForKey(final String key) throws KeyNotFoundException, BackingStoreException {
        return runner.run(String.format(executable, key)).trim();
    }

    /**
     * Gets value from hiera
     *
     * @param key the key to search for
     * @return founded value
     * @throws BackingStoreException thrown if error occurd
     * @throws KeyNotFoundException thrown if key is not found
     */
    public String get(final String key) throws BackingStoreException, KeyNotFoundException {
        try {
            final String ret = getCache().get(key);
            if (ret == null || "nil".equals(ret) || "null".equals(ret)) {
                throw new KeyNotFoundException();
            }
            return ret;
        } catch (ExecutionException ex) {
            final Throwable cause = ex.getCause();
            if (cause instanceof KeyNotFoundException) {
                throw (KeyNotFoundException) cause;
            }
            throw new BackingStoreException(ex);
        }
    }
}
