<script setup>
import MascotDisplay from '../MascotDisplay.vue'
import { useMascot } from '../../composables/useMascot.js'
import { useNavigation } from '../../composables/useNavigation.js'
import { useQuiz } from '../../composables/useQuiz.js'

const { mascotSpeech, mascotMouthClass, onMascotClick } = useMascot()
const { showScreen } = useNavigation()
const {
  resultTitle,
  resultStarsList,
  scoreText,
  resultMessage,
  retryLevel,
  canGoNextLevel,
  goToNextLevel,
} = useQuiz()
</script>

<template>
  <div class="screen" style="display:flex;">
    <div class="card" style="padding:28px 20px;">
      <MascotDisplay
        :speech="mascotSpeech"
        :mouth-class="mascotMouthClass"
        @mascot-click="onMascotClick"
      />
      <h2 :style="{ fontSize: '1.5rem', color: 'var(--text)' }">{{ resultTitle }}</h2>
      <div class="result-stars">
        <span v-for="(star, idx) in resultStarsList" :key="idx"
          class="big-star"
          :style="{ animationDelay: star.delay + 's', color: star.earned ? '#FFD700' : '#DDD' }">
          {{ star.earned ? '⭐' : '☆' }}
        </span>
      </div>
      <div class="score-badge" style="margin:8px 0;">{{ scoreText }}</div>
      <p style="font-weight:600;color:#777;">{{ resultMessage }}</p>
      <div style="display:flex;gap:12px;flex-wrap:wrap;justify-content:center;margin-top:8px;">
        <button class="btn btn-primary" @click="retryLevel()">🔄 Coba Lagi</button>
        <button v-if="canGoNextLevel" class="btn btn-secondary" @click="goToNextLevel()">▶ Level Berikutnya</button>
        <button class="btn btn-accent" @click="showScreen('screen-level')">📋 Pilih Level</button>
      </div>
    </div>
  </div>
</template>
