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

import com.github.christapley.windbg.windbgrestcontroller.db.entity.DumpFileEntry;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.QueryByExampleExecutor;

/**
 *
 * @author ctapley
 */
public interface DumpFileEntryRepository extends PagingAndSortingRepository<DumpFileEntry, Long>, QueryByExampleExecutor<DumpFileEntry> {
    
    @Query("SELECT dt.id FROM DumpType dt, DumpEntryGroup deg, DumpFileEntry dfe WHERE dt.id=deg.dumpType AND deg.id=dfe.dumpEntryGroup AND dfe.id = :id")
    Long findDumpTypeIdFromDumpFileEntryId(@Param("id") Long dumpFileEntryId);
    
    List<DumpFileEntry> findAllByDumpEntryGroup(Long dumpEntryGroup);    
}
