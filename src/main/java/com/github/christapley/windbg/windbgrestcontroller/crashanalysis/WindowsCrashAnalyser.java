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

import com.github.christapley.windbg.windbgrestcontroller.util.TemporaryFile;
import java.io.File;
import java.io.IOException;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 *
 * @author Chris
 */
@Service
public class WindowsCrashAnalyser implements CrashAnalyser {

    @Value("${windows.windbg.exe.path}")
    String windbgExePath;
    
    @Value("${windows.windbg.symbols}")
    String windbgSymbols;
    
    @Value("${windows.windbg.command.timeout.milliseconds:300000}")
    int windbgCommandTimeoutMs;
    
    TemporaryFile writeWindbgCommandsFile() throws IOException {
        
        String windbgCrashDiagScript = 
                ".sympath " + windbgSymbols + "\n" +
                "!sym\n" +
                ".reload\n" +
                ".expr /s c++\n" +
                ".ecxr\n" +
                "!analyze -v -f\n" +
                "~*k 0\n" + 
                "q\n";
           
        TemporaryFile tempFile = new TemporaryFile();
        FileUtils.writeStringToFile(tempFile.getTemporaryFile(), windbgCrashDiagScript, "UTF8");
        return tempFile;
    }
    
    TemporaryFile getWindbgOutputFile() throws IOException {
        return new TemporaryFile();
    }
    
    void runWindbgCommand(File dumpFile, File windbgCommandsFile, File windbgOutputFile) throws IOException {
        String command = String.format("%s -z \"%s\" -c \"$<%s\" -logo \"%s\"", 
                windbgExePath, 
                dumpFile.getAbsolutePath(), 
                windbgCommandsFile.getAbsolutePath(),
                windbgOutputFile.getAbsolutePath());
        
        CommandLine cmdLine = CommandLine.parse(command);
        DefaultExecutor executor = new DefaultExecutor();
        ExecuteWatchdog watchdog = new ExecuteWatchdog(windbgCommandTimeoutMs);
        executor.setWatchdog(watchdog);
        int exitValue = executor.execute(cmdLine);
        if(exitValue != 0) {
            throw new IOException(String.format("Command (%s) failed with exit code %d", command, exitValue));
        }
    }
    
    WindowsCrashAnalysis getWindowsCrashAnalysis(String rawWindbgOutput) {
        return new WindowsCrashAnalysis(rawWindbgOutput);
    }
    
    CrashAnalysis parseWindbgOutput(File windbgOutputFile, String dumpFileName) throws IOException {
        String rawWindbgOutput = FileUtils.readFileToString(windbgOutputFile, "UTF8");
        WindowsCrashAnalysis windowsCrashAnalysis = getWindowsCrashAnalysis(rawWindbgOutput);
        windowsCrashAnalysis.setCrashFileName(dumpFileName);
        windowsCrashAnalysis.parse();
        return windowsCrashAnalysis;
    }
            
    @Override
    public CrashAnalysis analyseCrashDump(File dumpFile) {
        try (TemporaryFile windbgCommandsFile = writeWindbgCommandsFile()) {
            try (TemporaryFile windbgOutputFile = getWindbgOutputFile()) {
                runWindbgCommand(dumpFile, windbgCommandsFile.getTemporaryFile(), windbgOutputFile.getTemporaryFile());
                return parseWindbgOutput(windbgOutputFile.getTemporaryFile(), dumpFile.getName());
            }
        } catch (Exception ex) {
            throw new CrashAnalyserException(String.format("Failed to process dump file %s", dumpFile.getAbsolutePath()), ex);
        }
    }
    
}
