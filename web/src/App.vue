<template>
  <div>
    <div>Bot昵称：{{ bot_name }}</div>
    <div>Bot性别：{{ bot_sex }}</div>
  </div>
  <router-view></router-view>
</template>

<script>
import $ from 'jquery';
import { ref } from 'vue';

export default {
  name: "App",
  setup: () => {
    let bot_name = ref("");
    let bot_sex = ref("");

    $.ajax({
      url: "http://127.0.0.1:3000/pk/index/",
      type: "get",
      success: resp => {
        console.log(resp);
        bot_name.value = resp.name;
        bot_sex.value = resp.sex;
      }
    });

    return {
      bot_name,
      bot_sex
    }
  }
}
</script>

<style>
body {
  background-image: url("@/assets/logo.png");
  background-size: cover;
}
</style>
