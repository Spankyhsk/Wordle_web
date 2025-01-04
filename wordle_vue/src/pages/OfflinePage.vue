<template>
  <div class="offline-page">
    <h1>Tic Tac Toe</h1>
    <p>Du spielst gegen einen Bot!</p>

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

    // Minimax Algorithmus zur Berechnung des besten Zugs für den Bot
    const minimax = (boardState, depth, isMaximizingPlayer) => {
      const scores = { X: -1, O: 1, Tie: 0 };

      // Überprüfen, ob das Spiel zu Ende ist
      checkWinner();

      if (winner.value === "X") return scores.X;
      if (winner.value === "O") return scores.O;
      if (!boardState.includes(null)) return scores.Tie;

      if (isMaximizingPlayer) {
        let best = -Infinity;
        for (let i = 0; i < 9; i++) {
          if (boardState[i] === null) {
            boardState[i] = "O";
            best = Math.max(best, minimax(boardState, depth + 1, false));
            boardState[i] = null;
          }
        }
        return best;
      } else {
        let best = Infinity;
        for (let i = 0; i < 9; i++) {
          if (boardState[i] === null) {
            boardState[i] = "X";
            best = Math.min(best, minimax(boardState, depth + 1, true));
            boardState[i] = null;
          }
        }
        return best;
      }
    };

    // Bestes Zug für den Bot
    const botMove = () => {
      if (winner.value) return; // Spiel ist vorbei, keine weiteren Züge

      let bestScore = -Infinity;
      let move = -1;

      // Suche nach dem besten Zug für den Bot (O)
      for (let i = 0; i < 9; i++) {
        if (board.value[i] === null) {
          board.value[i] = "O"; // Teste den Zug
          let score = minimax(board.value, 0, false); // Führe Minimax aus
          board.value[i] = null; // Rückgängig machen

          if (score > bestScore) {
            bestScore = score;
            move = i;
          }
        }
      }

      // Bot zieht an der besten Stelle
      if (move !== -1) {
        board.value[move] = "O";
        currentPlayer.value = "X"; // Jetzt ist der Spieler dran
        checkWinner();
      }
    };

    // Spielerzug machen
    const makeMove = (index) => {
      if (board.value[index] || winner.value || currentPlayer.value !== "X")
        return;

      board.value[index] = "X";
      currentPlayer.value = "O"; // Bot ist dran
      checkWinner();

      // Botzug ausführen (kurze Verzögerung für realistischeres Verhalten)
      setTimeout(() => {
        if (!winner.value) {
          botMove();
        }
      }, 500);
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

