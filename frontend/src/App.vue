<script setup>
import SeniorDesignTeamsView from './views/SeniorDesignTeamsView.vue'
</script>

<template>
  <main class="app-shell">
    <header class="app-header">
      <div>
        <p class="eyebrow">Project Pulse</p>
        <h1>Senior design teams</h1>
      </div>
      <p class="header-copy">
        Find teams and optionally filter by senior design section.
      </p>
    </header>

    <SeniorDesignTeamsView />
  </main>
</template>
