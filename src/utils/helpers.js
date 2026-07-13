export function shuffleArray(arr) {
    const a = [...arr];
    for (let i = a.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [a[i], a[j]] = [a[j], a[i]];
    }
    return a;
}

export function getRandomEncouragement() {
    const phrases = ['Bagus sekali! 🎉', 'Hebat! Kamu pintar! 🌟', 'Betul! Lanjutkan! 💪', 'Keren! Jawaban tepat! ✨', 'Wah, kamu jago! 👏', 'Tepat! Ayo terus! 🚀'];
    return phrases[Math.floor(Math.random() * phrases.length)];
}

export function getRandomComfort() {
    const phrases = ['Ups, coba lagi ya! 💪', 'Semangat! Jangan menyerah! 🌈', 'Hampir benar! Tetap semangat! 😊', 'Tidak apa-apa, ayo lanjut! ⭐', 'Kamu pasti bisa berikutnya! 🌟'];
    return phrases[Math.floor(Math.random() * phrases.length)];
}
