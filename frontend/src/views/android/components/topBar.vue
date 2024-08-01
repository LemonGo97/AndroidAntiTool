<template>
  <n-flex size="small" style="align-items: center;" class="p-5">
    <div class="mr-10">设备选择: </div>
    <n-select
      class="w-300"
      :render-label="renderLabel"
      value-field="serial"
      v-model:value="value"
      :options="devices" />
  </n-flex>
</template>
<script lang="ts">
import {defineComponent, ref, VNodeChild} from 'vue'
import {SelectOption} from "naive-ui";

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
      renderLabel: (option: SelectOption): VNodeChild => {
        return `${option.serial} (${option.state})`
      },
      excee: (option, obj1) => {
        console.log(option, obj1)
        return `${option.serial} (${option.state})`
      }
    }
  }})
</script>
