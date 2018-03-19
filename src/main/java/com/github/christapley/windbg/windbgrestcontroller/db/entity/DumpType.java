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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author ctapley
 */
@Entity
@Data
@NoArgsConstructor(force = true)
@Table(indexes = {
    @Index(columnList = "failureBucketId", name = "failureBucketId_hidx")
})
public class DumpType {
    @Id
    @GeneratedValue
    private Long id;
    
    @Column(nullable = false, unique = true, length = 2048)
    private String failureBucketId;
    
    @Column(nullable = true)
    private String briefDescription;
    
    @Column(nullable = false)
    private boolean resolved;
}
