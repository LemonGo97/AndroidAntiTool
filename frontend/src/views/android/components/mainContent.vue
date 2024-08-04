<template>
<div class="h-full p-5 flex flex-col">
  <div class="h-full">主要内容</div>
  <div class="bottom flex-1 p-5" v-show="bottomWindowVisible">
    <logcat :device="device" v-show="bottomWindowVisible === 'logcat'"/>
    <shell v-show="bottomWindowVisible === 'shell'"/>
  </div>
</div>
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
