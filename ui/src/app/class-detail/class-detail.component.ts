import { Component, OnInit, Input } from '@angular/core';
import { Router, ActivatedRoute, ParamMap } from '@angular/router';
import { DataService } from '../services/data.service';
import { ClassDetail } from '../types/classdetail';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-class-detail',
  templateUrl: './class-detail.component.html',
  styleUrls: ['./class-detail.component.css']
})
export class ClassDetailComponent implements OnInit {
  constructor(
    private route: ActivatedRoute,
    private backendservice: DataService
  ) {}

  data: Observable<ClassDetail[]>;
  name: string;
  arrClass: object[];
  graph: object[];
  ngOnInit(): void {
    // console.log(this.route.snapshot.params['name']);
    // this.route.params.subscribe(params => {
    //   console.log(params['name']);
    // });
    this.name = this.route.snapshot.params['name'];
    this.showClassDetail(this.name);
  }
  showClassDetail(class_name: string): void {
    this.backendservice.getClassDetail(class_name).subscribe(data => {
      this.arrClass = data as object[]; // FILL THE ARRAY WITH DATA.
      console.log(this.arrClass);
      this.graph = data['@graph'];
      console.log(this.graph);
    });
  }
}
