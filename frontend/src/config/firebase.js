import { initializeApp } from 'firebase/app';
import { getAuth, GoogleAuthProvider, signInWithPopup, signOut } from 'firebase/auth';
import { getAnalytics } from 'firebase/analytics';

// Firebase configuration
const firebaseConfig = {
    apiKey: "AIzaSyDMwwGoA-tSmA9eEs6pGrCivFtdNMOIheQ",
    authDomain: "decentral-bay.firebaseapp.com",
    projectId: "decentral-bay",
    storageBucket: "decentral-bay.appspot.com",
    messagingSenderId: "214530034235",
    appId: "1:214530034235:web:efd49b8193333640266a1e",
    measurementId: "G-HP4RGMVR8Z"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);
const analytics = getAnalytics(app);
const auth = getAuth(app);
const googleProvider = new GoogleAuthProvider();

export { auth, googleProvider, signInWithPopup, signOut, analytics };
