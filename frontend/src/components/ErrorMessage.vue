<template>
  <div class="state-box error-box" role="alert">
    <strong>{{ title }}</strong>
    <p>{{ message }}</p>
  </div>
</template>

<script setup>
defineProps({
  title: {
    type: String,
    default: 'Something went wrong',
  },
  message: {
    type: String,
    required: true,
  },
})
</script>
