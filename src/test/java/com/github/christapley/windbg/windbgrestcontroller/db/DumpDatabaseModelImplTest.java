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

import java.text.ParseException;
import java.util.Date;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author Chris
 */
public class DumpDatabaseModelImplTest {
    
    
    DumpDatabaseModelImpl impl;
    
    @Before
    public void setup() {
        impl = new DumpDatabaseModelImpl();
    }
    
    @Test
    public void parseFromCrashAnalysisString_known() throws ParseException {
        Date actual = impl.parseFromCrashAnalysisString("Fri Feb  2 12:49:23.000 2018 (UTC + 8:00)");
        int stop =0 ;
    }
    
}
