<template>
  <div class="state-box muted-box">
    <p>{{ message }}</p>
  </div>
</template>

<script setup>
defineProps({
  message: {
    type: String,
    required: true,
  },
})
</script>
