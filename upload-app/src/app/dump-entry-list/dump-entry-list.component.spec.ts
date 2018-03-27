import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DumpEntryListComponent } from './dump-entry-list.component';

describe('DumpEntryListComponent', () => {
  let component: DumpEntryListComponent;
  let fixture: ComponentFixture<DumpEntryListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DumpEntryListComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DumpEntryListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
