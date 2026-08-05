<script setup>
import MascotDisplay from '../MascotDisplay.vue'
import { useMascot } from '../../composables/useMascot.js'
import { useNavigation } from '../../composables/useNavigation.js'
import { useQuiz } from '../../composables/useQuiz.js'

const { mascotSpeech, mascotMouthClass, onMascotClick } = useMascot()
const { showScreen } = useNavigation()
const { selectMode } = useQuiz()
</script>

<template>
  <div class="screen" style="display:flex;">
    <div class="card">
      <MascotDisplay
        :speech="mascotSpeech"
        :mouth-class="mascotMouthClass"
        @mascot-click="onMascotClick"
      />
      <h2 style="font-size:1.6rem;color:var(--text);">Pilih Mode</h2>
      <div style="display:flex;gap:16px;flex-wrap:wrap;justify-content:center;">
        <button class="btn btn-secondary btn-large" @click="selectMode('practice')" style="min-width:160px;">
          🧘 Latihan<br><small style="font-weight:400;">Tanpa Timer</small>
        </button>
        <button class="btn btn-primary btn-large" @click="selectMode('challenge')" style="min-width:160px;">
          ⚡ Tantangan<br><small style="font-weight:400;">30 Detik/Soal</small>
        </button>
      </div>
      <button class="btn btn-accent" @click="showScreen('screen-menu')" style="margin-top:8px;">⬅ Kembali</button>
    </div>
  </div>
</template>
