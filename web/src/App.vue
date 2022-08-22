<template>
  <div>
    <div>姓名：{{ name }}</div>
    <div>性别：{{ sex }}</div>
  </div>
  <router-view></router-view>
</template>

<script>
import $ from 'jquery';
import { ref } from 'vue';

export default {
  name: "App",
  setup: () => {
    let name = ref("");
    let sex = ref("");

    $.ajax({
      URL: "http://localhost:3000/pk/index/",
      type: "get",
      success: resp => {
        console.log(resp);
        name.value = resp.name;
        sex.value = resp.sex;
      }
    });

    return {
      name,
      sex
    }
  }
}
</script>

<style>
body {
  background-image: url("@/assets/logo.png");
}
</style>