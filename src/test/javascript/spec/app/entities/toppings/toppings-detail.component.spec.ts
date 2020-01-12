import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { PizzastoreTestModule } from '../../../test.module';
import { ToppingsDetailComponent } from 'app/entities/toppings/toppings-detail.component';
import { Toppings } from 'app/shared/model/toppings.model';

describe('Component Tests', () => {
  describe('Toppings Management Detail Component', () => {
    let comp: ToppingsDetailComponent;
    let fixture: ComponentFixture<ToppingsDetailComponent>;
    const route = ({ data: of({ toppings: new Toppings(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [PizzastoreTestModule],
        declarations: [ToppingsDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }]
      })
        .overrideTemplate(ToppingsDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(ToppingsDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load toppings on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.toppings).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
