<template>
  <div id="player" style="border-radius: 5px">
    <div class="d-flex file-title">
      <div class="wrap1-long">
        <div class="wrap2">
          <div
            class="ml-2 medium text-color"
            :class="music.fileName.length > 50 ? 'wrap3' : 'wrap4'"
          >
            {{ music.fileName }}
          </div>
        </div>
      </div>

      <v-spacer></v-spacer>
      <v-btn
        icon
        dark
        plain
        @click="sendDelete()"
        v-if="status === 'Master'"
        :disabled="!isReady"
      >
        <v-icon>mdi-close</v-icon>
      </v-btn>
    </div>
    <div class="d-flex align-center border">
      <div>
        <v-btn
          v-if="this.state == 'paused' || this.state == 'stopped'"
          icon
          dark
          plain
          class="mt-4"
          :disabled="
            player == null ||
            isAllPlaying ||
            isSetRecording ||
            isSetPlaying ||
            isSetMetronome
          "
          v-on:click="start()"
        >
          <v-icon>mdi-play</v-icon>
        </v-btn>
        <v-btn
          v-else
          icon
          plain
          class="mt-4"
          dark
          :disabled="player == null"
          v-on:click="pause()"
        >
          <v-icon>mdi-pause</v-icon>
        </v-btn>
        <v-btn
          icon
          plain
          dark
          class="mt-4"
          :disabled="
            isAllPlaying ||
            isSetRecording ||
            isSetMetronome ||
            (isSetIdx != -1 && isSetIdx != n)
          "
          v-on:click="stop()"
        >
          <v-icon>mdi-stop</v-icon>
        </v-btn>
      </div>

      <div class="" style="flex: 10">
        <Waveform
          :class="status === 'Master' ? '' : 'mr-4'"
          :url="music.url"
          height="64"
          mouse="true"
          timeline="true"
          :idx="n"
          full="false"
          color="#00A2FF"
          @setTime="setTime"
          @isReady="nowReady"
          @setDuration="setDuration"
          ref="waveform"
        ></Waveform>
      </div>

      <!-- 이 부분부터 ReadOnly -->
      <div class="ml-3 mr-3" v-if="status === 'Master'">
        <!-- dropdown button -->
        <v-btn icon dark plain class="mt-4" v-on:click="toggleDropdown()">
          <v-icon v-if="isShow == 0">mdi-chevron-down</v-icon>
          <v-icon v-else>mdi-chevron-up</v-icon>
        </v-btn>
      </div>
    </div>

    <div>
      <v-sheet
        style="margin: 0px 61px 0px 72.5px; background-color: #00ff0000"
        height="220px"
        :hidden="isShow == 0"
      >
        <v-card
          class="d-flex"
          height="100%"
          style="background-color: #00ff0000; border-radius: 0px"
        >
          <div
            class="px-3 pt-3 d-flex justify-space-around"
            style="min-width: 260px"
          >
            <div class="d-flex-column justify-center" style="width: 65px">
              <p style="font-size: 10px; text-align: center">Volume</p>
              <v-slider
                vertical
                color="#ffffffbb"
                track-color="grey darken-2"
                v-model="music.volume.value"
                min="-80"
                max="-5"
                step="0.01"
                @change="changeVolume(music.volume.value)"
              ></v-slider>
            </div>
            <div class="d-flex-column justify-center" style="width: 65px">
              <p style="font-size: 10px; text-align: center">Distortion</p>
              <v-slider
                vertical
                color="#ffffffbb"
                track-color="grey darken-2"
                v-model="music.distortion.value"
                min="0"
                max="5"
                step="0.01"
                @change="changeDistortion(music.distortion.value)"
              ></v-slider>
            </div>
            <div class="d-flex-column justify-center" style="width: 65px">
              <p style="font-size: 10px; text-align: center">Gain</p>
              <v-slider
                vertical
                color="#ffffffbb"
                track-color="grey darken-2"
                v-model="music.gain.value"
                min="0"
                max="10"
                step="0.01"
                @change="changeGain(music.gain.value)"
              ></v-slider>
            </div>
            <div class="d-flex-column justify-center" style="width: 65px">
              <p style="font-size: 10px; text-align: center">Reverb</p>
              <v-slider
                vertical
                color="#ffffffbb"
                track-color="grey darken-2"
                v-model="music.reverb.value"
                min="0"
                max="1"
                step="0.01"
                @change="changeReverb(music.reverb.value)"
              ></v-slider>
            </div>
          </div>

          <v-divider
            vertical
            style="background-color: rgba(255, 255, 255, 0.733)"
          ></v-divider>

          <div>
            <div class="pa-3" style="width: 240px; height: 33%">
              <v-tooltip max-width="180px" bottom>
                <template v-slot:activator="{ on, attrs }">
                  <v-icon
                    color="#ffffffbb"
                    style="position: absolute; top: 12px; right: 12px"
                    v-bind="attrs"
                    v-on="on"
                    size="14px"
                    >mdi-help-circle-outline</v-icon
                  >
                </template>
                <span style="font-size: 10px"
                  >시작시간(start)과 끝시간(end)를 설정해 그 사이를 반복합니다
                </span></v-tooltip
              >
              <p class="d-flex" style="font-size: 10px">
                Loop
                <v-btn icon plain dark small class="mt-n2" @click="toggleLoop">
                  <v-icon v-if="music.loop.loop" color="red" small
                    >mdi-repeat</v-icon
                  >
                  <v-icon v-else small>mdi-repeat-off</v-icon>
                </v-btn>
              </p>
              <div style="max-height: 38px" class="d-flex justify-space-around">
                <div class="d-flex align-center">
                  <p style="font-size: 10px">Start</p>
                  <v-text-field
                    class="ml-2"
                    type="number"
                    dark
                    style="width: 60px !important"
                    min="0"
                    :max="duration"
                    v-model.number="music.loop.loopStart"
                    v-on:change="setLoopTime()"
                  ></v-text-field>
                </div>
                <div class="d-flex align-center">
                  <p style="font-size: 10px">End</p>
                  <v-text-field
                    class="ml-2"
                    type="number"
                    dark
                    style="width: 60px !important"
                    min="0"
                    :max="duration"
                    v-model.number="music.loop.loopEnd"
                    v-on:change="setLoopTime()"
                  ></v-text-field>
                </div>
              </div>
            </div>
            <v-divider
              style="background-color: rgba(255, 255, 255, 0.733)"
            ></v-divider>
            <div class="pa-3" style="width: 240px; height: 33%">
              <v-tooltip max-width="180px" bottom>
                <template v-slot:activator="{ on, attrs }">
                  <v-icon
                    color="#ffffffbb"
                    style="position: absolute; top: 83px; right: 12px"
                    v-bind="attrs"
                    v-on="on"
                    size="14px"
                    >mdi-help-circle-outline</v-icon
                  >
                </template>
                <span style="font-size: 10px"
                  >설정 시간(초) 부터 재생됩니다.</span
                ></v-tooltip
              >
              <p style="font-size: 10px">Start Point</p>
              <div style="max-height: 48px" class="pa-3 d-flex">
                <div class="d-flex align-center">
                  <p style="font-size: 10px">Time</p>
                  <v-text-field
                    class="ml-2"
                    dark
                    type="number"
                    style="width: 80px !important"
                    min="0"
                    v-model="music.delay.offset"
                    @change="updateMusicOption('offset')"
                  ></v-text-field>
                </div>
              </div>
            </div>
            <v-divider
              style="background-color: rgba(255, 255, 255, 0.733)"
            ></v-divider>
            <div class="pa-3" style="width: 240px; height: 33%">
              <v-tooltip max-width="180px" bottom>
                <template v-slot:activator="{ on, attrs }">
                  <v-icon
                    color="#ffffffbb"
                    style="position: absolute; top: 157px; right: 12px"
                    v-bind="attrs"
                    v-on="on"
                    size="14px"
                    >mdi-help-circle-outline</v-icon
                  >
                </template>
                <span style="font-size: 10px"
                  >설정 시간(초) 후 재생됩니다.</span
                >
              </v-tooltip>
              <p style="font-size: 10px">Delay</p>
              <div style="max-height: 44px" class="pa-3 d-flex">
                <div class="d-flex align-center">
                  <p style="font-size: 10px">Time</p>
                  <v-text-field
                    class="ml-2"
                    type="number"
                    dark
                    style="width: 80px !important"
                    min="0"
                    v-model.number="music.delay.delay"
                    v-on:change="updateMusicOption('delay')"
                  ></v-text-field>
                </div>
              </div>
            </div>
          </div>
        </v-card>
      </v-sheet>
    </div>
    <Alert
      :showAlert="showAlert"
      :title="alertTitle"
      :content="alertContent"
      @close="closeAlert"
    />
  </div>
</template>

<script>
import * as Tone from "tone";
import Waveform from "./Waveform.vue";
import { mapState } from "vuex";
import Alert from "@/components/common/Alert.vue";

export default {
  name: "Player",
  props: {
    music: Object,
    n: Number,
  },
  components: {
    Waveform,
    Alert,
  },
  data() {
    return {
      player: null,
      state: "stopped",
      isShow: 0,
      isExist: false,
      currentTime: 0,
      isReady: 0,
      duration: null,
      startTime: 0,
      checkFinish: false,
      alertTitle: "제목",
      alertContent: "내용",
      showAlert: false,
    };
  },
  created() {
    this.constructor();
  },
  computed: {
    ...mapState([
      "isSetIdx",
      "isSetPlaying",
      "isAllPlaying",
      "isSetRecording",
      "isSetMetronome",
    ]),
  },
  watch: {
    music: function () {
      if (this.player) this.player.dispose();
      this.constructor();
    },
  },
  methods: {
    constructor() {
      const player = new Tone.Player(this.music.url, () => {
        this.player = player;
        this.player.onstop = () => {
          if (this.state == "stopped" || this.state == "paused") {
            //Tone.Transport.stop();
          } else {
            // 재생 시간이 최대 시간 이후일 때 (정지)
            if (
              player.buffer.duration + player.context.lookAhead <
              Tone.Transport.seconds
            ) {
              //this.stop();
            }
          }

          if (!this.checkFinish) {
            this.checkFinish = true;
            this.$emit("finished");
          }

          this.$store.state.isSetPlaying = false;
          this.$store.state.isSetIdx = -1;

          this.$refs.waveform.setTime(0);
          this.currentTime = 0;
          this.startTime = 0;
          this.state = "stopped";
          this.player.unsync();
        };
        player.volume.value = this.music.volume.value;
        player.loop = this.music.loop.loop;
        player.loopStart = this.music.loop.loopStart;
        player.loopEnd = this.music.loop.loopEnd;
        const gain = new Tone.Gain(0).toDestination();
        this.music.gain.object = gain;
        this.player.connect(gain);

        const distortion = new Tone.Distortion(0).toDestination();
        distortion.distortion = this.music.distortion.value;
        this.music.distortion.object = distortion;
        this.player.connect(distortion);

        const reverb = new Tone.Reverb(1.5).toDestination();
        reverb.wet.value = this.music.reverb.value;
        this.music.reverb.object = reverb;
        this.player.connect(reverb);
      }).toDestination();

      this.status = this.$store.state.status;
    },
    start() {
      // 자신 이외의 모든 Player stop
      this.$emit("release", this.n);

      this.$store.state.isSetPlaying = true;
      this.$store.state.isSetIdx = this.n;

      Tone.start(); // ...start()를 실행하기 위한 사전 작업
      this.state = "started"; // delay를 줄 경우, player.state로 즉시 받아오면 stopped가 넘어옴
      this.player.unsync();
      Tone.start(); // ...start()를 실행하기 위한 사전 작업
      Tone.Transport.stop();
      Tone.Transport.cancel(); // clean objects

      this.player
        .sync()
        .start(
          this.music.delay.delay,
          this.music.delay.offset +
            (this.music.loop.loop ? this.music.loop.loopStart : 0) +
            this.startTime
        );

      // now: Transport 생성 후 현재 시간
      // offset: 시작할 오프셋 위치. 초 단위
      // 박자로 시간과 오프셋을 맞추고싶다면: time or offset + Tone.Time(박자).toSeconds();
      Tone.Transport.start();
      this.moveProgressBar();
    },
    pause() {
      this.$store.state.isSetPlaying = false;
      this.$store.state.isSetIdx = -1;

      this.state = "paused";
      this.player.unsync();
      Tone.Transport.stop();
    },
    stop() {
      this.$store.state.isSetPlaying = false;
      this.$store.state.isSetIdx = -1;

      this.$refs.waveform.setTime(0);
      this.currentTime = 0;
      this.startTime = 0;
      this.state = "stopped";
      this.player.unsync();
      Tone.Transport.stop();
    },
    changeDistortion(value) {
      this.music.distortion.object.distortion = value;
      this.updateMusicOption();
    },
    changeVolume(value) {
      this.player.volume.value = value;
      this.updateMusicOption();
    },
    changeGain(value) {
      this.music.gain.object.gain.value = value;
      this.updateMusicOption();
    },
    changeReverb(value) {
      this.music.reverb.object.wet.value = value;
      this.updateMusicOption();
    },
    updateMusicOption(point) {
      if (point == "offset") {
        if (this.music.delay.offset == "") {
          this.music.delay.offset = 0;
        } else if (this.music.delay.offset < 0) {
          this.music.delay.offset = 0;
          this.showAlert = true;
          this.alertTitle = "스타트포인트 에러";
          this.alertContent = "0 보다 작은 값은 입력할 수 없습니다.";
        } else if (this.music.delay.offset >= this.duration) {
          this.showAlert = true;
          this.music.delay.offset = 0;
          this.alertTitle = "스타트포인트 에러";
          this.alertContent =
            "총 길이보다 크거나 같은 값은 입력할 수 없습니다.";
        }
      } else {
        if (this.music.delay.delay == "") {
          this.music.delay.delay = 0;
        } else if (this.music.delay.delay < 0) {
          this.music.delay.delay = 0;
          this.showAlert = true;
          this.alertTitle = "딜레이 에러";
          this.alertContent = "0 보다 작은 값은 입력할 수 없습니다.";
        }
      }

      this.$emit("updateMusicOption", this.n);
    },

    updateAll() {
      this.music.distortion.object.distortion = this.music.distortion.value;
      this.player.volume.value = this.music.volume.value;
      this.music.gain.object.gain.value = this.music.gain.value;
      this.music.reverb.object.wet.value = this.music.reverb.value;
      this.player.loop = this.music.loop.loop;
      this.player.loopStart = this.music.loop.loopStart;
      this.player.loopEnd = this.music.loop.loopEnd;
    },
    toggleDropdown() {
      this.isShow ^= 1;
    },
    toggleLoop() {
      this.music.loop.loop = !this.music.loop.loop;
      this.player.loop = !this.player.loop;
      this.updateMusicOption();
    },
    setLoopTime() {
      if (this.music.loop.loopStart == "") {
        this.music.loop.loopStart = 0;
      } else if (this.music.loop.loopStart < 0) {
        this.music.loop.loopStart = 0;
        this.showAlert = true;
        this.alertTitle = "루프 에러";
        this.alertContent = "0 보다 작은 값은 입력할 수 없습니다.";
      } else if (this.music.loop.loopStart >= this.duration) {
        this.music.loop.loopStart = 0;
        this.showAlert = true;
        this.alertTitle = "루프 에러";
        this.alertContent = "총 길이보다 크거나 같은 값은 입력할 수 없습니다.";
      }

      if (this.music.loop.loopEnd == "") {
        this.music.loop.loopEnd = 0;
      } else if (this.music.loop.loopEnd < 0) {
        this.music.loop.loopEnd = 0;
        this.showAlert = true;
        this.alertTitle = "루프 에러";
        this.alertContent = "0 보다 작은 값은 입력할 수 없습니다.";
      } else if (this.music.loop.loopEnd <= this.music.loop.loopStart) {
        this.music.loop.loopEnd = 0;
        this.showAlert = true;
        this.alertTitle = "루프 에러";
        this.alertContent =
          "루프 시작 시간보다 작거나 같은 값은 입력할 수 없습니다.";
      } else if (this.music.loop.loopEnd > this.duration) {
        this.music.loop.loopEnd = 0;
        this.showAlert = true;
        this.alertTitle = "루프 에러";
        this.alertContent = "총 길이보다 크거나 같은 값은 입력할 수 없습니다.";
      }

      if (
        !this.music.loop.loopEnd ||
        this.music.loop.loopEnd < 0 ||
        this.music.loop.loopEnd <= this.music.loop.loopStart
      ) {
        this.music.loop.loopEnd = 0;
      }

      this.player.loopStart = this.music.loop.loopStart;
      this.player.loopEnd = this.music.loop.loopEnd;
      this.updateMusicOption();
    },
    addToTransport() {
      this.player.unsync();
      this.player
        .sync()
        .start(
          this.music.delay.delay,
          this.music.delay.offset + this.music.loop.loopStart
        );

      this.checkFinish = false;
      this.moveProgressBar();
    },
    moveProgressBar(wait = this.music.delay.delay * 1000) {
      setTimeout(() => {
        let interval = setInterval(() => {
          if (this.duration < this.startTime + this.currentTime) {
            this.$store.state.isSetPlaying = false;
            this.$store.state.isSetIdx = -1;

            this.$refs.waveform.setTime(0);
            this.currentTime = 0;
            this.startTime = 0;
            this.state = "stopped";
            this.player.unsync();
          }

          let time;
          if (Tone.Transport.seconds > 0) {
            if (this.player.loop) {
              if (this.music.loop.loopEnd > this.music.loop.loopStart) {
                time =
                  (Tone.Transport.seconds %
                    (this.music.loop.loopStart - this.music.loop.loopEnd)) +
                  this.music.loop.loopStart;
              } else {
                time =
                  this.music.loop.loopStart +
                  (Tone.Transport.seconds %
                    (this.duration - this.music.loop.loopStart));
              }
            } else {
              time =
                Tone.Transport.seconds +
                this.music.delay.offset -
                this.music.delay.delay;
            }
            this.currentTime = time;
            //if (this.currentTime > this.duration) this.stop();
            this.$refs.waveform.setTime(this.startTime + time);
          }
          if (this.player.state != "started") {
            this.startTime += this.currentTime;
            clearInterval(interval);
          }
        }, 50);
      }, wait);
    },
    removeFromTransport() {
      this.currentTime = 0;
      this.startTime = 0;
      this.player.unsync();
    },
    sendDelete() {
      this.$store.state.isSetPlaying = false;
      this.$store.state.isAllPlaying = false;
      this.$store.state.isSetIdx = -1;

      this.player.unsync();
      this.player.dispose();
      this.$emit("deleteMusic", this.n);
    },
    setTime(sec) {
      this.player.unsync();

      this.startTime = parseInt(sec);
      this.currentTime = 0;
      if (this.state == "started") {
        this.start();
      }
    },
    nowReady() {
      this.isReady = true;
    },
    setDuration(sec) {
      this.duration = sec;
    },
    closeAlert() {
      this.showAlert = false;
    },
  },
};
</script>

<style>
.file-title {
  padding: 5px 5px 0px 15px;
  margin: 0px !important;
}

.file-name {
  margin: 0px !important;
}

#player p {
  margin: 0px !important;
}

#player .v-slider__thumb {
  align-self: center !important;
  width: 20px !important;
  height: 10px !important;
  border-radius: 0px !important;
  background-color: #8cbbd7 !important;
  transform: translateX(-20%) !important;
}

#player .v-slider__track-container {
  width: 10px !important;
}
</style>
