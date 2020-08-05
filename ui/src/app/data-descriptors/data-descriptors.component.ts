import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { Observable } from 'rxjs';
import { DataService } from '../services/data.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-data-descriptors',
  templateUrl: './data-descriptors.component.html',
  styleUrls: ['./data-descriptors.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DataDescriptorsComponent implements OnInit {
  descriptors: Observable<any>;
  desType: any;
  DesDocument: [];
  documents: string;
  arraydocs: any;
  results: any;
  constructor(private backendService: DataService, private router: Router) {}

  ngOnInit(): void {
    this.getDescriptors();
  }
  getDescriptors() {
    this.descriptors = this.backendService.getAlldataDescriptors();
    this.descriptors.subscribe((res) => {
      console.log(res);
      this.results = res;
    });
  }
}
