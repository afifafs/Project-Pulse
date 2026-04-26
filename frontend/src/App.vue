<script setup>
import { computed, onMounted, ref } from 'vue'

const sections = ref([])
const selectedSectionId = ref(null)
const selectedSection = ref(null)
const loadingSections = ref(true)
const loadingDetail = ref(false)
const sectionsError = ref('')
const detailError = ref('')

const selectedSectionSummary = computed(() => {
  if (!selectedSection.value) {
    return null
  }

  const teamCount = selectedSection.value.teams.length
  const assignedStudents = selectedSection.value.teams.reduce(
    (total, team) => total + team.students.length,
    0,
  )
  const unassignedStudents = selectedSection.value.studentsNotAssignedToTeams.length
  const totalStudents = assignedStudents + unassignedStudents

  return {
    teamCount,
    assignedStudents,
    unassignedStudents,
    totalStudents,
  }
})

const dateFormatter = new Intl.DateTimeFormat('en-US', {
  month: 'short',
  day: 'numeric',
  year: 'numeric',
})

function formatDate(date) {
  return dateFormatter.format(new Date(`${date}T12:00:00`))
}

async function fetchSections() {
  loadingSections.value = true
  sectionsError.value = ''

  try {
    const response = await fetch('/sections')

    if (!response.ok) {
      throw new Error('Unable to load sections.')
    }

    const payload = await response.json()
    sections.value = payload

    if (payload.length > 0) {
      await selectSection(payload[0].id)
    }
  } catch (error) {
    sectionsError.value = error.message || 'Unable to load sections.'
  } finally {
    loadingSections.value = false
  }
}

async function selectSection(sectionId) {
  selectedSectionId.value = sectionId
  selectedSection.value = null
  detailError.value = ''
  loadingDetail.value = true

  try {
    const response = await fetch(`/sections/${sectionId}`)

    if (!response.ok) {
      throw new Error('Unable to load section details.')
    }

    selectedSection.value = await response.json()
  } catch (error) {
    detailError.value = error.message || 'Unable to load section details.'
  } finally {
    loadingDetail.value = false
  }
}

onMounted(fetchSections)
</script>

<template>
  <div class="shell">
    <header class="hero">
      <div>
        <p class="eyebrow">Project Pulse demo</p>
        <h1>Peer evaluation, course teams, and rubric detail in one local flow.</h1>
        <p class="lede">
          This demo reads live data from the Spring Boot backend and gives you a polished
          view of sections, teams, students, and evaluation criteria.
        </p>
      </div>
      <div class="hero-card">
        <p>Local stack</p>
        <ul>
          <li>Vue 3 + Vite frontend</li>
          <li>Spring Boot + H2 backend</li>
          <li>Seeded demo data on startup</li>
        </ul>
      </div>
    </header>

    <main class="layout">
      <aside class="panel sidebar">
        <div class="panel-head">
          <div>
            <p class="panel-label">Sections</p>
            <h2>Active courses</h2>
          </div>
        </div>

        <p v-if="loadingSections" class="status">Loading sections...</p>
        <p v-else-if="sectionsError" class="status error">{{ sectionsError }}</p>

        <div v-else class="section-list">
          <button
            v-for="section in sections"
            :key="section.id"
            class="section-button"
            :class="{ selected: section.id === selectedSectionId }"
            @click="selectSection(section.id)"
          >
            <span class="course">{{ section.courseCode }}</span>
            <strong>{{ section.name }}</strong>
          </button>
        </div>
      </aside>

      <section class="content">
        <div v-if="loadingDetail" class="panel state-panel">
          <p class="status">Loading section detail...</p>
        </div>

        <div v-else-if="detailError" class="panel state-panel">
          <p class="status error">{{ detailError }}</p>
        </div>

        <template v-else-if="selectedSection">
          <section class="panel overview">
            <div class="overview-copy">
              <p class="panel-label">{{ selectedSection.courseCode }}</p>
              <h2>{{ selectedSection.name }}</h2>
              <p>
                {{ formatDate(selectedSection.startDate) }} to
                {{ formatDate(selectedSection.endDate) }}
              </p>
            </div>

            <div class="instructors">
              <span>Instructors</span>
              <ul>
                <li v-for="instructor in selectedSection.instructors" :key="instructor">
                  {{ instructor }}
                </li>
              </ul>
            </div>
          </section>

          <section class="metrics">
            <article class="metric-card">
              <span>Total students</span>
              <strong>{{ selectedSectionSummary.totalStudents }}</strong>
            </article>
            <article class="metric-card">
              <span>Teams</span>
              <strong>{{ selectedSectionSummary.teamCount }}</strong>
            </article>
            <article class="metric-card">
              <span>Assigned</span>
              <strong>{{ selectedSectionSummary.assignedStudents }}</strong>
            </article>
            <article class="metric-card">
              <span>Unassigned</span>
              <strong>{{ selectedSectionSummary.unassignedStudents }}</strong>
            </article>
          </section>

          <section class="two-up">
            <article class="panel">
              <div class="panel-head">
                <div>
                  <p class="panel-label">Teams</p>
                  <h2>Current roster</h2>
                </div>
              </div>

              <div class="team-grid">
                <article v-for="team in selectedSection.teams" :key="team.id" class="team-card">
                  <div class="team-title">
                    <h3>{{ team.name }}</h3>
                    <span>{{ team.students.length }} students</span>
                  </div>
                  <ul>
                    <li v-for="student in team.students" :key="student.id">
                      <strong>{{ student.firstName }} {{ student.lastName }}</strong>
                      <span>{{ student.email }}</span>
                    </li>
                  </ul>
                </article>
              </div>

              <div
                v-if="selectedSection.studentsNotAssignedToTeams.length"
                class="unassigned-block"
              >
                <h3>Not yet assigned</h3>
                <ul>
                  <li
                    v-for="student in selectedSection.studentsNotAssignedToTeams"
                    :key="student.id"
                  >
                    <strong>{{ student.firstName }} {{ student.lastName }}</strong>
                    <span>{{ student.email }}</span>
                  </li>
                </ul>
              </div>
            </article>

            <article class="panel rubric-panel">
              <div class="panel-head">
                <div>
                  <p class="panel-label">Rubric</p>
                  <h2>{{ selectedSection.rubric?.name || 'No rubric attached' }}</h2>
                </div>
              </div>

              <p v-if="selectedSection.rubric?.description" class="rubric-description">
                {{ selectedSection.rubric.description }}
              </p>

              <div v-if="selectedSection.rubric" class="criteria-list">
                <article
                  v-for="criterion in selectedSection.rubric.criteria"
                  :key="criterion.id"
                  class="criterion-card"
                >
                  <div class="criterion-head">
                    <h3>{{ criterion.name }}</h3>
                    <span>{{ criterion.maxScore }} pts</span>
                  </div>
                  <p>{{ criterion.description }}</p>
                </article>
              </div>

              <p v-else class="status">This section does not have a rubric yet.</p>
            </article>
          </section>
        </template>
      </section>
    </main>
  </div>
</template>
