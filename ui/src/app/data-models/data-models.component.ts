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
  Colors: Array<any> = [
    '#bfefff',
    '#F6EdFF',
    '#F5FFFA',
    '#FFFFED',
    '#CAE1FF',
    '#bfefff	',
    '#D9D9F3',
    '#fb9667'
  ];
  icons: Array<any> = [];
  constructor(private backendService: DataService) {}

  ngOnInit(): void {
    return this.getDataModels();
  }

  getDataModels() {
    this.datamodels = this.backendService.searchRelationship(
      'subClassOf',
      'DataModel'
    );
  }
  getDataModelDomain(value, event) {
    console.log(value, event);
  }
  getColors(index) {
    let num = this.getnumber(index);
    return this.Colors[num];
  }
  getnumber(data) {
    let i = data;
    if (i > this.Colors.length - 1) {
      i = i - this.Colors.length;
      if (i < this.Colors.length) {
        return i;
      } else {
        this.getnumber(i);
      }
    } else {
      return i;
    }
  }
}
