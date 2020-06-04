import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';

@Component({
  selector: 'app-entities',
  templateUrl: './entities.component.html',
  styleUrls: ['./entities.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class EntitiesComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {
  }

}
