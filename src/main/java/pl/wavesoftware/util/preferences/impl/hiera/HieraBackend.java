package pl.wavesoftware.util.preferences.impl.hiera;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.prefs.BackingStoreException;

/**
 *
 * @author Krzysztof Suszyński <krzysztof.suszynski@wavesoftware.pl>
 */
public class HieraBackend {

    private static HieraBackend inst;

    private static final Object LOCK = new Object();

    protected transient CliRunner runner;

	private transient boolean disabled = false;

	private String executable = "hiera";

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
            inst.runner = new Runner();
            inst.disabled = false;
        }
    }

    protected HieraBackend() {
        runner = new Runner();
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
		String ret;
        try {
			ret = get(key);
        } catch (KeyNotFoundException ex) {
			ret = defaultValue;
		}
		return ret;
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
		final String ret = runner.run(executable + " " + key).trim();
        if ("nil".equals(ret)) {
            throw new KeyNotFoundException();
        }
        return ret;
    }

    /**
     * Cli runner
     */
    public interface CliRunner {

        /**
         * Runs command in CLI
         *
         * @param command to run at CLI
         * @return Executed output STDOUT
         * @throws KeyNotFoundException if key is not found
         * @throws BackingStoreException if error occurd
         */
        String run(final String command) throws KeyNotFoundException, BackingStoreException;
    }

    protected static class Runner implements CliRunner {

        @Override
        public String run(final String command) throws KeyNotFoundException, BackingStoreException {
            try {
                final Process proc = Runtime.getRuntime().exec(command);
                proc.waitFor();

                if (proc.exitValue() != 0) {
                    throw new KeyNotFoundException();
                }
                return convertStreamToString(proc.getInputStream());
            } catch (IOException e) {
                throw new BackingStoreException(e);
            } catch (InterruptedException ex) {
                throw new BackingStoreException(ex);
            }
        }

        private static String convertStreamToString(final InputStream inputStream) {
            final Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        }
    }

    /**
     * Key is not found
     */
    public static final class KeyNotFoundException extends Exception {

		private static final long serialVersionUID = 1L;

        /**
         * Default constr
         */
        public KeyNotFoundException() {
            super("Key not found");
        }

    }
}
