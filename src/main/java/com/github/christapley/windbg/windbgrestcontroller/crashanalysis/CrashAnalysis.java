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

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

/**
 *
 * @author Chris
 */
public interface CrashAnalysis {
    
    @JsonIgnore
    String getRawAnalysis();
    
    String getCrashFileName();
    String getCrashTime();
    
    // Microsoft's bucketing http://www.freepatentsonline.com/9710371.html
    String getWatsonBucketModule();
    String getWatsonBucketModStamp();
    String getWatsonBucketModOffset();
    String getWatsonBucketModVer();
    String getFailureBucketId();
    
    List<CallStackEntry> getStackOfCrashingThread();
}
