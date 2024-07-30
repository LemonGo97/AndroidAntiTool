<template>

  <CommonPage show-footer no-padding>
    <div class="android-main overflow-hidden">
      <div class="android-header android-border p-5"><top-bar/></div>
      <div class="android-left android-border p-5"><left-tool-bar/></div>
      <div class="android-container">
        <div class="android-main-content android-border p-5"><main-content/></div>
        <div class="android-footer android-border"><bottom-tool-bar/></div>
      </div>
      <div class="android-right android-border p-5"><right-tool-bar/></div>
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
  grid-template-rows: 50px 1fr;
  grid-template-columns: 50px 1fr 50px; /* 你可以调整这个宽度 */
  height: 100%;

  .android-border {
    border-style: solid;
    border-color: #ebecf0;
    border-width: 1px;
  }

  .android-header {
    grid-column: 1 / -1;
    background-color: #f7f8fa;
  }

  .android-left {
    grid-row: 2;
    background-color: #f7f8fa;
  }

  .android-container {
    grid-row: 2;
    background-color: #f7f8fa;

    display: grid;
    grid-template-rows:  1fr 50px;

    .android-main-content {
      background-color: #ffffff;
    }

    .android-footer {
      background-color: #f7f8fa;
    }
  }

  .android-right {
    grid-row: 2;
    background-color: #f7f8fa;
  }
}





</style>
