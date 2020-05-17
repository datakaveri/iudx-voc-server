import { Injectable } from '@angular/core';
import Globals from './global';
import { Observable, throwError as observableThrowError } from 'rxjs';
import { HttpClient, HttpErrorResponse, HttpHeaders} from '@angular/common/http';
import { catchError } from 'rxjs/operators';

import { SearchRes } from '../types/searchRes';

@Injectable({
  providedIn: 'root'
})

export class SearchService {
  private baseURL = Globals.api;
  headers = new HttpHeaders();

  constructor(private http: HttpClient) {
    this.headers = this.headers.set(
      'Content-Type',
      'application/json; charset=utf-8'
    );
  }
  
  search(term: string): Observable<SearchRes[]> {
    return this.http
                .get<SearchRes[]>(`${this.baseURL}?search=${term}`,
                                  { headers: this.headers })
                .pipe(catchError(this.handleError));
  }

  private handleError(res: HttpErrorResponse) {
    console.error(res.error);
    return observableThrowError(res.error || 'Server error');
  }
}
