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
import { DataService } from '../services/data.service';
import { AppSettings } from '../appSettings';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css'],
  providers: [DataService]
})
export class SearchComponent implements OnInit {
  searchRes: Observable<SearchRes[]>;
  private searchTerms = new Subject<string>();

  constructor(
    private dataService: DataService,
    private router: Router
  ) { 

  }

  search(term: string): void {
    this.searchTerms.next(term);
  }

  ngOnInit(): void {
    this.searchRes = this.searchTerms.pipe(
      debounceTime(500),
      distinctUntilChanged(),
      switchMap(
        term => term?this.dataService.search(term):of<SearchRes[]>([])
      ),
      catchError(error => {
        console.log(`Error in component ... ${error}`);
        return of<SearchRes[]>([]);
      })
    );
  }

  gotoTerm(res: SearchRes): void {
    const link = [ "/classes"];
    console.log(link);
    this.router.navigate(link);
  }

}
