import { CONFETTI_COLORS } from './constants.js';

export function spawnConfetti() {
    const container = document.getElementById('confetti-container');
    for (let i = 0; i < 60; i++) {
        const piece = document.createElement('div');
        piece.className = 'confetti-piece';
        piece.style.left = Math.random() * 100 + '%';
        piece.style.top = -(Math.random() * 40 + 10) + 'px';
        piece.style.backgroundColor = CONFETTI_COLORS[Math.floor(Math.random() * CONFETTI_COLORS.length)];
        piece.style.width = (Math.random() * 10 + 6) + 'px';
        piece.style.height = (Math.random() * 10 + 6) + 'px';
        piece.style.animationDuration = (Math.random() * 2 + 2) + 's';
        piece.style.animationDelay = Math.random() * 0.8 + 's';
        piece.style.borderRadius = Math.random() > 0.5 ? '50%' : '3px';
        container.appendChild(piece);
        setTimeout(() => piece.remove(), 3500);
    }
}

export function spawnMiniConfetti(element) {
    const rect = element.getBoundingClientRect();
    const container = document.getElementById('confetti-container');
    const cx = rect.left + rect.width / 2;
    const cy = rect.top + rect.height / 2;
    const miniColors = ['#FFD700', '#FF6B6B', '#4ECDC4', '#A78BFA', '#FFE66D'];
    for (let i = 0; i < 20; i++) {
        const piece = document.createElement('div');
        piece.className = 'confetti-piece';
        piece.style.position = 'fixed';
        piece.style.left = cx + 'px';
        piece.style.top = cy + 'px';
        piece.style.backgroundColor = miniColors[Math.floor(Math.random() * miniColors.length)];
        piece.style.width = '8px';
        piece.style.height = '8px';
        piece.style.borderRadius = '50%';
        piece.style.animation = 'none';
        piece.style.transition = 'all 0.8s ease-out';
        piece.style.opacity = '1';
        piece.style.zIndex = '999';
        container.appendChild(piece);
        const dx = (Math.random() - 0.5) * 160;
        const dy = -(Math.random() * 100 + 40);
        requestAnimationFrame(() => {
            piece.style.transform = `translate(${dx}px, ${dy}px) scale(0)`;
            piece.style.opacity = '0';
        });
        setTimeout(() => piece.remove(), 900);
    }
}
