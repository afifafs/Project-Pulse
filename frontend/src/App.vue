<script setup>
import SectionDirectory from './views/SectionDirectory.vue'
</script>

<template>
  <main class="app-shell">
    <header class="app-header">
      <div>
        <p class="eyebrow">Project Pulse</p>
        <h1>Course sections</h1>
      </div>
      <p class="header-copy">
        Search sections and review rosters, teams, instructors, and rubric details.
      </p>
    </header>

    <SectionDirectory />
  </main>
</template>
