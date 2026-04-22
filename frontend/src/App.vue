<script setup>
import SectionActiveWeeksView from './views/SectionActiveWeeksView.vue'
</script>

<template>
  <main class="app-shell">
    <header class="app-header">
      <div>
        <p class="eyebrow">Project Pulse</p>
        <h1>Set active weeks</h1>
      </div>
      <p class="header-copy">
        Load a senior design section and choose which weeks are active.
      </p>
    </header>

    <SectionActiveWeeksView />
  </main>
</template>
