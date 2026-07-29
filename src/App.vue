<script setup>
import { ref, computed, nextTick } from 'vue'
import MascotDisplay from './components/MascotDisplay.vue'
import { TOTAL_QUESTIONS, LEVELS } from './utils/constants.js'
import { getRandomEncouragement, getRandomComfort } from './utils/helpers.js'
import { generateQuestions } from './utils/questions.js'
import { renderHandsForNumber } from './utils/hands.js'
import { spawnConfetti, spawnMiniConfetti } from './utils/effects.js'
import * as api from './utils/api.js'

const currentScreen = ref('screen-menu')
const currentMode = ref('practice')
const currentLevelId = ref(1)
const currentQuestionIndex = ref(0)
const correctCount = ref(0)
const questions = ref([])
const timerSeconds = ref(30)
const timerInterval = ref(null)
const answered = ref(false)
const selectedOption = ref(null)
const mascotSpeech = ref('Halo! Ayo belajar <strong>Jarimatika</strong> bersamaku! 🖐️✨')
const mascotMouthClass = ref('happy')
const quizCardWiggle = ref(false)
const starsEarned = ref(0)

const currentUser = ref(null)
const loginTab = ref('login')
const loginEmail = ref('')
const loginPassword = ref('')
const loginName = ref('')
const loginLoading = ref(false)
const loginError = ref('')

const stars = ref({ 1: 0, 2: 0, 3: 0, 4: 0 })

const isLoggedIn = computed(() => currentUser.value !== null)

function loadSavedUser() {
  try {
    const raw = localStorage.getItem('jarimatika_user')
    if (raw) {
      const u = JSON.parse(raw)
      currentUser.value = u
      fetchStarsFromApi(u.id)
      return true
    }
  } catch { /* ignore */ }
  return false
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

async function handleRegister() {
  const email = loginEmail.value.trim()
  const password = loginPassword.value
  const displayName = loginName.value.trim()
  if (!email || !password || !displayName) {
    loginError.value = 'Isi email, password, dan nama panggilan!'
    return
  }
  if (password.length < 4) {
    loginError.value = 'Password minimal 4 karakter!'
    return
  }
  loginLoading.value = true
  loginError.value = ''
  try {
    const user = await api.registerUser(email, password, displayName)
    currentUser.value = { id: user.id, email: user.email, displayName: user.displayName }
    localStorage.setItem('jarimatika_user', JSON.stringify(currentUser.value))
    await fetchStarsFromApi(user.id)
    showScreen('screen-mode')
  } catch (err) {
    if (err.status === 409) {
      loginError.value = 'Email sudah terdaftar, silakan login.'
    } else if (err.status) {
      loginError.value = 'Gagal (kode ' + err.status + '). Cek console untuk detail.'
    } else {
      loginError.value = 'Tidak bisa hubungi server. Jalankan backend dulu!'
      console.error('Register error:', err)
    }
  } finally {
    loginLoading.value = false
  }
}

async function handleLogin() {
  const email = loginEmail.value.trim()
  const password = loginPassword.value
  if (!email || !password) {
    loginError.value = 'Isi email dan password!'
    return
  }
  loginLoading.value = true
  loginError.value = ''
  try {
    const user = await api.loginUser(email, password)
    currentUser.value = { id: user.id, email: user.email, displayName: user.displayName }
    localStorage.setItem('jarimatika_user', JSON.stringify(currentUser.value))
    await fetchStarsFromApi(user.id)
    showScreen('screen-mode')
  } catch (err) {
    if (err.status === 401) {
      loginError.value = 'Email atau password salah.'
    } else if (err.status) {
      loginError.value = 'Gagal (kode ' + err.status + '). Cek console.'
    } else {
      loginError.value = 'Tidak bisa hubungi server. Jalankan backend dulu!'
      console.error('Login error:', err)
    }
  } finally {
    loginLoading.value = false
  }
}

loadSavedUser()

function isLevelUnlocked(levelId) {
  if (levelId === 1) return true
  return (stars.value[levelId - 1] || 0) >= 3
}

const currentQuestion = computed(() => questions.value[currentQuestionIndex.value])

const correctAnswer = computed(() => {
  const q = currentQuestion.value
  return q ? q.correctAnswer : null
})

const progressText = computed(() => {
  return 'Soal ' + (currentQuestionIndex.value + 1) + '/' + TOTAL_QUESTIONS
})

const isChallenge = computed(() => currentMode.value === 'challenge')

const timerPercentage = computed(() => (timerSeconds.value / 30) * 100)

const timerBarClass = computed(() => {
  if (timerSeconds.value <= 5) return 'timer-bar-inner danger'
  if (timerSeconds.value <= 15) return 'timer-bar-inner warning'
  return 'timer-bar-inner'
})

const timerDisplayText = computed(() => {
  if (timerSeconds.value <= 5) return '⚠️ ' + timerSeconds.value + ' detik!'
  return '⏱ ' + timerSeconds.value + ' detik'
})

const timerTextClass = computed(() => {
  return timerSeconds.value <= 5 ? 'timer-text danger' : 'timer-text'
})

const handDisplayHtml = computed(() => {
  const q = currentQuestion.value
  if (!q) return ''
  return renderHandsForNumber(q.a) +
    '<span class="operator-symbol">+</span>' +
    renderHandsForNumber(q.b) +
    '<span class="equals-symbol">=</span>' +
    '<span class="question-mark">?</span>'
})

const resultStarsList = computed(() => {
  const earned = starsEarned.value
  return [1, 2, 3].map(i => ({ earned: i <= earned, delay: 0.1 + (i - 1) * 0.3 }))
})

const scoreText = computed(() => {
  return '✅ ' + correctCount.value + ' / ' + TOTAL_QUESTIONS + ' Benar (' + Math.round((correctCount.value / TOTAL_QUESTIONS) * 100) + '%)'
})

const resultTitle = computed(() => {
  if (starsEarned.value >= 3) return '🎉 Luar Biasa!'
  if (starsEarned.value >= 2) return '🌟 Bagus!'
  if (starsEarned.value >= 1) return '👍 Cukup Baik!'
  return '💪 Tetap Semangat!'
})

const resultMessage = computed(() => {
  if (starsEarned.value >= 3) return 'Kamu mendapatkan 3 bintang! Level berikutnya terbuka! 🚀'
  if (starsEarned.value >= 2) return 'Dapat 2 bintang! Tingkatkan lagi untuk membuka level berikutnya! ⭐'
  if (starsEarned.value >= 1) return '1 bintang! Ayo coba lagi untuk hasil lebih baik! 🌈'
  return 'Jangan menyerah! Latihan lagi ya, pasti bisa! 💪'
})

const canGoNextLevel = computed(() => starsEarned.value >= 3 && currentLevelId.value < 4)

const modeBadgeText = computed(() => {
  return currentMode.value === 'practice' ? '🧘 Mode Latihan' : '⚡ Mode Tantangan (30dtk)'
})

const quizLevelLabel = computed(() => {
  const level = LEVELS.find(l => l.id === currentLevelId.value)
  return level ? '⭐ ' + level.name : ''
})

const quizModeLabel = computed(() => {
  return currentMode.value === 'practice' ? '🧘 Latihan' : '⚡ Tantangan'
})

const quizModeColor = computed(() => {
  return currentMode.value === 'practice' ? 'var(--green)' : 'var(--primary)'
})

function showScreen(screenId) {
  currentScreen.value = screenId
  if (screenId === 'screen-menu') {
    mascotSpeech.value = 'Halo! Ayo belajar <strong>Jarimatika</strong> bersamaku! 🖐️✨'
  }
  if (screenId === 'screen-mode') {
    mascotSpeech.value = 'Pilih mode bermainmu ya! 😊'
  }
  if (screenId === 'screen-level') {
    mascotSpeech.value = 'Pilih level bintangmu! ⭐'
  }
}

function onMascotClick() {
  const funMessages = ['Halo! 🖐️✨', 'Kamu keren! 🌟', 'Ayo bermain! 🎮', 'Jarimatika itu seru! 🤩', 'Aku Kakak Jari! 👋', 'Semangat! 💪🌈']
  mascotSpeech.value = funMessages[Math.floor(Math.random() * funMessages.length)]
}

function selectMode(mode) {
  currentMode.value = mode
  showScreen('screen-level')
}

function startQuiz(levelId) {
  currentLevelId.value = levelId
  currentQuestionIndex.value = 0
  correctCount.value = 0
  questions.value = generateQuestions(levelId, TOTAL_QUESTIONS)
  answered.value = false
  selectedOption.value = null
  timerSeconds.value = 30
  clearTimer()
  showScreen('screen-quiz')
  mascotSpeech.value = 'Ayo, pasti bisa! 💪'
  mascotMouthClass.value = 'happy'
  nextTick(renderQuestion)
}

function renderQuestion() {
  if (currentQuestionIndex.value >= TOTAL_QUESTIONS) {
    endQuiz()
    return
  }
  answered.value = false
  selectedOption.value = null
  timerSeconds.value = 30
  clearTimer()
  mascotSpeech.value = 'Ayo, yang ini berapa ya? 🤔'
  mascotMouthClass.value = 'happy'
  if (isChallenge.value) {
    startTimer()
  }
}

function optionClass(opt) {
  let cls = 'btn btn-option'
  if (!answered.value) return cls
  if (opt === correctAnswer.value) cls += ' correct'
  if (opt === selectedOption.value && opt !== correctAnswer.value) cls += ' wrong'
  return cls
}

function dotClass(index) {
  if (index < currentQuestionIndex.value) {
    const q = questions.value[index]
    return q && q.userCorrect ? 'progress-dot done' : 'progress-dot wrong-dot'
  }
  if (index === currentQuestionIndex.value && !answered.value) {
    return 'progress-dot current'
  }
  return 'progress-dot'
}

function handleAnswer(selectedValue) {
  if (answered.value) return
  answered.value = true
  selectedOption.value = selectedValue
  clearTimer()

  const q = currentQuestion.value
  const isCorrect = selectedValue === q.correctAnswer
  q.userCorrect = isCorrect

  if (isCorrect) {
    correctCount.value++
    mascotSpeech.value = getRandomEncouragement()
    mascotMouthClass.value = 'cheer'
    nextTick(() => {
      const btn = document.querySelector('.btn-option.correct')
      if (btn) spawnMiniConfetti(btn)
    })
  } else {
    mascotSpeech.value = getRandomComfort()
    mascotMouthClass.value = ''
    quizCardWiggle.value = true
    setTimeout(() => { quizCardWiggle.value = false }, 400)
  }

  setTimeout(() => {
    currentQuestionIndex.value++
    if (currentQuestionIndex.value >= TOTAL_QUESTIONS) {
      endQuiz()
    } else {
      renderQuestion()
    }
  }, 1600)
}

function startTimer() {
  clearTimer()
  timerSeconds.value = 30
  timerInterval.value = setInterval(() => {
    timerSeconds.value--
    if (timerSeconds.value <= 0) {
      clearTimer()
      if (!answered.value) handleTimeout()
    }
  }, 1000)
}

function clearTimer() {
  if (timerInterval.value) {
    clearInterval(timerInterval.value)
    timerInterval.value = null
  }
}

function handleTimeout() {
  if (answered.value) return
  answered.value = true
  selectedOption.value = -1
  const q = currentQuestion.value
  q.userCorrect = false
  mascotSpeech.value = 'Waktunya habis! ⏰ Yuk lanjut!'
  mascotMouthClass.value = ''
  setTimeout(() => {
    currentQuestionIndex.value++
    if (currentQuestionIndex.value >= TOTAL_QUESTIONS) endQuiz()
    else renderQuestion()
  }, 1600)
}

async function endQuiz() {
  clearTimer()
  const correct = correctCount.value
  const percentage = Math.round((correct / TOTAL_QUESTIONS) * 100)

  let earned = 0
  if (percentage >= 90) earned = 3
  else if (percentage >= 70) earned = 2
  else if (percentage >= 50) earned = 1
  starsEarned.value = earned

  const prevStars = stars.value[currentLevelId.value] || 0
  if (earned > prevStars) {
    stars.value[currentLevelId.value] = earned
    saveLocalStars(stars.value)
  }

  if (isLoggedIn.value) {
    try {
      await api.submitQuizResult({
        userId: currentUser.value.id,
        levelId: currentLevelId.value,
        mode: currentMode.value,
        totalQuestions: TOTAL_QUESTIONS,
        correctCount: correct,
      })
      await fetchStarsFromApi(currentUser.value.id)
    } catch { /* ignore */ }
  }

  showScreen('screen-result')

  if (earned >= 3) {
    mascotSpeech.value = 'KAMU HEBAT SEKALI! 🎉🌟✨'
    mascotMouthClass.value = 'cheer'
    nextTick(() => spawnConfetti())
  } else if (earned >= 2) {
    mascotSpeech.value = 'Bagus! Tingkatkan lagi ya! ⭐'
    mascotMouthClass.value = 'happy'
  } else if (earned >= 1) {
    mascotSpeech.value = 'Coba lagi ya, kamu pasti bisa! 💪'
    mascotMouthClass.value = ''
  } else {
    mascotSpeech.value = 'Jangan menyerah! Ayo latihan! 🌈'
    mascotMouthClass.value = ''
  }
}

function retryLevel() {
  startQuiz(currentLevelId.value)
}

function goToNextLevel() {
  if (currentLevelId.value < 4) {
    currentLevelId.value++
    startQuiz(currentLevelId.value)
  }
}

function goToPlay() {
  if (isLoggedIn.value) {
    showScreen('screen-mode')
  } else {
    showScreen('screen-login')
  }
}
</script>

<template>
  <div>
    <Transition name="slide" mode="out-in">
      <div v-if="currentScreen === 'screen-menu'" key="menu" class="screen" style="display:flex;">
        <div class="card" style="padding: 32px 20px;">
          <MascotDisplay
            speech="Halo! Ayo belajar &lt;strong&gt;Jarimatika&lt;/strong&gt; bersamaku! 🖐️✨"
            mouth-class="happy"
            @mascot-click="onMascotClick"
          />
          <h1 class="title-main" style="margin-top:8px;">🌟 <span class="highlight">Jarimatika</span> Star Quiz 🌟</h1>
          <p style="color:#777;font-weight:600;">Kuis Matematika Jari yang Seru!</p>
          <div style="margin-top:4px;display:flex;gap:14px;flex-wrap:wrap;justify-content:center;">
            <button class="btn btn-primary btn-large" @click="goToPlay()">🎮 Mulai Bermain!</button>
          </div>
          <div v-if="isLoggedIn" style="margin-top:12px;font-size:0.85rem;color:#888;">
            👤 {{ currentUser.displayName }} ({{ currentUser.email }})
            <button class="btn btn-accent" style="padding:6px 16px;font-size:0.8rem;margin-left:8px;" @click="currentUser=null; localStorage.removeItem('jarimatika_user'); showScreen('screen-menu')">Logout</button>
          </div>
        </div>
      </div>

      <div v-else-if="currentScreen === 'screen-login'" key="login" class="screen" style="display:flex;">
        <div class="card" style="padding:28px 24px;">
          <MascotDisplay
            speech="Halo! Ayo daftar atau masuk! 😊"
            mouth-class="happy"
            @mascot-click="onMascotClick"
          />
          <div style="display:flex;gap:0;margin:12px auto 0;max-width:320px;border-radius:16px;overflow:hidden;border:3px solid var(--primary);">
            <button :style="{ flex:1, padding:'10px', fontWeight:700, fontSize:'0.95rem', border:'none', cursor:'pointer', background:loginTab==='login'?'var(--primary)':'#FFF0F0', color:loginTab==='login'?'#fff':'var(--primary)', transition:'all 0.2s' }" @click="loginTab='login';loginError=''">Masuk</button>
            <button :style="{ flex:1, padding:'10px', fontWeight:700, fontSize:'0.95rem', border:'none', cursor:'pointer', background:loginTab==='register'?'var(--primary)':'#FFF0F0', color:loginTab==='register'?'#fff':'var(--primary)', transition:'all 0.2s' }" @click="loginTab='register';loginError=''">Daftar</button>
          </div>
          <div style="display:flex;flex-direction:column;gap:12px;width:100%;max-width:320px;margin:16px auto;">
            <input v-model="loginEmail" type="email" placeholder="Email" style="padding:12px 16px;border-radius:16px;border:3px solid #E8E0D8;font-size:1rem;font-family:var(--font);outline:none;transition:border-color 0.2s;" @focus="$event.target.style.borderColor='var(--primary)'" @blur="$event.target.style.borderColor='#E8E0D8'">
            <input v-if="loginTab==='register'" v-model="loginName" type="text" placeholder="Nama Panggilan" style="padding:12px 16px;border-radius:16px;border:3px solid #E8E0D8;font-size:1rem;font-family:var(--font);outline:none;transition:border-color 0.2s;" @focus="$event.target.style.borderColor='var(--primary)'" @blur="$event.target.style.borderColor='#E8E0D8'">
            <input v-model="loginPassword" type="password" placeholder="Password" style="padding:12px 16px;border-radius:16px;border:3px solid #E8E0D8;font-size:1rem;font-family:var(--font);outline:none;transition:border-color 0.2s;" @focus="$event.target.style.borderColor='var(--primary)'" @blur="$event.target.style.borderColor='#E8E0D8'">
            <p v-if="loginError" style="color:var(--primary);font-weight:700;font-size:0.9rem;">{{ loginError }}</p>
            <button v-if="loginTab==='login'" class="btn btn-primary btn-large" style="width:100%;" :disabled="loginLoading" @click="handleLogin">
              {{ loginLoading ? '⏳ Sebentar...' : '🔑 Masuk' }}
            </button>
            <button v-if="loginTab==='register'" class="btn btn-primary btn-large" style="width:100%;" :disabled="loginLoading" @click="handleRegister">
              {{ loginLoading ? '⏳ Sebentar...' : '📝 Daftar' }}
            </button>
          </div>
          <button class="btn btn-accent" @click="showScreen('screen-menu')" style="margin-top:4px;">⬅ Kembali</button>
        </div>
      </div>

      <div v-else-if="currentScreen === 'screen-mode'" key="mode" class="screen" style="display:flex;">
        <div class="card">
          <MascotDisplay
            speech="Pilih mode bermainmu ya! 😊"
            mouth-class="happy"
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

      <div v-else-if="currentScreen === 'screen-level'" key="level" class="screen" style="display:flex;">
        <div class="card">
          <MascotDisplay
            speech="Pilih level bintangmu! ⭐"
            mouth-class="happy"
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

      <div v-else-if="currentScreen === 'screen-quiz'" key="quiz" class="screen" style="display:flex;">
        <div class="card" style="padding:20px;" :style="{ animation: quizCardWiggle ? 'wiggle 0.4s ease' : '' }">
          <div style="display:flex;justify-content:space-between;align-items:center;width:100%;flex-wrap:wrap;gap:8px;">
            <span style="font-weight:700;color:var(--text);">{{ quizLevelLabel }}</span>
            <span :style="{ fontWeight: 700, color: quizModeColor }">{{ quizModeLabel }}</span>
            <span style="font-weight:700;color:var(--text);">{{ progressText }}</span>
          </div>
          <div class="progress-dots" style="margin:8px 0;">
            <span v-for="i in TOTAL_QUESTIONS" :key="i" :class="dotClass(i - 1)"></span>
          </div>
          <div v-if="isChallenge" style="width:100%;margin-bottom:8px;">
            <div class="timer-bar-outer">
              <div class="timer-bar-inner" :class="timerBarClass" :style="{ width: timerPercentage + '%' }"></div>
            </div>
            <div :class="timerTextClass">{{ timerDisplayText }}</div>
          </div>
          <div class="hand-display" v-html="handDisplayHtml"></div>
          <p style="font-size:1.3rem;font-weight:700;color:var(--text);margin:4px 0;">Berapa hasil penjumlahannya? 🤔</p>
          <div class="options-grid">
            <button v-for="(opt, idx) in currentQuestion ? currentQuestion.options : []" :key="idx"
              :class="optionClass(opt)"
              :disabled="answered"
              @click="handleAnswer(opt)">
              {{ opt }}
            </button>
          </div>
          <div style="margin-top:8px;transform:scale(0.7);">
            <MascotDisplay
              :speech="mascotSpeech"
              :mouth-class="mascotMouthClass"
              @mascot-click="onMascotClick"
            />
          </div>
        </div>
      </div>

      <div v-else-if="currentScreen === 'screen-result'" key="result" class="screen" style="display:flex;">
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
    </Transition>

    <div id="confetti-container" style="position:fixed;top:0;left:0;width:100%;height:100%;pointer-events:none;z-index:998;"></div>
  </div>
</template>
