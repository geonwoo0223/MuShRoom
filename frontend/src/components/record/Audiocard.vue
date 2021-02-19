<template>
  <div>
    <v-card
      elevation="0"
      outlined
      class="mx-auto mt-2 smallcomponent-color"
      width="90%"
      height="auto"
      style="border: 4px solid rgb(61, 61, 61); border-radius: 5px;"
    >
      <v-card-title width="50%" max-width="60px">
        <div class="wrap1">
          <div class="wrap2">
            <div class="ml-2 medium text-color" :class="fileData.fileName.length > 18 ? 'wrap3':'wrap4'">{{
              fileData.fileName
            }}</div>
          </div>
        </div>
        <v-spacer></v-spacer>
        <v-btn icon dark plain @click="addThis"
          ><v-icon>mdi-plus </v-icon></v-btn
        >
        <v-btn :disabled="!isReady"
        class="mr-2" plain icon dark @click="deleteThis"
          ><v-icon>mdi-delete</v-icon></v-btn
        >
      </v-card-title>

      <Waveform class="mx-auto none" :url="fileData.downloadURL" height="60" :full="false" color="#999" @isReady="nowReady"/>
    </v-card>
  </div>
</template>
<script>
import Waveform from "../practiceroom/Waveform";
export default {
  props: ["fileData", "idx"],
  components: {
    Waveform,
  },
  data: () => ({
    showMenu: false,
    isReady: false
  }),
  methods: {
    deleteThis() {
      this.$emit("delRecord", this.idx);
    },
    addThis() {
      this.$emit("addRecord", this.idx);
    },
    nowReady() {
      this.isReady = true;
    }
  },
};
</script>

<style scoped >
.none {
  pointer-events: none;
}

.v-card__subtitle, .v-card__text, .v-card__title {
    padding: 0px;
}


</style>