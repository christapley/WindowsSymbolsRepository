import { Component, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {MatPaginator, MatSort, MatTableDataSource} from '@angular/material';
import {IDumpFileEntry, IDumpEntryGroup, IDumpType, IDumpFileEntryListResponse} from "./list.results";
import {Observable} from 'rxjs/Observable';
import {merge} from 'rxjs/observable/merge';
import {of as observableOf} from 'rxjs/observable/of';
import {catchError} from 'rxjs/operators/catchError';
import {map} from 'rxjs/operators/map';
import {startWith} from 'rxjs/operators/startWith';
import {switchMap} from 'rxjs/operators/switchMap';
import {Globals} from "../globals";

@Component({
  selector: 'app-dump-entry-list',
  templateUrl: './dump-entry-list.component.html',
  styleUrls: ['./dump-entry-list.component.css']
})
export class DumpEntryListComponent implements OnInit, AfterViewInit {
  displayedColumns = ['id', 'fileName', 'dumpEntryGroup', 'enteredDateTime'];
  exampleDatabase: ExampleHttpDao | null;
  dataSource = new MatTableDataSource();

  resultsLength = 0;
  isLoadingResults = true;

  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;


  constructor(private http: HttpClient, private globals: Globals) { }

  ngOnInit() {
    this.exampleDatabase = new ExampleHttpDao(this.http, this.globals);
    
    // If the user changes the sort order, reset back to the first page.
    this.sort.sortChange.subscribe(() => this.paginator.pageIndex = 0);
  }

  ngAfterViewInit() {
    merge(this.sort.sortChange, this.paginator.page)
      .pipe(
        startWith({}),
        switchMap(() => {
          this.isLoadingResults = true;
          return this.exampleDatabase!.getDumpEntryList(
            this.sort.active, this.sort.direction, this.paginator.pageIndex);
        }),
        map(data => {
          // Flip flag to show that loading has finished.
          this.isLoadingResults = false;
          this.resultsLength = data.totalFileEntries;

          return data.dumpFileEntries;
        }),
        catchError(() => {
          this.isLoadingResults = false;
          return observableOf([]);
        })
      ).subscribe(data => this.dataSource.data = data);
    
  }
}


export class ExampleHttpDao {
  constructor(private http: HttpClient, private globals: Globals) {}

  getDumpEntryList(sort: string, order: string, page: number): Observable<IDumpFileEntryListResponse> {
    const href = this.globals.dumpsUrlHost + this.globals.dumpFileEntryList;
    const requestUrl = `${href}?sort=${sort}&order=${order}&page=${page}`;
    //const requestUrl = `${href}?sort=enteredDateTime&order=${order}&page=${page}`;
    return this.http.get<IDumpFileEntryListResponse>(requestUrl);
  }
}
