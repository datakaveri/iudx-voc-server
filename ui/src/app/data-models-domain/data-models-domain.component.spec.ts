import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DataModelsDomainComponent } from './data-models-domain.component';

describe('DataModelsDomainComponent', () => {
  let component: DataModelsDomainComponent;
  let fixture: ComponentFixture<DataModelsDomainComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [DataModelsDomainComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DataModelsDomainComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
