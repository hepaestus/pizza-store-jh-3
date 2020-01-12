import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IToppings } from 'app/shared/model/toppings.model';

@Component({
  selector: 'jhi-toppings-detail',
  templateUrl: './toppings-detail.component.html'
})
export class ToppingsDetailComponent implements OnInit {
  toppings: IToppings | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ toppings }) => {
      this.toppings = toppings;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
