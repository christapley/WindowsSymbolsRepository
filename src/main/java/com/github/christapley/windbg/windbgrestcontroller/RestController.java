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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.christapley.windbg.windbgrestcontroller.crashanalysis.AsyncCrashAnalyser;
import com.github.christapley.windbg.windbgrestcontroller.crashanalysis.WindowsAsyncCrashAnalyser;
import com.github.christapley.windbg.windbgrestcontroller.db.CrashAnalysisStatusRepository;
import com.github.christapley.windbg.windbgrestcontroller.db.entity.CrashAnalysisStatus;
import com.github.christapley.windbg.windbgrestcontroller.storage.StorageFileNotFoundException;
import com.github.christapley.windbg.windbgrestcontroller.storage.StorageService;
import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Collectors;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author Chris
 */


@Controller
public class RestController {
    
    private static final Logger LOG = LoggerFactory.getLogger(RestController.class);
    
    @Autowired
    StorageService storageService;
    
    @Autowired
    AsyncCrashAnalyser asyncCrashAnalyser;

    @GetMapping("/")
    public String listUploadedFiles(Model model) throws IOException {

        model.addAttribute("files", storageService.loadAll().map(
                path -> MvcUriComponentsBuilder.fromMethodName(RestController.class,
                        "serveFile", path.getFileName().toString()).build().toString())
                .collect(Collectors.toList()));

        return "uploadForm";
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }
    
    @GetMapping("/dump/process/{processId}/status")
    @ResponseBody
    public ResponseEntity<CrashAnalysisStatus> processDumpFileStatus(@PathVariable("processId") long processId) {
        return ResponseEntity.ok().body(asyncCrashAnalyser.getStatus(processId));
    }
    
    @PostMapping("/dump/process")
    public String processDumpFile(@RequestParam("file") MultipartFile file,
            RedirectAttributes redirectAttributes) throws JsonProcessingException {

        Path storedDumpFile = storageService.store(file);
        
        CrashAnalysisStatus status = asyncCrashAnalyser.start(storedDumpFile.toFile());
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        
        redirectAttributes.addFlashAttribute("message",
               mapper.writeValueAsString(status));

        return "redirect:/";
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    } 
}
