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
package com.github.christapley.windbg.windbgrestcontroller.db.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;


/**
 *
 * @author ctapley
 */
@Entity
@Data
@NoArgsConstructor(force = true)
public class CrashAnalysisStatus {
    @Id
    @GeneratedValue
    private Long id;
    
    @Enumerated(EnumType.STRING)
    private ProcessingStatus status;
    
    @Column(nullable = true)
    private String message;
    
    @Column
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date startDateTime;
    
    @Column(nullable = true)
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date endDateTime;
    
    @Column(nullable = true)
    private Long dumpId;
    
    @Column(nullable = false)
    private String dumpFileName;
    
    @JsonIgnore
    @Column(nullable = false)
    private String dumpFile;
}
