 
 export interface ICrashAnalysisStatus {
    id: number,
    dumpFileName: string,
    status: string,
    startDateTime: number,
    endDateTime: number,
    dumpId: number;
 }
 
 export class ICrashAnalysisPublicStatus {
    dumpFileName: string,
    status: string,
    progress: number,
    dumpId: number,
    uploadId: number,
    processingId: number;
 }