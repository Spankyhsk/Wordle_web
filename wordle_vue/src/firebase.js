// Import the functions you need from the SDKs you need
import { initializeApp } from "firebase/app";
import { getAuth, GoogleAuthProvider, signInWithPopup, signInAnonymously } from "firebase/auth";
import { getAnalytics } from "firebase/analytics";
import { logEvent } from "firebase/analytics";

// Your web app's Firebase configuration
const firebaseConfig = {
    apiKey: "AIzaSyAtKUNZYpn7cGvFAwGdP4h8fTE3zjw2c3o",
    authDomain: "wordle-webapplication.firebaseapp.com",
    projectId: "wordle-webapplication",
    storageBucket: "wordle-webapplication.firebasestorage.app",
    messagingSenderId: "734010675109",
    appId: "1:734010675109:web:ca7f9abedf3c8b342ba24c",
    measurementId: "G-TV72EKK1EM"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);

// Initialisiere und exportiere den Authentifizierungsdienst
export const auth = getAuth(app);
const analytics = getAnalytics(app);

// Google Provider exportieren
export const googleProvider = new GoogleAuthProvider();

// Funktionen für Login
export const signInWithGoogle = async () => {
    try {
        const result = await signInWithPopup(auth, googleProvider);
        const user = result.user;
        logEvent(analytics, 'login', {
            method: 'Google',
            userId: user.uid
        });
        return user; // Gibt den User zurück, falls du ihn weiterverarbeiten willst
    } catch (error) {
        console.error("Fehler beim Google Login:", error.message);
        throw error;
    }
};

export const signInAnonymouslyUser = async () => {
    try {
        const result = await signInAnonymously(auth);
        const user = result.user;
        logEvent(analytics, 'login', {
            method: 'Anonym',
            userId: user.uid
        });
        return user; // Gibt den User zurück
    } catch (error) {
        console.error("Fehler beim anonymen Login:", error.message);
        throw error;
    }
};