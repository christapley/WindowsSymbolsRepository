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
package com.github.christapley.windbg.windbgrestcontroller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.christapley.windbg.windbgrestcontroller.crashanalysis.AsyncCrashAnalyser;
import com.github.christapley.windbg.windbgrestcontroller.db.DumpDatabaseModel;
import com.github.christapley.windbg.windbgrestcontroller.db.DumpFileEntryRepository;
import com.github.christapley.windbg.windbgrestcontroller.db.entity.CrashAnalysisStatus;
import com.github.christapley.windbg.windbgrestcontroller.db.entity.DumpFileEntry;
import com.github.christapley.windbg.windbgrestcontroller.response.DumpFileEntryPagingContextResponse;
import com.github.christapley.windbg.windbgrestcontroller.response.DumpTypeResponse;
import com.github.christapley.windbg.windbgrestcontroller.storage.DumpFileStorageService;
import com.github.christapley.windbg.windbgrestcontroller.storage.StorageFileNotFoundException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

/**
 *
 * @author Chris
 */

@CrossOrigin
@Controller
public class RestController {
    
    private static final Logger LOG = LoggerFactory.getLogger(RestController.class);
    
    @Autowired
    DumpFileStorageService storageService;
    
    @Autowired
    AsyncCrashAnalyser asyncCrashAnalyser;

    @Autowired
    DumpDatabaseModel dumpDatabaseModel;
    
    @Autowired
    DumpFileEntryRepository dumpFileEntryRepository;
    
    @GetMapping("dump/entry/list")
    @ResponseBody
    public ResponseEntity<DumpFileEntryPagingContextResponse> listDumps(
            @RequestParam(name = "sort", defaultValue = "enteredDateTime") String sort,
            @RequestParam(name = "order", defaultValue = "desc") String order,
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size
    ) {
        
        Pageable pageable = new PageRequest(page, size, new Sort(Direction.fromString(order), sort));
        Page<DumpFileEntry> pageContent = dumpFileEntryRepository.findAll(pageable);
        
        DumpFileEntryPagingContextResponse response = new DumpFileEntryPagingContextResponse();
        response.setDumpFileEntries(pageContent.getContent());
        response.setTotalFileEntries(pageContent.getTotalElements());
        response.setTotalFileEntryPages((long)pageContent.getTotalPages());
        response.setRequestedPageNumber((long)page);
        response.setRequestedPageSize((long)size);
        
        return ResponseEntity.ok().body(response);
    }
    
    @GetMapping("dump/list/{dumps}")
    @ResponseBody
    public ResponseEntity<List<DumpTypeResponse>> listDumps(@PathVariable("dumps") List<Long> fileEntryIds) {
        return ResponseEntity.ok().body(dumpDatabaseModel.findFromDumpEntryIds(fileEntryIds));
    }
    
    @GetMapping("dump/file/{dumpFileEntryId}/dump")
    @ResponseBody
    public ResponseEntity<Resource> getDumpFile(@PathVariable("dumpFileEntryId") Long fileEntryId) {
        Resource file = storageService.getDumpFileFromJobAreaAsResource(fileEntryId);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }
    
    @GetMapping("dump/file/{dumpFileEntryId}/analysis/raw")
    @ResponseBody
    public ResponseEntity<Resource> getDumpRawAnalysisFile(@PathVariable("dumpFileEntryId") Long fileEntryId) {
        Resource file = storageService.getRawCrashAnalysisFromJobAreaAsResource(fileEntryId);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }
    
    @GetMapping("dump/file/{dumpFileEntryId}/analysis/parsed")
    @ResponseBody
    public ResponseEntity<Resource> getDumpParsedAnalysisFile(@PathVariable("dumpFileEntryId") Long fileEntryId) {
        Resource file = storageService.getCrashAnalysisFromJobAreaAsResource(fileEntryId);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }
    
    @GetMapping("/dump/process/status")
    @ResponseBody
    public ResponseEntity<List<CrashAnalysisStatus>> processDumpFileStatuses() {
        return ResponseEntity.ok().body(new ArrayList<>());
    }
    
    @GetMapping("/dump/process/status/{processIds}")
    @ResponseBody
    public ResponseEntity<List<CrashAnalysisStatus>> processDumpFileStatuses(@PathVariable("processIds") List<Long> processIds) {
        return ResponseEntity.ok().body(asyncCrashAnalyser.getStatuses(processIds));
    }
    
    @PostMapping("/dump/process")
    @ResponseBody
    public ResponseEntity<CrashAnalysisStatus> processDumpFile(@RequestParam("file") MultipartFile file,
            RedirectAttributes redirectAttributes) throws JsonProcessingException {

        File storedDumpFile = storageService.storeDumpFileInTempArea(file);
        CrashAnalysisStatus status = asyncCrashAnalyser.start(storedDumpFile);
        return ResponseEntity.ok().body(status);
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    } 
}
