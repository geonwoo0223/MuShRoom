<template>
    <v-container id="alertModal">
        <v-dialog
        v-model="showAlert"
        max-width="400"
        persistent>
            <v-card
            class="d-flex flex-column main-color-light" 
            height="auto"
            >
                <v-card-title class="card-title">
                    <div>
                        <div style="color: #ffffffbb; padding-top: 5px; padding-left:5px">{{ title }}</div>
                   </div>
                </v-card-title>
                <v-card-text class="pl-10 pt-2 d-flex text-color align-center">
                    {{ content }}
                </v-card-text>
                <v-card-actions class="card-actions">
                    <v-btn color="#ffffffbb" text plain @click="close" >확인</v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>
    </v-container>
</template>

<script>
export default {
    props: ['showAlert', 'title', 'content'],
    data() {
        return {
            // 
        }
    },
    methods: {
        close() {
            this.$emit('close');
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