import { Moment } from 'moment';
import { IPizza } from 'app/shared/model/pizza.model';

export interface IInvoice {
  id?: number;
  phone?: string;
  orderplaced?: Moment;
  pizzas?: IPizza[];
}

export class Invoice implements IInvoice {
  constructor(public id?: number, public phone?: string, public orderplaced?: Moment, public pizzas?: IPizza[]) {}
}
