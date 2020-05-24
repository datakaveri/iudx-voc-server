import { Component, OnInit } from '@angular/core';
import { DataService } from '../services/data.service';
import { Class } from '../types/class';
import { Property } from '../types/property';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-overview',
  templateUrl: './overview.component.html',
  styleUrls: ['./overview.component.css']
})
export class OverviewComponent implements OnInit {
  classes: Observable<Class[]>;
  properties: Observable<Property[]>;

  constructor(private backendservice: DataService) {}

  ngOnInit(): void {
    return this.populateClassTable();
  }
  populateClassTable(): void {
    this.classes = this.backendservice.getAllClasses();
  }
  populatePropertyTable(): void {
    this.properties = this.backendservice.getAllProperties();
  }
}
