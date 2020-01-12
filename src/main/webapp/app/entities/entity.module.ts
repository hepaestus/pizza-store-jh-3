import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'customer',
        loadChildren: () => import('./customer/customer.module').then(m => m.PizzastoreCustomerModule)
      },
      {
        path: 'invoice',
        loadChildren: () => import('./invoice/invoice.module').then(m => m.PizzastoreInvoiceModule)
      },
      {
        path: 'pizza',
        loadChildren: () => import('./pizza/pizza.module').then(m => m.PizzastorePizzaModule)
      },
      {
        path: 'toppings',
        loadChildren: () => import('./toppings/toppings.module').then(m => m.PizzastoreToppingsModule)
      }
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ])
  ]
})
export class PizzastoreEntityModule {}
