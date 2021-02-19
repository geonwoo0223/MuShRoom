<template>
  <div class="ar">
    <div class="ar-content pt-2 main-color">
      <div class="ar-recorder__duration pr-3">{{ recordedTime }}</div>
      <div class="ar-recorder">
        <icon-button
          class="ar-icon ar-icon__lg"
          :name="iconButtonType"
          :class="{
            'ar-icon--rec': isRecording,
            'ar-icon--pulse': isRecording,
          }"
          @click.native="startRecorder"
        />
        <icon-button
          class="ar-icon ar-icon__lg ar-recorder__stop"
          name="stop"
          @click.native="stopRecorder"
        />
      </div>
    </div>
  </div>
</template>

<script>
import IconButton from "./icon-button";
import Recorder from "@/lib/recorder";
import UploaderPropsMixin from "@/mixins/uploader-props";
import { convertTimeMMSS } from "@/lib/utils";

export default {
  mixins: [UploaderPropsMixin],
  props: {
    attempts: { type: Number },
    time: { type: Number },
    bitRate: { type: Number, default: 192 },
    sampleRate: { type: Number, default: 44100 },

    micFailed: { type: Function },
    beforeRecording: { type: Function },
    pauseRecording: { type: Function },
    afterRecording: { type: Function },
    failedUpload: { type: Function },
    beforeUpload: { type: Function },
    successfulUpload: { type: Function },
    selectRecord: { type: Function },
    crossState: { type: Boolean },
  },
  data() {
    return {
      recorder: this._initRecorder(),
      recordList: [],
      selected: {},
    };
  },
  beforeDestroy() {
    this.stopRecorder();
  },
  components: {
    IconButton,
  },
  methods: {
    startRecorder() {
      
      if (this.attempts && this.recorder.records.length >= this.attempts) {
        return;
      }

      if (!this.isRecording || (this.isRecording && this.isPause)) {
        if (this.crossState) {
          this.$store.commit("setRC", "startMetro");
        } else {
          this.recorder.start();
        }
      }
      this.$store.state.isSetRecording = true;
    },
    stopRecorder() {
      if (!this.isRecording) {
        return;
      }
      if (this.crossState) {
        this.$store.commit("setRC", "stopMetro");
      } else {
        this.recorder.stop();
        this.recordList = this.recorder.recordList();
      }
      this.$store.state.isSetRecording = false;
    },
    _initRecorder() {
      return new Recorder({
        beforeRecording: this.beforeRecording,
        afterRecording: this.afterRecording,
        pauseRecording: this.pauseRecording,
        micFailed: this.micFailed,
        bitRate: this.bitRate,
        sampleRate: this.sampleRate,
        format: this.format,
      });
    },
  },
  computed: {
    iconButtonType() {
      return this.isRecording && this.isPause
        ? "mic"
        : this.isRecording
        ? "pause"
        : "mic";
    },
    isPause() {
      return this.recorder.isPause;
    },
    isRecording() {
      return this.recorder.isRecording;
    },
    recordedTime() {
      if (this.time && this.recorder.duration >= this.time * 60) {
        this.stopRecorder();
      }
      return convertTimeMMSS(this.recorder.duration);
    },
    getRC() {
      return this.$store.getters.getRC;
    },
  },

  watch: {
    getRC(val) {
      if (val === "startRecord") {
        this.recorder.start();
      } else if (val === "stopRecord") {
        this.recorder.stop();
        this.recordList = this.recorder.recordList();
      }
    },
  },
};
</script>
<style lang="scss">
.ar {
  width: auto;
  font-family: "Roboto", sans-serif;
  border-radius: 0px;
  position: relative;
  box-sizing: content-box;

  &-content {
    display: flex;
    justify-content: center;
    align-items: center;
  }

  &-records {
    padding-top: 1px;
    overflow-y: auto;
    margin-bottom: 20px;

    &__record {
      padding: 0 10px;
      margin: 0 auto;
      line-height: 45px;
      display: flex;
      justify-content: space-between;
      border-bottom: 1px solid #e8e8e8;
      position: relative;

      &--selected {
        border: 1px solid #e8e8e8;
        border-radius: 24px;
        background-color: #ffffff;
        margin-top: -1px;
        padding: 0 34px;
      }
    }
  }

  &-recorder {
    position: relative;
    display: flex;
    flex-direction: column;
    align-items: center;

    &__duration {
      color: #aeaeae;
      font-size: 32px;
      font-weight: 500;
      margin-top: 3px;
    }

    &__stop {
      fill: white !important;
      background-color: #ff6b64 !important;
      position: absolute;
      width: 38px;
      height: 38px;
      display: none;
    }
  }
}

@import "../../scss/icons";
</style>