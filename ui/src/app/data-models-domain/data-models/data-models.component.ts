import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { DataService } from '../../services/data.service';
import { Observable } from 'rxjs';
import { DataModel } from 'src/app/types/dataModel';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-data-models',
  templateUrl: './data-models.component.html',
  styleUrls: ['./data-models.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DataModelsComponent implements OnInit {
  domains: Observable<DataModel[]>;
  value: string;

  constructor(
    private backendService: DataService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    return this.getDomainValue();
  }
  getDomainValue() {
    this.route.params.subscribe((params) => {
      this.value = params['domainName'];
    });
    this.domains = this.backendService.searchRelationship(
      'subClassOf',
      this.value
    );
  }
}
