import { LEVELS } from './constants.js';
import { shuffleArray } from './helpers.js';

export function generateDistractors(correct, levelId) {
    const distractors = new Set();
    let range;
    if (levelId === 1) range = [1, 6];
    else if (levelId === 2) range = [3, 20];
    else if (levelId === 3) range = [10, 50];
    else range = [20, 200];
    const maxAllowed = levelId === 1 ? 9 : Infinity;
    let attempts = 0;
    while (distractors.size < 3 && attempts < 100) {
        const offset = Math.floor(Math.random() * (range[1] - range[0] + 1)) + range[0];
        const sign = Math.random() < 0.5 ? -1 : 1;
        const candidate = correct + sign * offset;
        if (candidate !== correct && candidate > 0 && candidate <= maxAllowed && !distractors.has(candidate)) {
            distractors.add(candidate);
        }
        attempts++;
    }
    while (distractors.size < 3) {
        if (levelId === 1) {
            const fallback = Math.floor(Math.random() * 9) + 1;
            if (fallback !== correct && !distractors.has(fallback)) {
                distractors.add(fallback);
            }
        } else {
            const fallback = correct + distractors.size + 1 + Math.floor(Math.random() * 5);
            if (!distractors.has(fallback) && fallback !== correct && fallback > 0) {
                distractors.add(fallback);
            }
        }
    }
    return Array.from(distractors).slice(0, 3);
}

export function generateQuestions(levelId, count) {
    const level = LEVELS.find(l => l.id === levelId);
    const min = level.range[0];
    const max = level.range[1];
    const questions = [];
    const usedPairs = new Set();
    for (let i = 0; i < count; i++) {
        let a, b, key;
        let attempts = 0;
        do {
            a = Math.floor(Math.random() * (max - min + 1)) + min;
            b = Math.floor(Math.random() * (max - min + 1)) + min;
            if (a + b > max * 2.5 && levelId >= 3) {
                a = Math.floor(Math.random() * (max - min + 1)) + min;
                b = Math.floor(Math.random() * (max - min + 1)) + min;
            }
            key = a + '-' + b;
            attempts++;
        } while ((usedPairs.has(key) || (levelId === 1 && a + b > 9)) && attempts < 50);
        usedPairs.add(key);
        const correctAnswer = a + b;
        const distractors = generateDistractors(correctAnswer, levelId);
        questions.push({ a, b, correctAnswer, options: shuffleArray([correctAnswer, ...distractors]) });
    }
    return questions;
}
