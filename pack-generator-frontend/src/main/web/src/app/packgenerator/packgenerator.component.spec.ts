import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PackgeneratorComponent } from './packgenerator.component';

describe('PackgeneratorComponent', () => {
  let component: PackgeneratorComponent;
  let fixture: ComponentFixture<PackgeneratorComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PackgeneratorComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PackgeneratorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
