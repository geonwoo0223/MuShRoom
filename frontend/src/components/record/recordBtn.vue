<template>
  <v-card
    class="record main-color d-flex flex-column"
    style="z-index: 100"
    v-if="showRecord"
  >
    <recBtn
      style="border: none"
      ref="recorder"
      :after-recording="setRecorded"
      :before-recording="startRecord"
      :bit-rate="192"
      :sampleRate="44100"
      :crossState="cross"
    />
    <v-spacer></v-spacer>
    <v-card
      elevation="0"
      width="80%"
      :disabled="nowstate"
      class="d-flex mb-4 ml-7 align-center main-color"
    >
      <v-text-field
        dark
        v-model="inputFileName"
        :rules="filenameRules"
        label="파일이름"
        hide-details
      ></v-text-field>
      <v-btn icon dark @click="upload" class="mt-3 mx-2"
        ><v-icon size="30px">mdi-plus</v-icon></v-btn
      >
    </v-card>
    <v-btn
      icon
      dark
      :disabled="isSetRecording"
      style="position: absolute; right: 5px; top: 5px"
      @click="closeRecord"
    >
      <v-icon> mdi-close </v-icon>
    </v-btn>
    <v-tooltip max-width="200px" left>
      <template v-slot:activator="{ on, attrs }">
        <v-btn
          icon
          dark
          v-bind="attrs"
          v-on="on"
          :disabled="isSetRecording"
          style="position: absolute; left: 15px; top: 15px"
          @click="changeCross"
        >
          <v-icon v-if="cross"> mdi-metronome-tick </v-icon>
          <v-icon v-else color="black"> mdi-metronome-tick </v-icon>
          <v-icon v-if="!cross" style="position: absolute"> mdi-close </v-icon>
        </v-btn>
      </template>
      <span v-if="cross">메트로놈 시작 한 마디 후 녹음이 시작됩니다</span>
      <span v-else>바로 녹음이 시작됩니다.</span></v-tooltip
    >
  </v-card>
</template>
<script>
import recBtn from "./recBtn.vue";
import sendfile from "@/service/filecontrol";
import { getYyyyMmDdMmSsToString } from "@/lib/timestamp";
import { mapState } from "vuex";

export default {
  components: { recBtn },
  props: ["showRecord"],
  data: function () {
    return {
      inputFileName: "",
      //컴포넌트에서 녹화한 파일을 담는 변수
      // blob 형태 {size , type 두가지 정보} , duration 재생길이 , url => 로컬 다운로드 url
      file: {}, // 녹음 완료 후 파일 정보
      nowstate: true,
      recording: false,
      cross: true,
      filenameRules: [(value) => !!value || "Required."],
    };
  },
  computed: {
    ...mapState(["isSetRecording"]),
  },
  methods: {
    changeCross() {
      this.cross = !this.cross;
    },
    closeRecord() {
      this.$emit("closeRecord");
    },
    expandInit() {
      this.inputFileName = "";
    },
    async upload() {
      if (this.file === "") return;
      if (this.file.blob === undefined) return;
      const sendFileData = {
        fileName: "",
        downloadURL: "",
      };
      if (this.inputFileName === "") return;
      const data = new FormData();
      sendFileData.fileName = this.inputFileName;
      var date = new Date();
      date = getYyyyMmDdMmSsToString(date);
      data.append(
        "file",
        this.file.blob,
        `${sendFileData.fileName}_${date.toString()}.mp3`
      );
      await sendfile
        .send(data)
        .then((result) => {
          sendFileData.downloadURL = result.data.fileDownloadUri;
        })
        .catch((err) => {
          console.debug("녹음 파일 업로드 실패", err);
        });
      this.$emit("sendData", sendFileData);
      this.closeRecord();
      this.nowstate = true;
      this.inputFileName = null;
      this.file = "";
    },

    hideStopBtn() {
      const $stopBtn = this.$refs.recorder.$el.querySelector(
        ".ar-recorder__stop"
      );
      $stopBtn.style.display = "none";
    },
    showStopBtn() {
      const $stopBtn = this.$refs.recorder.$el.querySelector(
        ".ar-recorder__stop"
      );
      $stopBtn.style.display = "block";
    },
    setRecentRecord() {
      const recorder = this.$refs.recorder;
      if (recorder) {
        const top = recorder.recordList.length - 1;
        recorder.selected = recorder.recordList[top];
        this.file = recorder.selected;
      }
    },
    // :after-recording
    setRecorded() {
      this.hideStopBtn();

      setTimeout(() => {
        this.setRecentRecord();
      }, 800);
      this.recording = false;
      this.nowstate = false;
    },

    //:before-recording=
    startRecord() {
      this.nowstate = true;
      this.recording = true;
      this.showStopBtn();
    },
  },
};
</script>

<style scoped>
.record {
  padding: 0;
  position: fixed;
  right: 10px;
  width: 300px;
  height: 130px;
  top: 15px;
  right: 16px;
  border: 1px solid white;
  border-radius: 0px;
}
</style>