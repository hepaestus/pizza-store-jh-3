import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption, Search } from 'app/shared/util/request-util';
import { IToppings } from 'app/shared/model/toppings.model';

type EntityResponseType = HttpResponse<IToppings>;
type EntityArrayResponseType = HttpResponse<IToppings[]>;

@Injectable({ providedIn: 'root' })
export class ToppingsService {
  public resourceUrl = SERVER_API_URL + 'api/toppings';
  public resourceSearchUrl = SERVER_API_URL + 'api/_search/toppings';

  constructor(protected http: HttpClient) {}

  create(toppings: IToppings): Observable<EntityResponseType> {
    return this.http.post<IToppings>(this.resourceUrl, toppings, { observe: 'response' });
  }

  update(toppings: IToppings): Observable<EntityResponseType> {
    return this.http.put<IToppings>(this.resourceUrl, toppings, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IToppings>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IToppings[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IToppings[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }
}
