<template>
  <n-flex size="small" style="align-items: center;" class="p-5">
    <n-space>
    <n-input-group>
      <n-input-group-label>设备选择:</n-input-group-label>
      <n-select
        class="w-300"
        label-field="serial"
        value-field="serial"
        v-model:value="value"
        :options="devices"/>
      <n-button @click="refreshDevices">
        <i class="i-fe:refresh-cw mr-4 "/>
        刷新
      </n-button>
    </n-input-group>
    <n-input-group ml-20>
      <n-input-group-label>连接设备</n-input-group-label>
      <n-input :style="{ width: '33%' }" placeholder="IP" v-model:value="host"/>
      <n-input-number
        placeholder="端口"
        :style="{ width: '15%' }"
        v-model:value="port"
        :min="1024"
        :max="65535"
        :show-button="false"/>
      <n-button @click="connect" type="primary">
        <i class="i-fe:zap mr-4"/>
        连接
      </n-button>
    </n-input-group>
    </n-space>
  </n-flex>
</template>
<script lang="ts">
import api from '../api'

export default {
  data() {
    return {
      value: undefined,
      host: "",
      port: "",
      devices: [],
    }
  },
  mounted() {
    this.refresh();
  },
  methods: {
    refresh() {
      this.refreshDevices()
    },
    async refreshDevices() {
      this.devices = await api.devices()
    },
    async connect(){
      let formData = new FormData();
      formData.append("host", this.host)
      formData.append("port", this.port)
      await api.connect(formData)
      await this.refreshDevices()
    }
  }
}
</script>
