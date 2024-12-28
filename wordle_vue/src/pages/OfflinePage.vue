<!-- wordle_vue/src/pages/OfflinePage.vue -->
<template>
  <div class="offline-page">
    <h1>Tic Tac Toe</h1>
    <p>Du bist offline. Spiele eine Runde Tic Tac Toe!</p>

    <div class="board">
      <div
          v-for="(cell, index) in board"
          :key="index"
          class="cell"
          :class="{ 'x': cell === 'X', 'o': cell === 'O' }"
          @click="makeMove(index)"
      >
        {{ cell }}
      </div>
    </div>

    <p v-if="winner" class="winner">
      {{ winner === 'Tie' ? 'Unentschieden!' : `${winner} gewinnt!` }}
    </p>
    <button @click="resetGame">Neustart</button>
  </div>
</template>

<script>
import { ref } from "vue";

export default {
  name: "OfflinePage",
  setup() {
    const board = ref(Array(9).fill(null)); // Spielfeld
    const currentPlayer = ref("X"); // Startspieler
    const winner = ref(null); // Gewinner oder unentschieden

    // Überprüfen, ob jemand gewonnen hat
    const checkWinner = () => {
      const winningCombinations = [
        [0, 1, 2],
        [3, 4, 5],
        [6, 7, 8],
        [0, 3, 6],
        [1, 4, 7],
        [2, 5, 8],
        [0, 4, 8],
        [2, 4, 6],
      ];

      for (let combination of winningCombinations) {
        const [a, b, c] = combination;
        if (
            board.value[a] &&
            board.value[a] === board.value[b] &&
            board.value[a] === board.value[c]
        ) {
          winner.value = board.value[a];
          return;
        }
      }

      if (!board.value.includes(null)) {
        winner.value = "Tie"; // Unentschieden
      }
    };

    // Spielerzug machen
    const makeMove = (index) => {
      if (board.value[index] || winner.value) return;

      board.value[index] = currentPlayer.value;
      currentPlayer.value = currentPlayer.value === "X" ? "O" : "X";
      checkWinner();
    };

    // Spiel zurücksetzen
    const resetGame = () => {
      board.value = Array(9).fill(null);
      currentPlayer.value = "X";
      winner.value = null;
    };

    return { board, currentPlayer, winner, makeMove, resetGame };
  },
};
</script>

<style scoped>
.offline-page {
  text-align: center;
  padding: 20px;
  font-family: Arial, sans-serif;
}

.board {
  display: grid;
  grid-template-columns: repeat(3, 100px);
  gap: 5px;
  justify-content: center;
  margin: 20px auto;
}

.cell {
  width: 100px;
  height: 100px;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 24px;
  background-color: #f0f0f0;
  border: 2px solid #ccc;
  cursor: pointer;
}

.cell.x {
  color: #ff4500;
}

.cell.o {
  color: #1e90ff;
}

.winner {
  font-size: 18px;
  margin-top: 20px;
  font-weight: bold;
}

button {
  padding: 10px 20px;
  margin-top: 20px;
  font-size: 16px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

button:hover {
  background-color: #0056b3;
}
</style>
