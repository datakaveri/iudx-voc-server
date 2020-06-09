import { Component, OnInit, Output } from '@angular/core';
import { DataService } from '../services/data.service';
import { Class } from '../types/class';
import { Observable } from 'rxjs';

interface DynamicRoute {
  path: string;
  component: ClassesComponent;
}

@Component({
  selector: 'app-classes',
  templateUrl: './classes.component.html',
  styleUrls: ['./classes.component.css']
})
export class ClassesComponent implements OnInit {
  classes: Observable<Class[]>;
  clsRoutes: DynamicRoute[];

  constructor(private backendService: DataService) {}

  ngOnInit(): void {
    this.populateClassTable();
  }
  populateClassTable(): void {
    this.classes = this.backendService.getAllClasses();
  }
}
