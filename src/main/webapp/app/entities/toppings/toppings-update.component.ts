import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { IToppings, Toppings } from 'app/shared/model/toppings.model';
import { ToppingsService } from './toppings.service';
import { IPizza } from 'app/shared/model/pizza.model';
import { PizzaService } from 'app/entities/pizza/pizza.service';

@Component({
  selector: 'jhi-toppings-update',
  templateUrl: './toppings-update.component.html'
})
export class ToppingsUpdateComponent implements OnInit {
  isSaving = false;

  pizzas: IPizza[] = [];

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required]],
    description: [],
    price: [null, [Validators.required, Validators.min(0)]],
    pizza: []
  });

  constructor(
    protected toppingsService: ToppingsService,
    protected pizzaService: PizzaService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ toppings }) => {
      this.updateForm(toppings);

      this.pizzaService
        .query()
        .pipe(
          map((res: HttpResponse<IPizza[]>) => {
            return res.body ? res.body : [];
          })
        )
        .subscribe((resBody: IPizza[]) => (this.pizzas = resBody));
    });
  }

  updateForm(toppings: IToppings): void {
    this.editForm.patchValue({
      id: toppings.id,
      name: toppings.name,
      description: toppings.description,
      price: toppings.price,
      pizza: toppings.pizza
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const toppings = this.createFromForm();
    if (toppings.id !== undefined) {
      this.subscribeToSaveResponse(this.toppingsService.update(toppings));
    } else {
      this.subscribeToSaveResponse(this.toppingsService.create(toppings));
    }
  }

  private createFromForm(): IToppings {
    return {
      ...new Toppings(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      description: this.editForm.get(['description'])!.value,
      price: this.editForm.get(['price'])!.value,
      pizza: this.editForm.get(['pizza'])!.value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IToppings>>): void {
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

  trackById(index: number, item: IPizza): any {
    return item.id;
  }
}
