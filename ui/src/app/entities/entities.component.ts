import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { DataService } from '../services/data.service';
import { Observable } from 'rxjs';
import { Class } from '../types/class';

@Component({
  selector: 'app-entities',
  templateUrl: './entities.component.html',
  styleUrls: ['./entities.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class EntitiesComponent implements OnInit {
  entities: Observable<Class[]>;

  constructor(private backendService: DataService) {}

  ngOnInit(): void {
    this.getEntities();
  }

  getEntities(): void {
    this.entities = this.backendService.searchRelationship(
      'subClassOf',
      'IUDXEntity'
    );
  }
}
