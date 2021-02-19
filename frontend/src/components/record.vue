<template>
  <v-card class="main-color-light" width="100%" height="91%">
    <v-card
      class="overflow-y-auto main-color-light"
      style="height: inherit !important; border-radius: 0px"
      v-scroll.self="onScroll"
    >
      <recordCard
        v-for="(item, index) in records"
        :key="index"
        v-on:delRecord="delRecord"
        v-on:addRecord="addRecord"
        v-bind:fileData="records[index]"
        :idx="index"
      />
    </v-card>
    <v-divider style="background-color: rgba(255, 255, 255, 0.733)"></v-divider>
    <v-card
      height="50px"
      class="main-color-light d-flex align-center"
      style="border-radius: 0px"
    >
      <v-btn
        icon
        dark
        plain
        class="ml-5"
        :disabled="
          isSetPlaying || isSetRecording || isAllPlaying || isSetMetronome
        "
        @click="showRecord = !showRecord"
        ><v-icon size="26px">mdi-microphone</v-icon>
      </v-btn>
      <v-btn icon dark plain @click="file_upload_open"
        ><v-icon size="26px">mdi-file-upload</v-icon>
      </v-btn>
      <recordBtn
        :showRecord="showRecord"
        @sendData="receiveData"
        @closeRecord="closeRecord"
        ref="recBtn"
      />
      <UploadBtn
        @sendData="receiveData"
        @fileUploading="fileUploading"
        ref="fileupload"
      />
    </v-card>
  </v-card>
</template> 

<script>
import recordBtn from "./record/recordBtn";
import UploadBtn from "./record/fileupload";
import recordCard from "./record/Audiocard";
import Stomp from "webstomp-client";
import SockJS from "sockjs-client";
import Config from "@/store/config";
import options from "@/store/option";
import { mapState } from "vuex";

export default {
  data: () => {
    return {
      expand: false,
      expand2: false, //
      showRecord: false,
      scrollInvoked: 0,
    };
  },
  computed: {
    records: function () {
      return this.$store.getters.getRecords;
    },
    ...mapState([
      "isSetPlaying",
      "isSetRecording",
      "isAllPlaying",
      "isSetMetronome",
    ]),
  },
  created() {
    // 권한
    navigator.mediaDevices
      .getUserMedia({ audio: true })
      .then()
      .catch((err) => console.log(err));
    this.idx = this.records.length;

    this.code = document.location.href.split("=")[1];

    this.connect();
  },
  components: {
    recordCard,
    recordBtn,
    UploadBtn,
  },
  methods: {
    send(type, msg) {
      if (type == "record")
        this.recordStompClient.send(
          "/socket/record/" + this.code + "/receive",
          JSON.stringify(msg),
          {}
        );
      else if (type == "music")
        this.musicStompClient.send(
          "/socket/music/" + this.code + "/0/receive",
          JSON.stringify(msg),
          {}
        );
    },
    connect() {
      const serverURL = Config.ServerURL;

      let recordSocket = new SockJS(serverURL);
      this.recordStompClient = Stomp.over(recordSocket);
      this.recordStompClient.debug = () => {};
      this.recordStompClient.connect(
        {},
        (frame) => {
          // 소켓 연결 성공
          this.connected = true;
          frame;

          this.recordStompClient.subscribe(
            "/socket/record/" + this.code + "/send",
            (res) => {
              const resBody = JSON.parse(res.body);

              if (resBody["type"] == "add") {
                this.$toast(
                  `[${resBody["obj"]["fileName"]}]이(가) 업로드 되었습니다.`,
                  options
                );
                this.$store.commit("updateRecord", {
                  fileName: resBody["obj"]["fileName"],
                  downloadURL: resBody["obj"]["url"],
                  id: resBody["index"],
                });
              }
              if (resBody["type"] == "delete") {
                this.$toast.error(
                  `[${
                    this.$store.getters.getRecords[resBody.index].fileName
                  }]이(가) 제거 되었습니다.`,
                  options
                );
                this.$store.commit("deleteRecord", resBody.index);
              }
            }
          );
        },
        (error) => {
          // 소켓 연결 실패
          console.log("소켓 연결 실패", error);
          this.connected = false;
        }
      );

      let musicSocket = new SockJS(serverURL);
      this.musicStompClient = Stomp.over(musicSocket);
      this.musicStompClient.debug = () => {};
      this.musicStompClient.connect(
        {},
        (frame) => {
          // // 소켓 연결 성공
          this.connected = true;
          frame;
          this.musicStompClient.subscribe(
            "/socket/music/" + this.code + "/0/send",
            (res) => {
              const resBody = JSON.parse(res.body);
              if (resBody["type"] == "add") {
                this.$toast(
                  `[${resBody["obj"]["fileName"]}]이(가) 뮤직보드로 이동했습니다`,
                  options
                );
                this.$store.commit("addMusic", {
                  record: {
                    fileName: resBody["obj"]["fileName"],
                    downloadURL: resBody["obj"]["url"],
                    id: resBody["index"],
                  },
                });
              }
            }
          );
        },
        (error) => {
          // 소켓 연결 실패
          console.log("소켓 연결 실패", error);
          this.connected = false;
        }
      );
    },

    rec_show() {
      this.$refs.recBtn.show();
    },
    file_upload_open() {
      this.$refs.fileupload.inputClick();
    },

    addCard(data) {
      this.send("record", {
        type: "add",
        index: this.records.length,
        obj: { url: data["downloadURL"], fileName: data["fileName"] },
      });
    },
    receiveData(data) {
      this.$emit("uploadComplete");
      data["id"] = this.idx;
      this.idx += 1;
      this.addCard(data);
      this.expand2 = false;
      this.expand = false;
    },
    delRecord(id) {
      this.send("record", {
        type: "delete",
        index: id,
      });
    },
    addRecord(id) {
      this.send("music", {
        type: "add",
        index: this.$store.getters.getBoard.length,
        obj: {
          url: this.records[id]["downloadURL"],
          fileName: this.records[id]["fileName"],
          distortion: 0,
          gain: 0,
          volume: 0,
          reverb: 0,
          loop: false,
          loopStart: 0,
          loopEnd: 0,
          delay: 0,
          offset: 0,
        },
      });
    },
    onScroll() {
      this.scrollInvoked++;
    },
    closeRecord() {
      this.showRecord = false;
    },
    // 파일 업로드 될 때
    fileUploading() {
      this.$emit("uploadStart");
    },
  },
};
</script>


<style>
</style>
