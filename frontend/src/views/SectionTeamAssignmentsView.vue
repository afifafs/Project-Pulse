<script setup>
import { computed, reactive, ref } from 'vue'
import { assignStudentsToTeams, getSectionDetails } from '../api/projectPulseApi'
import EmptyState from '../components/EmptyState.vue'
import ErrorMessage from '../components/ErrorMessage.vue'
import LoadingState from '../components/LoadingState.vue'

const sectionId = ref('')
const section = ref(null)
const assignments = reactive({})
const isLoading = ref(false)
const isSaving = ref(false)
const loadError = ref('')
const saveError = ref('')
const successMessage = ref('')

const unassignedStudents = computed(() => section.value?.studentsNotAssignedToTeams ?? [])
const teams = computed(() => section.value?.teams ?? [])
const canSave = computed(() => {
  return unassignedStudents.value.some((student) => assignments[student.id])
})

function studentName(student) {
  return `${student.firstName} ${student.lastName}`
}

function resetAssignments() {
  Object.keys(assignments).forEach((studentId) => {
    delete assignments[studentId]
  })
}

async function loadSection() {
  loadError.value = ''
  saveError.value = ''
  successMessage.value = ''
  section.value = null
  resetAssignments()

  if (!sectionId.value) {
    loadError.value = 'Enter a section ID to load students and teams.'
    return
  }

  isLoading.value = true

  try {
    section.value = await getSectionDetails(sectionId.value)
  } catch (err) {
    loadError.value = err.message
  } finally {
    isLoading.value = false
  }
}

function buildAssignmentPayload() {
  return Object.entries(assignments)
    .filter(([, teamId]) => Boolean(teamId))
    .map(([studentId, teamId]) => ({
      studentId: Number(studentId),
      teamId: Number(teamId),
    }))
}

async function saveAssignments() {
  saveError.value = ''
  successMessage.value = ''

  const payload = buildAssignmentPayload()

  if (!payload.length) {
    saveError.value = 'Choose at least one team assignment before saving.'
    return
  }

  isSaving.value = true

  try {
    await assignStudentsToTeams(section.value.id, payload)
    successMessage.value = `${payload.length} ${payload.length === 1 ? 'assignment was' : 'assignments were'} saved.`
    await loadSection()
    successMessage.value = `${payload.length} ${payload.length === 1 ? 'assignment was' : 'assignments were'} saved.`
  } catch (err) {
    saveError.value = err.message
  } finally {
    isSaving.value = false
  }
}
</script>

<template>
  <section class="panel assignment-panel">
    <div class="panel-header">
      <div>
        <p class="eyebrow">Teams</p>
        <h2>Assign students to teams</h2>
      </div>
    </div>

    <form class="search-form" @submit.prevent="loadSection">
      <label for="section-id">Section ID</label>
      <div class="search-row">
        <input id="section-id" v-model="sectionId" min="1" type="number" placeholder="Enter section ID" />
        <button type="submit" :disabled="isLoading">Load</button>
      </div>
    </form>

    <ErrorMessage v-if="loadError" title="Could not load section" :message="loadError" />
    <LoadingState v-else-if="isLoading" label="Loading students and teams" />

    <div v-else-if="section" class="assignment-workspace">
      <div class="section-summary">
        <div>
          <p class="eyebrow">{{ section.courseCode }}</p>
          <h3>{{ section.name }}</h3>
        </div>
        <span class="result-count">{{ unassignedStudents.length }} unassigned</span>
      </div>

      <section class="detail-section">
        <h3>Teams</h3>
        <div v-if="teams.length" class="team-grid">
          <article v-for="team in teams" :key="team.id" class="team-card">
            <h4>{{ team.name }}</h4>
            <p class="quiet">{{ team.students?.length ?? 0 }} assigned</p>
          </article>
        </div>
        <EmptyState v-else message="No teams are available for this section." />
      </section>

      <section class="detail-section">
        <h3>Students</h3>
        <div v-if="unassignedStudents.length && teams.length" class="table-wrap">
          <table class="sections-table assignment-table">
            <thead>
              <tr>
                <th scope="col">Student</th>
                <th scope="col">Email</th>
                <th scope="col">Assign to team</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="student in unassignedStudents" :key="student.id">
                <td>{{ studentName(student) }}</td>
                <td>{{ student.email }}</td>
                <td>
                  <select v-model="assignments[student.id]">
                    <option value="">Choose team</option>
                    <option v-for="team in teams" :key="team.id" :value="team.id">
                      {{ team.name }}
                    </option>
                  </select>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        <EmptyState v-else-if="!teams.length" message="Create teams before assigning students." />
        <EmptyState v-else message="Every student in this section is already assigned." />
      </section>

      <ErrorMessage v-if="saveError" title="Could not save assignments" :message="saveError" />
      <p v-if="successMessage" class="success-message" role="status">{{ successMessage }}</p>

      <button class="submit-button" type="button" :disabled="isSaving || !canSave" @click="saveAssignments">
        {{ isSaving ? 'Saving...' : 'Save assignments' }}
      </button>
    </div>

    <EmptyState v-else message="Load a section to assign students to teams." />
  </section>
</template>
