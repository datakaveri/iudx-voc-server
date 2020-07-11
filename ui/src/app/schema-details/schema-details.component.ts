import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { DataService } from '../services/data.service';
import { ClassDetail } from '../types/classDetail';
import { PropertyDetail } from '../types/propertyDetail';
import { Observable } from 'rxjs';
import { share } from 'rxjs/operators';

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
  value: string;
  examples: boolean = false;
  code: any;
  label: string;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private backendService: DataService
  ) {
    this.propertyView = false;
    this.classView = false;
    this.examples = false;
    this.label = 'Example';
  }

  ngOnInit(): void {
    this.showProperty();
  }
  showProperty() {
    this.route.params.subscribe(params => {
      this.value = params['schemaName'];
      // console.log(params['schemaName'][0].toUpperCase());
      if (params['schemaName'][0] === params['schemaName'][0].toUpperCase()) {
        this.classView = true;
        this.propertyView = false;
        this.classDetail = this.backendService
          .getClass(params['schemaName'])
          .pipe(share());
        this.classDetail.subscribe(
          resp => console.log(resp),
          error => {
            //console.log(error);
            if (error == 'Server error') {
              this.router.navigate(['404', 'not-found']);
              this.displayProp = true;
            }
          }
        );
      } else {
        this.propertyView = true;
        this.classView = false;
        this.propertyDetail = this.backendService
          .getProperty(params['schemaName'])
          .pipe(share());

        this.propertyDetail.subscribe(resp =>
          //console.log(resp),

          error => {
            if (error == 'Server error') {
              this.router.navigate(['404', 'not-found']);
              this.displayProp = true;
            }
          }
        );
      }
      //console.log(this.value);
      this.showExamples(this.value);
    });
  }
  showExamples(val: string) {
    this.backendService.getExamples(val).subscribe(response => {
      if (response == [] || response.length == 0) {
        //console.log(response);
        this.examples = false;

        //console.log(this.examples);
      } else {
        this.examples = true;
        this.code = response;
        //console.log(response);
        //console.log(this.examples);
      }
    });
  }
}
