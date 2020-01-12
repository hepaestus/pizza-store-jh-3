import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IPizza } from 'app/shared/model/pizza.model';
import { PizzaService } from './pizza.service';

@Component({
  templateUrl: './pizza-delete-dialog.component.html'
})
export class PizzaDeleteDialogComponent {
  pizza?: IPizza;

  constructor(protected pizzaService: PizzaService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  clear(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.pizzaService.delete(id).subscribe(() => {
      this.eventManager.broadcast('pizzaListModification');
      this.activeModal.close();
    });
  }
}
