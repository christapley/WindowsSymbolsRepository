import { Component, OnInit } from '@angular/core';
import {ICrashAnalysisStatus} from "./upload.status";
import {Http, Response} from "@angular/http";
import {Observable} from 'rxjs/Rx';

@Component({
  selector: 'app-upload-dump-process',
  templateUrl: './upload-dump-process.component.html',
  styleUrls: ['./upload-dump-process.component.css']
})
export class UploadDumpProcessComponent implements OnInit {

  activeUploadIds: Array<number>;
  currentUploadResults: Array<ICrashAnalysisStatus>;

  private _postsHost = "http://localhost:8899"
  private _postsURL = "/dump/process/";

  constructor(private http: Http) {
    this.activeUploadIds = [];
    this.currentUploadResults = [];
  }

  ngOnInit() {
    this.refresh()
        .subscribe(
            resultArray => this.currentUploadResults = resultArray,
            error => console.log("Error :: " + error)
        )
  }

  refresh(): Observable<ICrashAnalysisStatus[]> {

    return this.http
        .get(this._postsHost + this._postsURL + this.activeUploadIds.join(",") + "/status/")
        .map((response: Response) => {
            return <ICrashAnalysisStatus[]>response.json();
        })
        .catch(this.handleError);
  }

  clear() {
    this.activeUploadIds = [];
  }

  private handleError(error: Response) {
    return Observable.throw(error.statusText);
  }

  addUploadId(id: number) {
    for(var i = 0; i < this.activeUploadIds.length; i++) {
      if(this.activeUploadIds[i] == id) {
        return;
      }
    }
    this.activeUploadIds.push(id);
    this.refresh();
  }


}
