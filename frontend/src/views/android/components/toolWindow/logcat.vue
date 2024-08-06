<template class="h-full">
  <n-flex :wrap="false" class="h-full" style="column-gap: 5px">
    <n-button-group class="pl-1 pr-1 h-full" vertical border="1px solid light_border dark:dark_border">
      <n-tooltip>
        <template #trigger>
          <n-button size="small" @click="scrollTo('top',true, false)">
            <i class="i-fe:arrow-up"/>
          </n-button>
        </template>
        滚动到最上
      </n-tooltip>
      <n-tooltip>
        <template #trigger>
          <n-button size="small" @click="scrollTo('bottom',true, false)">
            <i class="i-fe:arrow-down"/>
          </n-button>
        </template>
        滚动到最后
      </n-tooltip>
      <n-tooltip>
        <template #trigger>
          <n-button size="small" @click="scrollTo('bottom',true, true)">
            <i class="i-fe:chevrons-down"/>
          </n-button>
        </template>
        日志追随
      </n-tooltip>
      <n-tooltip>
        <template #trigger>
          <n-button size="small" @click="clean">
            <i class="i-fe:trash-2"/>
          </n-button>
        </template>
        清除屏幕
      </n-tooltip>

    </n-button-group>
    <div class="h-full">
      <n-log class="pr-5 flex-1" ref="logInst" :hljs="hljs" :rows="rows" :lines="lines" language="console" trim/>
    </div>
  </n-flex>
</template>
<script>
import {createEventSource} from "@/utils/index.js";
import hljs from "highlight.js";

export default {
  props: {
    device: {
      type: String,
      default: () => ""
    },
    visible: {
      type: Boolean,
      default: () => false
    },
  },
  data() {
    return {
      lines: [],
      autoScrollToBottom: true,
      rows: 15,
      bufferSize: 1024,
      hljs: hljs,
      eventSource: null
    }
  },
  watch: {
    visible: {
      immediate: true,
      handler(newVal) {
        nextTick(() => {
          this.resize()
        });
      }
    },
    device: {
      immediate: true,
      handler(newVal, oldVal) {
        if (!newVal || newVal === oldVal) {
          return
        }
        this.clean()
        this.unsubscribe(oldVal)
        this.subscribe(newVal)
      }
    },
    lines: {
      deep: true,
      immediate: true,
      handler(newVal) {
        if (newVal.length > 0 && this.autoScrollToBottom) {
          nextTick(() => {
            this.$refs.logInst.scrollTo({position: "bottom", silent: true});
          });
        }
      }
    }
  },
  methods: {
    scrollTo(position, silent, autoScrollToBottom) {
      this.autoScrollToBottom = autoScrollToBottom;
      nextTick(() => {
        this.$refs.logInst.scrollTo({position: position, silent: silent});
      });
    },
    clean() {
      this.lines = []
    },
    subscribe(device) {
      this.eventSource = createEventSource(`/logcat?serial=${device}`, (e) => {
        if (this.lines.length > this.bufferSize) {
          this.lines.shift();
        }
        this.lines.push(e.data)
      })
    },
    unsubscribe(device) {
      if (!this.eventSource) {
        return
      }
      this.eventSource.close();
    },
    resize() {
      if (!this.$refs.logInst) return;
      let logNode = this.$refs.logInst.$el;
      logNode.style.height = logNode.parentElement.offsetHeight + 'px';
      if (this.autoScrollToBottom) {
        nextTick(() => {
          this.$refs.logInst.scrollTo({position: "bottom", silent: true});
        });
      }
    }
  }
}
</script>
<script setup lang="ts">
</script>
<script setup lang="ts">
</script>
<script setup lang="ts">
</script>
<script setup lang="ts">
</script>
