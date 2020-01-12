import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { JhiEventManager, JhiDataUtils } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IPizza } from 'app/shared/model/pizza.model';
import { PizzaService } from './pizza.service';
import { PizzaDeleteDialogComponent } from './pizza-delete-dialog.component';

@Component({
  selector: 'jhi-pizza',
  templateUrl: './pizza.component.html'
})
export class PizzaComponent implements OnInit, OnDestroy {
  pizzas?: IPizza[];
  eventSubscriber?: Subscription;
  currentSearch: string;

  constructor(
    protected pizzaService: PizzaService,
    protected dataUtils: JhiDataUtils,
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
      this.pizzaService
        .search({
          query: this.currentSearch
        })
        .subscribe((res: HttpResponse<IPizza[]>) => (this.pizzas = res.body ? res.body : []));
      return;
    }
    this.pizzaService.query().subscribe((res: HttpResponse<IPizza[]>) => {
      this.pizzas = res.body ? res.body : [];
      this.currentSearch = '';
    });
  }

  search(query: string): void {
    this.currentSearch = query;
    this.loadAll();
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInPizzas();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: IPizza): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(contentType: string, base64String: string): void {
    return this.dataUtils.openFile(contentType, base64String);
  }

  registerChangeInPizzas(): void {
    this.eventSubscriber = this.eventManager.subscribe('pizzaListModification', () => this.loadAll());
  }

  delete(pizza: IPizza): void {
    const modalRef = this.modalService.open(PizzaDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.pizza = pizza;
  }
}
