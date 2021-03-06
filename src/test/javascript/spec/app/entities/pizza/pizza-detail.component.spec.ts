import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { JhiDataUtils } from 'ng-jhipster';

import { PizzastoreTestModule } from '../../../test.module';
import { PizzaDetailComponent } from 'app/entities/pizza/pizza-detail.component';
import { Pizza } from 'app/shared/model/pizza.model';

describe('Component Tests', () => {
  describe('Pizza Management Detail Component', () => {
    let comp: PizzaDetailComponent;
    let fixture: ComponentFixture<PizzaDetailComponent>;
    let dataUtils: JhiDataUtils;
    const route = ({ data: of({ pizza: new Pizza(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [PizzastoreTestModule],
        declarations: [PizzaDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }]
      })
        .overrideTemplate(PizzaDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(PizzaDetailComponent);
      comp = fixture.componentInstance;
      dataUtils = fixture.debugElement.injector.get(JhiDataUtils);
    });

    describe('OnInit', () => {
      it('Should load pizza on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.pizza).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });

    describe('byteSize', () => {
      it('Should call byteSize from JhiDataUtils', () => {
        // GIVEN
        spyOn(dataUtils, 'byteSize');
        const fakeBase64 = 'fake base64';

        // WHEN
        comp.byteSize(fakeBase64);

        // THEN
        expect(dataUtils.byteSize).toBeCalledWith(fakeBase64);
      });
    });

    describe('openFile', () => {
      it('Should call openFile from JhiDataUtils', () => {
        // GIVEN
        spyOn(dataUtils, 'openFile');
        const fakeContentType = 'fake content type';
        const fakeBase64 = 'fake base64';

        // WHEN
        comp.openFile(fakeContentType, fakeBase64);

        // THEN
        expect(dataUtils.openFile).toBeCalledWith(fakeContentType, fakeBase64);
      });
    });
  });
});
