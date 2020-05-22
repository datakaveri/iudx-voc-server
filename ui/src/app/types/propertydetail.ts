export interface PropertyDetail {
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
    }
  ];
}
