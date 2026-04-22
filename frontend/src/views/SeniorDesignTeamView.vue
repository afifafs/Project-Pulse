<script setup>
import { computed, ref } from 'vue'
import { getSectionDetails, getTeamDetails } from '../api/projectPulseApi'
import EmptyState from '../components/EmptyState.vue'
import ErrorMessage from '../components/ErrorMessage.vue'
import LoadingState from '../components/LoadingState.vue'

const teamId = ref('')
const sectionId = ref('')
const team = ref(null)
const section = ref(null)
const isLoading = ref(false)
const error = ref('')

const memberCountLabel = computed(() => {
  const count = team.value?.students?.length ?? 0
  return `${count} ${count === 1 ? 'member' : 'members'}`
})

function studentName(student) {
  return `${student.firstName} ${student.lastName}`
}

function normalizeTeamResponse(teamResponse) {
  team.value = teamResponse
  section.value = teamResponse.section ?? null
}

async function loadTeamFromSection() {
  const loadedSection = await getSectionDetails(sectionId.value)
  const matchingTeam = loadedSection.teams?.find((candidate) => String(candidate.id) === String(teamId.value))

  if (!matchingTeam) {
    throw new Error('Team not found in that section.')
  }

  team.value = matchingTeam
  section.value = {
    id: loadedSection.id,
    name: loadedSection.name,
    courseCode: loadedSection.courseCode,
    startDate: loadedSection.startDate,
    endDate: loadedSection.endDate,
  }
}

async function loadTeam() {
  error.value = ''
  team.value = null
  section.value = null

  if (!teamId.value) {
    error.value = 'Enter a team ID to load.'
    return
  }

  isLoading.value = true

  try {
    if (sectionId.value) {
      await loadTeamFromSection()
    } else {
      normalizeTeamResponse(await getTeamDetails(teamId.value))
    }
  } catch (err) {
    error.value = err.message
  } finally {
    isLoading.value = false
  }
}
</script>

<template>
  <section class="panel team-detail-panel">
    <div class="panel-header">
      <div>
        <p class="eyebrow">Teams</p>
        <h2>View team details</h2>
      </div>
    </div>

    <form class="search-form" @submit.prevent="loadTeam">
      <div class="form-grid">
        <label for="team-id">
          Team ID
          <input id="team-id" v-model="teamId" min="1" type="number" placeholder="Enter team ID" />
        </label>

        <label for="section-id">
          Section ID
          <input id="section-id" v-model="sectionId" min="1" type="number" placeholder="Optional section ID" />
        </label>
      </div>

      <button class="submit-button" type="submit" :disabled="isLoading">
        {{ isLoading ? 'Loading...' : 'Load team' }}
      </button>
    </form>

    <ErrorMessage v-if="error" title="Could not load team" :message="error" />
    <LoadingState v-else-if="isLoading" label="Loading team" />

    <article v-else-if="team" class="team-detail-card">
      <div class="section-summary">
        <div>
          <p class="eyebrow">Team</p>
          <h3>{{ team.name }}</h3>
        </div>
        <span class="result-count">{{ memberCountLabel }}</span>
      </div>

      <section class="detail-section">
        <h3>Associated section</h3>
        <div v-if="section" class="associated-section">
          <strong>{{ section.name }}</strong>
          <span>{{ section.courseCode }}</span>
          <small v-if="section.startDate || section.endDate">
            {{ section.startDate }} - {{ section.endDate }}
          </small>
        </div>
        <EmptyState v-else message="No associated section was returned for this team." />
      </section>

      <section class="detail-section">
        <h3>Members</h3>
        <ul v-if="team.students?.length" class="student-list">
          <li v-for="student in team.students" :key="student.id">
            <span>{{ studentName(student) }}</span>
            <small>{{ student.email }}</small>
          </li>
        </ul>
        <EmptyState v-else message="No members are assigned to this team." />
      </section>
    </article>

    <EmptyState v-else message="Enter a team ID to view team details." />
  </section>
</template>
