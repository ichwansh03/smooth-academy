<script setup>
import MascotDisplay from '../MascotDisplay.vue'
import { useMascot } from '../../composables/useMascot.js'
import { useNavigation } from '../../composables/useNavigation.js'
import { useAuth } from '../../composables/useAuth.js'

const { mascotSpeech, mascotMouthClass, onMascotClick } = useMascot()
const { showScreen } = useNavigation()
const {
  loginTab,
  loginEmail,
  loginPassword,
  loginName,
  loginLoading,
  loginError,
  handleLogin,
  handleRegister,
} = useAuth()
</script>

<template>
  <div class="screen" style="display:flex;">
    <div class="card" style="padding:28px 24px;">
      <MascotDisplay
        :speech="mascotSpeech"
        :mouth-class="mascotMouthClass"
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
</template>
