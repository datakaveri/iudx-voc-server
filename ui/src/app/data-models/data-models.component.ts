import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { DataService } from '../services/data.service';
import { Observable } from 'rxjs';
import { DataModel } from '../types/dataModel';

@Component({
  selector: 'app-data-models',
  templateUrl: './data-models.component.html',
  styleUrls: ['./data-models.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class DataModelsComponent implements OnInit {
  datamodels: Observable<DataModel[]>;

  constructor(private backendService: DataService) {}

  ngOnInit(): void {
    return this.getDataModels();
  }

  getDataModels() {
    this.datamodels = this.backendService.searchRelationship("subClassOf", "DataModel");
  }
}
