import { Component, OnInit } from '@angular/core';
import {IDumpFileEntry, IDumpEntryGroup, IDumpType} from "./search.results";
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

  ngOnInit() {
  }

}
