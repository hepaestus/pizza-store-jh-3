import { IPizza } from 'app/shared/model/pizza.model';

export interface IToppings {
  id?: number;
  name?: string;
  description?: string;
  price?: number;
  pizza?: IPizza;
}

export class Toppings implements IToppings {
  constructor(public id?: number, public name?: string, public description?: string, public price?: number, public pizza?: IPizza) {}
}
