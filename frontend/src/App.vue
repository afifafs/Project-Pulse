<script setup>
import SeniorDesignTeamEditView from './views/SeniorDesignTeamEditView.vue'
</script>

<template>
  <main class="app-shell">
    <header class="app-header">
      <div>
        <p class="eyebrow">Project Pulse</p>
        <h1>Edit senior design team</h1>
      </div>
      <p class="header-copy">
        Load a team, update its name and members, then save changes.
      </p>
    </header>

    <SeniorDesignTeamEditView />
  </main>
</template>
