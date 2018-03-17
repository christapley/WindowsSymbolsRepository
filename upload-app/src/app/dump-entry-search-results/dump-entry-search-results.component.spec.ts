import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DumpEntrySearchResultsComponent } from './dump-entry-search-results.component';

describe('DumpEntrySearchResultsComponent', () => {
  let component: DumpEntrySearchResultsComponent;
  let fixture: ComponentFixture<DumpEntrySearchResultsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DumpEntrySearchResultsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DumpEntrySearchResultsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
