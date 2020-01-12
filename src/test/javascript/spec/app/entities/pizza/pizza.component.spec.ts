import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { PizzastoreTestModule } from '../../../test.module';
import { PizzaComponent } from 'app/entities/pizza/pizza.component';
import { PizzaService } from 'app/entities/pizza/pizza.service';
import { Pizza } from 'app/shared/model/pizza.model';

describe('Component Tests', () => {
  describe('Pizza Management Component', () => {
    let comp: PizzaComponent;
    let fixture: ComponentFixture<PizzaComponent>;
    let service: PizzaService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [PizzastoreTestModule],
        declarations: [PizzaComponent],
        providers: []
      })
        .overrideTemplate(PizzaComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(PizzaComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(PizzaService);
    });

    it('Should call load all on init', () => {
      // GIVEN
      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [new Pizza(123)],
            headers
          })
        )
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.pizzas && comp.pizzas[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
