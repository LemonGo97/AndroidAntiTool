<template>
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
</template>
<script>
import {watchEffect} from "vue";

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
      rows: 15
    }
  },
  watch: {
    device: {
      immediate: true,
      handler: function (newVal, oldVal) {
        console.log("===", newVal, "===", oldVal);
      }
    }
  },
  mounted() {
    setInterval(() => {
      const characters = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
      let result = '';
      for (let i = 0; i < 200; i++) {
        let randomIndex = Math.floor(Math.random() * characters.length);
        result += characters[randomIndex];
      }
      this.lines.push(result)
    }, 1000);
    watchEffect(() => {
      if (this.autoScrollToBottom && this.lines.length >= 15) {
        nextTick(() => {
          this.$refs.logInst.scrollTo({position: "bottom", silent: true});
        });
      }
    });
  },
  methods: {
    scrollTo(position, silent, autoScrollToBottom) {
      this.autoScrollToBottom = autoScrollToBottom;
      this.$refs.logInst.scrollTo({position: position, silent: silent});
    },
    clean(){
      this.lines = []
    }
  }
}
</script>
