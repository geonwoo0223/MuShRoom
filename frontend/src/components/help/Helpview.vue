<template>
  <v-container>
    <v-sheet height="420" color="323232 " elevation="20" rounded>
      <v-row no-gutters class="d-flex flex-column justify-center">
        <v-sheet
          height="340"
          color="#1e1e1e"
          rounded
          class="d-flex align-center justify-center"
        >
          <div>
            <v-img
              max-height="380"
              max-width="500"
              contain
              :src="src"
            ></v-img></div
        ></v-sheet>
        <v-divider></v-divider>
        <div class="d-flex justify-center align-center flex-column">
          <v-sheet width="100%" color="#1e1e1e"
            ><div
              class="d-flex align-center justify-center pa-4 text-center"
              v-html="describe[0]"
            ></div
          ></v-sheet>
        </div>
      </v-row>
    </v-sheet>
  </v-container>
</template>

<script>
export default {
  props: {
    describe: Array,
    src: String,
  },
  methods: {
    changeHelpState() {
      this.$store.commit("helpShowChange");
    },
  },
};
</script>

<style>
</style>