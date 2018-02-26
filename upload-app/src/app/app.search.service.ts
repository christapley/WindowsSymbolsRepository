import {Injectable} from "@angular/core";
import {Http, Response} from "@angular/http";
import {Observable} from "rxjs/Observable";
import "rxjs/Rx";
import {IDumpFileEntry, IDumpEntryGroup, IDumpType} from "./search.results";
 
@Injectable()
export class SearchService {
 
    private _postsURL = "/dump/list/48,60";
 
    constructor(private http: Http) {
    }
 
    getSearchResults(): Observable<IDumpType[]> {
         return this.http
             .get(this._postsURL)
             .map((response: Response) => {
                 return <IDumpType[]>response.json();
             })
             .catch(this.handleError);
    }
 
    private handleError(error: Response) {
         return Observable.throw(error.statusText);
    }
}