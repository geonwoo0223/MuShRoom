<template>
    <!-- id 값에 따라 채팅 칸 위치, 색 달라짐 -->
    <div>
        <div
        v-if="!isSameId()"
        style="width: 150px; font-size: 14px;"
        :class="msg.id==id ? 'myInfo text-right':'yourInfo'">
            {{ msg.nickName }}
        </div>
        <v-card
        light
        width="150px"
        :class="msg.id==id ? 'mine' : 'others'"
        class="px-2"
        style="font-size: 14px; border-radius: 10px;"
        dark
        elevation="0">
            {{ msg.text }}
        </v-card>
        <div
        v-if="showTime()"
        :class="msg.id==id ? 'myInfo':'yourInfoTime'"
        style="width: 140px; font-size : 10px; color : grey;">
            {{ msg.time | time() }}
        </div>
    </div>
</template>

<script>
import Vue from 'vue';

Vue.filter("time", (value) => {

    const temp = value.split(":");
    var hour = temp[0];
    var min = temp[1];
    if (temp[0].length == 1) {
        hour = '0' + temp[0];
    }
    if (temp[1].length == 1 ) {
        min = '0' + temp[1];
    }

    return hour + ':' + min;

})


export default {
    props: ['msg', 'id', 'msgList', 'idx'],
    methods: {
        isSameId() {
            if (this.idx < 1) {
                return false;
            } else {
                if (this.msgList[this.idx].id == this.msgList[this.idx-1].id) {
                    return true;
                } else {
                    return false;
                }
            }
        },
        isSameTime() {
            if (this.idx < 1) {
                return false;
            } else {
                if (this.msgList[this.idx].time == this.msgList[this.idx-1].time) {
                    return true;
                } else {
                    return false;
                }
            }
        },
        showTime() {
            if (this.idx == this.msgList.length -1 ) {
                return true;
            } else {
                if ( this.msgList[this.idx].id != this.msgList[this.idx+1].id) {
                    return true
                }
                if ( this.msgList[this.idx].time != this.msgList[this.idx+1].time) {
                    return true;
                }

                return false;
            }
        }
    },
}
</script>

<style>
.mine {
    float: right;
    margin-top: 2px;
    background-color: #00A2FF !important;
}

.others{
    float: left;
    margin-top: 2px;
    background-color: #003D5E !important;
}

.myInfo {
    float: right;
}

.yourInfo {
    float: left;
}

.yourInfoTime {
    float:left;
    padding-left:110px;
}

</style>