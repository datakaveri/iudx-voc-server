import { Component, OnInit } from '@angular/core';
import { DataService } from '../services/data.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-property',
  templateUrl: './property.component.html',
  styleUrls: ['./property.component.css']
})
export class PropertyComponent implements OnInit {
  constructor(private backendservice: DataService) {}

  // propertyList: Observable<PropertyResult[]>;
  ngOnInit(): void {
    // this.propertyList = this.backendservice.getAllProperties();
  }
}
