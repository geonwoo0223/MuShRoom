<template>
    <div v-if="isRoom">
        <v-menu    
        bottom
        offset-y
        :close-on-content-click="false">
        <template v-slot:activator="{ on, attrs }">
            <span class="text-color">|</span>
            <v-btn
                class="text-color"
                dark="dark"
                text
                plain
                :retain-focus-on-click="false"
                @click="reset"
                v-bind="attrs"
                v-on="on">
                <v-icon size="26px" class="pr-2">mdi-share-variant</v-icon>
                Share
            </v-btn>
        </template>

        <v-list class="component-color" width="300px">
                <v-list-item v-for="(item, idx) in filtered_urls" :key="idx">
                    <v-list-item-content style="padding: 0px !important; margin-bottom: 5px;">
                        <v-list-item-title class="text-color" style="font-size:12pt !important; font-weight:bold;">
                            {{item.name}}
                        </v-list-item-title>
                        <div class="share-component">
                            <input
                                class="share-url-input"
                                v-model="item.url"
                                type="text"
                                readonly="readonly"
                                ref="textToCopy">
                            <v-btn
                                plain
                                fab="fab"
                                tile="tile"
                                text="text"
                                class="share-url-button"
                                @click="copyShareUrl(item.name)">
                                <v-icon small>mdi-content-copy</v-icon>
                            </v-btn>
                        </div>
                        <p v-if="copied === item.name" style="width: 12px; padding-left: 16px; font-size: 8px;">{{ item.name }} url copid on Clipboard!</p>
                    </v-list-item-content>
                </v-list-item>
            </v-list>
        </v-menu>
    </div>

</template>

<script>
import Config from '@/store/config'

export default {
    data() {
        return {
            roomNo:'',
            isRoom : false,
            copied: '',
        };
    },
    created() {
        this.status = this.$store.state.status;

        this.loc = document.location.href.split('?');
        if(this.loc.length > 1)
            this.isRoom = true;
        else
            this.isRoom = false;
    },
    methods: {
        copyShareUrl(name) {
            this.copied = name;
            let copied = this.$refs.textToCopy;
            if (name == "Musician") {
                copied[0].select()
                document.execCommand("copy");
            } else if (name == "Audience") {
                copied[1].select()
                document.execCommand("copy");
            }
        },
        reset() {
            this.copied = "";
        }
    },
    computed: {
        filtered_urls() {
            if (this.$store.state.status == "Master") {
                return [{
                    name: "Musician",
                    url: Config.HostURL + "/practiceroom/?shareUrl=" + this.$store.state.shareUrl[0]
                }, {
                    name: "Audience",
                    url: Config.HostURL + "/practiceroom/?shareUrl=" + this.$store.state.shareUrl[1]
                }];

            } else {
                return [{
                    name: "Audience",
                    url: Config.HostURL + "/practiceroom/?shareUrl=" + this.$store.state.shareUrl[1]
                }];

            }
        },
    },
}
</script>

<style >
.share-component {
    display: flex;
    align-items: center;
    height: 30px;
    border: 2px solid #bbbbbb;
    margin-left: 4px;
}
.share-url-input {
    background-color: white;
    border: 2px solid #bbbbbb;
    border-left: 0px;
    flex: 5;
    font-size: 12px;
    height: 30px !important;
    padding-left: 4px;
}
.share-url-button {
    height: 30px !important;
    width: 30px !important;
    flex: 1;
    border-left: 4px solid #bbbbbb;
    border: 2px solid #bbbbbb;
    background-color: #efefef;
}

</style>