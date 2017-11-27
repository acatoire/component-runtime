/*
 * Copyright (C) 2006-2017 Talend Inc. - www.talend.com
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.talend.sdk.component.starter.server.test;

import lombok.NoArgsConstructor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static lombok.AccessLevel.PRIVATE;
import static org.junit.Assert.fail;

@NoArgsConstructor(access = PRIVATE)
public class Resources {

    public static String resourceFileToString(final String filePath) {
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(requireNonNull(
                Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath), filePath)))) {
            return reader.lines().collect(joining("\n"));
        } catch (final IOException e) {
            fail(e.getMessage());
            throw new IllegalStateException(e);
        }
    }
}
