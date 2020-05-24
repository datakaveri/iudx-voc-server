import { Component, OnInit } from '@angular/core';
import { DataService } from '../services/data.service';
import { Property } from '../types/property';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-properties',
  templateUrl: './properties.component.html',
  styleUrls: ['./properties.component.css']
})
export class PropertiesComponent implements OnInit {
  properties: Observable<Property[]>;

  constructor(private backendservice: DataService) {}

  ngOnInit(): void {
    return this.populatePropertyTable();
  }
  populatePropertyTable(): void {
    this.properties = this.backendservice.getAllProperties();
  }
}
