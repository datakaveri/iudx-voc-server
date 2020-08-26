import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { Observable } from 'rxjs';
import { Descriptors, Descriptor } from '../types/descriptors';
import { DataService } from '../services/data.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-data-descriptors',
  templateUrl: './data-descriptors.component.html',
  styleUrls: ['./data-descriptors.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DataDescriptorsComponent implements OnInit {
  results: Observable<Descriptors>;
  desType: any;
  DesDocument: [];
  documents: string;
  arraydocs: any;
  constructor(private backendService: DataService, private router: Router) {}

  ngOnInit(): void {
    this.getDescriptors();
  }
  getDescriptors() {
    this.results = this.backendService.getAlldataDescriptors();
  }
}
