/*
 * Copyright 2014 Krzysztof Suszyński <krzysztof.suszynski@wavesoftware.pl>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pl.wavesoftware.util.preferences.impl.hiera;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Scanner;
import java.util.prefs.BackingStoreException;

/**
 *
 * @author Krzysztof Suszyński <krzysztof.suszynski@wavesoftware.pl>
 */
class DefaultCommandLineRunner implements CliRunner, Serializable {

    private static final long serialVersionUID = 1L;

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
            throw new KeyNotFoundException(e);
        } catch (InterruptedException ex) {
            throw new BackingStoreException(ex);
        }
    }

    private static String convertStreamToString(final InputStream inputStream) {
        final Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }

}
