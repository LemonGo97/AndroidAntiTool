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

api.devices().then(res => {
  console.log(res)
})

export default defineComponent({
  setup() {
    return {
      value: ref(null),
      devices: [
        {
          serial: "XiaoMi 8",
          state: "Device"
        },
        {
          serial: "OPPO R9s",
          state: "Offline"
        }
      ],
    }
  }})
</script>
