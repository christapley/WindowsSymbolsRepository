import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UploadDumpProcessComponent } from './upload-dump-process.component';

describe('UploadDumpProcessComponent', () => {
  let component: UploadDumpProcessComponent;
  let fixture: ComponentFixture<UploadDumpProcessComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UploadDumpProcessComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UploadDumpProcessComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
