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


import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Chris
 */
public class WindowsCallStackEntryTest {
    
    @Test
    public void stackTextLine() {
        WindowsCallStackEntry entry = new WindowsCallStackEntry("000000cc`d22f7160 00007ff9`dcd21ac2 : 000000cc`d22f7c00 00007ff9`00000000 00000000`00000000 00000000`0000000f : NCSServerUtil!NCS::IWS::Metadata::CombinedMetadataReader::GetFileMetadata+0xb08");
        Assert.assertEquals("NCSServerUtil", entry.getModule());
        Assert.assertEquals("NCS::IWS::Metadata::CombinedMetadataReader::GetFileMetadata", entry.getFunction());
        Assert.assertEquals("0xb08", entry.getOffset());
    }
    
    @Test
    public void callStacksTextLine() {
        WindowsCallStackEntry entry = new WindowsCallStackEntry("00 000000cc`d156f9d8 00007ff9`fcf83acf ntdll!NtWaitForSingleObject+0x14");
        Assert.assertEquals("ntdll", entry.getModule());
        Assert.assertEquals("NtWaitForSingleObject", entry.getFunction());
        Assert.assertEquals("0x14", entry.getOffset());
    }
}
