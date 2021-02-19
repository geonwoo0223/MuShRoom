<template>
  <v-card
    class="musicBoard main-color-light"
    elevation="0"
    width="100%"
    height="100%"
  >
    <v-card
      id="musicListId"
      class="musicList overflow-y-auto main-color-light"
      style="height: inherit !important; border-radius: 0px"
      v-scroll.self="onScroll"
    >
      <div
        v-if="music.length == 0"
        style="height: 100%"
        class="d-flex justify-center align-center"
      >
        <div>
          <v-img
            width="250px !important"
            style="object-fit: cover"
            src="@/assets/grey.png"
          >
          </v-img>
          <div class="ml-10" style="color: #4a4a4a">
            어떤 음악을 만들지 기대돼요
          </div>
        </div>
      </div>
      <Player
        class="mx-8 mt-4 smallcomponent-color"
        v-for="(item, idx) in music"
        :key="item.id"
        :n="idx"
        :music="item"
        @deleteMusic="deleteMusic"
        @updateMusicOption="updateMusicOption"
        @release="musicStopButton"
        @finished="isAllFinished"
        ref="player"
      />
    </v-card>
    <v-divider style="background-color: rgba(255, 255, 255, 0.733)"></v-divider>
    <v-card height="50px" class="buttonBar d-flex main-color-light">
      <v-spacer></v-spacer>
      <v-card
        style="border-radius: 0px"
        class="d-flex align-center justify-end main-color-light"
        elevation="0"
      >
        <v-btn
          class="musicboard_btn"
          :disabled="
            isSetRecording ||
            isSetPlaying ||
            play ||
            music.length == 0 ||
            isSetMetronome
          "
          icon
          dark
          plain
          @click="musicPlayButton"
        >
          <v-icon size="30px">mdi-play</v-icon>
        </v-btn>
        <v-btn
          class="musicboard_btn mr-5"
          icon
          dark
          plain
          :disabled="isSetRecording || isSetPlaying || !play"
          @click="musicStopButton"
        >
          <v-icon size="30px">mdi-stop</v-icon>
        </v-btn>
      </v-card>
    </v-card>
  </v-card>
</template>

<script>
import Player from "./practiceroom/Player";
import * as Tone from "tone";
import Stomp from "webstomp-client";
import SockJS from "sockjs-client";
import Config from "@/store/config";
import options from "@/store/option";
import { mapState } from "vuex";

export default {
  components: {
    Player,
  },
  data() {
    return {
      play: false,
      scrollInvoked: 0,
      code: "",
      status,
      finished: 0,
    };
  },
  created() {
    this.status = this.$store.state.status;

    this.code = document.location.href.split("=")[1];

    this.connect();
  },
  computed: {
    getURL() {
      return this.$store.getters.getURL;
    },
    music: function () {
      return this.$store.getters.getBoard;
    },
    numOfMusic: function () {
      return this.music.length;
    },
    ...mapState(["isSetRecording", "isSetPlaying", "isSetMetronome"]),
  },
  methods: {
    send(type, msg) {
      if (type == "music")
        this.musicStompClient.send(
          "/socket/music/" + this.code + "/0/receive",
          JSON.stringify(msg),
          {}
        );
    },
    connect() {
      const serverURL = Config.ServerURL;

      let musicSocket = new SockJS(serverURL);
      this.musicStompClient = Stomp.over(musicSocket);
      this.musicStompClient.debug = () => {};
      this.musicStompClient.connect(
        {},
        (frame) => {
          // 소켓 연결 성공
          this.connected = true;
          frame;

          this.musicStompClient.subscribe(
            "/socket/music/" + this.code + "/0/send",
            (res) => {
              const resBody = JSON.parse(res.body);
              if (resBody["type"] == "delete") {
                this.$toast.error(
                  `[${resBody["obj"]["fileName"]}]이(가) 뮤직보드에서 제거되었습니다.`,
                  options
                );
                this.$store.commit("deleteMusic", {
                  idx: resBody["index"],
                });

                Tone.Transport.stop();
                this.play = false;
                this.$refs.player[resBody["index"]].player.unsync();
                this.$refs.player[resBody["index"]].player.dispose();
              }
              if (resBody["type"] == "update") {
                this.$toast.success(
                  `[${resBody["obj"]["fileName"]}]이(가) 뮤직보드에서 수정되었습니다.`,
                  options
                );
                this.$store.commit("updateMusic", {
                  music: {
                    id: resBody["index"],
                    url: resBody["obj"]["url"],
                    fileName: resBody["obj"]["fileName"],
                    timestamp: resBody["obj"]["timestamp"],
                    distortion: {
                      object: null,
                      value: resBody["obj"]["distortion"],
                    },
                    volume: {
                      object: null,
                      value: resBody["obj"]["volume"],
                    },
                    gain: {
                      object: null,
                      value: resBody["obj"]["gain"],
                    },
                    reverb: {
                      object: null,
                      value: resBody["obj"]["reverb"],
                    },
                    loop: {
                      loop: resBody["obj"]["loop"],
                      loopStart: resBody["obj"]["loopStart"],
                      loopEnd: resBody["obj"]["loopEnd"],
                    },
                    delay: {
                      delay: resBody["obj"]["delay"],
                      offset: resBody["obj"]["offset"],
                    },
                  },
                });
                // 받은 값으로 업데이트
                this.$refs.player[resBody["index"]].updateAll();
              }
            }
          );
        },
        (error) => {
          // 소켓 연결 실패
          console.log("뮤직보드 소켓 연결 실패", error);
          this.connected = false;
        }
      );
    },
    musicPlayButton() {
      this.musicStopButton();
      this.$store.state.isAllPlaying = true;
      if (this.$refs.player) {
        this.$refs.player.forEach((el) => {
          el.addToTransport();
        });
        Tone.Transport.start();
        this.$refs.player.forEach((el) => {
          el.moveProgressBar();
        });
      } else {
        // error
        //console.log("player 가 존재하지 않습니다.");
      }

      this.play = true;
    },
    musicStopButton(idx) {
      // Feat: release all
      Tone.Transport.stop();
      this.$store.state.isAllPlaying = false;
      this.play = false;

      if (this.$refs.player) {
        this.$refs.player.forEach((el) => {
          if (el.n != idx) el.stop();
        });
      }
      this.finished = 0;
    },
    onScroll() {
      this.scrollInvoked++;
    },
    deleteMusic(id) {
      this.send("music", {
        type: "delete",
        index: id,
        obj: { url: this.music[id].url, fileName: this.music[id].fileName },
      });
    },
    updateMusicOption(id) {
      this.send("music", {
        type: "update",
        index: id,
        obj: {
          url: this.music[id].url,
          fileName: this.music[id].fileName,
          volume: this.music[id].volume.value,
          distortion: this.music[id].distortion.value,
          gain: this.music[id].gain.value,
          reverb: this.music[id].reverb.value,
          loop: this.music[id].loop.loop,
          loopStart: this.music[id].loop.loopStart,
          loopEnd: this.music[id].loop.loopEnd,
          delay: this.music[id].delay.delay,
          offset: this.music[id].delay.offset,
        },
      });
    },
    isAllFinished() {
      if (++this.finished == this.numOfMusic) this.musicStopButton();
    },
  },
};
</script>

<style>
</style>
