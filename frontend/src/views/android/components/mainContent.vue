<template>
  <n-split direction="vertical" :default-size="0.6">
    <template #1>
      <div>主要内容</div>
    </template>
    <template #2>
      <div class="h-full">
        <logcat :device="device" v-show="true||bottomWindowVisible === 'logcat'"/>
        <shell v-show="bottomWindowVisible === 'shell'"/>
      </div>
    </template>
  </n-split>
</template>
<script>
import Logcat from './toolWindow/logcat.vue'
import Shell from './toolWindow/shell.vue'

export default {
  props: {
    device: {
      type: String,
      default: () => ""
    },
    bottomWindowVisible: {
      type: String,
      default: () => ""
    },
    topWindowVisible: {
      type: Array,
      default: () => []
    },
    leftWindowVisible: {
      type: Array,
      default: () => []
    },
    rightWindowVisible: {
      type: Array,
      default: () => []
    }
  },
  components: {
    Logcat,
    Shell
  },
}
</script>
