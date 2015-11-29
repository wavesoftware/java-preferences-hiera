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

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.prefs.BackingStoreException;

import static pl.wavesoftware.eid.utils.EidPreconditions.UnsafeProcedure;
import static pl.wavesoftware.eid.utils.EidPreconditions.tryToExecute;

/**
 *
 * @author Krzysztof Suszyński <krzysztof.suszynski@wavesoftware.pl>
 */
@RequiredArgsConstructor
class DefaultCommandLineRunner implements CliRunner {

    public static final int SYSTEM_SUCCESS = 0;
    private final Runtime runtime;

    protected DefaultCommandLineRunner() {
        this.runtime = Runtime.getRuntime();
    }

    @Override
    public String run(final String[] command) throws BackingStoreException {
        try {
            final Process proc = runtime.exec(command);
            waitForProc(proc);
            String out = convertStreamToString(proc.getInputStream());
            if (!isSuccessful(proc)) {
                String error = convertStreamToString(proc.getErrorStream());
                String message = makeErrorMessage(error, proc);
                throw new BackingStoreException(message);
            }
            return out;
        } catch (IOException ex) {
            throw new BackingStoreException(ex);
        }
    }

    private static String makeErrorMessage(String error, Process proc) {
        return String.format("[%d] %s", proc.exitValue(), error);
    }

    private static boolean isSuccessful(final Process proc) {
        return proc.exitValue() == SYSTEM_SUCCESS;
    }

    private static void waitForProc(final Process proc) {
        tryToExecute(new UnsafeProcedure() {
            @Override
            public void execute() throws InterruptedException {
                proc.waitFor(120L, TimeUnit.SECONDS);
            }
        }, "20151129:125832");
    }

    private static String convertStreamToString(final InputStream inputStream) {
        try {
            final Scanner scanner = new Scanner(inputStream, Charset.defaultCharset().name()).useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        } finally {
            tryToExecute(new UnsafeProcedure() {
                @Override
                public void execute() throws IOException {
                    inputStream.close();
                }
            }, "20151127:165452");
        }
    }

}
