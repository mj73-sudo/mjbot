/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import VueRouter from 'vue-router';

import * as config from '@/shared/config/config';
import KlineDetailComponent from '@/entities/kline/kline-details.vue';
import KlineClass from '@/entities/kline/kline-details.component';
import KlineService from '@/entities/kline/kline.service';
import router from '@/router';
import AlertService from '@/shared/alert/alert.service';

const localVue = createLocalVue();
localVue.use(VueRouter);

config.initVueApp(localVue);
const store = config.initVueXStore(localVue);
localVue.component('font-awesome-icon', {});
localVue.component('router-link', {});

describe('Component Tests', () => {
  describe('Kline Management Detail Component', () => {
    let wrapper: Wrapper<KlineClass>;
    let comp: KlineClass;
    let klineServiceStub: SinonStubbedInstance<KlineService>;

    beforeEach(() => {
      klineServiceStub = sinon.createStubInstance<KlineService>(KlineService);

      wrapper = shallowMount<KlineClass>(KlineDetailComponent, {
        store,
        localVue,
        router,
        provide: { klineService: () => klineServiceStub, alertService: () => new AlertService() },
      });
      comp = wrapper.vm;
    });

    describe('OnInit', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        const foundKline = { id: 123 };
        klineServiceStub.find.resolves(foundKline);

        // WHEN
        comp.retrieveKline(123);
        await comp.$nextTick();

        // THEN
        expect(comp.kline).toBe(foundKline);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundKline = { id: 123 };
        klineServiceStub.find.resolves(foundKline);

        // WHEN
        comp.beforeRouteEnter({ params: { klineId: 123 } }, null, cb => cb(comp));
        await comp.$nextTick();

        // THEN
        expect(comp.kline).toBe(foundKline);
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
