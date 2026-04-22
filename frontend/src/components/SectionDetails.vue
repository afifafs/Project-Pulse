<script setup>
import EmptyState from './EmptyState.vue'

defineProps({
  section: {
    type: Object,
    required: true,
  },
})

function studentName(student) {
  return `${student.firstName} ${student.lastName}`
}
</script>

<template>
  <article class="detail-panel">
    <div class="detail-header">
      <div>
        <p class="eyebrow">{{ section.courseCode }}</p>
        <h2>{{ section.name }}</h2>
      </div>
      <span class="date-range">{{ section.startDate }} - {{ section.endDate }}</span>
    </div>

    <section class="detail-section">
      <h3>Instructors</h3>
      <div class="pill-row" v-if="section.instructors?.length">
        <span class="pill" v-for="instructor in section.instructors" :key="instructor">
          {{ instructor }}
        </span>
      </div>
      <EmptyState v-else message="No instructors listed." />
    </section>

    <section class="detail-section">
      <h3>Teams</h3>
      <div class="team-grid" v-if="section.teams?.length">
        <article class="team-card" v-for="team in section.teams" :key="team.id">
          <h4>{{ team.name }}</h4>
          <ul v-if="team.students?.length">
            <li v-for="student in team.students" :key="student.id">
              <span>{{ studentName(student) }}</span>
              <small>{{ student.email }}</small>
            </li>
          </ul>
          <p class="quiet" v-else>No students assigned.</p>
        </article>
      </div>
      <EmptyState v-else message="No teams found for this section." />
    </section>

    <section class="detail-section">
      <h3>Unassigned students</h3>
      <ul class="student-list" v-if="section.studentsNotAssignedToTeams?.length">
        <li v-for="student in section.studentsNotAssignedToTeams" :key="student.id">
          <span>{{ studentName(student) }}</span>
          <small>{{ student.email }}</small>
        </li>
      </ul>
      <EmptyState v-else message="Every student is assigned to a team." />
    </section>

    <section class="detail-section">
      <h3>Rubric</h3>
      <div class="rubric-summary" v-if="section.rubric">
        <h4>{{ section.rubric.name }}</h4>
        <p v-if="section.rubric.description">{{ section.rubric.description }}</p>
        <ul>
          <li v-for="criterion in section.rubric.criteria" :key="criterion.id">
            <span>{{ criterion.name }}</span>
            <strong>{{ criterion.maxScore }} pts</strong>
          </li>
        </ul>
      </div>
      <EmptyState v-else message="No rubric assigned yet." />
    </section>
  </article>
</template>
