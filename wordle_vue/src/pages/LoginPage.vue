<script>
import { signInWithGoogle, signInAnonymouslyUser } from "@/firebase";
export default {
  data() {
    return {
      error: null,
    };
  },
  methods: {
    async loginWithGoogle() {
      try {
        await signInWithGoogle();
        this.$router.push('/multi'); //Weiterleitung auf die Multi-Seite
      } catch (err) {
        this.error = err.message;
      }
    },
    async loginAnonym() {
      try {
        await signInAnonymouslyUser();
        this.$router.push('/multi'); //Weiterleitung auf die Multi-Seite
      } catch (err) {
        this.error = err.message;
      }
    },
  },
};
</script>

<template>
  <v-container>
    <h2>Login</h2>
    <v-row class="button-row" justify="center">
      <v-col cols="12" sm="auto">
        <v-btn
            prepend-icon="mdi-google"
            @click="loginWithGoogle"
            color = "red"
        >
          Login mit Google
        </v-btn>
      </v-col>
      <v-col cols="12" sm="auto">
        <v-btn
            prepend-icon="mdi-account"
            @click="loginAnonym"
            color="grey"
        >
          Anonym einloggen
        </v-btn>
      </v-col>
    </v-row>
    <p v-if="error">{{ error }}</p>
  </v-container>
</template>

<style scoped>

</style>