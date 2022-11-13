/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import VueRouter from 'vue-router';

import * as config from '@/shared/config/config';
import SymbolDetailComponent from '@/entities/symbol/symbol-details.vue';
import SymbolClass from '@/entities/symbol/symbol-details.component';
import SymbolService from '@/entities/symbol/symbol.service';
import router from '@/router';
import AlertService from '@/shared/alert/alert.service';

const localVue = createLocalVue();
localVue.use(VueRouter);

config.initVueApp(localVue);
const store = config.initVueXStore(localVue);
localVue.component('font-awesome-icon', {});
localVue.component('router-link', {});

describe('Component Tests', () => {
  describe('Symbol Management Detail Component', () => {
    let wrapper: Wrapper<SymbolClass>;
    let comp: SymbolClass;
    let symbolServiceStub: SinonStubbedInstance<SymbolService>;

    beforeEach(() => {
      symbolServiceStub = sinon.createStubInstance<SymbolService>(SymbolService);

      wrapper = shallowMount<SymbolClass>(SymbolDetailComponent, {
        store,
        localVue,
        router,
        provide: { symbolService: () => symbolServiceStub, alertService: () => new AlertService() },
      });
      comp = wrapper.vm;
    });

    describe('OnInit', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        const foundSymbol = { id: 123 };
        symbolServiceStub.find.resolves(foundSymbol);

        // WHEN
        comp.retrieveSymbol(123);
        await comp.$nextTick();

        // THEN
        expect(comp.symbol).toBe(foundSymbol);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundSymbol = { id: 123 };
        symbolServiceStub.find.resolves(foundSymbol);

        // WHEN
        comp.beforeRouteEnter({ params: { symbolId: 123 } }, null, cb => cb(comp));
        await comp.$nextTick();

        // THEN
        expect(comp.symbol).toBe(foundSymbol);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        comp.previousState();
        await comp.$nextTick();

        expect(comp.$router.currentRoute.fullPath).toContain('/');
      });
    });
  });
});
