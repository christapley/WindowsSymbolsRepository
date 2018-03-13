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
import com.github.christapley.windbg.windbgrestcontroller.db.DumpDatabaseModel;
import com.github.christapley.windbg.windbgrestcontroller.db.entity.CrashAnalysisStatus;
import com.github.christapley.windbg.windbgrestcontroller.db.entity.DumpFileEntry;
import com.github.christapley.windbg.windbgrestcontroller.db.entity.ProcessingStatus;
import com.github.christapley.windbg.windbgrestcontroller.storage.DumpFileStorageService;
import java.io.File;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import org.apache.commons.lang3.exception.ExceptionUtils;
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
 
    @Autowired
    DumpFileStorageService storageService;
    
    @Autowired
    DumpDatabaseModel dumpDatbaseModel;
    
    private static final Logger LOG = LoggerFactory.getLogger(WindowsAsyncCrashAnalyserInternal.class);
    
    public void completeStatus(CrashAnalysisStatus status) {
        status.setStatus(ProcessingStatus.Complete);
        status.setMessage(null);
        status.setEndDateTime(Date.from(Instant.now()));
        crashAnalysisStatusRepository.save(status);
    }
    
    public void failStatus(CrashAnalysisStatus status, Exception ex) {
        status.setStatus(ProcessingStatus.Failed);
        status.setMessage(Arrays.toString(ExceptionUtils.getRootCauseStackTrace(ex)));
        status.setEndDateTime(Date.from(Instant.now()));
        crashAnalysisStatusRepository.save(status);
    }
    
    @Async
    public void processDumpFileAsync(Long statusId) {
        CrashAnalysisStatus status = crashAnalysisStatusRepository.findOne(statusId);
        try {
            status.setStatus(ProcessingStatus.Processing);
            status.setMessage("Extracting dump information");

            crashAnalysisStatusRepository.save(status);

            LOG.info("Processing " + status.getDumpFile());
            CrashAnalysis crashAnalysis = crashAnalyser.analyseCrashDump(new File(status.getDumpFile()));

            status.setMessage("Storing dump information");
            crashAnalysisStatusRepository.save(status);

            DumpFileEntry entry = dumpDatbaseModel.insertCrashAnalysis(crashAnalysis);
            storageService.moveDumpFileInTempAreaToJobArea(new File(status.getDumpFile()), entry.getId());
            storageService.storeCrashAnalysisInJobArea(crashAnalysis, entry.getId());

            status.setDumpId(entry.getId());
            
            completeStatus(status);
        } catch(Exception ex) {
            LOG.error(String.format("Failed to process job %d", statusId.intValue()), ex);
            failStatus(status, ex);
        }
    }
}
