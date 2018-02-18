import { Component } from '@angular/core';
import { FileUploader } from 'ng2-file-upload';
import {MatTableDataSource} from '@angular/material'

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'app';
  public uploader:FileUploader = new FileUploader({url: '/dump/process'});
  public hasBaseDropZoneOver:boolean = false;
  public fileOverBase(e:any):void {
    this.hasBaseDropZoneOver = e;
  }
  public filesToUpload = ['w3wp.2312.dmp', 'w3wp.2212.dmp', 'w3wp.3452.dmp', 'w3wp.4564.dmp', 'w3wp.1121.dmp'];
  public dumps = ['w3wp.2312.dmp', 'w3wp.2212.dmp', 'w3wp.3452.dmp', 'w3wp.4564.dmp', 'w3wp.1121.dmp', 'w3wp.2312.dmp', 'w3wp.2212.dmp', 'w3wp.3452.dmp', 'w3wp.4564.dmp', 'w3wp.1121.dmp', 'w3wp.2312.dmp', 'w3wp.2212.dmp', 'w3wp.3452.dmp', 'w3wp.4564.dmp', 'w3wp.1121.dmp'];



}
