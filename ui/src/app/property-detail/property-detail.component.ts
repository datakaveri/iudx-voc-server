import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute, ParamMap } from '@angular/router';
import { DataService } from '../services/data.service';
import { PropertyDetail } from '../types/propertydetail';

@Component({
  selector: 'app-property-detail',
  templateUrl: './property-detail.component.html',
  styleUrls: ['./property-detail.component.css']
})
export class PropertyDetailComponent implements OnInit {
  constructor(
    private route: ActivatedRoute,
    private backendservice: DataService
  ) {}
  property: PropertyDetail;
  propname: any;
  arrProp: object;
  graph: object;
  type: any;
  domain: any;
  comment: string;
  ngOnInit(): void {
    console.log(this.route.snapshot.params['propname']);
    // this.route.params.subscribe(params => {
    //   console.log(params['name']);
    // });
    this.propname = this.route.snapshot.params['propname'];
    this.showPropertyDetail(this.propname);
  }
  showPropertyDetail(prop_name: String): void {
    this.backendservice.getPropertyDetail(prop_name).subscribe(data => {
      this.arrProp = data as object[]; // FILL THE ARRAY WITH DATA.
      console.log(this.arrProp);
      this.graph = data['@graph'][0];
      this.type = this.graph['@type'][0].split('iudx:')[1];
      this.domain = this.graph['iudx:domainIncludes'];
      this.comment = this.graph['rdfs:comment'];
      console.log(this.graph);
      console.log(this.type);
    });
  }
}
