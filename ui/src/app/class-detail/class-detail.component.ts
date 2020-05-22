import { Component, OnInit, Input } from '@angular/core';
import { DataService } from '../services/data.service';
import { ClassDetail } from '../types/classdetail';
import { Observable } from 'rxjs';
import { Class } from '../types/class';

@Component({
  selector: 'app-class-detail',
  templateUrl: './class-detail.component.html',
  styleUrls: ['./class-detail.component.css']
})
export class ClassDetailComponent implements OnInit {
  constructor() {}
  @Input() class: Class;
  @Input() graphData: Object;
  ngOnInit(): void {}
}
