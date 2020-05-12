import { Component, OnInit } from '@angular/core';
import { DataService } from '../services/data.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-classes',
  templateUrl: './classes.component.html',
  styleUrls: ['./classes.component.css']
})
export class ClassesComponent implements OnInit {
  public classList = [];
  public classData = [];
  public classDesc = [];
  constructor(private backendservice: DataService) {}

  ngOnInit(): void {
    this.backendservice.getAllClasses().subscribe(data => {
      this.classList = Array.from(Object.keys(data), k => data[k]);
      console.log(this.classList);
      for (var i = 0; i < this.classList.length; i++) {
        this.classData[i] = this.classList[i]['rdfs:label'];
        this.classDesc[i] = this.classList[i]['rdfs:comment'];
        // console.log(this.classData);
      }
    });
  }
}
