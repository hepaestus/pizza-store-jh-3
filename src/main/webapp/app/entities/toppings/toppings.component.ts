import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IToppings } from 'app/shared/model/toppings.model';
import { ToppingsService } from './toppings.service';
import { ToppingsDeleteDialogComponent } from './toppings-delete-dialog.component';

@Component({
  selector: 'jhi-toppings',
  templateUrl: './toppings.component.html'
})
export class ToppingsComponent implements OnInit, OnDestroy {
  toppings?: IToppings[];
  eventSubscriber?: Subscription;
  currentSearch: string;

  constructor(
    protected toppingsService: ToppingsService,
    protected eventManager: JhiEventManager,
    protected modalService: NgbModal,
    protected activatedRoute: ActivatedRoute
  ) {
    this.currentSearch =
      this.activatedRoute.snapshot && this.activatedRoute.snapshot.queryParams['search']
        ? this.activatedRoute.snapshot.queryParams['search']
        : '';
  }

  loadAll(): void {
    if (this.currentSearch) {
      this.toppingsService
        .search({
          query: this.currentSearch
        })
        .subscribe((res: HttpResponse<IToppings[]>) => (this.toppings = res.body ? res.body : []));
      return;
    }
    this.toppingsService.query().subscribe((res: HttpResponse<IToppings[]>) => {
      this.toppings = res.body ? res.body : [];
      this.currentSearch = '';
    });
  }

  search(query: string): void {
    this.currentSearch = query;
    this.loadAll();
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInToppings();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: IToppings): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInToppings(): void {
    this.eventSubscriber = this.eventManager.subscribe('toppingsListModification', () => this.loadAll());
  }

  delete(toppings: IToppings): void {
    const modalRef = this.modalService.open(ToppingsDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.toppings = toppings;
  }
}
