<template>
    <v-footer 
    absolute
    padless color="#00ff0000">
        <v-card class="text-center"
        elevation="0"
        color="#00ff0000"
        tile
        width="100%">
            <v-img
            v-for="img in team"
            :key="img"
            :src="img"
            width="100"
            height="100"
            class="d-inline-block">
            </v-img>
        </v-card>
    </v-footer>
</template>

<script>
export default {
    data() {
        return {
            team: [
                require('@/assets/bremen/JSR.png'),
                require('@/assets/bremen/KYW.png'),
                require('@/assets/bremen/SGH.png'),
                require('@/assets/bremen/LGW.png'),
                require('@/assets/bremen/LBH.png'),
                require('@/assets/bremen/JJH.png'),
            ],
            img: require("@/assets/bremen.png")
        }
    }
}
</script>

<style>

</style>