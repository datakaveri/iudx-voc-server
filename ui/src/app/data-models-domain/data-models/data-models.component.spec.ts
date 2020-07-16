import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DataModelsComponent } from './../data-models/data-models.component';

describe('DataModelsComponent', () => {
  let component: DataModelsComponent;
  let fixture: ComponentFixture<DataModelsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [DataModelsComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DataModelsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
