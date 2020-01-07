import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { PizzastoreSharedModule } from 'app/shared/shared.module';
import { HOME_ROUTE } from './home.route';
import { HomeComponent } from './home.component';

@NgModule({
  imports: [PizzastoreSharedModule, RouterModule.forChild([HOME_ROUTE])],
  declarations: [HomeComponent]
})
export class PizzastoreHomeModule {}
