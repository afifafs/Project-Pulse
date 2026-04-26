<script setup>
import { computed, onMounted, ref } from 'vue'

const sections = ref([])
const selectedSectionId = ref(null)
const selectedSection = ref(null)
const loadingSections = ref(true)
const loadingDetail = ref(false)
const sectionsError = ref('')
const detailError = ref('')
const activeView = ref('my-evaluations')

const weekOptions = [
  { label: 'Week 12, 2026', range: '03-16-2026 -- 03-22-2026' },
  { label: 'Week 13, 2026', range: '03-23-2026 -- 03-29-2026' },
  { label: 'Week 14, 2026', range: '03-30-2026 -- 04-05-2026' },
  { label: 'Week 15, 2026', range: '04-06-2026 -- 04-12-2026' },
  { label: 'Week 16, 2026', range: '04-13-2026 -- 04-19-2026' },
]

const selectedWeekIndex = ref(0)

const navGroups = computed(() => [
  {
    label: 'Home',
    items: [{ id: 'home', title: 'Home', icon: '⌂' }],
  },
  {
    label: 'Weekly Activity Reports',
    items: [
      { id: 'my-activities', title: 'My Activities', icon: '◫' },
      { id: 'team-activities', title: "Team's Activities", icon: '▣' },
    ],
  },
  {
    label: 'Peer Evaluations',
    items: [
      { id: 'my-evaluations', title: 'My Evaluations', icon: '◔' },
      { id: 'submit-evaluations', title: 'Submit Evaluations', icon: '◍' },
    ],
  },
  {
    label: 'My Profile',
    items: [
      { id: 'user-profile', title: 'User Profile', icon: '◌' },
      { id: 'reset-password', title: 'Reset Password', icon: '✎' },
    ],
  },
])

const criterionColumns = computed(() => selectedSection.value?.rubric?.criteria ?? [])

const rosterStudents = computed(() => {
  if (!selectedSection.value) {
    return []
  }

  const assigned = selectedSection.value.teams.flatMap((team) =>
    team.students.map((student) => ({ ...student, teamName: team.name })),
  )

  const unassigned = selectedSection.value.studentsNotAssignedToTeams.map((student) => ({
    ...student,
    teamName: 'Unassigned',
  }))

  return [...assigned, ...unassigned]
})

const selectedSectionSummary = computed(() => {
  if (!selectedSection.value) {
    return null
  }

  const totalStudents = rosterStudents.value.length
  const teamCount = selectedSection.value.teams.length

  return {
    totalStudents,
    teamCount,
    evaluationCount: totalStudents * criterionColumns.value.length,
    rubricName: selectedSection.value.rubric?.name ?? 'No rubric',
  }
})

const currentWeek = computed(() => weekOptions[selectedWeekIndex.value])
const previousWeekDisabled = computed(() => selectedWeekIndex.value === 0)
const nextWeekDisabled = computed(() => selectedWeekIndex.value === weekOptions.length - 1)

const pageMeta = computed(() => {
  const meta = {
    'home': {
      group: 'Dashboard',
      title: 'Project Pulse Overview',
      panelTitle: 'Overview',
    },
    'my-activities': {
      group: 'Weekly Activity Reports',
      title: 'My Activities',
      panelTitle: 'My Weekly Activities',
    },
    'team-activities': {
      group: 'Weekly Activity Reports',
      title: "Team's Activities",
      panelTitle: "Team's Weekly Activities",
    },
    'my-evaluations': {
      group: 'Peer Evaluations',
      title: 'My Evaluations',
      panelTitle: 'My Peer Evaluations',
    },
    'submit-evaluations': {
      group: 'Peer Evaluations',
      title: 'Submit Evaluations',
      panelTitle: 'Submit Peer Evaluations',
    },
    'user-profile': {
      group: 'My Profile',
      title: 'User Profile',
      panelTitle: 'User Profile',
    },
    'reset-password': {
      group: 'My Profile',
      title: 'Reset Password',
      panelTitle: 'Reset Password',
    },
  }

  return meta[activeView.value]
})

const currentUser = computed(() => {
  const student = rosterStudents.value[0]

  if (!student) {
    return null
  }

  return {
    id: student.id,
    username: student.email,
    firstName: student.firstName,
    lastName: student.lastName,
    email: student.email,
    status: 'Enabled',
    role: 'student',
  }
})

const evaluationRows = computed(() => {
  if (!selectedSection.value) {
    return []
  }

  return weekOptions.slice(1).map((week, weekIndex) => {
    const scores = criterionColumns.value.map((criterion, criterionIndex) => {
      const base = 9 + ((criterionIndex + weekIndex) % 2)
      return Number(base.toFixed(2))
    })

    const averageScore = scores.reduce((sum, score) => sum + score, 0)

    return {
      weekLabel: week.label.replace(', ', ' ').replace('Week ', '').replace(' 2026', ''),
      averageScore: averageScore.toFixed(2),
      scores: scores.map((score) => score.toFixed(2)),
    }
  })
})

const submitRows = computed(() =>
  rosterStudents.value.map((student, studentIndex) => ({
    ...student,
    comment: `Consistently supported ${student.teamName.toLowerCase()} deliverables and team follow-through.`,
    scores: criterionColumns.value.map((criterion, criterionIndex) =>
      Math.max(8, 10 - ((studentIndex + criterionIndex) % 3)),
    ),
  })),
)

const teamActivityGroups = computed(() => {
  const labels = ['DESIGN', 'RESEARCH', 'DEVELOPMENT', 'DOCUMENTATION', 'MISCELLANEOUS']

  return rosterStudents.value.map((student, index) => ({
    studentName: `${student.firstName} ${student.lastName}`,
    rows: [
      {
        category: labels[index % labels.length],
        activity: index % 2 === 0 ? 'Sprint planning' : 'Prototype review',
        description: `${student.firstName} contributed to ${student.teamName.toLowerCase()} week ${selectedWeekIndex.value + 12} goals.`,
        plannedHours: 2 + (index % 3),
        actualHours: 2 + (index % 3),
        status: index % 3 === 0 ? 'COMPLETED' : 'IN REVIEW',
      },
    ],
  }))
})

function formatWeekLabel(label) {
  return label.replace(', ', ' ').replace('Week ', 'W')
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

function goToPreviousWeek() {
  if (!previousWeekDisabled.value) {
    selectedWeekIndex.value -= 1
  }
}

function goToNextWeek() {
  if (!nextWeekDisabled.value) {
    selectedWeekIndex.value += 1
  }
}

function goToCurrentWeek() {
  selectedWeekIndex.value = 0
}

onMounted(fetchSections)
</script>

<template>
  <div class="app-shell">
    <aside class="sidebar">
      <div class="brand">
        <div class="brand-mark">
          <span class="pulse-line"></span>
        </div>
        <h1>Project Pulse</h1>
      </div>

      <nav class="nav">
        <section v-for="group in navGroups" :key="group.label" class="nav-group">
          <button class="nav-group-title" type="button">
            <span>{{ group.label }}</span>
            <span v-if="group.items.length > 1" class="caret">⌄</span>
          </button>

          <div class="nav-items">
            <button
              v-for="item in group.items"
              :key="item.id"
              type="button"
              class="nav-item"
              :class="{ active: item.id === activeView }"
              @click="activeView = item.id"
            >
              <span class="nav-icon">{{ item.icon }}</span>
              <span>{{ item.title }}</span>
            </button>
          </div>
        </section>
      </nav>
    </aside>

    <div class="main-shell">
      <header class="topbar">
        <div class="crumbs">
          <span class="menu-toggle">☰</span>
          <span class="crumb-sep">›</span>
          <strong>{{ pageMeta.group }}</strong>
          <span class="crumb-sep">›</span>
          <span>{{ pageMeta.title }}</span>
        </div>

        <div class="profile-tools">
          <span class="tool-circle">↻</span>
          <span class="tool-circle">⤢</span>
          <div class="user-chip">
            <span class="avatar">●</span>
            <strong>{{ currentUser?.firstName || 'Student' }}</strong>
            <span class="crumb-sep">⌄</span>
          </div>
        </div>
      </header>

      <main class="workspace">
        <div v-if="loadingDetail" class="page-card empty-state">
          <p>Loading section detail...</p>
        </div>

        <div v-else-if="detailError" class="page-card empty-state error-state">
          <p>{{ detailError }}</p>
        </div>

        <template v-else-if="selectedSection">
          <section class="page-card">
            <header class="card-header">
              <h2>{{ pageMeta.panelTitle }}</h2>
            </header>

            <div v-if="activeView === 'home'" class="card-body home-body">
              <div class="section-switcher">
                <label for="section-select">Active course:</label>
                <select
                  id="section-select"
                  :value="selectedSectionId"
                  @change="selectSection(Number($event.target.value))"
                >
                  <option v-for="section in sections" :key="section.id" :value="section.id">
                    {{ section.courseCode }} - {{ section.name }}
                  </option>
                </select>
              </div>

              <div class="summary-grid">
                <article class="summary-box">
                  <span>Course</span>
                  <strong>{{ selectedSection.courseCode }}</strong>
                  <p>{{ selectedSection.name }}</p>
                </article>
                <article class="summary-box">
                  <span>Total students</span>
                  <strong>{{ selectedSectionSummary.totalStudents }}</strong>
                  <p>{{ selectedSectionSummary.teamCount }} teams active</p>
                </article>
                <article class="summary-box">
                  <span>Rubric</span>
                  <strong>{{ selectedSectionSummary.rubricName }}</strong>
                  <p>{{ criterionColumns.length }} evaluation criteria</p>
                </article>
                <article class="summary-box">
                  <span>Window</span>
                  <strong>{{ currentWeek.label }}</strong>
                  <p>{{ currentWeek.range }}</p>
                </article>
              </div>

              <div class="home-tables">
                <div class="mini-card">
                  <h3>Team roster</h3>
                  <table class="data-table compact">
                    <thead>
                      <tr>
                        <th>Name</th>
                        <th>Team</th>
                        <th>Email</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr v-for="student in rosterStudents" :key="student.id">
                        <td>{{ student.firstName }} {{ student.lastName }}</td>
                        <td>{{ student.teamName }}</td>
                        <td>{{ student.email }}</td>
                      </tr>
                    </tbody>
                  </table>
                </div>

                <div class="mini-card">
                  <h3>Rubric criteria</h3>
                  <table class="data-table compact">
                    <thead>
                      <tr>
                        <th>Criterion</th>
                        <th>Max score</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr v-for="criterion in criterionColumns" :key="criterion.id">
                        <td>{{ criterion.name }}</td>
                        <td>{{ criterion.maxScore }}</td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </div>

            <div v-else-if="activeView === 'my-evaluations'" class="card-body">
              <div class="toolbar-row">
                <div class="filter-grid">
                  <div class="field-group">
                    <label>Start Week:</label>
                    <div class="fake-input">{{ weekOptions[0].label }}</div>
                  </div>
                  <div class="field-group">
                    <label>End Week:</label>
                    <div class="fake-input">{{ weekOptions[4].label }}</div>
                  </div>
                  <button class="action-btn primary">Query</button>
                  <button class="action-btn">Reset</button>
                </div>

                <div class="range-row">
                  <span>Start Week: <strong>{{ weekOptions[0].range }}</strong></span>
                  <span>End Week: <strong>{{ weekOptions[4].range }}</strong></span>
                </div>
              </div>

              <div class="table-scroll">
                <table class="data-table wide">
                  <thead>
                    <tr>
                      <th>Week</th>
                      <th>Average Total Score</th>
                      <th v-for="criterion in criterionColumns" :key="criterion.id">
                        {{ criterion.name }}
                      </th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="row in evaluationRows" :key="row.weekLabel">
                      <td>{{ row.weekLabel }}</td>
                      <td>{{ row.averageScore }}</td>
                      <td v-for="(score, index) in row.scores" :key="`${row.weekLabel}-${index}`">
                        {{ score }}
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>

            <div v-else-if="activeView === 'submit-evaluations'" class="card-body">
              <div class="intro-block">
                <p>
                  Submit Peer Evaluations for week:
                  <span class="highlight-chip">{{ weekOptions[3].range }}</span>
                </p>
                <p class="subcopy">
                  The default score for each criterion is 10. You can change it to a value between
                  0 and 10.
                </p>
              </div>

              <div class="table-scroll">
                <table class="data-table wide">
                  <thead>
                    <tr>
                      <th>Student Name</th>
                      <th v-for="criterion in criterionColumns" :key="criterion.id">
                        {{ criterion.name }}
                      </th>
                      <th>Public Comment</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="student in submitRows" :key="student.id">
                      <td>{{ student.firstName }} {{ student.lastName }}</td>
                      <td v-for="(score, index) in student.scores" :key="`${student.id}-${index}`">
                        <div class="score-pill">{{ score }}</div>
                      </td>
                      <td class="comment-cell">{{ student.comment }}</td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>

            <div v-else-if="activeView === 'team-activities'" class="card-body">
              <div class="week-toolbar">
                <div class="field-group inline">
                  <label>Pick a week:</label>
                  <div class="fake-input">{{ currentWeek.label }}</div>
                </div>
                <button
                  class="action-btn nav-btn"
                  :disabled="previousWeekDisabled"
                  @click="goToPreviousWeek"
                >
                  ‹ Previous Week
                </button>
                <div class="week-range">{{ currentWeek.range }}</div>
                <button
                  class="action-btn nav-btn"
                  :disabled="nextWeekDisabled"
                  @click="goToNextWeek"
                >
                  Next Week ›
                </button>
                <button class="action-btn nav-btn" @click="goToCurrentWeek">Go to current week</button>
              </div>

              <div class="activity-stack">
                <section
                  v-for="group in teamActivityGroups"
                  :key="group.studentName"
                  class="activity-group"
                >
                  <h3>{{ group.studentName }}'s Weekly Activities:</h3>
                  <table class="data-table compact">
                    <thead>
                      <tr>
                        <th>Category</th>
                        <th>Activity</th>
                        <th>Description</th>
                        <th>Planned Hours</th>
                        <th>Actual Hours</th>
                        <th>Status</th>
                        <th>Operations</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr v-for="row in group.rows" :key="`${group.studentName}-${row.activity}`">
                        <td><span class="tag">{{ row.category }}</span></td>
                        <td>{{ row.activity }}</td>
                        <td>{{ row.description }}</td>
                        <td>{{ row.plannedHours }}</td>
                        <td>{{ row.actualHours }}</td>
                        <td>
                          <span class="status-tag" :class="{ pending: row.status !== 'COMPLETED' }">
                            {{ row.status }}
                          </span>
                        </td>
                        <td><span class="op-badge">⋯</span></td>
                      </tr>
                    </tbody>
                  </table>
                </section>
              </div>
            </div>

            <div v-else-if="activeView === 'user-profile'" class="card-body form-body">
              <div class="profile-form">
                <div class="form-row">
                  <label>Id</label>
                  <div class="fake-field disabled">{{ currentUser.id }}</div>
                </div>
                <div class="form-row">
                  <label><span class="required">*</span> Username</label>
                  <div class="fake-field">{{ currentUser.username }}</div>
                </div>
                <div class="form-row">
                  <label><span class="required">*</span> First Name</label>
                  <div class="fake-field">{{ currentUser.firstName }}</div>
                </div>
                <div class="form-row">
                  <label><span class="required">*</span> Last Name</label>
                  <div class="fake-field">{{ currentUser.lastName }}</div>
                </div>
                <div class="form-row">
                  <label><span class="required">*</span> Email</label>
                  <div class="fake-field">{{ currentUser.email }}</div>
                </div>
                <div class="form-row">
                  <label>Status</label>
                  <div class="fake-field disabled">{{ currentUser.status }}</div>
                </div>
                <div class="form-row">
                  <label>Roles</label>
                  <div class="role-chip">{{ currentUser.role }}</div>
                </div>
                <button class="action-btn primary update-btn">Update</button>
              </div>
            </div>

            <div v-else class="card-body empty-state">
              <p>This page is not wired yet, but the app shell now matches the Project Pulse style.</p>
            </div>
          </section>

          <footer class="page-footer">Project Pulse 1.5.0</footer>
        </template>
      </main>
    </div>
  </div>
</template>
