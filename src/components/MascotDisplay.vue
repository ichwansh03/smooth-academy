<script setup>
import { ref } from 'vue'

defineProps({
  speech: { type: String, default: '' },
  mouthClass: { type: String, default: 'happy' },
})

const emit = defineEmits(['mascotClick'])
const bubbleRef = ref(null)

function onClick() {
  emit('mascotClick')
}

function triggerBounce() {
  if (!bubbleRef.value) return
  bubbleRef.value.style.animation = 'none'
  void bubbleRef.value.offsetHeight
  bubbleRef.value.style.animation = 'bounceIn 0.5s ease'
}

defineExpose({ triggerBounce })
</script>

<template>
  <div class="mascot-container">
    <div class="mascot" @click="onClick">
      <div class="mascot-hat"></div>
      <div class="mascot-face">
        <div class="mascot-eyes">
          <div class="mascot-eye"></div>
          <div class="mascot-eye"></div>
        </div>
        <div class="mascot-cheek left"></div>
        <div class="mascot-cheek right"></div>
        <div class="mascot-mouth" :class="mouthClass"></div>
      </div>
    </div>
    <div ref="bubbleRef" class="speech-bubble" v-html="speech"></div>
  </div>
</template>
