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

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;

/**
 *
 * @author Chris
 */
public class WindowsCrashAnalysisTest {
    
    String windbgRawAnalysis;
    
    WindowsCrashAnalysis analysis;
    
    @Before
    public void setUp() throws IOException {
        windbgRawAnalysis = FileUtils.readFileToString(new File("src/test/resources/w3wp.exe.3808.dmp.log"), "UTF8");
        analysis = new WindowsCrashAnalysis(windbgRawAnalysis);
    }
    
    @Test
    public void parseWatsonBucketModule() {
        analysis.parseWatsonBucketModule();
        Assert.assertEquals("sblib.dll", analysis.getWatsonBucketModule());
    }
    
    @Test
    public void parseWatsonBucketModStamp() {
        analysis.parseWatsonBucketModStamp();
        Assert.assertEquals("5a62f8c3", analysis.getWatsonBucketModStamp());
    }
    
    @Test
    public void parseWatsonBucketModOffset() {
        analysis.parseWatsonBucketModOffset();
        Assert.assertEquals("80586", analysis.getWatsonBucketModOffset());
    }
    
    @Test
    public void parseFailureBucketId() {
        analysis.parseFailureBucketId();
        Assert.assertEquals("NULL_CLASS_PTR_READ_c0000005_sblib.dll!HexGeo::SpatialModeler::Port::GetOperator", analysis.getFailureBucketId());
    }
    
    @Test
    public void parseStackOfCrashingThread() {
        analysis.parseStackOfCrashingThread();
        List<CallStackEntry> callstack = analysis.getStackOfCrashingThread();
        Assert.assertEquals("NCSServerUtil", callstack.get(4).getModule());
        Assert.assertEquals("NCS::IWS::Metadata::CombinedMetadataReader::GetFileMetadata", callstack.get(4).getFunction());
        Assert.assertEquals("0xb08", callstack.get(4).getOffset());
    }
}
