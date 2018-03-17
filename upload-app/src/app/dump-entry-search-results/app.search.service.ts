import {Injectable} from "@angular/core";
import {Http, Response} from "@angular/http";
import {Observable} from "rxjs/Observable";
import "rxjs/Rx";
import {IDumpFileEntry, IDumpEntryGroup, IDumpType} from "./search.results";
 
@Injectable()
export class SearchService {
 
    private _postsHost = "http://localhost:8899"
    private _postsURL = "/dump/list/";
 
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
 
    private handleError(error: Response) {
         return Observable.throw(error.statusText);
    }
}