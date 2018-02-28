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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.thymeleaf.util.StringUtils;

/**
 *
 * @author Chris
 */
public class WindowsCrashAnalysis implements CrashAnalysis {
    String rawAnalysis;
    private String crashFileName;
    String watsonBucketModule;
    String watsonBucketModStamp;
    String watsonBucketModOffset;
    String failureBucketId;
    String watsonBucketModVer;
    String crashTime;
    List<CallStackEntry> stackOfCrashingThread;

    public WindowsCrashAnalysis(String rawAnalysis) {
        this.rawAnalysis = rawAnalysis;
    }
    
    String parseValueByRegexInternal(String key) {
        String patternString = String.format("^%s: +(.*)$", key);
        Pattern pattern = Pattern.compile(patternString, Pattern.MULTILINE);
        Matcher regexMatcher = pattern.matcher(rawAnalysis);
        if(regexMatcher.find()) {
            return regexMatcher.group(1);
        }
        throw new IllegalArgumentException(String.format("Unable to find %s in raw dump file", key));
    }
    
    void parseWatsonBucketModule() {
        watsonBucketModule = parseValueByRegexInternal("WATSON_BKT_MODULE");
    }
    
    void parseWatsonBucketModStamp() {
        watsonBucketModStamp = parseValueByRegexInternal("WATSON_BKT_MODSTAMP");
    }
    
    void parseWatsonBucketModOffset() {
        watsonBucketModOffset = parseValueByRegexInternal("WATSON_BKT_MODOFFSET");
    }
    
    void parseWatsonBucketModVer() {
        watsonBucketModVer = parseValueByRegexInternal("WATSON_BKT_MODVER");
    }
    
    void parseCrashTime() {
        crashTime = parseValueByRegexInternal("Debug session time");
    }
    
    void parseFailureBucketId() {
        failureBucketId = parseValueByRegexInternal("FAILURE_BUCKET_ID");
    }
    
    void parseStackOfCrashingThread() {
        /*
        String patternString = "\\RSTACK_TEXT:\\R(.*){\\R,2}";
        Pattern pattern = Pattern.compile(patternString, Pattern.MULTILINE);
        Matcher regexMatcher = pattern.matcher(rawAnalysis);
        if(regexMatcher.find()) {
            String stackText = regexMatcher.group(1);
            String[] lines = stackText.split("\\R");
            
            stackOfCrashingThread = new ArrayList<>();
            try {
                for(String line : lines) {
                    stackOfCrashingThread.add(new WindowsCallStackEntry(line));
                }
            } catch(Exception ex) {
                throw new IllegalArgumentException(String.format("Unable parse stack text '%s'", stackText), ex);
            }
        } else {
            throw new IllegalArgumentException("Unable to parse STACK_TEXT from raw dump file");
        }
*/
        int start = rawAnalysis.indexOf("STACK_TEXT:");
        
        if(start < 0) {
            throw new IllegalStateException("Cannot find start of STACK_TEXT in " + rawAnalysis);
        }
        start += 12;
        
        int end = rawAnalysis.indexOf(System.lineSeparator() + System.lineSeparator(), start);
        if(end < start) {
            end = rawAnalysis.indexOf("\n\n", start);
        }
        if(end < start) {
            throw new IllegalStateException("Cannot find end of STACK_TEXT in " + rawAnalysis);
        }
        
        String stackText = rawAnalysis.substring(start, end);
        String[] lines = stackText.split("\\R");
            
        stackOfCrashingThread = new ArrayList<>();
        try {
            for(String line : lines) {
                if(StringUtils.isEmptyOrWhitespace(line)) {
                    continue;
                }
                stackOfCrashingThread.add(new WindowsCallStackEntry(line));
            }
        } catch(Exception ex) {
            throw new IllegalArgumentException(String.format("Unable parse stack text '%s'", stackText), ex);
        }
    }
    
    public void parse() {
        parseWatsonBucketModule();
        parseWatsonBucketModStamp();
        parseWatsonBucketModOffset();
        parseWatsonBucketModVer();
        parseFailureBucketId();
        parseStackOfCrashingThread();
        parseCrashTime();
    }
    
    @Override
    public String getRawAnalysis() {
        return rawAnalysis;
    }

    @Override
    public String getWatsonBucketModule() {
        return watsonBucketModule;
    }

    @Override
    public String getWatsonBucketModStamp() {
        return watsonBucketModStamp;
    }

    @Override
    public String getWatsonBucketModOffset() {
        return watsonBucketModOffset;
    }
    
    @Override
    public String getWatsonBucketModVer() {
        return watsonBucketModVer;
    }
    
    @Override
    public String getFailureBucketId() {
        return failureBucketId;
    }
    
    @Override
    public List<CallStackEntry> getStackOfCrashingThread() {
        return stackOfCrashingThread;
    }

    /**
     * @return the crashFileName
     */
    @Override
    public String getCrashFileName() {
        return crashFileName;
    }

    /**
     * @param crashFileName the crashFileName to set
     */
    public void setCrashFileName(String crashFileName) {
        this.crashFileName = crashFileName;
    }

    @Override
    public String getCrashTime() {
        return crashTime;
    }
 
}
