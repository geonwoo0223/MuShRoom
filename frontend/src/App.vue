<template>
  <v-app>
    <v-main class="overflow-x-auto">
      <router-view/>
    </v-main>
  </v-app>
</template>

<script>

import "@/assets/css/myStyle.css"

export default {
  name: 'App',
};
</script>

<style>
@font-face {
  font-family: 12롯데마트드림;
  src: local(※), url('/fonts/12롯데마트드림Light.tff') format('truetype');
}
#app {
  font-family: '12롯데마트드림Light', sans-serif;
  color: rgba(255, 255, 255, 0.733);
}

.v-btn--active.no-active::before {
  opacity: 0 !important;
}
</style>