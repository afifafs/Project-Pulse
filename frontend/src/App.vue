<script setup>
import SectionTeamAssignmentsView from './views/SectionTeamAssignmentsView.vue'
</script>

<template>
  <main class="app-shell">
    <header class="app-header">
      <div>
        <p class="eyebrow">Project Pulse</p>
        <h1>Assign students</h1>
      </div>
      <p class="header-copy">
        Assign senior design students to teams in a selected section.
      </p>
    </header>

    <SectionTeamAssignmentsView />
  </main>
</template>
