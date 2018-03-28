import {Injectable} from "@angular/core";
import {Http, Response, RequestOptions, Headers, ResponseContentType} from "@angular/http";
import {Observable} from "rxjs/Observable";
import "rxjs/Rx";
import {IDumpFileEntry, IDumpEntryGroup, IDumpType} from "./search.results";
import {Globals} from '../globals'
import {of as observableOf} from 'rxjs/observable/of';

@Injectable()
export class SearchService {

    constructor(private http: Http, private globals: Globals) {
    }

    getSearchResults(dumpEntryIds: Array<number>): Observable<IDumpType[]> {
      if(dumpEntryIds.length == 0) {
        return observableOf([]);
      } else {
        return this.http
          .get(this.globals.dumpsUrlHost + this.globals.dumpsListUrlPath + dumpEntryIds.join(","))
          .map((response: Response) => {
            return <IDumpType[]>response.json();
          })
          .catch(this.handleError);
      }
    }

    setMultipartHeader(localHeaders:Headers){
        localHeaders.append('method', 'GET');
    }

    private downloadFileInternal(url: string): Observable<Blob> {
        var localHeaders = new Headers();
        this.setMultipartHeader(localHeaders);
        let requestOptions = new RequestOptions({
            headers: localHeaders,
            responseType:ResponseContentType.Blob
        });
        return this.http.get(url, requestOptions).map(response => {
            var blob = new Blob( [response.blob()], { type: "application/octet-stream"} );
            return blob;
        });
    }

    getDumpFile(dumpFileEntryId: number): Observable<Blob> {
        var url = this.globals.dumpsUrlHost + this.globals.dumpFileUrlPath;
        url = url.replace("{id}", dumpFileEntryId.toString());

        return this.downloadFileInternal(url);
    }

    getDumpRawAnalysis(dumpFileEntryId: number): Observable<Blob> {
        var url = this.globals.dumpsUrlHost + this.globals.dumpRawAnalysisUrlPath;
        url = url.replace("{id}", dumpFileEntryId.toString());

        return this.downloadFileInternal(url);
    }

 
    private handleError(error: Response) {
         return Observable.throw(error.statusText);
    }
}