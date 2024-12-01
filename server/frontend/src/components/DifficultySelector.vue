<script>
export default {
  name: "DifficultySelector"
}
</script>

<template>
  <div class="difficulty-selector">
    <h2>Wähle deine Schwierigkeitsstufe</h2>
    <a href="@routes.WordleController.newgame(1, 'solo', 'anonym')" class="btn btn-primary" role="button">Einfach</a><br>
    <a href="@routes.WordleController.newgame(2, 'solo', 'anonym')" class="btn btn-primary" role="button">Mittel</a><br>
    <a href="@routes.WordleController.newgame(3, 'solo', 'anonym')" class="btn btn-primary" role="button">Schwer</a><br>
  </div>
</template>

<style scoped>
/* todo: noch nicht fertig (muss warten bis ich die seite sehen kann) */

.difficulty-selector {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  margin-top: 2vw;
  font-size: 2.5em;
}
</style>