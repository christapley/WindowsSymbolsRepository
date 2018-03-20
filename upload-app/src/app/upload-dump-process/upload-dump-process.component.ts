import { Component, OnInit } from '@angular/core';
import {ICrashAnalysisStatus} from "./upload.status";
import {Http, Response} from "@angular/http";
import {FileUploader, FileItem, ParsedResponseHeaders} from 'ng2-file-upload';
import {Observable} from 'rxjs/Rx';
import {ISubscription} from 'rxjs/Subscription';
import {Globals} from '../globals'

@Component({
  selector: 'app-upload-dump-process',
  templateUrl: './upload-dump-process.component.html',
  styleUrls: ['./upload-dump-process.component.css']
})
export class UploadDumpProcessComponent implements OnInit {

  activeUploadIds: Array<number>;
  currentUploadResults: Array<ICrashAnalysisStatus>;
  completedUploadResults: Array<ICrashAnalysisStatus>;
  myFromNowInterval: any;
  private http: Http;
  private isPolling: boolean;
  private subscription: ISubscription;

  public uploader:FileUploader = new FileUploader({
    url: this.globals.dumpsUrlHost + this.globals.dumpUploadUrlPath,
    headers: [{name:'Accept', value:'application/json'}],
    autoUpload: true
  });

  constructor(private httpIn: Http, private globals: Globals) { 
    this.activeUploadIds = [];
    this.currentUploadResults = [];
    this.completedUploadResults = [];
    this.http = httpIn;
    this.subscription = null;
  }

  public hasBaseDropZoneOver:boolean = false;
  public fileOverBase(e:any):void {
    this.hasBaseDropZoneOver = e;
  }

  public clearAllResults() {
    this.completedUploadResults = [];
    this.uploader.clearQueue();
  }

  handleNewlyCompletedUploads(resultArray: ICrashAnalysisStatus[]) {

    var indexToRemove = [];

    resultArray.forEach((item, index) => {
      if(item.status == "Complete") {
        this.removeUploadId(item.id);
        this.completedUploadResults.push(item);
        this.onItemCompleted(item);
        indexToRemove.push(index);
      } else if(item.status == "Failed") {
        this.removeUploadId(item.id);
        this.completedUploadResults.push(item);
        this.onItemFailed(item);
        indexToRemove.push(index);
      }
    });

    for(var index = 0; index < indexToRemove.length; index++) {
      resultArray.splice(indexToRemove[index], 1);
    }

    if(this.activeUploadIds.length == 0) {
      this.subscription.unsubscribe();
      this.subscription = null;
    } 
  }

  onSuccessItem(item: FileItem, response: string, status: number, headers: ParsedResponseHeaders): any {
    console.debug(response);
    let statusObject = JSON.parse(response) as ICrashAnalysisStatus;
    this.addUploadId(statusObject.id);
    item.remove();
  }

  public onItemCompleted(item: ICrashAnalysisStatus): any {
    return { item };
  }

  public onItemFailed(item: ICrashAnalysisStatus): any {
    return { item };
  }

  public hasActiveUploads(): boolean {
    return this.subscription != null || this.currentUploadResults.length != 0 || this.uploader.isUploading;
  }
  
  onReceivedData(resultArray: ICrashAnalysisStatus[]) {
    this.handleNewlyCompletedUploads(resultArray);
    this.currentUploadResults = resultArray;
  }

  ngOnInit() {
    this.uploader.onSuccessItem = (item, response, status, headers) => this.onSuccessItem(item, response, status, headers);
  }

  ngOnDestroy() {
    clearInterval(this.myFromNowInterval);
  }

  startPolling() {
    if(this.subscription == null) {

      this.subscription = Observable.interval(2000)
        .switchMap(() => this.http.get(this.globals.dumpsUrlHost + this.globals.dumpsProcessStatusUrlPath + this.activeUploadIds.join(","))).map((data) => data.json())
        .subscribe((data) => {
          this.onReceivedData(data as Array<ICrashAnalysisStatus>);
        });
      
    }
  }

  clear() {
    this.activeUploadIds = [];
  }

  private handleError(error: Response) {
    return Observable.throw(error.statusText);
  }

  removeUploadId(id: number) {
    const index: number = this.activeUploadIds.indexOf(id);
    if(index !== -1) {
      this.activeUploadIds.splice(index, 1);
    }
  }

  addUploadId(id: number) {
    const index: number = this.activeUploadIds.indexOf(id);
    if(index === -1) {
      this.activeUploadIds.push(id);
      this.startPolling();
    }
  }
}
