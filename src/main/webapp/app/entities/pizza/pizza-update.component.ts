import { Component, OnInit, ElementRef } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { JhiDataUtils, JhiFileLoadError, JhiEventManager, JhiEventWithContent } from 'ng-jhipster';

import { IPizza, Pizza } from 'app/shared/model/pizza.model';
import { PizzaService } from './pizza.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { IInvoice } from 'app/shared/model/invoice.model';
import { InvoiceService } from 'app/entities/invoice/invoice.service';

@Component({
  selector: 'jhi-pizza-update',
  templateUrl: './pizza-update.component.html'
})
export class PizzaUpdateComponent implements OnInit {
  isSaving = false;

  invoices: IInvoice[] = [];

  editForm = this.fb.group({
    id: [],
    name: [],
    description: [],
    price: [null, [Validators.required, Validators.min(0)]],
    image: [],
    imageContentType: [],
    size: [null, [Validators.required]],
    invoice: []
  });

  constructor(
    protected dataUtils: JhiDataUtils,
    protected eventManager: JhiEventManager,
    protected pizzaService: PizzaService,
    protected invoiceService: InvoiceService,
    protected elementRef: ElementRef,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ pizza }) => {
      this.updateForm(pizza);

      this.invoiceService
        .query()
        .pipe(
          map((res: HttpResponse<IInvoice[]>) => {
            return res.body ? res.body : [];
          })
        )
        .subscribe((resBody: IInvoice[]) => (this.invoices = resBody));
    });
  }

  updateForm(pizza: IPizza): void {
    this.editForm.patchValue({
      id: pizza.id,
      name: pizza.name,
      description: pizza.description,
      price: pizza.price,
      image: pizza.image,
      imageContentType: pizza.imageContentType,
      size: pizza.size,
      invoice: pizza.invoice
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(contentType: string, base64String: string): void {
    this.dataUtils.openFile(contentType, base64String);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe(null, (err: JhiFileLoadError) => {
      this.eventManager.broadcast(
        new JhiEventWithContent<AlertError>('pizzastoreApp.error', { message: err.message })
      );
    });
  }

  clearInputImage(field: string, fieldContentType: string, idInput: string): void {
    this.editForm.patchValue({
      [field]: null,
      [fieldContentType]: null
    });
    if (this.elementRef && idInput && this.elementRef.nativeElement.querySelector('#' + idInput)) {
      this.elementRef.nativeElement.querySelector('#' + idInput).value = null;
    }
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const pizza = this.createFromForm();
    if (pizza.id !== undefined) {
      this.subscribeToSaveResponse(this.pizzaService.update(pizza));
    } else {
      this.subscribeToSaveResponse(this.pizzaService.create(pizza));
    }
  }

  private createFromForm(): IPizza {
    return {
      ...new Pizza(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      description: this.editForm.get(['description'])!.value,
      price: this.editForm.get(['price'])!.value,
      imageContentType: this.editForm.get(['imageContentType'])!.value,
      image: this.editForm.get(['image'])!.value,
      size: this.editForm.get(['size'])!.value,
      invoice: this.editForm.get(['invoice'])!.value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPizza>>): void {
    result.subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError(): void {
    this.isSaving = false;
  }

  trackById(index: number, item: IInvoice): any {
    return item.id;
  }
}
