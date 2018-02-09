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
package com.github.christapley.windbg.windbgrestcontroller.storage;

import com.github.christapley.windbg.windbgrestcontroller.crashanalysis.CrashAnalysis;
import java.io.File;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author ctapley
 */
public interface DumpFileStorageService {
    File storeDumpFileInTempArea(MultipartFile file);
    void moveDumpFileInTempAreaToJobArea(File dumpFileInTempArea, Long dumpId);
    void storeCrashAnalysisInJobArea(CrashAnalysis crashAnalysis, Long dumpId);
    
    Resource getDumpFileFromJobAreaAsResource(Long dumpId);
    Resource getCrashAnalysisFromJobAreaAsResource(Long dumpId);
    Resource getRawCrashAnalysisFromJobAreaAsResource(Long dumpId);
}
