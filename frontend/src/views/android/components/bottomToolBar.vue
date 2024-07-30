<template>
  <n-flex size="small" class="p-5">
    <div class="h-40 w-50 p-5 rounded-5 hover:bg-primary text-align-center icon"
         v-for="item in items"
         :key="item" @click="item.action">
      <i :class="`${item.icon}?mask`" class="text-30" style="color: #6c707e" />
    </div>
  </n-flex>
</template>
<script setup>
const items = [
  {
    name: "logcat",
    icon: "i-me:logcat",
    action: logcat,
  },
  {
    name: "shell",
    icon: "i-me:shell",
    action: shell
  },
]

function logcat(){
  console.log("logcat")
}

function shell(){
  console.log("shell")
}
</script>
<style scoped>
.icon:hover {
  background-color: #dbdcde;
}
</style>
