import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { MatToolbarModule } from '@angular/material/toolbar';
import {MatSidenavModule} from '@angular/material/sidenav';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import { AppComponent } from './app.component';
import {MatCardModule} from '@angular/material/card';
import { FileUploadModule } from "ng2-file-upload";
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatGridListModule, MatListModule} from '@angular/material';
import {FlexLayoutModule} from "@angular/flex-layout";
import {HttpModule} from "@angular/http";
import { UploadDumpProcessComponent } from './upload-dump-process/upload-dump-process.component';
import { DumpEntrySearchResultsComponent } from './dump-entry-search-results/dump-entry-search-results.component';

@NgModule({
  declarations: [
    AppComponent,
    UploadDumpProcessComponent,
    DumpEntrySearchResultsComponent
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
    FlexLayoutModule,
    HttpModule
  ],
  providers: [UploadDumpProcessComponent, DumpEntrySearchResultsComponent],
  bootstrap: [AppComponent]
})
export class AppModule { }
