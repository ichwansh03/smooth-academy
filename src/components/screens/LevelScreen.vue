<script setup>
import MascotDisplay from '../MascotDisplay.vue'
import { LEVELS } from '../../utils/constants.js'
import { useMascot } from '../../composables/useMascot.js'
import { useNavigation } from '../../composables/useNavigation.js'
import { useStars } from '../../composables/useStars.js'
import { useQuiz } from '../../composables/useQuiz.js'

const { mascotSpeech, mascotMouthClass, onMascotClick } = useMascot()
const { showScreen } = useNavigation()
const { stars, isLevelUnlocked } = useStars()
const { modeBadgeText, startQuiz } = useQuiz()
</script>

<template>
  <div class="screen" style="display:flex;">
    <div class="card">
      <MascotDisplay
        :speech="mascotSpeech"
        :mouth-class="mascotMouthClass"
        @mascot-click="onMascotClick"
      />
      <h2 style="font-size:1.4rem;color:var(--text);">Pilih Level</h2>
      <span :style="{ fontWeight: 700, color: 'var(--primary)', background: '#FFF0F0', padding: '6px 16px', borderRadius: '20px', fontSize: '0.9rem' }">{{ modeBadgeText }}</span>
      <div class="level-grid">
        <div v-for="lvl in LEVELS" :key="lvl.id"
          :class="['level-card', { locked: !isLevelUnlocked(lvl.id) }]"
          @click="isLevelUnlocked(lvl.id) && startQuiz(lvl.id)">
          <div class="level-icon">{{ lvl.icon }}</div>
          <div class="level-title">{{ lvl.name }}</div>
          <div class="level-range">🔢 {{ lvl.label }}</div>
          <div class="level-stars">
            <span v-for="s in 3" :key="s"
              :class="['star', { earned: s <= (stars[lvl.id] || 0) }]">
              {{ s <= (stars[lvl.id] || 0) ? '⭐' : '☆' }}
            </span>
          </div>
          <div v-if="!isLevelUnlocked(lvl.id)" class="lock-icon">🔒</div>
        </div>
      </div>
      <button class="btn btn-accent" @click="showScreen('screen-mode')" style="margin-top:4px;">⬅ Ganti Mode</button>
    </div>
  </div>
</template>
