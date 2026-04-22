<script setup>
import RubricCreateView from './views/RubricCreateView.vue'
</script>

<template>
  <main class="app-shell">
    <header class="app-header">
      <div>
        <p class="eyebrow">Project Pulse</p>
        <h1>Create rubric</h1>
      </div>
      <p class="header-copy">
        Build a rubric with multiple scoring criteria and send it to the backend.
      </p>
    </header>

    <RubricCreateView />
  </main>
</template>
