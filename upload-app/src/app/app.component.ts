import {Component, ViewChild} from '@angular/core';
import {FileUploader, FileItem, ParsedResponseHeaders} from 'ng2-file-upload';
import {MatTableDataSource} from '@angular/material'

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
  
  public uploader:FileUploader = new FileUploader({
    url: 'http://localhost:8899/dump/process',
    headers: [{name:'Accept', value:'application/json'}],
    autoUpload: true
  });

  public hasBaseDropZoneOver:boolean = false;
  public fileOverBase(e:any):void {
    this.hasBaseDropZoneOver = e;
  }
  
  onSuccessItem(item: FileItem, response: string, status: number, headers: ParsedResponseHeaders): any {
    console.debug(response);
    let statusObject = JSON.parse(response) as ICrashAnalysisStatus;
    this.uploadProcessor.addUploadId(statusObject.id);
  }

  onDumpProcessingCompleted(item: ICrashAnalysisStatus) {
    this.dumpEntrySearcher.addDumpEntryId(item.dumpId)
  }

  
  constructor() {
    
  }
 
  

  ngOnInit(): void {
      this.uploader.onSuccessItem = (item, response, status, headers) => this.onSuccessItem(item, response, status, headers);
      this.uploadProcessor.onItemCompleted = (item) => this.onDumpProcessingCompleted(item);

      this.dumpEntrySearcher.addDumpEntryId(119)
  }
}
