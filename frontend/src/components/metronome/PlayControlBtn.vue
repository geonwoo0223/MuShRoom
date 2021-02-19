<template>
  <div>
    <v-btn
      icon
      dark
      plain
      v-if="isPlaying"
      @click="stop"
      :disabled="recordStartState === 'startMetro' || recordStartState === 'startRecord' ? true : false"
    >
      <v-icon size="30px">mdi-stop</v-icon>
    </v-btn>
    <v-btn
      icon
      dark
      plain
      :disabled="isSetPlaying || isAllPlaying"
      v-else
      @click="start"
    >
      <v-icon size="30px">mdi-play</v-icon>
    </v-btn>
  </div>
</template>

<script>
import { mapState } from 'vuex'

export default {
  name: 'MetronomeBtn',
  props: {
    isPlaying: Boolean,
  },
  methods: {
    start() {
      this.$store.state.isSetMetronome = true;
      this.$emit('start');
    },
    stop() {
      this.$store.state.isSetMetronome = false;
      this.$emit('stop');
    },
  },
  computed: {
    ...mapState(['recordStartState',"isSetPlaying", "isSetRecording", "isAllPlaying"])
  }
}
</script>

<style>

</style>