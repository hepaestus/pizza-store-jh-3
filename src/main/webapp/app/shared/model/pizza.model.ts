import { IToppings } from 'app/shared/model/toppings.model';
import { IInvoice } from 'app/shared/model/invoice.model';
import { Size } from 'app/shared/model/enumerations/size.model';

export interface IPizza {
  id?: number;
  name?: string;
  description?: string;
  price?: number;
  imageContentType?: string;
  image?: any;
  size?: Size;
  toppings?: IToppings[];
  invoice?: IInvoice;
}

export class Pizza implements IPizza {
  constructor(
    public id?: number,
    public name?: string,
    public description?: string,
    public price?: number,
    public imageContentType?: string,
    public image?: any,
    public size?: Size,
    public toppings?: IToppings[],
    public invoice?: IInvoice
  ) {}
}
