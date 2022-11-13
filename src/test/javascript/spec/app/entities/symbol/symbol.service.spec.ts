/* tslint:disable max-line-length */
import axios from 'axios';
import sinon from 'sinon';

import SymbolService from '@/entities/symbol/symbol.service';
import { Symbol } from '@/shared/model/symbol.model';

const error = {
  response: {
    status: null,
    data: {
      type: null,
    },
  },
};

const axiosStub = {
  get: sinon.stub(axios, 'get'),
  post: sinon.stub(axios, 'post'),
  put: sinon.stub(axios, 'put'),
  patch: sinon.stub(axios, 'patch'),
  delete: sinon.stub(axios, 'delete'),
};

describe('Service Tests', () => {
  describe('Symbol Service', () => {
    let service: SymbolService;
    let elemDefault;

    beforeEach(() => {
      service = new SymbolService();
      elemDefault = new Symbol(
        123,
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        false,
        false,
        false
      );
    });

    describe('Service methods', () => {
      it('should find an element', async () => {
        const returnedFromService = Object.assign({}, elemDefault);
        axiosStub.get.resolves({ data: returnedFromService });

        return service.find(123).then(res => {
          expect(res).toMatchObject(elemDefault);
        });
      });

      it('should not find an element', async () => {
        axiosStub.get.rejects(error);
        return service
          .find(123)
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should create a Symbol', async () => {
        const returnedFromService = Object.assign(
          {
            id: 123,
          },
          elemDefault
        );
        const expected = Object.assign({}, returnedFromService);

        axiosStub.post.resolves({ data: returnedFromService });
        return service.create({}).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not create a Symbol', async () => {
        axiosStub.post.rejects(error);

        return service
          .create({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should update a Symbol', async () => {
        const returnedFromService = Object.assign(
          {
            symbol: 'BBBBBB',
            name: 'BBBBBB',
            baseCurrency: 'BBBBBB',
            quoteCurrency: 'BBBBBB',
            feeCurrency: 'BBBBBB',
            market: 'BBBBBB',
            baseMinSize: 'BBBBBB',
            quoteMinSize: 'BBBBBB',
            baseMaxSize: 'BBBBBB',
            quoteMaxSize: 'BBBBBB',
            baseIncrement: 'BBBBBB',
            quoteIncrement: 'BBBBBB',
            priceIncrement: 'BBBBBB',
            priceLimitRate: 'BBBBBB',
            minFunds: 'BBBBBB',
            isMarginEnabled: true,
            enableTrading: true,
            active: true,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);
        axiosStub.put.resolves({ data: returnedFromService });

        return service.update(expected).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not update a Symbol', async () => {
        axiosStub.put.rejects(error);

        return service
          .update({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should partial update a Symbol', async () => {
        const patchObject = Object.assign(
          {
            symbol: 'BBBBBB',
            baseCurrency: 'BBBBBB',
            market: 'BBBBBB',
            quoteMinSize: 'BBBBBB',
            baseMaxSize: 'BBBBBB',
            quoteMaxSize: 'BBBBBB',
            baseIncrement: 'BBBBBB',
            priceIncrement: 'BBBBBB',
            minFunds: 'BBBBBB',
            isMarginEnabled: true,
          },
          new Symbol()
        );
        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign({}, returnedFromService);
        axiosStub.patch.resolves({ data: returnedFromService });

        return service.partialUpdate(patchObject).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not partial update a Symbol', async () => {
        axiosStub.patch.rejects(error);

        return service
          .partialUpdate({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should return a list of Symbol', async () => {
        const returnedFromService = Object.assign(
          {
            symbol: 'BBBBBB',
            name: 'BBBBBB',
            baseCurrency: 'BBBBBB',
            quoteCurrency: 'BBBBBB',
            feeCurrency: 'BBBBBB',
            market: 'BBBBBB',
            baseMinSize: 'BBBBBB',
            quoteMinSize: 'BBBBBB',
            baseMaxSize: 'BBBBBB',
            quoteMaxSize: 'BBBBBB',
            baseIncrement: 'BBBBBB',
            quoteIncrement: 'BBBBBB',
            priceIncrement: 'BBBBBB',
            priceLimitRate: 'BBBBBB',
            minFunds: 'BBBBBB',
            isMarginEnabled: true,
            enableTrading: true,
            active: true,
          },
          elemDefault
        );
        const expected = Object.assign({}, returnedFromService);
        axiosStub.get.resolves([returnedFromService]);
        return service.retrieve({ sort: {}, page: 0, size: 10 }).then(res => {
          expect(res).toContainEqual(expected);
        });
      });

      it('should not return a list of Symbol', async () => {
        axiosStub.get.rejects(error);

        return service
          .retrieve()
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should delete a Symbol', async () => {
        axiosStub.delete.resolves({ ok: true });
        return service.delete(123).then(res => {
          expect(res.ok).toBeTruthy();
        });
      });

      it('should not delete a Symbol', async () => {
        axiosStub.delete.rejects(error);

        return service
          .delete(123)
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });
    });
  });
});
