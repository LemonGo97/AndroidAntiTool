<template>
  <div>logcat
    <n-button-group>
      <n-button @click="scrollTo('top',true, false)">
        ⬆️
      </n-button>
      <n-button @click="scrollTo('bottom',true, false)">
        ⬇️
      </n-button>
      <n-button @click="scrollTo('bottom',true, true)">
        scroll
      </n-button>
    </n-button-group>
    <n-log ref="logInst" :lines="lines" language="naive-log" trim/>
  </div>
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
      autoScrollToBottom: true
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
      this.lines.push(Math.random().toString(16))
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
    }
  }
}
</script>
