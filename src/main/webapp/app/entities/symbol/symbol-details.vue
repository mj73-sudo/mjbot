<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="symbol">
        <h2 class="jh-entity-heading" data-cy="symbolDetailsHeading"><span>Symbol</span> {{ symbol.id }}</h2>
        <dl class="row jh-entity-details">
          <dt>
            <span>Symbol</span>
          </dt>
          <dd>
            <span>{{ symbol.symbol }}</span>
          </dd>
          <dt>
            <span>Name</span>
          </dt>
          <dd>
            <span>{{ symbol.name }}</span>
          </dd>
          <dt>
            <span>Base Currency</span>
          </dt>
          <dd>
            <span>{{ symbol.baseCurrency }}</span>
          </dd>
          <dt>
            <span>Quote Currency</span>
          </dt>
          <dd>
            <span>{{ symbol.quoteCurrency }}</span>
          </dd>
          <dt>
            <span>Fee Currency</span>
          </dt>
          <dd>
            <span>{{ symbol.feeCurrency }}</span>
          </dd>
          <dt>
            <span>Market</span>
          </dt>
          <dd>
            <span>{{ symbol.market }}</span>
          </dd>
          <dt>
            <span>Base Min Size</span>
          </dt>
          <dd>
            <span>{{ symbol.baseMinSize }}</span>
          </dd>
          <dt>
            <span>Quote Min Size</span>
          </dt>
          <dd>
            <span>{{ symbol.quoteMinSize }}</span>
          </dd>
          <dt>
            <span>Base Max Size</span>
          </dt>
          <dd>
            <span>{{ symbol.baseMaxSize }}</span>
          </dd>
          <dt>
            <span>Quote Max Size</span>
          </dt>
          <dd>
            <span>{{ symbol.quoteMaxSize }}</span>
          </dd>
          <dt>
            <span>Base Increment</span>
          </dt>
          <dd>
            <span>{{ symbol.baseIncrement }}</span>
          </dd>
          <dt>
            <span>Quote Increment</span>
          </dt>
          <dd>
            <span>{{ symbol.quoteIncrement }}</span>
          </dd>
          <dt>
            <span>Price Increment</span>
          </dt>
          <dd>
            <span>{{ symbol.priceIncrement }}</span>
          </dd>
          <dt>
            <span>Price Limit Rate</span>
          </dt>
          <dd>
            <span>{{ symbol.priceLimitRate }}</span>
          </dd>
          <dt>
            <span>Min Funds</span>
          </dt>
          <dd>
            <span>{{ symbol.minFunds }}</span>
          </dd>
          <dt>
            <span>Is Margin Enabled</span>
          </dt>
          <dd>
            <span>{{ symbol.isMarginEnabled }}</span>
          </dd>
          <dt>
            <span>Enable Trading</span>
          </dt>
          <dd>
            <span>{{ symbol.enableTrading }}</span>
          </dd>
          <dt>
            <span>Active</span>
          </dt>
          <dd>
            <span>{{ symbol.active }}</span>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span> Back</span>
        </button>
        <router-link v-if="symbol.id" :to="{ name: 'SymbolEdit', params: { symbolId: symbol.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span> Edit</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./symbol-details.component.ts"></script>
