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

/**
 * Key is not found
 *
 * @author Krzysztof Suszyński <krzysztof.suszynski@wavesoftware.pl>
 */
public final class KeyNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    private static final String KEY_NOT_FOUND = "Key not found";

    /**
     * Default constr
     */
    public KeyNotFoundException() {
        super(KEY_NOT_FOUND);
    }

    /**
     * Constructor with throwable
     *
     * @param throwable a throwable couse
     */
    public KeyNotFoundException(final Throwable throwable) {
        super(KEY_NOT_FOUND, throwable);
    }

}
