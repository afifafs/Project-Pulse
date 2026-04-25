<script setup>
import { computed, onMounted, ref } from 'vue'
import { getTeams } from '../api/projectPulseApi'
import EmptyState from '../components/EmptyState.vue'
import ErrorMessage from '../components/ErrorMessage.vue'
import LoadingState from '../components/LoadingState.vue'

const sectionId = ref('')
const teams = ref([])
const isLoading = ref(false)
const error = ref('')

const resultCountLabel = computed(() => {
  const count = teams.value.length
  return `${count} ${count === 1 ? 'team' : 'teams'} found`
})

function studentName(student) {
  return `${student.firstName} ${student.lastName}`
}

function studentSummary(students = []) {
  if (!students.length) {
    return 'No students assigned'
  }

  return students.map(studentName).join(', ')
}

async function loadTeams() {
  isLoading.value = true
  error.value = ''

  try {
    teams.value = await getTeams(sectionId.value)
  } catch (err) {
    teams.value = []
    error.value = err.message
  } finally {
    isLoading.value = false
  }
}

function clearFilter() {
  sectionId.value = ''
  loadTeams()
}

onMounted(loadTeams)
</script>

<template>
  <section class="panel teams-panel">
    <div class="panel-header">
      <div>
        <p class="eyebrow">Teams</p>
        <h2>Find senior design teams</h2>
      </div>
      <span class="result-count" v-if="!isLoading && !error">{{ resultCountLabel }}</span>
    </div>

    <form class="search-form" @submit.prevent="loadTeams">
      <label for="section-filter">Section ID</label>
      <div class="search-row">
        <input
          id="section-filter"
          v-model="sectionId"
          min="1"
          type="number"
          placeholder="Filter by section ID"
        />
        <button type="submit" :disabled="isLoading">Find teams</button>
        <button class="secondary-button" type="button" :disabled="isLoading || !sectionId" @click="clearFilter">
          Clear
        </button>
      </div>
    </form>

    <ErrorMessage v-if="error" title="Could not load teams" :message="error" />
    <LoadingState v-else-if="isLoading" label="Loading teams" />
    <EmptyState v-else-if="teams.length === 0" message="No senior design teams found." />

    <div v-else class="table-wrap">
      <table class="sections-table teams-table">
        <thead>
          <tr>
            <th scope="col">Team</th>
            <th scope="col">Members</th>
            <th scope="col">Team ID</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="team in teams" :key="team.id">
            <td>{{ team.name }}</td>
            <td>{{ studentSummary(team.students) }}</td>
            <td>{{ team.id }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>
