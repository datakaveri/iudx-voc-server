import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';

@Component({
  selector: 'app-data-models',
  templateUrl: './data-models.component.html',
  styleUrls: ['./data-models.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class DataModelsComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {
  }

}
