import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { DataService } from 'src/app/services/data.service';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-descriptor-details',
  templateUrl: './descriptor-details.component.html',
  styleUrls: ['./descriptor-details.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DescriptorDetailsComponent implements OnInit {
  value: string;
  details: Observable<any>;
  resultValues: unknown[];
  resultKeys: string[];
  results: any;

  constructor(
    private backendService: DataService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.getDesciptorValue();
  }
  getDesciptorValue() {
    this.route.params.subscribe((params) => {
      this.value = params['descriptorName'];
      console.log(this.value);
    });
    this.details = this.backendService.getDescriptorDetails(this.value);
    this.details.subscribe((res) => {
      console.log(typeof res);
      this.results = res;
      console.log(this.results);
      this.resultKeys = Object.keys(res[0]);
      this.resultValues = Object.values(res[0]);
      console.log(this.resultKeys);
      console.log(this.resultValues);
      this.resultKeys = this.resultKeys.slice(3, 10);
      this.resultValues = this.resultValues.slice(3, 10);
      // this.res.push({ key: this.resultKeys, values: this.resultValues });
      //   var res: any = {
      //     key: this.resultKeys,
      //     values: this.resultValues,
      //   };
      //   this.results = res;
      //   console.log(this.results);
    });
  }
}
