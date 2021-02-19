<template>
  <v-card class="metronome-main main-color-light " height="100%" width="100%">
    <v-card class="d-flex pa-2 metronome-screen main-color-light ">
      <BeatIndicator :bpm="bpm" :beatIndex="beatIndex" :isPlaying="isPlaying()"/>
      <v-card elevation="0" class="d-flex ml-2 justify-space-around smallcomponent-color" style="border-radius: 5px;" height="100%" width="100%">
        <div class="d-flex-column pl-4">
          <p class="metronome-now">{{ bpm }} <span class="metronome-label"> BPM </span> </p>
          <v-btn plain class="mt-n3" 
            icon dark @click="decrease('bpm')" 
            @mousedown="decreaseStart('bpm')" 
            @mouseleave="decreaseEnd()"
            @mouseup="decreaseEnd()"><v-icon>mdi-minus</v-icon>
          </v-btn>
          <v-btn plain class="mt-n3" 
            icon dark @click="increase('bpm')" 
            @mousedown="increaseStart('bpm')" 
            @mouseleave="increaseEnd()"
            @mouseup="increaseEnd()"><v-icon>mdi-plus</v-icon>
          </v-btn>
        </div>
        <div class="d-flex-column pr-4">
          <p class="metronome-now">{{ beatsPerBar }} <span class="metronome-label"> Beat </span> </p>
          <v-btn plain class="mt-n3 ml-n2" 
            icon dark @click="decrease('beat')" 
            @mousedown="decreaseStart('beat')" 
            @mouseleave="decreaseEnd()" @mouseup="decreaseEnd()" 
            :disabled="isPlaying()"><v-icon>mdi-minus</v-icon>
          </v-btn>
          <v-btn plain class="mt-n3" 
            icon dark @click="increase('beat')" 
            @mousedown="increaseStart('beat')" 
            @mouseleave="increaseEnd()" @mouseup="increaseEnd()"
            :disabled="isPlaying()"><v-icon>mdi-plus</v-icon>
          </v-btn>
        </div>
      </v-card>
    </v-card>
    <v-divider class="text-color"></v-divider>
    <v-card 
    style="border-radius: 0px;"
    class="d-flex align-center metronome-btn main-color-light ">
      <PlayControlBtn :isPlaying="isPlaying()" @start="onStart" @stop="onStop"/>
      <v-spacer></v-spacer>
      <div class="d-flex align-center metronome-slider">
        <v-btn
          icon
          dark
          plain
          @click="onVolumeMute"
        >
          <v-icon v-if="volume == -50 || mute">mdi-volume-mute</v-icon>
          <v-icon v-else-if="volume > -50 && volume <= 0">mdi-volume-medium</v-icon>
          <v-icon v-else>mdi-volume-high</v-icon>
        </v-btn>
        <v-slider
          dark
          color="#ffffffbb"
          class="pt-5"
          track-color="grey darken-2"
          v-model="volume"
          max="50"
          min="-50"
          style="width: 150px !important;"
          @change="onChangeBySlide"
        ></v-slider>
        <p class="volume-number">{{ volume | volume(mute) }}</p>
      </div>
    </v-card>
  </v-card>
</template>

<script>
import Vue from "vue";
import BeatIndicator from "@/components/metronome/BeatIndicator.vue";
import PlayControlBtn from "@/components/metronome/PlayControlBtn.vue";
import Config from '@/store/config'

import { mapState } from "vuex";
import { Player, Sequence, Transport, start, Destination } from "tone";

const accent = new Player(
  Config.ServerURL + "/downloadFile/Ping%20Hi.wav"
).toDestination();
const beat = new Player(
  Config.ServerURL + "/downloadFile/Ping%20Low.wav"
).toDestination();



Vue.filter("volume", (value, mute) => {
  if (mute) {
    return "0";
  }
  return `${value+50}`;
});



export default {
  name: "metronome",
  components:{
    BeatIndicator,
    PlayControlBtn,
  },
  data: function () {
    return {
      bpm: 60,
      hideSlider: true,
      beatsPerBar: 4,
      beatIndex: 0,

      interval: false,
      timer: '',

      isStopped: true,

      volume: 0,
      saveVolume: 0,
      mute: false,
    };
  },
  methods: {
    isPlaying() {
      if (this.isStopped) {
        return false;
      }
      return true;
    },

    isMute() {
      if (this.mute) {
        return true;
      }
      return false;
    },

    isMaxVolume() {
      if (this.volume == 50) {
        return true;
      }
      return false;
    },

    onStart() {
      Transport.stop()
      Transport.cancel(0);
      if (this.isPlaying()) {
        return;
      }
      if (this.isStopped) {
        start();
        const sequence = this.createNoteSequence();
        sequence.start(0);
      }
      this.isStopped = false;

      Transport.start();
    },

    onStop() {
      Transport.stop();
      Transport.cancel(0);
      Transport.seconds = 0;
      this.beatIndex = 0;
      this.isStopped = true;
    },
    onRecordStop() {
      if (this.beatIndex == 0) {
        this.$store.commit("setRC", "stopRecord");
        Transport.stop();
        Transport.cancel(0);
        Transport.seconds = 0;
        this.beatIndex = 0;
        this.isStopped = true;
        return;
      }
      setTimeout(() => {
        this.onRecordStop();
      }, (1000 * 60) / (this.bpm + 20));
    },

    onVolumeMute() {
      if (this.isSetMetronome ) {
        if(!this.mute) {
          this.saveVolume = this.volume;
          this.volume = -50;
        } else {
          this.volume = this.saveVolume;
        }
        this.mute = !this.mute;
        Destination.mute = this.mute;
      }

    },

    onChangeBySlide() {
      if (this.isSetMetronome) {
        if (this.mute) {
          this.onVolumeMute()
        }
        Destination.volume.value = this.volume/2;
        if (this.volume == -50) {
          Destination.mute = !this.mute;
        }
      }
    },

    decrease(point) {
      if (point == 'bpm'){
        this.bpm--;
      } else {
        this.beatsPerBar--;
      }
    },

    increase(point) {
      if (point == 'bpm'){
        this.bpm++;
      } else {
        this.beatsPerBar++;
      }
    },

    decreaseStart(point) {
      this.timer = setTimeout(() => {
        if (!this.interval) {
          this.interval = setInterval(() => this.decrease(point), 50);
        }
      },1000);
    },
    decreaseEnd() {
      clearTimeout(this.timer)
      clearInterval(this.interval)
      this.interval = false
    },

    increaseStart(point) {
      this.timer = setTimeout(() => {
        if (!this.interval) {
          this.interval = setInterval(() => this.increase(point), 50);
        }
      },1000);
    },
    increaseEnd() {
      clearTimeout(this.timer)
      clearInterval(this.interval)
      this.interval = false
    },




    createNoteSequence() {
      const accentNote = "G2";
      const beatNote = "C2";
      const notes = [
        accentNote,
        ...new Array(this.beatsPerBar - 1).fill(beatNote),
      ];
      return new Sequence(
        (time, note) => {
          switch (note) {
            case accentNote:
              this.beatIndex = 0;
              accent.start(time);
              break;
            case beatNote:
              this.beatIndex += 1;
              if (
                this.beatIndex == this.beatsPerBar - 1 &&
                this.recordStartState === "startMetro"
              ) {
                this.$store.commit("setRC", "startRecord");
              }
              beat.start(time);
              break;
            default:
              break;
          }
        },
        notes,
        "4n"
      );
    },


  },

  watch: {
    bpm: function () {
      if (this.bpm > 300) {
        this.bpm = 300;
      } else if (this.bpm < 20) {
        this.bpm = 20;
      }
      Transport.bpm.value = this.bpm;
    },

    beatsPerBar() {
      if (this.beatsPerBar > 16) {
        this.beatsPerBar = 16;
      } else if (this.beatsPerBar < 1) {
        this.beatsPerBar = 1;
      }
      Transport.timeSignature = this.beatsPerBar;
    },
    getRC(val) {
      if (val === "startMetro") {
        this.onStart();
      } else if (val === "stopMetro") {
        this.onRecordStop();
      }
    },
  },

  computed: {
    animationDuration() {
      return `${30 / this.bpm}s`;
    },
    getRC() {
      return this.$store.getters.getRC;
    },
    ...mapState(["recordStartState", "isSetMetronome"]),
  },

  mounted() {
    Transport.bpm.value = this.bpm;
    start();
  },
}
</script>

<style>

/* .metronome-main {
    border : 10px solid black !important;
} */

.metronome-screen {
    border-radius: 0px !important;
    height: 70%;
}

.metronome-label {
  margin-left: -6px;
  font-weight: 400;
  font-size: 16px !important;
}

.metronome-now {
  margin-bottom: 0px !important;
  font-weight: 600;
  font-size: 2rem;
}

.metronome-btn {
    height: 30%
}

.showVolumeBtn {
  display: none !important;
}

.volume-number {
  font-size: 25px;
  font-weight: 600;
  padding-top: 12px;
  width: 60px;
}

.metronome-slider .v-slider__thumb {
  width: 10px !important;
  height: 20px !important;
  border-radius: 0px !important;
}

.metronome-slider .v-slider__track-container {
   height: 10px !important;
}


</style>