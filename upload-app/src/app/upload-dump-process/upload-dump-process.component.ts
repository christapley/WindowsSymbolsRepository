import { Component, OnInit } from '@angular/core';
import {ICrashAnalysisStatus, ICrashAnalysisPublicStatus} from "./upload.status";
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
  mergedUploadResults: Array<ICrashAnalysisPublicStatus>;

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
    this.mergedUploadResults = [];
    this.http = httpIn;
    this.subscription = null;
  }

  public hasBaseDropZoneOver:boolean = false;
  public fileOverBase(e:any):void {
    this.hasBaseDropZoneOver = e;
  }

  public clearAllResults() {
    this.uploader.clearQueue();
    let toRemove:Array<number> = [];

    for (var i = this.mergedUploadResults.length - 1; i >= 0; i--) {
      if(this.mergedUploadResults[i].status == "Complete" || this.mergedUploadResults[i].status == "Failed") {
        this.mergedUploadResults.splice(i, 1);
      }
    }
  }

  handleNewlyCompletedUploads(resultArray: ICrashAnalysisStatus[]) {
    resultArray.forEach((item, index) => {
      let progress: number = 0;
      if(item.status == "Complete") {
        this.removeUploadId(item.id);
        this.onItemCompleted(item);
        progress = 100;
      } else if(item.status == "Failed") {
        this.removeUploadId(item.id);
        this.onItemFailed(item);
      }
      this.updateItemInMergedList(item, progress);
    });

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
    this.mergedUploadResults.forEach(mergedItem => {
      if(mergedItem.uploadId == item.index) {
        mergedItem.processingId = statusObject.id;
        mergedItem.status = "Queued";
        mergedItem.progress = 0;
        return;
      }
    });
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

  updateItemInMergedList(newItemStatus: ICrashAnalysisStatus, progress: number) {
    this.mergedUploadResults.forEach(item => {
      if(item.processingId == newItemStatus.id) {
        item.status = newItemStatus.status;
        item.progress = progress;
        return;
      }
    });
  }

  onAfterAddingFile(fileItem: FileItem) {
    let crashAnalysisPublicStatus: ICrashAnalysisPublicStatus  = new ICrashAnalysisPublicStatus();
    crashAnalysisPublicStatus.dumpFileName = fileItem.file.name;
    crashAnalysisPublicStatus.uploadId = fileItem.index;
    crashAnalysisPublicStatus.status = "Uploading";
    crashAnalysisPublicStatus.progress = 0;
    this.mergedUploadResults.push(crashAnalysisPublicStatus);
  }

  onUploadProgressItem(fileItem: FileItem, progress: any) {
    this.mergedUploadResults.forEach(item => {
      if(item.uploadId == fileItem.index) {
        item.progress = fileItem.progress;
        return;
      }
    });
  }

  ngOnInit() {
    this.uploader.onAfterAddingFile = (fileItem) => this.onAfterAddingFile(fileItem);
    this.uploader.onProgressItem = (fileItem, progress) => this.onUploadProgressItem(fileItem, progress);
    this.uploader.onSuccessItem = (item, response, status, headers) => this.onSuccessItem(item, response, status, headers);
  }

  ngOnDestroy() {
    
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
