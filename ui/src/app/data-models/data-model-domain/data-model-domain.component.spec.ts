import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DataModelDomainComponent } from './data-model-domain.component';

describe('DataModelDomainComponent', () => {
  let component: DataModelDomainComponent;
  let fixture: ComponentFixture<DataModelDomainComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DataModelDomainComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DataModelDomainComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
