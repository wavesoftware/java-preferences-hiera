package pl.wavesoftware.util.preferences.impl.hiera;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.prefs.BackingStoreException;

import static pl.wavesoftware.eid.utils.EidPreconditions.checkArgument;
import static pl.wavesoftware.eid.utils.EidPreconditions.checkNotNull;

/**
 *
 * @author Krzysztof Suszyński <krzysztof.suszynski@wavesoftware.pl>
 */
public class HieraBackend {

    private static HieraBackend inst;

    private static final Object LOCK = new Object();

    private static final int CACHE_TIME = 120;

    private static final TimeUnit CACHE_TIME_UNIT = TimeUnit.SECONDS;

    protected CliRunner runner;

    private boolean disabled = false;

    private String executable = "hiera '%s'";

    private LoadingCache<String, String> cache;

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
        Execution exec = get(key);
        return exec.isOk ? exec.result : defaultValue;
    }

    private String runRunnerForKey(final String key) throws KeyNotFoundException, BackingStoreException {
        ensureIsSecureKey(key);
        String command = String.format(executable, key);
        return runner.run(command).trim();
    }

    private void ensureIsSecureKey(final String key) {
        checkNotNull(key, "20151127:163952");
        checkArgument(key.matches("^[a-zA-Z0-9.,_+=:-]+$"), "20151127:164119");
    }

    /**
     * Gets value from hiera
     *
     * @param key the key to search for
     * @return founded value
     * @throws BackingStoreException thrown if error occurd
     */
    protected Execution get(final String key) throws BackingStoreException {
        try {
            return getInsecurely(key);
        } catch (ExecutionException ex) {
            throw new BackingStoreException(ex);
        }
    }

    private Execution getInsecurely(String key) throws ExecutionException {
        final String ret = getCache().get(key);
        Execution execution;
        if (ret == null || "nil".equals(ret) || "null".equals(ret)) {
            execution = Execution.fail();
        } else {
            execution = Execution.ok(ret);
        }
        return execution;
    }

    protected static class Execution {
        protected final String result;
        protected final boolean isOk;

        private Execution(String result, boolean isOk) {
            this.result = result;
            this.isOk = isOk;
        }

        static Execution ok(String result) {
            return new Execution(result, true);
        }

        static Execution fail() {
            return new Execution(null, false);
        }
    }
}
