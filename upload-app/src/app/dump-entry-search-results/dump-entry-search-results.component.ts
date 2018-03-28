import { Component, OnInit } from '@angular/core';
import {IDumpFileEntry, IDumpEntryGroup, IDumpType} from "./search.results";
import {Globals} from "../globals";
import {SearchService} from "./app.search.service";

@Component({
  selector: 'app-dump-entry-search-results',
  templateUrl: './dump-entry-search-results.component.html',
  styleUrls: ['./dump-entry-search-results.component.css'],
  providers: [SearchService]
})
export class DumpEntrySearchResultsComponent implements OnInit {

  searchQuery: number[];
  searchResults: IDumpType[];

  constructor(private searchService: SearchService) { 
    this.searchQuery = [];
    this.searchResults = [];
  }

  public getSearchResults(): void {
    this.searchService.getSearchResults(this.searchQuery)
        .subscribe(
            resultArray => this.searchResults = resultArray,
            error => console.log("Error :: " + error)
        )
  }

  public addDumpEntryId(id: number): void {
    const index: number = this.searchQuery.indexOf(id);
    if(index === -1) {
      this.searchQuery.push(id);
      this.getSearchResults();
    }
  }

  public removeDumpEntryId(id: number): void {
    const index: number = this.searchQuery.indexOf(id);
    if(index >= 0) {
      this.searchQuery.splice(index, 1);
      this.getSearchResults();
    }
  }

  onDownloadFile(blob: Blob, fileName: string) {
    var link=document.createElement('a');
    link.href=window.URL.createObjectURL(blob);
    link.download=fileName;
    link.click();
  }

  public downloadDumpFile(id: number, fileName: string) {
    this.searchService.getDumpFile(id)
      .subscribe(blob => {
        this.onDownloadFile(blob, fileName);
      })
  }

  public downloadRawAnalysis(id: number, fileName: string) {
    this.searchService.getDumpRawAnalysis(id)
      .subscribe(blob => {
        this.onDownloadFile(blob, fileName);
      })
  }

  ngOnInit() {
  }

}
