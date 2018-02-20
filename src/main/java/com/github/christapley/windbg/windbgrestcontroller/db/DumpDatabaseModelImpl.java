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
import com.github.christapley.windbg.windbgrestcontroller.db.entity.DumpEntryGroup;
import com.github.christapley.windbg.windbgrestcontroller.db.entity.DumpFileEntry;
import com.github.christapley.windbg.windbgrestcontroller.db.entity.DumpType;
import com.github.christapley.windbg.windbgrestcontroller.response.DumpTypeResponse;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.transaction.Transactional;
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
    DumpEntryGroupRepository dumpEntryGroupRepository;
    
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
    
    @Transactional
    DumpEntryGroup findOrCreateDumpEntryGroup(CrashAnalysis crashAnalysis) {
        DumpEntryGroup dumpEntryGroup = dumpEntryGroupRepository.findOneByDumpModuleAndDumpVersionAndDumpOffset(crashAnalysis.getWatsonBucketModule(), crashAnalysis.getWatsonBucketModVer(), crashAnalysis.getWatsonBucketModOffset());
        if(dumpEntryGroup == null) {
            dumpEntryGroup = new DumpEntryGroup();
            dumpEntryGroup.setDumpChecksum(crashAnalysis.getWatsonBucketModStamp());
            dumpEntryGroup.setDumpModule(crashAnalysis.getWatsonBucketModule());
            dumpEntryGroup.setDumpOffset(crashAnalysis.getWatsonBucketModOffset());
            dumpEntryGroup.setDumpVersion(crashAnalysis.getWatsonBucketModVer());
            dumpEntryGroup.setDumpType(findOrCreateDumpType(crashAnalysis));
            try {
                dumpEntryGroupRepository.save(dumpEntryGroup);
            } catch(Exception ex) {
                dumpEntryGroup = dumpEntryGroupRepository.findOneByDumpModuleAndDumpVersionAndDumpOffset(crashAnalysis.getWatsonBucketModule(), crashAnalysis.getWatsonBucketModVer(), crashAnalysis.getWatsonBucketModOffset());
            }
        }
        return dumpEntryGroup;
    }
    
    @Override
    public DumpFileEntry insertCrashAnalysis(CrashAnalysis crashAnalysis) {
        DumpEntryGroup dumpEntryGroup = findOrCreateDumpEntryGroup(crashAnalysis);
        
        DumpFileEntry entry = new DumpFileEntry();
        entry.setEnteredDateTime(Date.from(Instant.now()));
        entry.setCrashDateTime(Date.from(Instant.now()));
        entry.setDumpEntryGroup(dumpEntryGroup);
        dumpFileEntryRepository.save(entry);
        
        return entry;
    }

    // this should be a query
    List<Long> findUniqueDumpTypeIds(List<Long> dumpEntryIds) {
        Map<Long, Boolean> dumpTypeIds = new HashMap<>();
        for(Long dumpFileEntryId : dumpEntryIds) {
            Long dumpTypeId = dumpFileEntryRepository.findDumpTypeIdFromDumpFileEntryId(dumpFileEntryId);
            dumpTypeIds.put(dumpTypeId, Boolean.TRUE);
        }
        return new ArrayList<>(dumpTypeIds.keySet());
    }
    
    @Override
    public List<DumpTypeResponse> findFromDumpEntryIds(List<Long> dumpEntryIds) {
        List<Long> uniqueDumpTypeIds = findUniqueDumpTypeIds(dumpEntryIds);
        return null;
    }
}
