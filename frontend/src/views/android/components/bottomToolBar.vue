<template>
  <n-flex size="small" class="p-5">
    <n-tooltip trigger="hover" v-for="item in items" :key="item">
      <template #trigger>
        <div class="h-40 w-50 p-5 rounded-5 text-align-center icon hover:bg-[#d5d6d8] dark:hover:bg-[#404143]"
             @click="item.action">
          <i :class="`${item.icon}?mask`" class="text-30 c-[#6c707e] dark:c-[#c7cad0]"/>
        </div>
      </template>
      {{ item.name }}
    </n-tooltip>
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

</style>
