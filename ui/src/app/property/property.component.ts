import { Component, OnInit } from '@angular/core';
import { DataService } from '../services/data.service';
import { Property } from '../types/property';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-property',
  templateUrl: './property.component.html',
  styleUrls: ['./property.component.css']
})
export class PropertyComponent implements OnInit {
  properties: Observable<Property[]>;

  constructor(private backendservice: DataService) {}

  ngOnInit(): void {
    return this.populatePropertyTable();
  }
  populatePropertyTable(): void {
    this.properties = this.backendservice.getAllProperties();
  }
}
