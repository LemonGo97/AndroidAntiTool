<template>

  <CommonPage show-footer no-padding>
    <div class="android-main">
      <div class="android-header p-5"><top-bar/></div>
      <div class="android-left p-5"><left-tool-bar/></div>
      <div class="android-container">
        <div class="android-main-content p-5"><main-content/></div>
        <div class="android-footer"><bottom-tool-bar/></div>
      </div>
      <div class="android-right p-5"><right-tool-bar/></div>
    </div>
  </CommonPage>

</template>
<script setup lang="ts">
import api from './api'
import {CommonPage} from "@/components";
import BottomToolBar from "./components/bottomToolBar.vue";
import TopBar from "./components/topBar.vue";
import LeftToolBar from "./components/leftToolBar.vue";
import RightToolBar from "./components/rightToolBar.vue";
import MainContent from "@/views/android/components/mainContent.vue";
</script>
<style scoped>
.android-main {
  display: grid;
  grid-template-rows: 12.5vh 1fr;
  grid-template-columns: 2.5vw 1fr 2.5vw; /* 你可以调整这个宽度 */
  height: 100%;
  box-sizing: border-box;

  .android-header {
    grid-column: 1 / -1;
    background-color: #f1f1f1;
  }

  .android-left {
    grid-row: 2;
    background-color: #d1d1d1;
  }

  .android-container {
    grid-row: 2;
    background-color: #e1e1e1;

    display: grid;
    grid-template-rows:  1fr 12vh;

    .android-footer {
      background-color: #d1d1d1;
    }
  }

  .android-right {
    grid-row: 2;
    background-color: #d1d1d1;
  }
}





</style>
