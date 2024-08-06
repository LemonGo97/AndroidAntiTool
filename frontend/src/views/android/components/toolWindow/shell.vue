<template class="h-full">
  <n-flex :wrap="false" class="h-full" style="column-gap: 5px">
    <n-button-group class="pl-1 pr-1 h-full" vertical border="1px solid light_border dark:dark_border">
      <n-tooltip>
        <template #trigger>
          <n-button size="small" @click="reconnect">
            <i class="i-fe:repeat"/>
          </n-button>
        </template>
        重新连接
      </n-tooltip>
      <n-tooltip>
        <template #trigger>
          <n-button size="small" @click="closeWebSocket">
            <i class="i-fe:square"/>
          </n-button>
        </template>
        断开连接
      </n-tooltip>
      <n-tooltip>
        <template #trigger>
          <n-button size="small">
            <i class="i-fe:arrow-up" @click="scrollTerminalToTop"/>
          </n-button>
        </template>
        滚动到最上
      </n-tooltip>
      <n-tooltip>
        <template #trigger>
          <n-button size="small">
            <i class="i-fe:arrow-down" @click="scrollTerminalToBottom"/>
          </n-button>
        </template>
        滚动到最后
      </n-tooltip>
      <n-tooltip>
        <template #trigger>
          <n-button size="small" @click="clearTerminalScreen">
            <i class="i-fe:trash-2"/>
          </n-button>
        </template>
        清除屏幕
      </n-tooltip>
    </n-button-group>
    <div id="terminal" ref="terminal" class="h-full w-full">
    </div>
  </n-flex>
</template>
<script>
import {Terminal} from '@xterm/xterm';
import {FitAddon} from '@xterm/addon-fit';
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
    visible: {
      immediate: true,
      handler(newVal) {
        nextTick(() => {
          this.resize()
        });
      }
    },
  },
  mounted() {
    this.setupTerminal()
  },
  beforeUnmount() {
    console.log("关闭websocket")
    this.closeWebSocket()
  },
  methods: {
    setupWebSocket(device) {
      if (!device) return;
      this.websocket = createWebSocket("/shell?device=" + device)
      this.websocket.onmessage = (e) => {
        this.terminal.write(e.data)
      }
      this.websocket.onclose = (e) => {
        console.log("websocket closed")
        this.resetTerminalScreen()
        this.terminal.write("this connection closed...")
      }
    },
    setupTerminal() {
      this.terminal = new Terminal({
        rendererType: "canvas",
        disableStdin: false,
        cursorBlink: true,
      })
      this.terminalAddon = new FitAddon()
      this.terminal.loadAddon(this.terminalAddon)
      this.terminal.open(this.$refs.terminal)
      this.terminalAddon.fit()

      window.addEventListener("resize", () => this.resize())
      this.setupWebSocket(this.device);
      this.terminal.onData(input => {
        if (!this.websocket || this.websocket.readyState !== WebSocket.OPEN) {
          return
        }
        this.websocket.send(input)
      })
    },
    closeWebSocket() {
      this.resetTerminalScreen()
      if (!this.websocket) {
        return
      }
      this.websocket.close()
      this.resetTerminalScreen()
    },
    clearTerminalScreen() {
      if (!this.terminal) return
      this.terminal.clear()
    },
    resetTerminalScreen() {
      if (!this.terminal) return
      this.terminal.reset()
    },
    scrollTerminalToTop() {
      this.terminal.scrollToTop();
    },
    scrollTerminalToBottom() {
      this.terminal.scrollToBottom();
    },
    reconnect() {
      if (this.websocket && this.websocket.readyState !== WebSocket.CLOSED) {
        this.closeWebSocket()
      }
      if (!this.device) {
        $message.warning('请选择待操作的设备！')
        return
      }
      this.setupWebSocket(this.device);
    },
    resize() {
      if (!this.terminal) return;
      this.terminalAddon.fit();
      this.terminal.scrollToBottom();
    }
  }
}
</script>

