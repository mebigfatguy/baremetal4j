/*
 * baremetal4j - A java aspect for allowing debugging at the byte code level from source debuggers (as in IDEs)
 * Copyright 2016-2019 MeBigFatGuy.com
 * Copyright 2016-2019 Dave Brosius
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations
 * under the License.
 */
package com.mebigfatguy.baremetal4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public final class SourceWriterFactory {

    private SourceWriterFactory() {
    }

    public static String name(String clsName, Options options) {
        File f = new File(options.getSourcePath(), clsName.replace('.', '/') + ".java");
        return f.getName();
    }

    public static PrintWriter get(String clsName, Options options) throws IOException {
        File f = new File(options.getSourcePath(), clsName.replace('.', '/') + ".java");
        f.getParentFile().mkdirs();

        FileOutputStream fos = new FileOutputStream(f);
        return new PrintWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8));
    }
}
