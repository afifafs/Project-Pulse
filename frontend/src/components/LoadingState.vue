<template>
  <div class="state-box muted-box" aria-live="polite">
    <span class="spinner" aria-hidden="true"></span>
    <p>{{ label }}</p>
  </div>
</template>

<script setup>
defineProps({
  label: {
    type: String,
    default: 'Loading',
  },
})
</script>
