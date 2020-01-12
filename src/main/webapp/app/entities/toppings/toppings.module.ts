import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { PizzastoreSharedModule } from 'app/shared/shared.module';
import { ToppingsComponent } from './toppings.component';
import { ToppingsDetailComponent } from './toppings-detail.component';
import { ToppingsUpdateComponent } from './toppings-update.component';
import { ToppingsDeleteDialogComponent } from './toppings-delete-dialog.component';
import { toppingsRoute } from './toppings.route';

@NgModule({
  imports: [PizzastoreSharedModule, RouterModule.forChild(toppingsRoute)],
  declarations: [ToppingsComponent, ToppingsDetailComponent, ToppingsUpdateComponent, ToppingsDeleteDialogComponent],
  entryComponents: [ToppingsDeleteDialogComponent]
})
export class PizzastoreToppingsModule {}
