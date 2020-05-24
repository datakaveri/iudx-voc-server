import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BaseschemasComponent } from './baseschemas.component';

describe('BaseschemasComponent', () => {
  let component: BaseschemasComponent;
  let fixture: ComponentFixture<BaseschemasComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ BaseschemasComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BaseschemasComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
