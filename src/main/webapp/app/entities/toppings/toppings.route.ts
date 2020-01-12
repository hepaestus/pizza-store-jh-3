import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { IToppings, Toppings } from 'app/shared/model/toppings.model';
import { ToppingsService } from './toppings.service';
import { ToppingsComponent } from './toppings.component';
import { ToppingsDetailComponent } from './toppings-detail.component';
import { ToppingsUpdateComponent } from './toppings-update.component';

@Injectable({ providedIn: 'root' })
export class ToppingsResolve implements Resolve<IToppings> {
  constructor(private service: ToppingsService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IToppings> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((toppings: HttpResponse<Toppings>) => {
          if (toppings.body) {
            return of(toppings.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Toppings());
  }
}

export const toppingsRoute: Routes = [
  {
    path: '',
    component: ToppingsComponent,
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'Toppings'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: ToppingsDetailComponent,
    resolve: {
      toppings: ToppingsResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'Toppings'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: ToppingsUpdateComponent,
    resolve: {
      toppings: ToppingsResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'Toppings'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: ToppingsUpdateComponent,
    resolve: {
      toppings: ToppingsResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'Toppings'
    },
    canActivate: [UserRouteAccessService]
  }
];
