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

import java.util.prefs.BackingStoreException;

/**
 * Cli runner
 *
 * @author Krzysztof Suszyński <krzysztof.suszynski@wavesoftware.pl>
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
