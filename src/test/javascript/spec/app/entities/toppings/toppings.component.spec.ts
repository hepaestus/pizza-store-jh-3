import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { PizzastoreTestModule } from '../../../test.module';
import { ToppingsComponent } from 'app/entities/toppings/toppings.component';
import { ToppingsService } from 'app/entities/toppings/toppings.service';
import { Toppings } from 'app/shared/model/toppings.model';

describe('Component Tests', () => {
  describe('Toppings Management Component', () => {
    let comp: ToppingsComponent;
    let fixture: ComponentFixture<ToppingsComponent>;
    let service: ToppingsService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [PizzastoreTestModule],
        declarations: [ToppingsComponent],
        providers: []
      })
        .overrideTemplate(ToppingsComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ToppingsComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(ToppingsService);
    });

    it('Should call load all on init', () => {
      // GIVEN
      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [new Toppings(123)],
            headers
          })
        )
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.toppings && comp.toppings[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
