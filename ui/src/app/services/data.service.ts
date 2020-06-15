import { Injectable } from '@angular/core';
import { AppSettings } from '../appSettings';
import { Observable, throwError as observableThrowError } from 'rxjs';
import { map } from 'rxjs/operators';
import {
  HttpClient,
  HttpErrorResponse,
  HttpHeaders
} from '@angular/common/http';
import { catchError } from 'rxjs/operators';

import { SearchRes } from '../types/searchRes';
import {
  ClassDetail,
  Class,
  Classes,
  Property,
  Properties
} from '../types/classDetail';
import { PropertyDetail } from '../types/propertyDetail';
import { DataModel } from '../types/dataModel';

@Injectable({
  providedIn: 'root'
})
export class DataService {
  [x: string]: any;
  private baseURL = AppSettings.BASE_URL;
  headers = new HttpHeaders();
  headersLD = new HttpHeaders();

  constructor(private http: HttpClient) {
    this.headers = this.headers.set(
      'Content-Type',
      'application/json; charset=utf-8'
    );
    this.headers = this.headers.append(
      'Accept',
      'application/json; charset=utr-8'
    );
    this.headersLD = this.headers.set(
      'Content-Type',
      'application/ld+json; charset=utf-8'
    );
    this.headersLD = this.headersLD.append(
      'Accept',
      'application/ld+json; charset=utr-8'
    );
  }

  getAllClasses(): Observable<Class[]> {
    return this.http
      .get<Class[]>(`${this.baseURL}/classes`, { headers: this.headers })
      .pipe(catchError(this.handleError));
  }

  getAllProperties(): Observable<Property[]> {
    return this.http
      .get<Property[]>(`${this.baseURL}/properties`, { headers: this.headers })
      .pipe(catchError(this.handleError));
  }

  getClass(className: string) {
    return this.http
      .get(`${this.baseURL}/${className}`, { headers: this.headersLD })
      .pipe(
        map(resp => {
          var flattened = <ClassDetail>{};
          flattened.superClasses = <Classes>[];
          console.log(resp['@graph']);
          for (var node of resp['@graph']) {
            // console.log(node);
            var nodeId = node['@id'].split(':')[1];
            // console.log(nodeId);
            var nodeComment = node['rdfs:comment'];
            // console.log(nodeComment);
            if (node['@type'].includes('rdfs:Class')) {
              if (nodeId == className) {
                flattened.baseClass = {
                  label: className,
                  comment: nodeComment,
                  properties: <Properties>[]
                };
                if (node.hasOwnProperty('rdfs:subClassOf')) {
                  var nodename = node['rdfs:subClassOf']['@id'].split(':')[1];
                  flattened.subclasses = {
                    label: nodename,
                    comment: ''
                  };
                }
              } else {
                //TODO: Implementation with superclass
              }
            } else {
              var ranges = <string[]>[];
              for (var range of node['iudx:rangeIncludes']) {
                ranges.push(range['@id'].split(':')[1]);
                this.ranges = ranges;
                //console.warn(this.ranges);
                // this.ranges = ranges;
              }
              for (var domain of node['iudx:domainIncludes']) {
                if (domain['@id'] == 'iudx:' + className) {
                  flattened.baseClass.properties.push(<Property>{
                    label: nodeId,
                    comment: nodeComment,
                    type: this.ranges
                  });
                }
              }
            }
          }
          console.log(flattened);
          return flattened;
        }),
        catchError(this.handleError)
      );
  }

  getProperty(propertyName: string) {
    // console.log('Getting property ' + propertyName);
    return this.http
      .get(`${this.baseURL}/${propertyName}`, { headers: this.headersLD })
      .pipe(
        map(resp => {
          var flattened = <PropertyDetail>{};
          flattened.type = <string[]>[];
          flattened.domains = <string[]>[];
          flattened.supers = <string[]>[];
          for (var node of resp['@graph']) {
            if (node['@id'].split(':')[1] == propertyName) {
              var nodeId = node['@id'].split(':')[1];
              var nodeComment = node['rdfs:comment'];
              flattened.label = propertyName;
              flattened.comment = nodeComment;
              // TODO: More efficient was of extracting
              for (var range of node['iudx:rangeIncludes']) {
                flattened.type.push(range['@id'].split(':')[1]);
              }
              for (var domain of node['iudx:domainIncludes']) {
                flattened.domains.push(domain['@id'].split(':')[1]);
              }
              flattened.comment = nodeComment;
            }
          }

          return flattened;
        }),
        catchError(this.handleError)
      );
  }

  search(term: string): Observable<SearchRes[]> {
    return this.http
      .get<SearchRes[]>(`${this.baseURL}/search?q=${term}`, {
        headers: this.headers
      })
      .pipe(catchError(this.handleError));
  }

  searchRelationship(rel: string, val: string): Observable<SearchRes[]> {
    return this.http
      .get<SearchRes[]>(`${this.baseURL}/relationship?rel=${rel}&val=${val}`, {
        headers: this.headers
      })
      .pipe(catchError(this.handleError));
  }

  private handleError(res: HttpErrorResponse) {
    // console.error(res.error);
    return observableThrowError(res.error || 'Server error');
  }
}
