<template>
  <v-main class="main-color" style="height: 100%; width: 100%">
    <Header
      :openChat="openChat"
      v-on:toggleChat="toggleChat"
      v-on:openModal="openModal"
      :hasNickName="hasNickName"
      :newChat="newChat"
    />
    <!-- 부모 row -->
    <v-row no-gutters class="mx-auto" style="width: 1100px !important">
      <!-- 왼쪽 컴포넌트들 -->
      <v-col cols="8" class="mx-auto flex-grow-0 flex-shrink-0 px-4 pt-4 pb-2">
        <v-row no-gutters style="height: 600px">
          <v-card elevation="0" height="100%" width="100%" color="#00ff0000">
            <!-- 뮤직 보드 -->
            <v-card elevation="0" width="100%" height="100%" color="#00ff0000">
              <MusicBoard />
            </v-card>
          </v-card>
        </v-row>
      </v-col>

      <!-- 오른쪽 컴포넌트들 -->
      <v-col
        v-if="status === 'Master'"
        cols="4"
        class="flex-grow-0 flex-shrink-0 px-4 pt-4 pb-1"
      >
        <!-- 매트로놈 -->
        <v-row no-gutters style="height: 130px">
          <v-card elevation="0" width="100%" height="100%" color="#00ff0000">
            <Metronome />
          </v-card>
        </v-row>

        <!-- 파일 목록 -->
        <v-row no-gutters style="height: 552px">
          <v-card elevation="0" width="100%" height="100%" color="#00ff0000">
            <Record
              @uploadComplete="uploadComplete"
              @uploadStart="uploadStart"
            />
          </v-card>
        </v-row>
      </v-col>
    </v-row>
    <Chat
      :openChat="openChat"
      @toggleChat="toggleChat"
      :nickName="nickName"
      @newChat="arriveNewChat"
    />
    <SetNickName
      :showModal="showModal"
      @close="closeModal"
      @setNickName="setNickName"
    />
    <Help />
    <Uploading :uploading="uploading" />
  </v-main>
</template>

<script>
import Header from "@/components/common/Header.vue";
import Metronome from "@/components/MetronomeBody.vue";
import MusicBoard from "@/components/MusicBoard.vue";
import Record from "@/components/record.vue";
import axios from "@/service/axios.service.js";
import Chat from "@/components/chat/Chat.vue";
import SetNickName from "@/components/chat/SetNickName.vue";
import Help from "@/components/help/Helpshow.vue";
import Uploading from "@/components/record/Uploading.vue";

export default {
  components: {
    Header,
    Metronome,
    MusicBoard,
    Record,
    Chat,
    SetNickName,
    Help,
    Uploading,
  },
  mounted() {
    if (this.$cookies.isKey("visit")) {
      return;
    } else {
      if (this.$store.state.status === "Slave") return;

      this.$store.commit("helpShowChange");
    }
  },
  created() {
    // Status를 vuex에 저장

    this.init();
    this.load();
  },
  data() {
    return {
      status,
      openChat: false,
      showModal: false,
      hasNickName: false,
      nickName: "",
      newChat: 0,
      uploading: false, // 파일 업로드할 때 true
    };
  },
  beforeRouteLeave() {
    // 경로 이동 전에 호출되는 메소드
    window.location.reload(); // 현재 페이지 새로고침
  },
  methods: {
    init() {
      this.code = this.$route.query.shareUrl;

      // store에 있는 거 다 지워야함
      this.$store.commit("setData");
    },
    load() {
      axios.get("/data/" + this.code).then((res) => {
        this.$store.commit("pushShareUrl", [
          res.data.id.masterId,
          res.data.id.slaveId,
        ]);

        if (this.code == res.data.id.masterId)
          this.$store.commit("pushStatus", "Master");
        else this.$store.commit("pushStatus", "Slave");

        this.status = this.$store.state.status;

        // 받아온 res에서 뮤직보드, 레코드보드 불러오기 해야함
        // 뮤직 보드 불러오기

        for (let i = 0; i < res.data.musicPageList[0].musicList.length; i++) {
          // 일단 한 번 넣고 수정한다

          this.$store.commit("addMusic", {
            record: {
              fileName: res.data.musicPageList[0].musicList[i].fileName,
              downloadURL: res.data.musicPageList[0].musicList[i].url,
              id: i,
            },
          });

          // 업데이트
          this.$store.commit("updateMusic", {
            music: {
              id: i,
              url: res.data.musicPageList[0].musicList[i].url,
              fileName: res.data.musicPageList[0].musicList[i].fileName,
              timestamp:
                res.data.musicPageList[0].musicList[i].timestamp == null
                  ? 0
                  : res.data.musicPageList[0].musicList[i].timestamp,
              distortion: {
                object: null,
                value: res.data.musicPageList[0].musicList[i].distortion,
              },
              volume: {
                object: null,
                value: res.data.musicPageList[0].musicList[i].volume,
              },
              gain: {
                object: null,
                value: res.data.musicPageList[0].musicList[i].gain,
              },
              reverb: {
                object: null,
                value: res.data.musicPageList[0].musicList[i].reverb,
              },
              loop: {
                loop: res.data.musicPageList[0].musicList[i].loop,
                loopStart: res.data.musicPageList[0].musicList[i].loopStart,
                loopEnd: res.data.musicPageList[0].musicList[i].loopEnd,
              },
              delay: {
                delay: res.data.musicPageList[0].musicList[i].delay,
                offset: res.data.musicPageList[0].musicList[i].offset,
              },
            },
          });
        }
        // console.log("뮤직보드 불러오기 완료!");

        //레코드 보드 불러오기
        for (let i = 0; i < res.data.recordList.length; i++)
          this.$store.commit("updateRecord", {
            fileName: res.data.recordList[i].fileName,
            downloadURL: res.data.recordList[i].url,
            id: i,
          });

        //  console.log("레코드 불러오기 완료!");
      });
    },
    // 채팅창 버튼 누르면 열고 닫는 toggle
    toggleChat() {
      if (!this.hasNickName) return;

      if (!this.openChat) this.newChat = 0;

      this.openChat = !this.openChat;
    },
    // 닉네임 설정 모달
    openModal() {
      this.showModal = true;
    },
    // 취소 눌렀을 때
    closeModal() {
      this.showModal = false;
    },
    // 채팅하기 눌렀을 때
    setNickName(val) {
      this.showModal = false;
      this.hasNickName = true;
      this.nickName = val;
      this.toggleChat();
    },
    // 새로운 메세지가 왔을 때
    arriveNewChat() {
      if (this.nickName.length > 0 && !this.openChat)
        this.newChat = this.newChat < 99 ? this.newChat + 1 : this.newChat;
    },
    // 파일 업로드 완료
    uploadComplete() {
      this.uploading = false;
    },
    // 파일업로드 시작
    uploadStart() {
      this.uploading = true;
    },
  },
};
</script>

<style>
.tab_textfield input::placeholder {
  color: white !important;
}

.tab_textfield input {
  width: 55px;
  height: 5px;
  color: white !important;
}

.tab_textfield .v-input__slot {
  min-height: 30px;
}

.tab_btn {
  margin-top: 10px;
  margin-left: 59px;
}

.divider {
  border-right: 1px solid black;
  /* border-bottom: 1px solid white; */
}

.others {
  background-color: #3c4d5d;
}
</style>
