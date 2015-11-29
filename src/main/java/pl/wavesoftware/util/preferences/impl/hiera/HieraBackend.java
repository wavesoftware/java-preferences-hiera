package pl.wavesoftware.util.preferences.impl.hiera;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import pl.wavesoftware.eid.utils.EidPreconditions;

import javax.annotation.Nonnull;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.prefs.BackingStoreException;

import static pl.wavesoftware.eid.utils.EidPreconditions.checkArgument;
import static pl.wavesoftware.eid.utils.EidPreconditions.checkNotNull;
import static pl.wavesoftware.eid.utils.EidPreconditions.tryToExecute;

/**
 *
 * @author Krzysztof Suszyński <krzysztof.suszynski@wavesoftware.pl>
 */
public class HieraBackend {

    private static HieraBackend inst;

    private static final int CACHE_TIME = 120;

    private static final TimeUnit CACHE_TIME_UNIT = TimeUnit.SECONDS;

    protected CliRunner runner;

    private boolean disabled = false;

    private String executable = "hiera";

    private LoadingCache<String, Execution> cache;

    protected final void setCache(final LoadingCache<String, Execution> cache) {
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
        CacheLoader<String, Execution> loader = new CacheLoader<String, Execution>() {

            @Override
            public Execution load(@Nonnull final String key) {
                try {
                    return Execution.ok(runRunnerForKey(key));
                } catch (BackingStoreException e) {
                    return Execution.fail(e);
                }
            }
        };
        cache = CacheBuilder.newBuilder()
                .expireAfterAccess(cacheTime, cacheTimeUnit)
                .build(loader);
    }

    private LoadingCache<String, Execution> getCache() {
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
    public synchronized static HieraBackend instance() {
        if (inst == null || inst.disabled) {
            inst = new HieraBackend();
        }
        return inst;
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
     * @return founded value
     */
    public String get(final String key) throws BackingStoreException {
        Execution exec = getExecution(key);
        if (!exec.isOk) {
            throw backingStoreException(exec);
        }
        return exec.result;
    }

    private BackingStoreException backingStoreException(Execution exec) {
        Throwable cause;
        if (exec.cause != null) {
            cause = exec.cause;
        } else {
            cause = new BackingStoreException(exec.result);
        }
        BackingStoreException ex;
        if (cause instanceof BackingStoreException) {
            ex = BackingStoreException.class.cast(cause);
        } else {
            ex = new BackingStoreException(cause);
        }
        return ex;
    }

    /**
     * Gets value from hiera
     *
     * @param key the key to search for
     * @return founded value
     */
    protected Execution getExecution(final String key) {
        return tryToExecute(new EidPreconditions.UnsafeSupplier<Execution>() {
            @Override
            public Execution get() throws ExecutionException {
                return getInsecurely(key);
            }
        }, "20151129:135500");
    }

    private Execution getInsecurely(String key) throws ExecutionException {
        final Execution execution = getCache().get(key);
        String resolvedValue = execution.result;
        Execution returnExec = execution;
        if (resolvedValue == null || "nil".equals(resolvedValue) || "null".equals(resolvedValue)) {
            returnExec = Execution.fail();
        }
        return returnExec;
    }

    private String runRunnerForKey(final String key) throws BackingStoreException {
        ensureIsSecureKey(key);
        String[] command = new String[]{ executable, key };
        return runner.run(command).trim();
    }

    private void ensureIsSecureKey(final String key) {
        checkNotNull(key, "20151127:163952");
        checkArgument(key.matches("^[a-zA-Z0-9.,_+=:-]+$"), "20151127:164119");
    }

    protected static class Execution {
        protected final String result;
        protected final boolean isOk;
        protected final Throwable cause;

        private Execution(String result, boolean isOk, Throwable cause) {
            this.result = result;
            this.isOk = isOk;
            this.cause = cause;
        }

        static Execution ok(String result) {
            return new Execution(result, true, null);
        }

        static Execution fail() {
            return new Execution(null, false, null);
        }

        static Execution fail(Throwable throwable) {
            return new Execution(null, false, throwable);
        }
    }
}
