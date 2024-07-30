<template>

  <n-flex size="small" class="p-5">
    <div>设备选择器: </div>
    <n-select
      class="w-300"
      label-field="serial"
      value-field="serial"
      v-model:value="value"
      :options="devices" />
  </n-flex>
</template>
<script lang="ts">
import { defineComponent, ref } from 'vue'

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
      ]
    }
  }})
</script>
