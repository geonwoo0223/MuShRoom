<template>
  <v-fade-transition>
    <v-avatar
      :color="beatIndex ? '#78909C' : '#DD2C00'"
      :style="isPlaying ? { animationDuration: animationDuration } : 'background-color: rgb(61, 61, 61) !important;'"
      class="v-avatar--metronome"
      size="50"
      rounded="circle"
      ><span style="color: white;">{{ beatIndex + 1 }}</span></v-avatar
    >
  </v-fade-transition>
</template>

<script>
export default {
  name: 'BeatIndicator',
  props:{
    isPlaying: Boolean,
    beatIndex: Number,
    bpm: Number,
  },
  computed: {
    animationDuration() {
      return `${30 / this.bpm}s`;
    }
  }
}
</script>

<style>

@keyframes metronome-example {
  from {
    transform: scale(0.9);
  }

  to {
    transform: scale(1);
  }
}

.v-avatar--metronome {

  animation-name: metronome-example;
  animation-iteration-count: infinite;
  animation-direction: alternate;
}

</style>