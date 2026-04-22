<script setup>
import SectionDirectory from './views/SectionDirectory.vue'
</script>

<template>
  <main class="app-shell">
    <header class="app-header">
      <div>
        <p class="eyebrow">Project Pulse</p>
        <h1>Senior design sections</h1>
      </div>
      <p class="header-copy">
        Search and review available senior design course sections.
      </p>
    </header>

    <SectionDirectory />
  </main>
</template>
