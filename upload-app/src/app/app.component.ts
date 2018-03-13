import {Component} from '@angular/core';
import {FileUploader, FileItem, ParsedResponseHeaders} from 'ng2-file-upload';
import {MatTableDataSource} from '@angular/material'
import {SearchService} from "./app.search.service";
import {IDumpFileEntry, IDumpEntryGroup, IDumpType} from "./search.results";
import {UploadDumpProcessComponent} from "./upload-dump-process/upload-dump-process.component"
import {ICrashAnalysisStatus} from "./upload-dump-process/upload.status";
import { Http } from '@angular/http';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  providers: [SearchService]
})
export class AppComponent {
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
    let data = JSON.parse(response);
    console.log(response);
  }

  onDumpProcessingCompleted(item: ICrashAnalysisStatus) {
    this.searchQuery.push(item.dumpId);
  }

  searchQuery: number[];
  searchResults: IDumpType[];
 
  constructor(private searchService: SearchService, private uploadProcessor: UploadDumpProcessComponent) {
    this.searchQuery = [112];
  }
 
  addSearchQuery(): void {
    this.searchQuery.push(60);
    this.getSearchResults();
  }

  getSearchResults(): void {
    this.searchService.getSearchResults(this.searchQuery)
        .subscribe(
            resultArray => this.searchResults = resultArray,
            error => console.log("Error :: " + error)
        )
  }

  ngOnInit(): void {
      this.getSearchResults();
      this.uploader.onSuccessItem = (item, response, status, headers) => this.onSuccessItem(item, response, status, headers);
      this.uploadProcessor.onItemCompleted = (item) => this.onDumpProcessingCompleted(item);
  }
}
