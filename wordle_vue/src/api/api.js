import axios from "axios";

const API_BASE_URL = "http://localhost:9000";
axios.defaults.withCredentials = true; // Stelle sicher, dass Cookies immer gesendet werden


export default {
    getGameboard() {
        return axios.get(`${API_BASE_URL}/gameboard`);
    },
    nextRound() {
        return axios.get(`${API_BASE_URL}/round`);
    },
    endGame(input) {
        return axios.get(`${API_BASE_URL}/gameOver/${input}`);
    },
    newGame(input, mode, name){
      return axios.get(`${API_BASE_URL}/new/${input}/${mode}/${name}`)
    },
    stopGame(){
      return axios.get(`${API_BASE_URL}/stop`)
    },
    processInput(inputData){
        return axios.post(`${API_BASE_URL}/play`, inputData)
    }
};