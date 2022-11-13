<template>
  <div>
    <h2 id="page-heading" data-cy="KlineHeading">
      <span id="kline-heading">Klines</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon> <span>Refresh List</span>
        </button>
        <router-link :to="{ name: 'KlineCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-kline"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span> Create a new Kline </span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && klines && klines.length === 0">
      <span>No klines found</span>
    </div>
    <div class="table-responsive" v-if="klines && klines.length > 0">
      <table class="table table-striped" aria-describedby="klines">
        <thead>
          <tr>
            <th scope="row" v-on:click="changeOrder('id')">
              <span>ID</span> <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('time')">
              <span>Time</span> <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'time'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('open')">
              <span>Open</span> <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'open'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('close')">
              <span>Close</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'close'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('high')">
              <span>High</span> <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'high'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('low')">
              <span>Low</span> <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'low'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('volume')">
              <span>Volume</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'volume'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('turnover')">
              <span>Turnover</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'turnover'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('timeType')">
              <span>Time Type</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'timeType'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('symbol.symbol')">
              <span>Symbol</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'symbol.symbol'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="kline in klines" :key="kline.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'KlineView', params: { klineId: kline.id } }">{{ kline.id }}</router-link>
            </td>
            <td>{{ kline.time }}</td>
            <td>{{ kline.open }}</td>
            <td>{{ kline.close }}</td>
            <td>{{ kline.high }}</td>
            <td>{{ kline.low }}</td>
            <td>{{ kline.volume }}</td>
            <td>{{ kline.turnover }}</td>
            <td>{{ kline.timeType }}</td>
            <td>
              <div v-if="kline.symbol">
                <router-link :to="{ name: 'SymbolView', params: { symbolId: kline.symbol.id } }">{{ kline.symbol.symbol }}</router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'KlineView', params: { klineId: kline.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline">View</span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'KlineEdit', params: { klineId: kline.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline">Edit</span>
                  </button>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(kline)"
                  variant="danger"
                  class="btn btn-sm"
                  data-cy="entityDeleteButton"
                  v-b-modal.removeEntity
                >
                  <font-awesome-icon icon="times"></font-awesome-icon>
                  <span class="d-none d-md-inline">Delete</span>
                </b-button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <b-modal ref="removeEntity" id="removeEntity">
      <span slot="modal-title"
        ><span id="mjbotApp.kline.delete.question" data-cy="klineDeleteDialogHeading">Confirm delete operation</span></span
      >
      <div class="modal-body">
        <p id="jhi-delete-kline-heading">Are you sure you want to delete this Kline?</p>
      </div>
      <div slot="modal-footer">
        <button type="button" class="btn btn-secondary" v-on:click="closeDialog()">Cancel</button>
        <button
          type="button"
          class="btn btn-primary"
          id="jhi-confirm-delete-kline"
          data-cy="entityConfirmDeleteButton"
          v-on:click="removeKline()"
        >
          Delete
        </button>
      </div>
    </b-modal>
    <div v-show="klines && klines.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :itemsPerPage="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage" :change="loadPage(page)"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./kline.component.ts"></script>
