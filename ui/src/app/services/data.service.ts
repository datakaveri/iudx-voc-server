import { Injectable } from '@angular/core';
import { AppSettings } from '../appSettings';
import { Observable, throwError as observableThrowError } from 'rxjs';
import {
  HttpClient,
  HttpErrorResponse,
  HttpHeaders
} from '@angular/common/http';
import { catchError } from 'rxjs/operators';

import { SearchRes } from '../types/searchRes';
import { Class } from '../types/class';
import { Property } from '../types/property';
import { ClassDetail } from '../types/classdetail';

@Injectable({
  providedIn: 'root'
})
export class DataService {
  [x: string]: any;
  private baseURL = AppSettings.BASE_URL;
  headers = new HttpHeaders();
  headers_new = new HttpHeaders();

  constructor(private http: HttpClient) {
    this.headers = this.headers.set(
      'Content-Type',
      'application/json; charset=utf-8'
    );
    this.headers_new = this.headers.set(
      'Content-Type',
      'application/ld+json; charset=utf-8'
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

  // getClassDetail(class_type): Observable<ClassDetail[]> {
  //   return this.http
  //     .get<ClassDetail[]>(`${this.baseURL}/${class_type}`, {
  //       headers: this.headers_new
  //     })
  //     .pipe(catchError(this.handleError));
  // }
  getClassDetail(class_label) {
    return this.http.get(`${this.baseURL}/${class_label}`, {
      headers: this.headers_new
    });
  }
  getPropertyDetail(name) {
    return this.http.get(`${this.baseURL}/${name}`, {
      headers: this.headers_new
    });
  }
  search(term: string): Observable<SearchRes[]> {
    return this.http
      .get<SearchRes[]>(`${this.baseURL}/search?q=${term}`, {
        headers: this.headers
      })
      .pipe(catchError(this.handleError));
  }

  private handleError(res: HttpErrorResponse) {
    console.error(res.error);
    return observableThrowError(res.error || 'Server error');
  }
}
