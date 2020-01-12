import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { IPizza, Pizza } from 'app/shared/model/pizza.model';
import { PizzaService } from './pizza.service';
import { PizzaComponent } from './pizza.component';
import { PizzaDetailComponent } from './pizza-detail.component';
import { PizzaUpdateComponent } from './pizza-update.component';

@Injectable({ providedIn: 'root' })
export class PizzaResolve implements Resolve<IPizza> {
  constructor(private service: PizzaService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IPizza> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((pizza: HttpResponse<Pizza>) => {
          if (pizza.body) {
            return of(pizza.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Pizza());
  }
}

export const pizzaRoute: Routes = [
  {
    path: '',
    component: PizzaComponent,
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'Pizzas'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: PizzaDetailComponent,
    resolve: {
      pizza: PizzaResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'Pizzas'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: PizzaUpdateComponent,
    resolve: {
      pizza: PizzaResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'Pizzas'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: PizzaUpdateComponent,
    resolve: {
      pizza: PizzaResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'Pizzas'
    },
    canActivate: [UserRouteAccessService]
  }
];
