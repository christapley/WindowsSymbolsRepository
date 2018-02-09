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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.christapley.windbg.windbgrestcontroller.crashanalysis.CrashAnalysis;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import javax.annotation.PostConstruct;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author ctapley
 */
@Service
public class FileSystemDumpFileStorageService implements DumpFileStorageService {

    @Value("${filesystem.storage.root.path}")
    Path fileSystemRoot;
    
    Path tempArea;
    Path jobArea;
    
    @Value("${filesystem.storage.raw.crash.analysis.name:application.pdb.log}")
    String rawCrashAnalysisFileName;
    
    @Value("${filesystem.storage.crash.analysis.name:crashAnalysis.json}")
    String crashAnalysisFileName;
    
    @Value("${filesystem.storage.dump.file.name:application.pdb}")
    String dumpFileName;
    
    @PostConstruct
    public void init() throws IOException {
        tempArea = fileSystemRoot.resolve("temp");
        jobArea = fileSystemRoot.resolve("jobs");
        
        Files.createDirectories(tempArea);
        Files.createDirectories(jobArea);
    }
    
    public File storeMultipartFileInPath(MultipartFile file, Path destinationDir) {
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file " + filename);
            }
            if (filename.contains("..")) {
                // This is a security check
                throw new StorageException(
                        "Cannot store file with relative path outside current directory "
                                + filename);
            }
            Path destination = destinationDir.resolve(UUID.randomUUID().toString() + "-" + filename);
            Files.copy(file.getInputStream(), destination,
                    StandardCopyOption.REPLACE_EXISTING);
            return destination.toFile();
        }
        catch (IOException ex) {
            throw new StorageException("Failed to store file " + filename, ex);
        }
    }
    
    public Path getJobFolder(Long dumpId) {
        try {
            Path jobFolder = jobArea.resolve(dumpId.toString());
            if(!jobFolder.toFile().exists()) {
                Files.createDirectories(jobFolder);
            }
            return jobFolder;
        } catch(IOException ex) {
            throw new StorageException(String.format("Failed to get job area in %s for jobId %d", jobArea.toFile().getAbsolutePath(), dumpId.intValue()), ex);
        }
    }
    
    @Override
    public File storeDumpFileInTempArea(MultipartFile file) {
        return storeMultipartFileInPath(file, tempArea);
    }

    @Override
    public void moveDumpFileInTempAreaToJobArea(File dumpFileInTempArea, Long dumpId) {
        Path jobFolder = getJobFolder(dumpId);
        Path destinationFile = jobFolder.resolve(dumpFileName);
        try {
            FileUtils.moveFile(dumpFileInTempArea, destinationFile.toFile());
        } catch(IOException ex) {
            throw new StorageException(String.format("Failed to move temp dump file %s to job folder as file %s", dumpFileInTempArea.getAbsolutePath(), destinationFile.toFile().getAbsolutePath()), ex);
        }
    }

    public void storeRawCrashAnalysisInJobArea(String rawCrashAnalysis, Long dumpId) {
        Path jobFolder = getJobFolder(dumpId);
        Path destination = jobFolder.resolve(rawCrashAnalysisFileName);
        try {
            FileUtils.writeStringToFile(destination.toFile(),  rawCrashAnalysis, "UTF8");
        } catch(Exception ex) {
            throw new StorageException(String.format("Failed to store crash analysis to file %s for dumpId %d", destination.toFile().getAbsolutePath(), dumpId.intValue()), ex);
        }
    }
    
    public void storeCrashAnalysisJsonInJobArea(CrashAnalysis crashAnalysis, Long dumpId) {
        Path jobFolder = getJobFolder(dumpId);
        Path destination = jobFolder.resolve(crashAnalysisFileName);
        try {
            ObjectMapper mapper = new ObjectMapper();
            FileUtils.writeStringToFile(destination.toFile(),  mapper.writeValueAsString(crashAnalysis), "UTF8");
        } catch(Exception ex) {
            throw new StorageException(String.format("Failed to store crash analysis to file %s for dumpId %d", destination.toFile().getAbsolutePath(), dumpId.intValue()), ex);
        }
    }
    
    @Override
    public void storeCrashAnalysisInJobArea(CrashAnalysis crashAnalysis, Long dumpId) {
        storeCrashAnalysisJsonInJobArea(crashAnalysis, dumpId);
        storeRawCrashAnalysisInJobArea(crashAnalysis.getRawAnalysis(), dumpId);
    }

    public Resource getFileFromJobAreaAsResource(Long dumpId, String filename) {
        try {
            Path jobFolder = getJobFolder(dumpId);
            Path file = jobFolder.resolve(filename);
            
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
                throw new StorageFileNotFoundException("Could not read file: " + filename);
            }
        }
        catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }
    
    @Override
    public Resource getDumpFileFromJobAreaAsResource(Long dumpId) {
        return getFileFromJobAreaAsResource(dumpId, dumpFileName);
    }

    @Override
    public Resource getCrashAnalysisFromJobAreaAsResource(Long dumpId) {
        return getFileFromJobAreaAsResource(dumpId, crashAnalysisFileName);
    }
    
    @Override
    public Resource getRawCrashAnalysisFromJobAreaAsResource(Long dumpId) {
        return getFileFromJobAreaAsResource(dumpId, rawCrashAnalysisFileName);
    }
}
