import { Component } from '@angular/core';
import { FileUploader } from 'ng2-file-upload';
import {MatTableDataSource} from '@angular/material'
import {SearchService} from "./app.search.service";
import {IDumpFileEntry, IDumpEntryGroup, IDumpType} from "./search.results";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  providers: [SearchService]
})
export class AppComponent {
  title = 'app';
  public uploader:FileUploader = new FileUploader({url: '/dump/process'});
  public hasBaseDropZoneOver:boolean = false;
  public fileOverBase(e:any):void {
    this.hasBaseDropZoneOver = e;
  }

  searchResults: IDumpType[];
 
  constructor(private searchService: SearchService) {
 }
 
  getSearchResults(): void {
    this.searchService.getSearchResults()
        .subscribe(
            resultArray => this.searchResults = resultArray,
            error => console.log("Error :: " + error)
        )
  }

  ngOnInit(): void {
      this.getSearchResults();
  }
}
