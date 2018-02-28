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
import com.github.christapley.windbg.windbgrestcontroller.response.DumpEntryGroupResponse;
import com.github.christapley.windbg.windbgrestcontroller.response.DumpFileEntryResponse;
import com.github.christapley.windbg.windbgrestcontroller.response.DumpTypeResponse;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
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
                if(dumpType == null) {
                    throw ex;
                }
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
                if(dumpEntryGroup == null) {
                    throw ex;
                }
            }
        }
        return dumpEntryGroup;
    }
    
    public Date parseFromCrashAnalysisString(String dateString) throws ParseException {
        DateFormat df = new SimpleDateFormat("EEE MMM dd kk:mm:ss.SSS yyyy", Locale.ENGLISH);
        return df.parse(dateString);  
    }
    
    @Override
    public DumpFileEntry insertCrashAnalysis(CrashAnalysis crashAnalysis) {
        DumpEntryGroup dumpEntryGroup = findOrCreateDumpEntryGroup(crashAnalysis);
        
        DumpFileEntry entry = new DumpFileEntry();
        entry.setEnteredDateTime(Date.from(Instant.now()));
        try {
            entry.setCrashDateTime(parseFromCrashAnalysisString(crashAnalysis.getCrashTime()));
        } catch (ParseException ex) {
            entry.setCrashDateTime(entry.getEnteredDateTime());
        }
        entry.setDumpEntryGroup(dumpEntryGroup);
        entry.setFileName(crashAnalysis.getCrashFileName());
        dumpFileEntryRepository.save(entry);
        
        return entry;
    }

    // this should be a query
    public List<Long> findUniqueDumpTypeIds(List<Long> dumpEntryIds) {
        Map<Long, Boolean> dumpTypeIds = new HashMap<>();
        for(Long dumpFileEntryId : dumpEntryIds) {
            Long dumpTypeId = dumpFileEntryRepository.findDumpTypeIdFromDumpFileEntryId(dumpFileEntryId);
            dumpTypeIds.put(dumpTypeId, Boolean.TRUE);
        }
        return new ArrayList<>(dumpTypeIds.keySet());
    }
    
    public List<DumpFileEntryResponse> findDumpFileEntryForDumpEntryGroup(Long dumpEntryGroupId) {
        List<DumpFileEntryResponse> dumpFileEntryResponses = new ArrayList<>();
        List<DumpFileEntry> dumpFileEntries = dumpFileEntryRepository.findAllByDumpEntryGroupId(dumpEntryGroupId);
                
        for(DumpFileEntry dumpFileEntry : dumpFileEntries) {
            DumpFileEntryResponse dumpFileEntryResponse = new DumpFileEntryResponse();
            dumpFileEntryResponse.setId(dumpFileEntry.getId());
            dumpFileEntryResponse.setCrashDateTime(dumpFileEntry.getCrashDateTime());
            dumpFileEntryResponse.setEnteredDateTime(dumpFileEntry.getEnteredDateTime());
            dumpFileEntryResponse.setFileName(dumpFileEntry.getFileName());
            dumpFileEntryResponses.add(dumpFileEntryResponse);
        }
        
        return dumpFileEntryResponses;
    }
    
    public List<DumpEntryGroupResponse> findDumpEntryGroupsForDumpType(Long dumpTypeId) {
        
        List<DumpEntryGroupResponse> dumpEntryGroupsResponse = new ArrayList<>();
        List<DumpEntryGroup> dumpEntryGroups = dumpEntryGroupRepository.findAllByDumpTypeId(dumpTypeId);
        
        for(DumpEntryGroup dumpEntryGroup : dumpEntryGroups) {
            DumpEntryGroupResponse dumpEntryGroupResponse = new DumpEntryGroupResponse();
            dumpEntryGroupResponse.setId(dumpEntryGroup.getId());
            dumpEntryGroupResponse.setDumpChecksum(dumpEntryGroup.getDumpChecksum());
            dumpEntryGroupResponse.setDumpModule(dumpEntryGroup.getDumpModule());
            dumpEntryGroupResponse.setDumpOffset(dumpEntryGroup.getDumpOffset());
            dumpEntryGroupResponse.setDumpVersion(dumpEntryGroup.getDumpVersion());
            dumpEntryGroupResponse.setDumpFileEntries(findDumpFileEntryForDumpEntryGroup(dumpEntryGroup.getId()));
            dumpEntryGroupsResponse.add(dumpEntryGroupResponse);
        }
        return dumpEntryGroupsResponse;
    }
    
    public DumpTypeResponse findDumpTypeById(Long dumpTypeId) {
        DumpType dumpType = dumpTypeRepository.findOne(dumpTypeId);
        DumpTypeResponse dumpTypeResponse = new DumpTypeResponse();
        dumpTypeResponse.setBriefDescription(dumpType.getBriefDescription());
        dumpTypeResponse.setId(dumpType.getId());
        dumpTypeResponse.setResolved(dumpType.isResolved());
        dumpTypeResponse.setFailureBucketId(dumpType.getFailureBucketId());
        
        List<String> jiraIssues = new ArrayList<>();
        //todo
        dumpTypeResponse.setJiraIssues(jiraIssues);
                
        dumpTypeResponse.setDumpEntryGroups(findDumpEntryGroupsForDumpType(dumpTypeResponse.getId()));
        return dumpTypeResponse;
    }
    
    @Override
    public List<DumpTypeResponse> findFromDumpEntryIds(List<Long> dumpEntryIds) {
        List<Long> uniqueDumpTypeIds = findUniqueDumpTypeIds(dumpEntryIds);
        
        List<DumpTypeResponse> response = new ArrayList<>();
        
        for(Long dumpTypeId : uniqueDumpTypeIds) {
            response.add(findDumpTypeById(dumpTypeId));
        }
        
        return response;
    }
}
