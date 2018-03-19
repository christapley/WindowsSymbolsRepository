import { Injectable } from "@angular/core";

@Injectable()
export class Globals {
  dumpsUrlHost: string = "http://localhost:8899";
  
  dumpsProcessStatusUrlPath: string = "/dump/process/status/";
  dumpsListUrlPath: string = "/dump/list/";
  dumpFileUrlPath: string = "/dump/file/{id}/dump";
  dumpRawAnalysisUrlPath: string = "/dump/file/{id}/analysis/raw";
  dumpUploadUrlPath: string = "/dump/process";
}