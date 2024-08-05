<template>
  <n-split direction="vertical" :disabled="bottomWindowVisible === ''" resize-trigger-size="38" :default-size="0.6" @drag-end="resizeLogcat">
    <template #1>
      <div>主要内容</div>
    </template>
    <template #resize-trigger>
      <n-h6 v-show="bottomWindowVisible" class="mb-0 pb-5 pt-5 pl-5 tool-window-title" prefix="bar" border="1px solid light_border dark:dark_border">
        {{ bottomWindowVisible }}
      </n-h6>
    </template>
    <template #2>
        <logcat ref="logcat" :device="device" v-show="bottomWindowVisible === 'logcat'"/>
        <shell v-show="bottomWindowVisible === 'shell'"/>
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
  methods: {
    resizeLogcat(){
      this.$refs.logcat.resize()
    }
  }
}
</script>
