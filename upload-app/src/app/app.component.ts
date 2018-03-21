import {Component, ViewChild} from '@angular/core';
import {MatTabGroup, MatTab} from '@angular/material'

import {UploadDumpProcessComponent} from "./upload-dump-process/upload-dump-process.component"
import {ICrashAnalysisStatus} from "./upload-dump-process/upload.status";
import {DumpEntrySearchResultsComponent} from "./dump-entry-search-results/dump-entry-search-results.component"

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  @ViewChild(UploadDumpProcessComponent) uploadProcessor:UploadDumpProcessComponent;
  @ViewChild(DumpEntrySearchResultsComponent) dumpEntrySearcher:DumpEntrySearchResultsComponent;
  title = 'app';

  onDumpProcessingCompleted(item: ICrashAnalysisStatus) {
    this.dumpEntrySearcher.addDumpEntryId(item.dumpId)
  }
  
  constructor() {
    
  }

  ngOnInit(): void {
    this.uploadProcessor.onItemCompleted = (item) => this.onDumpProcessingCompleted(item);
  }
}
