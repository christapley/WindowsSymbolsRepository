import {Injectable} from "@angular/core";
import {Http, Response, RequestOptions, Headers, ResponseContentType} from "@angular/http";
import {Observable} from "rxjs/Observable";
import "rxjs/Rx";
import {IDumpFileEntry, IDumpEntryGroup, IDumpType} from "./search.results";
 
@Injectable()
export class SearchService {
 
    private _postsHost = "http://localhost:8899";
    private _postsURL = "/dump/list/";
    private dumpFileUrl = "/dump/file/{id}/dump";
    private dumpRawAnalysisUrl = "/dump/file/{id}/analysis/raw";
 
    constructor(private http: Http) {
    }

    getSearchResults(dumpEntryIds): Observable<IDumpType[]> {

         return this.http
             .get(this._postsHost + this._postsURL + dumpEntryIds.join(","))
             .map((response: Response) => {
                 return <IDumpType[]>response.json();
             })
             .catch(this.handleError);
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
        var url = this._postsHost + this.dumpFileUrl;
        url = url.replace("{id}", dumpFileEntryId.toString());

        return this.downloadFileInternal(url);
    }

    getDumpRawAnalysis(dumpFileEntryId: number): Observable<Blob> {
        var url = this._postsHost + this.dumpRawAnalysisUrl;
        url = url.replace("{id}", dumpFileEntryId.toString());

        return this.downloadFileInternal(url);
    }

 
    private handleError(error: Response) {
         return Observable.throw(error.statusText);
    }
}