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
  constructor(private backendservice: DataService) {}

  propertyList = [];
  public propertyData: Property[] = [];
  ngOnInit(): void {
    this.populatePropertyTable();
  }
  populatePropertyTable(): void {
    this.backendservice.getAllProperties().subscribe(data => {
      this.propertyList = Array.from(Object.keys(data), k => data[k]);
      console.log(this.propertyList);
      for (let pr in this.propertyList) {
        this.propertyData.push({
          label: this.propertyList[pr]['rdfs:label'],
          comment: this.propertyList[pr]['rdfs:comment']
        });
      }
    });
  }
  showPropertyDetail(event) {
    console.log(event);
  }
}
