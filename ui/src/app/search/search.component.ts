import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, Subject, of } from 'rxjs';
import {
  catchError,
  debounceTime,
  distinctUntilChanged,
  switchMap
} from 'rxjs/operators';
import { SearchRes } from '../types/searchRes';
import { SearchService } from '../services/search.service';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css'],
  providers: [SearchService]
})
export class SearchComponent implements OnInit {
  searchRes: Observable<SearchRes[]>;
  private searchTerms = new Subject<string>();

  constructor(
    private searchService: SearchService,
    private router: Router
  ) { }

  search(term: string): void {
    this.searchTerms.next(term);
  }

  ngOnInit(): void {
    this.searchRes = this.searchTerms.pipe(
      debounceTime(500),
      distinctUntilChanged(),
      switchMap(
        term => term?this.searchService.search(term):of<SearchRes[]>([])
      ),
      catchError(error => {
        console.log(`Error in component ... ${error}`);
        return of<SearchRes[]>([]);
      })
    );
  }

  gotoTerm(res: SearchRes): void {
    console.log(`GOt results ${res}`);
    const link = [ res.label];
    this.router.navigate(link);
  }

}
