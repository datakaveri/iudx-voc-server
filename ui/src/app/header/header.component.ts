import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import {FormControl} from '@angular/forms';
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
  styleUrls: ['./header.component.css'],
  providers: [DataService]
})
export class HeaderComponent implements OnInit {
  model: string;
  searchRes: Observable<SearchRes[]>;
  control = new FormControl();
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
    this.searchRes = this.control.valueChanges.pipe(
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
