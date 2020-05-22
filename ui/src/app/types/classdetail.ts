export interface ClassDetail {
  id: string;
  type: [];
  comment: string;
  label: string;
  domainIncludes: [
    {
      id: string;
    }
  ];
  rangeIncludes: [
    {
      id: string;
      type: string;
    },
    {
      id: string;
    }
  ];
}
