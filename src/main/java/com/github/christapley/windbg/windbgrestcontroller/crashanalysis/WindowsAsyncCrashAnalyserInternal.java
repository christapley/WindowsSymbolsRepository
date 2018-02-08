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
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 *
 * @author ctapley
 */
@Service
public class WindowsAsyncCrashAnalyserInternal {
    @Autowired
    CrashAnalyser crashAnalyser;

    @Autowired
    CrashAnalysisStatusRepository crashAnalysisStatusRepository;
 
    private static final Logger LOG = LoggerFactory.getLogger(WindowsAsyncCrashAnalyserInternal.class);
    
    public void completeStatus(CrashAnalysisStatus status) {
        status.setStatus(ProcessingStatus.Complete);
        status.setMessage(null);
        status.setEndDateTime(Date.from(Instant.now()));
        crashAnalysisStatusRepository.save(status);
    }
    
    @Async
    public CompletableFuture<Boolean> processDumpFileAsync(Long id) {
        CrashAnalysisStatus status = crashAnalysisStatusRepository.findOne(id);
        
        status.setStatus(ProcessingStatus.Processing);
        status.setMessage("Extracting dump information");
        
        crashAnalysisStatusRepository.save(status);
        
        LOG.info("Processing " + status.getDumpFile());
        CrashAnalysis crashAnalysis = crashAnalyser.analyseCrashDump(new File(status.getDumpFile()));
        
        completeStatus(status);
        
        return CompletableFuture.completedFuture(Boolean.TRUE);
    }
}
