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

import com.github.christapley.windbg.windbgrestcontroller.db.CrashAnalysisStatusRepository;
import com.github.christapley.windbg.windbgrestcontroller.db.entity.CrashAnalysisStatus;
import com.github.christapley.windbg.windbgrestcontroller.db.entity.ProcessingStatus;
import java.io.File;
import java.time.Instant;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author ctapley
 */

@Service
public class WindowsAsyncCrashAnalyser implements AsyncCrashAnalyser {

    private static final Logger LOG = LoggerFactory.getLogger(WindowsAsyncCrashAnalyser.class);
    
    @Autowired
    CrashAnalyser crashAnalyser;

    @Autowired 
    WindowsAsyncCrashAnalyserInternal windowsAsyncCrashAnalyserInternal;
    
    @Autowired
    CrashAnalysisStatusRepository crashAnalysisStatusRepository;

    public CrashAnalysisStatus createNewStatus(String filePath) {
        CrashAnalysisStatus status = new CrashAnalysisStatus();
        status.setStatus(ProcessingStatus.Queued);
        status.setStartDateTime(Date.from(Instant.now()));
        status.setDumpFile(filePath);
        return status;
    }
    
    @Override
    public CrashAnalysisStatus start(File dumpFile) {

        CrashAnalysisStatus status = createNewStatus(dumpFile.getAbsolutePath());
        crashAnalysisStatusRepository.save(status);
        
        windowsAsyncCrashAnalyserInternal.processDumpFileAsync(status.getId());
        
        return status;
    }

    @Override
    public CrashAnalysisStatus getStatus(Long id) {
        return crashAnalysisStatusRepository.findOne(id);
    }
    
}
