import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { PizzastoreSharedModule } from 'app/shared/shared.module';
import { PizzaComponent } from './pizza.component';
import { PizzaDetailComponent } from './pizza-detail.component';
import { PizzaUpdateComponent } from './pizza-update.component';
import { PizzaDeleteDialogComponent } from './pizza-delete-dialog.component';
import { pizzaRoute } from './pizza.route';

@NgModule({
  imports: [PizzastoreSharedModule, RouterModule.forChild(pizzaRoute)],
  declarations: [PizzaComponent, PizzaDetailComponent, PizzaUpdateComponent, PizzaDeleteDialogComponent],
  entryComponents: [PizzaDeleteDialogComponent]
})
export class PizzastorePizzaModule {}
