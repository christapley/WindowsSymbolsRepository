import { Component, OnInit } from '@angular/core';
import {ICrashAnalysisStatus} from "./upload.status";
import {Http, Response} from "@angular/http";
import {Observable} from 'rxjs/Rx';
import {ISubscription} from 'rxjs/Subscription';

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
  private _postsHost = "http://localhost:8899"
  private _postsURL = "/dump/process/status/";
  private subscription: ISubscription;

  constructor(private httpIn: Http) { 
    this.activeUploadIds = [];
    this.currentUploadResults = [];
    this.completedUploadResults = [];
    this.http = httpIn;
    this.subscription = null;
  }

  handleNewlyCompletedUploads(resultArray: ICrashAnalysisStatus[]) {
    resultArray.forEach(item => {
      if(item.status == "Complete") {
        this.removeUploadId(item.id);
        this.onItemCompleted(item);
      } else if(item.status == "Failed") {
        this.removeUploadId(item.id);
        this.onItemFailed(item);
      }
    });

    if(this.activeUploadIds.length == 0) {
      this.subscription.unsubscribe();
      this.subscription = null;
    } 
  }

  public onItemCompleted(item: ICrashAnalysisStatus): any {
    return { item };
  }

  public onItemFailed(item: ICrashAnalysisStatus): any {
    return { item };
  }

  onReceivedData(resultArray: ICrashAnalysisStatus[]) {
    this.handleNewlyCompletedUploads(resultArray);
    this.currentUploadResults = resultArray;
  }

  ngOnInit() {

  }

  ngOnDestroy() {
    clearInterval(this.myFromNowInterval);
  }

  startPolling() {
    if(this.subscription == null) {

      this.subscription = Observable.interval(2000)
        .switchMap(() => this.http.get(this._postsHost + this._postsURL + this.activeUploadIds.join(","))).map((data) => data.json())
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
