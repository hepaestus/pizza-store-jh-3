import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { PizzastoreTestModule } from '../../../test.module';
import { ToppingsUpdateComponent } from 'app/entities/toppings/toppings-update.component';
import { ToppingsService } from 'app/entities/toppings/toppings.service';
import { Toppings } from 'app/shared/model/toppings.model';

describe('Component Tests', () => {
  describe('Toppings Management Update Component', () => {
    let comp: ToppingsUpdateComponent;
    let fixture: ComponentFixture<ToppingsUpdateComponent>;
    let service: ToppingsService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [PizzastoreTestModule],
        declarations: [ToppingsUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(ToppingsUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ToppingsUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(ToppingsService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new Toppings(123);
        spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.update).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));

      it('Should call create service on save for new entity', fakeAsync(() => {
        // GIVEN
        const entity = new Toppings();
        spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.create).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));
    });
  });
});
