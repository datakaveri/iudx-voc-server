export interface Descriptors extends Array<Descriptor> {
  [index: number]: Descriptor;
}

export interface Descriptor {
  type: string;
  documents?: string[];
}
