export interface IDumpType {
    id: number,
    failureBucketId: string,
    briefDescription: string,
    resolved: boolean;
}

export interface IDumpEntryGroup {
    id: number,
    dumpModule: string,
    dumpChecksum: string,
    dumpOffset: string,
    dumpVersion: string,
    dumpType: IDumpType;
}

export interface IDumpFileEntry {
    id: number,
    fileName: string,
    enteredDateTime: number,
    crashDateTime: number,
    dumpEntryGroup: IDumpEntryGroup;
}

export interface IDumpFileEntryListResponse {
    totalFileEntries: number,
    totalFileEntryPages: number,
    requestedPageSize: number,
    requestedPageNumber: number,
    dumpFileEntries: Array<IDumpFileEntry>;
}

