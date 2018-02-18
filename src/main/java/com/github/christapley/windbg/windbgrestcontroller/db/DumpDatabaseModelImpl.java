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
package com.github.christapley.windbg.windbgrestcontroller.db;

import com.github.christapley.windbg.windbgrestcontroller.crashanalysis.CrashAnalysis;
import com.github.christapley.windbg.windbgrestcontroller.db.entity.DumpFileEntry;
import com.github.christapley.windbg.windbgrestcontroller.db.entity.DumpType;
import java.time.Instant;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author ctapley
 */
@Service
public class DumpDatabaseModelImpl implements DumpDatabaseModel {

    @Autowired
    DumpTypeRepository dumpTypeRepository;
    
    @Autowired
    DumpFileEntryRepository dumpFileEntryRepository;
    
    DumpType findOrCreateDumpType(CrashAnalysis crashAnalysis) {
        DumpType dumpType = dumpTypeRepository.findOneByFailureBucketIdIgnoreCase(crashAnalysis.getFailureBucketId());
        if(dumpType == null) {
            dumpType = new DumpType();
            dumpType.setFailureBucketId(crashAnalysis.getFailureBucketId());
            try {
                dumpTypeRepository.save(dumpType);
            } catch(Exception ex) {
                dumpType = dumpTypeRepository.findOneByFailureBucketIdIgnoreCase(crashAnalysis.getFailureBucketId());
            }
        }
        return dumpType;
    }
    
    @Override
    public DumpFileEntry insertCrashAnalysis(CrashAnalysis crashAnalysis) {
        DumpType dumpType = findOrCreateDumpType(crashAnalysis);
        
        DumpFileEntry entry = new DumpFileEntry();
        entry.setEnteredDataTime(Date.from(Instant.now()));
        entry.setDumpType(dumpType);
        entry.setDumpFileName(crashAnalysis.getCrashFileName());
        entry.setDumpChecksum(crashAnalysis.getWatsonBucketModStamp());
        entry.setDumpModule(crashAnalysis.getWatsonBucketModule());
        entry.setDumpOffset(crashAnalysis.getWatsonBucketModOffset());
        entry.setDumpVersion(crashAnalysis.getWatsonBucketModVer());
        dumpFileEntryRepository.save(entry);
        
        return entry;
    }
    
}
