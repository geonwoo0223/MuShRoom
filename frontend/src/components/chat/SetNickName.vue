<template>
    <v-container id="registerModal">
        <v-dialog 
        overlay-color="white"
        v-model="showModal"
        max-width="500"
        persistent>
            <v-card
            class="d-flex flex-column main-color" 
            height="auto"
            >
                <v-card-title class="card-title">
                    <div>
                        <div style="color: #ffffffbb">닉네임 설정</div>
                        <div style="font-size: 16px; color: grey;">
                        <v-icon small color="grey">mdi-alert-circle</v-icon>
                            닉네임 설정은 처음에만 가능합니다
                        </div>
                   </div>
                </v-card-title>
                <v-card-text class="mx-auto pt-2 d-flex justify-space-between align-center">
                    <v-text-field 
                    style="width: 50px !important;" 
                    color="#ffffffbb"
                    dark
                    autofocus
                    label="닉네임: " 
                    v-model="nickName"
                    :rules="rule"
                    @keyup.enter="enter()"
                    ></v-text-field>
                </v-card-text>
                <v-card-actions class="card-actions">
                    <v-btn color="#ffffffbb" text @click="close" >취소</v-btn>
                    <v-btn color="#ffffffbb" text @click="enter" >채팅하기</v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>
    </v-container>
</template>

<script>
export default {
    props: ['showModal'],
    data() {
        return {
            nickName: '',
            rule: [val => (val || '').length > 0 || '닉네임을 입력해주세요']
        }
    },
    methods: {
        close() {
            this.$emit('close');
        },
        enter() {
            // 닉네임이 빈칸이면 채팅 불가
            if(this.nickName.length==0)
                return;

            this.$emit('setNickName', this.nickName);
        },
    },
}
</script>

<style scoped lang="scss">

.card-title {
    padding: 10px !important;
}

.card-title > p {
    color: white;
}

label {
    color: black;
    font-size: 1rem;
    font-weight: 700;
}

.card-actions {
    display: flex;
    justify-content: center;
}




</style>