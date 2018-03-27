import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import {HttpClientModule} from '@angular/common/http';
import { MatToolbarModule } from '@angular/material/toolbar';
import {MatSidenavModule} from '@angular/material/sidenav';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import { AppComponent } from './app.component';
import {MatCardModule} from '@angular/material/card';
import { FileUploadModule } from "ng2-file-upload";
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatGridListModule, MatListModule, MatTabsModule, MatTableModule, MatProgressSpinnerModule, MatPaginatorModule, MatSortModule} from '@angular/material';
import {FlexLayoutModule} from "@angular/flex-layout";
import {HttpModule, Http} from "@angular/http";
import { UploadDumpProcessComponent } from './upload-dump-process/upload-dump-process.component';
import { DumpEntrySearchResultsComponent } from './dump-entry-search-results/dump-entry-search-results.component';
import {Globals} from "./globals";
import { DumpEntryListComponent } from './dump-entry-list/dump-entry-list.component';

@NgModule({
  declarations: [
    AppComponent,
    UploadDumpProcessComponent,
    DumpEntrySearchResultsComponent,
    DumpEntryListComponent
  ],
  imports: [
    BrowserModule,
    MatToolbarModule,
    MatSidenavModule,
    BrowserAnimationsModule,
    MatCardModule,
    FileUploadModule,
    MatButtonModule,
    MatIconModule,
    MatGridListModule,
    MatListModule,
    MatTableModule,
    FlexLayoutModule,
    MatTabsModule, 
    MatProgressSpinnerModule,
    MatPaginatorModule,
    MatSortModule,
    HttpModule,
    HttpClientModule 
  ],
  providers: [Globals, UploadDumpProcessComponent, DumpEntrySearchResultsComponent],
  bootstrap: [AppComponent]
})
export class AppModule { }
