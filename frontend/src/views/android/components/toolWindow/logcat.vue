<template>
<div>
  <n-h6 class="mb-0 pb-5 pt-5 pl-5" prefix="bar" border="1px solid light_border dark:dark_border">
    Logcat
  </n-h6>
  <n-space :wrap="false">
    <n-button-group class="pl-1 pr-1 h-full" vertical border="1px solid light_border dark:dark_border">
      <n-button size="small" @click="scrollTo('top',true, false)">
        <i class="i-fe:arrow-up"/>
      </n-button>
      <n-button size="small" @click="scrollTo('bottom',true, false)">
        <i class="i-fe:arrow-down"/>
      </n-button>
      <n-button size="small" @click="scrollTo('bottom',true, true)">
        <i class="i-fe:chevrons-down"/>
      </n-button>
      <n-button size="small" @click="clean">
        <i class="i-fe:trash-2"/>
      </n-button>
    </n-button-group>
    <n-log class="pr-5" ref="logInst" :rows="rows" :lines="lines" language="naive-log" trim/>
  </n-space>
</div>
</template>
<script>
import {createEventSource} from "@/utils/index.js";
export default {
  props: {
    device: {
      type: String,
      default: () => ""
    }
  },
  data() {
    return {
      lines: [],
      autoScrollToBottom: true,
      rows: 15,
      bufferSize: 1024,
      eventSource: null
    }
  },
  watch: {
    device: {
      immediate: true,
      handler(newVal, oldVal) {
        if (!newVal||newVal === oldVal){
          return
        }
        this.clean()
        this.unsubscribe(oldVal)
        this.subscribe(newVal)
      }
    },
    lines: {
      deep:true,
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
      this.$refs.logInst.scrollTo({position: position, silent: silent});
    },
    clean(){
      this.lines = []
    },
    subscribe(device){
      this.eventSource = createEventSource(`/logcat?serial=${device}`, (e)=>{
        if (this.lines.length > this.bufferSize){
          this.lines.shift();
        }
        this.lines.push(e.data)
      })
    },
    unsubscribe(device){
      if (!this.eventSource){
        return
      }
      this.eventSource.close();
    }
  }
}
</script>
