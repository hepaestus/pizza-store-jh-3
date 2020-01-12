export interface ICustomer {
  id?: number;
  name?: string;
  price?: number;
  description?: string;
  imageContentType?: string;
  image?: any;
}

export class Customer implements ICustomer {
  constructor(
    public id?: number,
    public name?: string,
    public price?: number,
    public description?: string,
    public imageContentType?: string,
    public image?: any
  ) {}
}
