import { Component, OnInit } from '@angular/core';
import { DataService } from '../services/data.service';
import { Class } from '../types/class';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-classes',
  templateUrl: './classes.component.html',
  styleUrls: ['./classes.component.css']
})
export class ClassesComponent implements OnInit {
  classList = [];
  public classData: Class[] = [];

  constructor(private backendservice: DataService) {}

  ngOnInit(): void {
    return this.populateClassTable();
  }
  populateClassTable(): void {
    this.backendservice.getAllClasses().subscribe(data => {
      this.classList = Array.from(Object.keys(data), k => data[k]);
      for (let cl in this.classList) {
        this.classData.push({
          label: this.classList[cl]['rdfs:label'],
          comment: this.classList[cl]['rdfs:comment']
        });
      }
    });
  }
}
