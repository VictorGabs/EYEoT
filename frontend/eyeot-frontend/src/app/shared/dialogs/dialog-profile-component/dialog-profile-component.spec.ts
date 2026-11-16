import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DialogProfileComponent } from './dialog-profile-component';

describe('DialogProfileComponent', () => {
  let component: DialogProfileComponent;
  let fixture: ComponentFixture<DialogProfileComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DialogProfileComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(DialogProfileComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
