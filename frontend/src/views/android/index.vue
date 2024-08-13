<template>

  <CommonPage show-footer no-padding>
    <div class="android-main overflow-hidden">
      <div class="android-header p-5 bg-[#f7f8faff] dark:bg-[#26282a]"
           border="1px solid light_border dark:dark_border">
        <top-bar @onDeviceSelected="onDeviceSelected"/>
      </div>
      <div class="android-left p-5 bg-[#f7f8faff] dark:bg-[#26282a]"
           border="1px solid light_border dark:dark_border">
        <left-tool-bar :device="device"/>
      </div>
      <div class="android-container">
        <div class="android-main-content bg-[#ffffff] dark:bg-[#1c1d1f]">
          <main-content :device="device" :bottomWindowVisible="bottomWindowVisible"/>
        </div>
        <div class="android-footer bg-[#f7f8faff] dark:bg-[#26282a]"
             border="1px solid light_border dark:dark_border">
          <bottom-tool-bar @onBottomToolBarClick="onBottomToolBarClick"/>
        </div>
      </div>
<!--      <div class="android-right p-5 bg-[#f7f8faff] dark:bg-[#26282a]"-->
<!--           border="1px solid light_border dark:dark_border">-->
<!--        <right-tool-bar/>-->
<!--      </div>-->
    </div>
  </CommonPage>

</template>
<script lang="ts">
import {CommonPage} from "@/components";
import BottomToolBar from "./components/bottomToolBar.vue";
import TopBar from "./components/topBar.vue";
import LeftToolBar from "./components/leftToolBar.vue";
import RightToolBar from "./components/rightToolBar.vue";
import MainContent from "./components/mainContent.vue";

export default {
  components: {
    CommonPage,
    TopBar,
    BottomToolBar,
    LeftToolBar,
    RightToolBar,
    MainContent,
  },
  data() {
    return {
      device: "",
      bottomWindowVisible: "",
    }
  },
  methods: {
    onDeviceSelected(value){
      this.device = value
    },
    onBottomToolBarClick(value){
      this.bottomWindowVisible = value
    }
  }
}
</script>
<style lang="scss" scoped>
.android-main {
  display: grid;
  grid-template-rows: 50px 1fr;
  grid-template-columns: 422px 1fr; /* 你可以调整这个宽度 */
  height: 100%;

  .android-header {
    grid-column: 1 / -1;
  }

  .android-left {
    grid-row: 2;
  }

  .android-container {
    grid-row: 2;

    display: grid;
    grid-template-rows:  1fr 50px;
  }

  //.android-right {
  //  grid-row: 2;
  //}
}
</style>
