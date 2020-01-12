import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IInvoice } from 'app/shared/model/invoice.model';
import { InvoiceService } from './invoice.service';
import { InvoiceDeleteDialogComponent } from './invoice-delete-dialog.component';

@Component({
  selector: 'jhi-invoice',
  templateUrl: './invoice.component.html'
})
export class InvoiceComponent implements OnInit, OnDestroy {
  invoices?: IInvoice[];
  eventSubscriber?: Subscription;
  currentSearch: string;

  constructor(
    protected invoiceService: InvoiceService,
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
      this.invoiceService
        .search({
          query: this.currentSearch
        })
        .subscribe((res: HttpResponse<IInvoice[]>) => (this.invoices = res.body ? res.body : []));
      return;
    }
    this.invoiceService.query().subscribe((res: HttpResponse<IInvoice[]>) => {
      this.invoices = res.body ? res.body : [];
      this.currentSearch = '';
    });
  }

  search(query: string): void {
    this.currentSearch = query;
    this.loadAll();
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInInvoices();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: IInvoice): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInInvoices(): void {
    this.eventSubscriber = this.eventManager.subscribe('invoiceListModification', () => this.loadAll());
  }

  delete(invoice: IInvoice): void {
    const modalRef = this.modalService.open(InvoiceDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.invoice = invoice;
  }
}
