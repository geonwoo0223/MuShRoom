<template>
  <div style="background-color: #2d2d2d">
    <div style="position: relative">
      <div v-show="timeline" :id="'container' + idx"></div>
      <vue-wave-surfer
        ref="waveform"
        :src="url"
        :options="options"
      ></vue-wave-surfer>
    </div>
    <div
      v-if="!isReady"
      style="position: relative; top: -45px; height: 0"
      class="text-center py-2"
    >
      <v-progress-circular indeterminate></v-progress-circular>
    </div>
  </div>
</template>

<script>
import Timeline from "wavesurfer.js/dist/plugin/wavesurfer.timeline";
export default {
  props: [
    "url",
    "height",
    "mouse",
    "audioCtx",
    "timeline",
    "idx",
    "full",
    "color",
  ],
  data() {
    return {
      options: {
        normalize: false,
        cursorColor: "#00ff0000",
        progressColor: "#FED134", //color of waveform before the cursor
        waveColor: this.color, //color of waveform after the cursor
        scrollParent: this.full,
        height: this.height,
        interact: this.mouse,
        audioContext: this.audioCtx,
        plugins: [
          Timeline.create({
            container: "#container" + this.idx,
            secondaryFontColor: "#ffffffbb",
          }),
        ],
      },
      isReady: false,
    };
  },
  mounted() {
    this.player.on("ready", () => {
      this.isReady = true;
      this.$emit("setDuration", this.player.getDuration());
      this.$emit("isReady");
    });
    this.player.on("seek", () => {
      this.$emit("setTime", this.player.getCurrentTime().toFixed(2));
    });
  },
  computed: {
    player() {
      return this.$refs.waveform.waveSurfer;
    },
  },
  methods: {
    play() {
      this.player.playPause();
    },
    setTime(sec) {
      this.player.drawer.progress(sec / this.player.getDuration());
    },
  },
};
</script>

<style>
.hide {
  max-height: 0px;
}
</style>
