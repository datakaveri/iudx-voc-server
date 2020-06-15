export interface ClassDetail {
  baseClass: Class;
  superClasses?: Classes;
  subclasses: Class;
}

export interface Class {
  label: string;
  comment: string;
  properties?: Properties;
}
export interface Classes extends Array<Class> {
  [index: number]: Class;
}

export interface Property {
  label: string;
  comment: string;
  type?: string[];
}
export interface Properties extends Array<Property> {
  [index: number]: Property;
}
