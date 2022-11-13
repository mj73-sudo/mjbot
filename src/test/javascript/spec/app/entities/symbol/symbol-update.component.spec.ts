/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import Router from 'vue-router';
import { ToastPlugin } from 'bootstrap-vue';

import * as config from '@/shared/config/config';
import SymbolUpdateComponent from '@/entities/symbol/symbol-update.vue';
import SymbolClass from '@/entities/symbol/symbol-update.component';
import SymbolService from '@/entities/symbol/symbol.service';

import AlertService from '@/shared/alert/alert.service';

const localVue = createLocalVue();

config.initVueApp(localVue);
const store = config.initVueXStore(localVue);
const router = new Router();
localVue.use(Router);
localVue.use(ToastPlugin);
localVue.component('font-awesome-icon', {});
localVue.component('b-input-group', {});
localVue.component('b-input-group-prepend', {});
localVue.component('b-form-datepicker', {});
localVue.component('b-form-input', {});

describe('Component Tests', () => {
  describe('Symbol Management Update Component', () => {
    let wrapper: Wrapper<SymbolClass>;
    let comp: SymbolClass;
    let symbolServiceStub: SinonStubbedInstance<SymbolService>;

    beforeEach(() => {
      symbolServiceStub = sinon.createStubInstance<SymbolService>(SymbolService);

      wrapper = shallowMount<SymbolClass>(SymbolUpdateComponent, {
        store,
        localVue,
        router,
        provide: {
          symbolService: () => symbolServiceStub,
          alertService: () => new AlertService(),
        },
      });
      comp = wrapper.vm;
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const entity = { id: 123 };
        comp.symbol = entity;
        symbolServiceStub.update.resolves(entity);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(symbolServiceStub.update.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        comp.symbol = entity;
        symbolServiceStub.create.resolves(entity);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(symbolServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundSymbol = { id: 123 };
        symbolServiceStub.find.resolves(foundSymbol);
        symbolServiceStub.retrieve.resolves([foundSymbol]);

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
