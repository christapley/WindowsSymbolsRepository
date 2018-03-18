

export interface IDumpFileEntry {
    id: number,
    fileName: string,
    enteredDateTime: number,
    crashDateTime: number;
}

export interface IDumpEntryGroup {
    id: number,
    dumpModule: string,
    dumpChecksum: string,
    dumpOffset: string,
    dumpVersion: string,
    dumpFileEntries: Array<IDumpFileEntry>;
}

export interface IDumpType {
    id: number,
    failureBucketId: string,
    briefDescription: string,
    resolved: boolean,
    dumpEntryGroups: Array<IDumpEntryGroup>;
    jiraIssues: Array<string>;
}
