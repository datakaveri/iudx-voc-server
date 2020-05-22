import { Component, OnInit } from '@angular/core';
import { DataService } from '../services/data.service';
import { Class } from '../types/class';
import { Observable } from 'rxjs';
import { ClassDetail } from '../types/classdetail';

@Component({
  selector: 'app-classes',
  templateUrl: './classes.component.html',
  styleUrls: ['./classes.component.css']
})
export class ClassesComponent implements OnInit {
  classes: Observable<Class[]>;
  data: Observable<ClassDetail[]>;
  selectedClass: Class;
  arrClass: object[];
  graph: object[];

  constructor(private backendservice: DataService) {}

  ngOnInit(): void {
    this.populateClassTable();
  }
  populateClassTable(): void {
    this.classes = this.backendservice.getAllClasses();
  }
  // showClassDetail(class_type: Class): void {
  //   this.selectedClass = class_type;
  //   this.data = this.backendservice.getClassDetail(this.selectedClass);
  // }
  showClassDetail(class_type: Class): void {
    this.selectedClass = class_type;
    this.backendservice.getClassDetail(this.selectedClass).subscribe(data => {
      this.arrClass = data as object[]; // FILL THE ARRAY WITH DATA.
      console.log(this.arrClass);
      this.graph = data['@graph'];
      console.log(this.graph);
      //  this.Drug = this.Drugs["DrugType"];
      //  console.log(Drug);
    });
  }
}
