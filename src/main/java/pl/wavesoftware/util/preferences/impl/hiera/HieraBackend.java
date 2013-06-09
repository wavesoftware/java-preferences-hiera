package pl.wavesoftware.util.preferences.impl.hiera;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.prefs.BackingStoreException;

public class HieraBackend {

	private static HieraBackend inst;

	public static HieraBackend instance() {
		if (inst == null) {
			inst = new HieraBackend();
		}
		return inst;
	}

	public static void clearInstance() {
		inst = null;
	}

	protected HieraBackend() {
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	ProcessRunner runner = new ProcessRunner() {
		public String run(String command) throws IllegalArgumentException, BackingStoreException {
			StringBuilder sb = new StringBuilder();
			String s;

			try {
				Process p = Runtime.getRuntime().exec(command);
				p.waitFor();

				if (p.exitValue() != 0) {
					throw new IllegalArgumentException("Key not found");
				}
				BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
				// read the output from the command
				while ((s = stdInput.readLine()) != null) {
					sb.append(s).append("\n");
				}
				sb.deleteCharAt(sb.length() - 1);
				return sb.toString();
			} catch (IOException e) {
				throw new BackingStoreException(e);
			} catch (InterruptedException ex) {
				throw new BackingStoreException(ex);
			}
		}
	};

	public String get(String key, String defaultValue) throws BackingStoreException {
		try {
			return get(key);
		} catch (IllegalArgumentException ex) {
			return defaultValue;
		}
	}

	public String get(String key) throws IllegalArgumentException, BackingStoreException {
		return runner.run("hiera " + key);
	}

	public interface ProcessRunner {

		String run(String command) throws IllegalArgumentException, BackingStoreException;
	}
}
