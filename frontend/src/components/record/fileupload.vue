<template>
  <div class="mx-auto align-center d-flex justify-center">
    <v-sheet width="80%" class="d-flex align-center">
      <input
        ref="uploader"
        type="file"
        class="d-none"
        multiple
        accept="audio/*"
        @change="onFileChanged"
    /></v-sheet>
  </div>
</template>
<script>
import sendfile from "@/service/filecontrol";
import { getYyyyMmDdMmSsToString } from "@/lib/timestamp";
export default {
  data: () => ({
    files: undefined,
  }),
  methods: {
    onFileChanged(e) {
      this.files = e.target.files;
      this.upload();
    },
    inputClick() {
      this.$refs.uploader.click();
    },
    async upload() {
      if (this.files === undefined) {
        return;
      }
      // 파일 업로드 상태 emit
      this.$emit("fileUploading");
      for (let i = 0; i < this.files.length; i++) {
        const data = new FormData();
        var date = new Date();
        date = getYyyyMmDdMmSsToString(date);
        const file = { fileName: "", downloadURL: "" };
        data.append("file", this.files[i]);
        await sendfile
          .send(data)
          .then((result) => {
            file["fileName"] = result.data.fileName;
            file["downloadURL"] = result.data.fileDownloadUri;
            this.$emit("sendData", file);
          })
          .catch((err) => {
            console.debug("업로드 실패", err);
          });
      }
      this.$refs.uploader.value = "";
      //console.log(this.$refs.uploader.value);
    },
  },
};
</script>
<style>
</style>