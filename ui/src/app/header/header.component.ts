import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormControl } from '@angular/forms';
import { Observable, Subject, of } from 'rxjs';
import {
  catchError,
  debounceTime,
  distinctUntilChanged,
  switchMap
} from 'rxjs/operators';
import { SearchRes } from '../types/searchRes';
import { DataService } from '../services/data.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {
  navbarOpen: boolean = false;
  model: string;
  searchRes: Observable<SearchRes[]>;
  control = new FormControl();
  private _searchTerm: string;
  // private searchTerms = new Subject<string>();
  get searchTerm(): string {
    return this._searchTerm;
  }

  set searchTerm(value: string) {
    this._searchTerm = value;
  }

  constructor(private dataService: DataService, private router: Router) {}

  // search(term: string): void {
  //   this.searchTerms.next(term);
  // }

  ngOnInit(): void {
    this.searchRes = this.control.valueChanges.pipe(
      debounceTime(1500),
      distinctUntilChanged(),
      switchMap(term =>
        term ? this.dataService.search(term) : of<SearchRes[]>([])
      ),
      catchError(error => {
        // console.log(`Error in component ... ${error}`);
        return of<SearchRes[]>([]);
      })
    );
  }
  toggleNavbar() {
    this.navbarOpen = !this.navbarOpen;
  }
  onSearch(text: string) {
    // console.log(text);
    // this.searchRes = this.dataService.search(text);
    // console.log(this.searchRes);
    // console.log(this._searchTerm);
    this.router.navigate(['/search/searchTerm'], {
      queryParams: { q: this._searchTerm }
    });
  }
}
