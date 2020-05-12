import { Injectable } from '@angular/core';
import Globals from './global';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class DataService {
  private BASE_URL = Globals.api;
  headers = new HttpHeaders();

  constructor(private http: HttpClient) {}

  getAllClasses() {
    this.headers = this.headers.set(
      'Content-Type',
      'application/json; charset=utf-8'
    );
    return this.http.get(this.BASE_URL + 'classes', { headers: this.headers });
  }

  getAllProperties() {
    this.headers = this.headers.set(
      'Content-Type',
      'application/json; charset=utf-8'
    );
    return this.http.get(this.BASE_URL + 'properties', {
      headers: this.headers
    });
  }
}
