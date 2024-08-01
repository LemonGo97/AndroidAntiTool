<template>
  <n-flex size="small" style="align-items: center;" class="p-5">
    <div class="mr-10">设备选择: </div>
    <n-select
      class="w-300"
      label-field="serial"
      value-field="serial"
      v-model:value="value"
      :options="devices" />
  </n-flex>
</template>
<script lang="ts">
import {defineComponent, ref} from 'vue'

import api from '../api'

export default defineComponent({
  setup() {
    return {
      value: ref(null),
      devices: api.devices(),
    }
  }})
</script>
