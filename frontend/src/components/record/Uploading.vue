<template>
  <v-overlay
    :value="uploading"
    absolute
    opacity="0.3"
    color="white">
    <v-card elevation="0" color="#00ff0000" class="text-center">
        <v-card-title class="d-flex justify-center" style="font-size: 30px;">
            파일 업로드 중
        </v-card-title>
        <div class="d-inline-block" v-for="gif in gifs" :key="gif">
          <v-img :src="gif"
          width="100"
          style="opacity: 1">
          </v-img>
        </div>
    </v-card>
  </v-overlay>
</template>

<script>
export default {
    props: ['uploading'],
    data() {
      return {
        gifs: [
          require('@/assets/uploading/jsrgif.gif'),
          require('@/assets/uploading/kywgif.gif'),
          require('@/assets/uploading/sghgif.gif'),
          require('@/assets/uploading/lgwgif.gif'),
          require('@/assets/uploading/lbhgif.gif'),
          require('@/assets/uploading/jjhgif.gif'),
        ]
      }
    }
}
</script>

<style>

</style>