import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { DataService } from '../services/data.service';
import { ClassDetail } from '../types/classDetail';
import { PropertyDetail } from '../types/propertyDetail';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-schema-details',
  templateUrl: './schema-details.component.html',
  styleUrls: ['./schema-details.component.css']
})
export class SchemaDetailsComponent implements OnInit {
  classDetail: Observable<ClassDetail>;
  propertyDetail: Observable<PropertyDetail>;
  propertyView: boolean;
  classView: boolean;
  displayProp: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private backendService: DataService
  ) {
    this.propertyView = false;
    this.classView = false;
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      if (params['schemaName'][0] === params['schemaName'][0].toUpperCase()) {
        this.classView = true;
        this.propertyView = false;
        this.classDetail = this.backendService.getClass(params['schemaName']);
        this.classDetail.subscribe(
          resp => console.log(resp),

          error => {
            console.log(error);
            if (error == 'Server error') {
              this.router.navigate(['404', 'not-found']);
              this.displayProp = true;
            }
          }
        );
      } else {
        this.propertyView = true;
        this.classView = false;
        this.propertyDetail = this.backendService.getProperty(
          params['schemaName']
        );

        this.propertyDetail.subscribe(
          resp => console.log(resp),

          error => {
            if (error == 'Server error') {
              this.router.navigate(['404', 'not-found']);
              this.displayProp = true;
            }
          }
        );
      }
    });
  }
}
