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
package com.github.christapley.windbg.windbgrestcontroller.crashanalysis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Chris
 */
public class WindowsCallStackEntry implements CallStackEntry {

    String module;
    String function;
    String offset;
    
    public WindowsCallStackEntry(String line) {
        Pattern pattern = Pattern.compile("^.* (.*)!(.*)\\+(.*)$", Pattern.MULTILINE);
        Matcher regexMatcher = pattern.matcher(line);
        if(regexMatcher.find()) {
            module = regexMatcher.group(1);
            function = regexMatcher.group(2);
            offset = regexMatcher.group(3);
        } else {
            throw new IllegalArgumentException(String.format("Unable to parse stack trace line %s", line));
        }
    }
    
    @Override
    public String getModule() {
        return module;
    }

    @Override
    public String getFunction() {
        return function;
    }

    @Override
    public String getOffset() {
        return offset;
    }
    
    @Override
    public String toString() {
        return String.format("%s!%S+%s", module, function, offset);
    }
}
