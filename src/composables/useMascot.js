import { ref } from 'vue'

const mascotSpeech = ref('Halo! Ayo belajar <strong>Jarimatika</strong> bersamaku! 🖐️✨')
const mascotMouthClass = ref('happy')

export function useMascot() {
  function onMascotClick() {
    const funMessages = ['Halo! 🖐️✨', 'Kamu keren! 🌟', 'Ayo bermain! 🎮', 'Jarimatika itu seru! 🤩', 'Aku Kakak Jari! 👋', 'Semangat! 💪🌈']
    mascotSpeech.value = funMessages[Math.floor(Math.random() * funMessages.length)]
  }

  return { mascotSpeech, mascotMouthClass, onMascotClick }
}
