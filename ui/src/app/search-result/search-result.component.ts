import { Component, OnInit } from '@angular/core';
import { SearchRes } from '../types/searchRes';
import { Observable } from 'rxjs';
import { ActivatedRoute } from '@angular/router';
import { DataService } from '../services/data.service';
import { AppSettings } from '../appSettings';

@Component({
  selector: 'app-search-result',
  templateUrl: './search-result.component.html',
  styleUrls: ['./search-result.component.css']
})
export class SearchResultComponent implements OnInit {
  searchDetail: Observable<SearchRes[]>;
  term: any;
  _url: string = AppSettings.BASE_URL + '/';
  error: boolean = false;
  results: boolean = true;
  constructor(
    private route: ActivatedRoute,
    private backendService: DataService
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      // console.log(params);
      this.term = params.q;
      // console.log(this.term);
      this.searchTerm(this.term);
      // this.getUrl(this.term);
    });
  }
  searchTerm(value: string) {
    this.searchDetail = this.backendService.search(value);
    this.searchDetail.subscribe(resp => {
      // console.log(resp);
      if (resp.length == 0) {
        this.error = true;
        this.results = false;
      } else {
        this.results = true;
        this.error = false;
      }
    });
  }
  //can be used in production
  getUrl(value: string) {
    this._url = document.location.origin + '/';
    // console.log(this._url);
  }
}
