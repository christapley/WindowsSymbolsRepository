/*
 * Copyright 2018 Pivotal Software, Inc..
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
package com.github.christapley.windbg.windbgrestcontroller.util;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author Chris
 */
public class TemporaryFile implements AutoCloseable {
    File temporaryFile;

    public TemporaryFile() throws IOException {
        this("TemporaryFile", ".tmp");
    }
    
    public TemporaryFile(String prefix, String suffix) throws IOException {
        temporaryFile = File.createTempFile(prefix, suffix);
    }
    
    public File getTemporaryFile() {
        return temporaryFile;
    }
    
    @Override
    public void close() throws Exception {
        if(temporaryFile != null && temporaryFile.exists()) {
            temporaryFile.delete();
        }
    }
    
    
}
