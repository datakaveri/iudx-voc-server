import { Injectable } from '@angular/core';
import { AppSettings } from '../appSettings';
import { Observable, throwError as observableThrowError } from 'rxjs';
import { HttpClient, HttpErrorResponse, HttpHeaders} from '@angular/common/http';
import { catchError } from 'rxjs/operators';

import { SearchRes } from '../types/searchRes';
import { Class } from '../types/class';
import { Property } from '../types/property';

@Injectable({
  providedIn: 'root'
})
export class DataService {
  private baseURL = AppSettings.BASE_URL;
  headers = new HttpHeaders();

  constructor(private http: HttpClient) {
    this.headers = this.headers.set(
      'Content-Type',
      'application/json; charset=utf-8'
    );
  }

  getAllClasses(): Observable<Class[]> {
    return this.http
              .get<Class[]>(`${this.baseURL}/classes`,
                            { headers: this.headers })
              .pipe(catchError(this.handleError));
  }

  getAllProperties(): Observable<Property[]> {
    return this.http
              .get<Property[]>(`${this.baseURL}/properties`,
                            { headers: this.headers })
              .pipe(catchError(this.handleError));
  }

  search(term: string): Observable<SearchRes[]> {
    return this.http
                .get<SearchRes[]>(`${this.baseURL}/search?q=${term}`,
                                  { headers: this.headers })
                .pipe(catchError(this.handleError));
  }

  private handleError(res: HttpErrorResponse) {
    console.error(res.error);
    return observableThrowError(res.error || 'Server error');
  }
}
