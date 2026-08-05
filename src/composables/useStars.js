import { ref } from 'vue'
import * as api from '../utils/api.js'

const stars = ref({ 1: 0, 2: 0, 3: 0, 4: 0 })

export function useStars() {
  function loadLocalStars() {
    try {
      const s = localStorage.getItem('jarimatika_stars')
      if (s) stars.value = JSON.parse(s)
    } catch { /* ignore */ }
  }

  function saveLocalStars(s) {
    try {
      localStorage.setItem('jarimatika_stars', JSON.stringify(s))
    } catch { /* ignore */ }
  }

  async function fetchStarsFromApi(userId) {
    try {
      const results = await api.getUserResults(userId)
      const best = { 1: 0, 2: 0, 3: 0, 4: 0 }
      for (const r of results) {
        const lid = r.level.id || r.levelId
        const s = r.starsEarned
        if (s > (best[lid] || 0)) best[lid] = s
      }
      stars.value = best
      saveLocalStars(best)
    } catch {
      loadLocalStars()
    }
  }

  function isLevelUnlocked(levelId) {
    if (levelId === 1) return true
    return (stars.value[levelId - 1] || 0) >= 3
  }

  return { stars, loadLocalStars, saveLocalStars, fetchStarsFromApi, isLevelUnlocked }
}
