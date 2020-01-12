import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IToppings } from 'app/shared/model/toppings.model';
import { ToppingsService } from './toppings.service';

@Component({
  templateUrl: './toppings-delete-dialog.component.html'
})
export class ToppingsDeleteDialogComponent {
  toppings?: IToppings;

  constructor(protected toppingsService: ToppingsService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  clear(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.toppingsService.delete(id).subscribe(() => {
      this.eventManager.broadcast('toppingsListModification');
      this.activeModal.close();
    });
  }
}
