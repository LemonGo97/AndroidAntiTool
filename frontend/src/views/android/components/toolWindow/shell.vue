<template class="h-full">
  <n-flex :wrap="false" class="h-full" style="column-gap: 5px">
    <n-button-group class="pl-1 pr-1 h-full" vertical border="1px solid light_border dark:dark_border">
      <n-button size="small">
        <i class="i-fe:trash-2"/>
      </n-button>
    </n-button-group>
    <div id="terminal" ref="terminal" class="h-full">
    </div>
  </n-flex>
</template>
<script>
import { Terminal } from '@xterm/xterm';
import { FitAddon } from '@xterm/addon-fit';
import {createWebSocket} from "@/utils/index.js";
import "@xterm/xterm/css/xterm.css"
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
      terminal: undefined,
      terminalAddon: undefined,
      websocket: undefined,
    }
  },
  watch: {
    device: {
      immediate: true,
      handler(newVal, oldVal) {
        if (!newVal || newVal === oldVal) {
          return
        }
        this.closeWebSocket()
        this.setupWebSocket(newVal)
      }
    },
  },
  mounted() {
    this.setupTerminal()
  },
  methods: {
    setupWebSocket(device){
      if(!device) return;
      this.websocket = createWebSocket("/shell?device=" + device)
      this.websocket.onmessage = (e) => {
        this.terminal.write(e.data)
      }
    },
    setupTerminal(){
      this.terminal = new Terminal({
        rendererType: "canvas",
        disableStdin: false,
        cursorBlink: true,
      })
      this.terminalAddon = new FitAddon()
      this.terminal.loadAddon(this.terminalAddon)
      this.terminal.open(this.$refs.terminal)
      this.terminalAddon.fit()

      window.addEventListener("resize", resizeScreen)
      function resizeScreen() {
        try {
          this.terminalAddon.fit()
        } catch (e) {
          console.log("e", e.message)
        }
      }
      this.setupWebSocket(this.device);
      this.terminal.onData(input => {
        if (!this.websocket || this.websocket.readyState !== WebSocket.OPEN) {
          return
        }
        this.websocket.send(input)
      })
    },
    closeWebSocket(){
      if (!this.websocket || this.websocket.readyState !== WebSocket.CLOSED) {
        return
      }
      this.websocket.close()
    }
  }
}
</script>

