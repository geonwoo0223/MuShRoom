<template>
  <v-overlay
    :absolute="true"
    :value="this.$store.getters.getHelp"
    opacity="0.1"
    color="white"
    z-index="100"
  >
    <v-sheet color="#00000a" width="700" height="500" rounded>
      <v-btn id="exit" color="#white" @click="changeHelpState" icon plain>
        <v-icon size="26px">mdi-close</v-icon>
      </v-btn>
      <v-carousel
        height="500"
        hide-delimiter-background
        show-arrows
        @change="test"
      >
        <template v-slot:prev="{ on, attrs }">
          <v-btn
            style="background-color: rgb(30, 30, 30)"
            icon
            plain
            v-bind="attrs"
            v-on="on"
            ><v-icon size="26px">mdi-arrow-left-bold-outline</v-icon></v-btn
          >
        </template>
        <template v-slot:next="{ on, attrs }">
          <v-btn
            style="background-color: rgb(30, 30, 30)"
            icon
            plain
            v-bind="attrs"
            v-on="on"
            ><v-icon size="26px">mdi-arrow-right-bold-outline</v-icon></v-btn
          >
        </template>
        <v-carousel-item v-for="(slide, i) in describe" :key="i">
          <Helpview :describe="slide" :src="imgsrc[i]" />
        </v-carousel-item>
      </v-carousel>
    </v-sheet>
  </v-overlay>
</template>

<script>
import Helpview from "./Helpview.vue";
export default {
  components: {
    Helpview,
  },

  data: () => ({
    firstIn: false,
    show: true,
    nowPage: 0,
    maxPage: 2,
    box: "",
    share: "",
    imgsrc: [
      // require("@/assets/help/mic.gif"),
      require("@/assets/help/share.gif"),
      require("@/assets/help/Metro.gif"),
      require("@/assets/help/record.gif"),
      require("@/assets/help/recordWithMet.gif"),
      require("@/assets/help/recordSolo.gif"),
      require("@/assets/help/fileupload.gif"),
      require("@/assets/help/AddDelete.gif"),
      require("@/assets/help/expand.gif"),
      require("@/assets/help/chat.gif"),
    ],
    describe: [
      // [`마이크를 허용해야 합니다`],
      [
        `Musician(편집 가능) 링크를 공유해 함께 음악을 만들고,<br>
      Audience(감상 전용) 링크를 공유해 내 음악을 함께 들을 수 있습니다.<br>
      <주의> Musician 링크는 항상 안전한 곳에 저장하십시오.`,
      ],
      [
        `메트로놈에서 BPM을 올려 속도를 높이고 Beat로 박자를 변경할 수 있습니다.<br>
      꾹 누르면 빠른 조절이 가능합니다.`,
      ],
      [`버튼을 누르면 녹음 창이 뜹니다.`],
      [
        `메트로놈 아이콘이 활성화 되어있으면 한마디 쉬고 녹음이 시작됩니다.<br>
      이어폰을 착용하지 않으면 메트로놈 소리가 함께 녹음될 수 있습니다.`,
      ],
      [
        `아이콘을 눌러 메트로놈을 해제하고 녹음이 가능합니다.<br>
      녹음이 완료되면 파일보드(오른쪽)에 추가할 수 있습니다.`,
      ],
      [`파일보드(오른쪽)에 직접 음악파일을 업로드 할 수 있습니다.`],
      [
        `파일보드(오른쪽)에서 뮤직보드(왼쪽)으로 음악파일을 추가하거나 삭제가 가능합니다.`,
      ],
      [`버튼을 눌러 확장된 화면에서 다양한 옵션을 줄 수 있습니다.`],
      [`다른 유저와 의견을 주고 받으며 음악을 함께 만들어 나갑니다.`],
    ],
  }),
  methods: {
    test() {
      this.$cookies.set("visit", "visited");
    },

    prev() {
      this.nowPage -= 1;
      this.show = false;
      setTimeout(() => {
        this.show = true;
      }, 1);
    },
    next() {
      this.nowPage += 1;
      this.show = false;
      setTimeout(() => {
        this.show = true;
      }, 1);
    },
    changeHelpState() {
      this.$store.commit("helpShowChange");
    },
  },
  mounted() {
    this.maxPage = this.describe.length - 1;
    this.visit = Array.from({ length: this.maxPage + 1 }, () => 0);
    if (this.$cookies.isKey("visit")) {
      this.firstIn = true;
    }
    if (navigator.mediaDevices) {
      const constraints = {
        audio: true,
      };
      navigator.mediaDevices
        .getUserMedia(constraints)
        .then((stream) => {
          stream;
        })
        .catch((err) => {
          console.log("The following error occurred: " + err);
        });
    }
  },
  computed: {
    showState() {
      return this.$store.getters.getHelp;
    },
  },
  watch: {
    showState(val) {
      if (val === false) return;
      this.nowPage = 0;
    },
  },
};
</script>

<style>
#exit {
  position: absolute;
  z-index: 30;
  top: 10px;
  right: 10px;
}
</style>