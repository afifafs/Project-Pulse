<script setup>
import { computed, onMounted, ref, watch } from 'vue'

const sections = ref([])
const selectedSectionId = ref(null)
const selectedSection = ref(null)
const loadingSections = ref(true)
const loadingDetail = ref(false)
const sectionsError = ref('')
const detailError = ref('')
const activeView = ref('home')
const collapsedGroups = ref({})

const selectedWeekIndex = ref(0)

const profile = ref(null)
const profileForm = ref({
  firstName: '',
  lastName: '',
  email: '',
})
const resetPasswordForm = ref({
  newPassword: '',
  confirmPassword: '',
})

const evaluationFilters = ref({
  startWeek: '',
  endWeek: '',
})
const evaluationRows = ref([])
const evaluationMessage = ref('')
const evaluationError = ref('')
const loadingEvaluations = ref(false)

const studentActivities = ref([])
const studentActivitiesMessage = ref('')
const studentActivitiesError = ref('')
const savingActivities = ref(false)

const teamActivityGroups = ref([])
const teamActivitiesError = ref('')
const loadingTeamActivities = ref(false)

const submitRows = ref([])
const submitMessage = ref('')
const submitError = ref('')
const submittingEvaluations = ref(false)

const profileMessage = ref('')
const profileError = ref('')
const savingProfile = ref(false)

const passwordMessage = ref('')
const passwordError = ref('')
const savingPassword = ref(false)

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

const pageMeta = computed(() => {
  const meta = {
    home: {
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

const currentStudent = computed(() => rosterStudents.value[0] ?? null)

const currentUser = computed(() => {
  if (profile.value) {
    return profile.value
  }

  if (!currentStudent.value) {
    return null
  }

  return {
    id: currentStudent.value.id,
    username: currentStudent.value.email,
    firstName: currentStudent.value.firstName,
    lastName: currentStudent.value.lastName,
    email: currentStudent.value.email,
    status: 'Enabled',
    role: 'student',
  }
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
    evaluationCount: evaluationRows.value.length,
    rubricName: selectedSection.value.rubric?.name ?? 'No rubric',
  }
})

const weekOptions = computed(() => buildWeekOptions(selectedSection.value))

const currentWeek = computed(() => weekOptions.value[selectedWeekIndex.value] ?? null)
const previousWeekDisabled = computed(() => selectedWeekIndex.value === 0)
const nextWeekDisabled = computed(() => selectedWeekIndex.value >= weekOptions.value.length - 1)

const submitWeekLabel = computed(() => (currentWeek.value ? currentWeek.value.range : 'No week selected'))

function buildWeekOptions(section) {
  if (!section?.startDate || !section?.endDate) {
    return []
  }

  const options = []
  let cursor = new Date(`${section.startDate}T00:00:00`)
  const endDate = new Date(`${section.endDate}T00:00:00`)
  let weekNumber = 1

  while (cursor <= endDate) {
    const start = new Date(cursor)
    const end = new Date(cursor)
    end.setDate(end.getDate() + 6)
    if (end > endDate) {
      end.setTime(endDate.getTime())
    }

    options.push({
      index: weekNumber,
      label: `Week ${weekNumber}, ${start.getFullYear()}`,
      startDate: toIsoDate(start),
      endDate: toIsoDate(end),
      range: `${formatDate(start)} -- ${formatDate(end)}`,
    })

    cursor.setDate(cursor.getDate() + 7)
    weekNumber += 1
  }

  return options
}

function toIsoDate(date) {
  return date.toISOString().slice(0, 10)
}

function formatDate(date) {
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${month}-${day}-${date.getFullYear()}`
}

function formatWeekLabel(option) {
  return option ? `W${option.index}` : ''
}

function rangeLabelFromDates(startDate, endDate) {
  return `${formatDate(new Date(`${startDate}T00:00:00`))} -- ${formatDate(new Date(`${endDate}T00:00:00`))}`
}

function findWeekIndexForDate(dateIso) {
  return weekOptions.value.findIndex((option) => option.startDate <= dateIso && option.endDate >= dateIso)
}

function findWeekOption(dateIso) {
  return weekOptions.value.find((option) => option.startDate === dateIso) ?? null
}

function toggleNavGroup(label) {
  collapsedGroups.value = {
    ...collapsedGroups.value,
    [label]: !collapsedGroups.value[label],
  }
}

async function apiFetch(url, options = {}) {
  const response = await fetch(url, {
    headers: {
      'Content-Type': 'application/json',
      ...(options.headers ?? {}),
    },
    ...options,
  })

  if (!response.ok) {
    let message = 'Request failed.'

    try {
      const payload = await response.json()
      if (payload?.message) {
        message = payload.message
      } else if (typeof payload === 'string') {
        message = payload
      }
    } catch {
      const text = await response.text()
      if (text) {
        message = text
      }
    }

    throw new Error(message)
  }

  if (response.status === 204) {
    return null
  }

  return response.json()
}

async function fetchSections() {
  loadingSections.value = true
  sectionsError.value = ''

  try {
    const payload = await apiFetch('/sections')
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
    selectedSection.value = await apiFetch(`/sections/${sectionId}`)
    initializeWeekState()
    initializeSubmissionRows()
    await hydrateCurrentStudentData()
  } catch (error) {
    detailError.value = error.message || 'Unable to load section details.'
  } finally {
    loadingDetail.value = false
  }
}

function initializeWeekState() {
  const options = weekOptions.value
  if (options.length === 0) {
    selectedWeekIndex.value = 0
    evaluationFilters.value = { startWeek: '', endWeek: '' }
    return
  }

  const todayIso = toIsoDate(new Date())
  const matchingIndex = findWeekIndexForDate(todayIso)
  selectedWeekIndex.value = matchingIndex >= 0 ? matchingIndex : 0

  evaluationFilters.value = {
    startWeek: options[0].startDate,
    endWeek: options[options.length - 1].startDate,
  }
}

async function hydrateCurrentStudentData() {
  if (!currentStudent.value) {
    profile.value = null
    return
  }

  await loadProfile()
  await Promise.all([loadStudentActivities(), loadTeamActivities(), queryEvaluations()])
}

async function loadProfile() {
  profileError.value = ''

  if (!currentStudent.value) {
    profile.value = null
    return
  }

  try {
    profile.value = await apiFetch(`/students/${currentStudent.value.id}/profile`)
    profileForm.value = {
      firstName: profile.value.firstName,
      lastName: profile.value.lastName,
      email: profile.value.email,
    }
  } catch (error) {
    profileError.value = error.message || 'Unable to load profile.'
  }
}

async function updateProfile() {
  if (!currentUser.value) {
    return
  }

  savingProfile.value = true
  profileError.value = ''
  profileMessage.value = ''

  try {
    profile.value = await apiFetch(`/students/${currentUser.value.id}/profile`, {
      method: 'PUT',
      body: JSON.stringify(profileForm.value),
    })
    syncProfileIntoSection()
    profileMessage.value = 'Profile updated.'
  } catch (error) {
    profileError.value = error.message || 'Unable to update profile.'
  } finally {
    savingProfile.value = false
  }
}

function syncProfileIntoSection() {
  if (!selectedSection.value || !profile.value) {
    return
  }

  const applyProfile = (student) => {
    if (student.id === profile.value.id) {
      student.firstName = profile.value.firstName
      student.lastName = profile.value.lastName
      student.email = profile.value.email
    }
  }

  selectedSection.value.teams.forEach((team) => team.students.forEach(applyProfile))
  selectedSection.value.studentsNotAssignedToTeams.forEach(applyProfile)
}

async function resetPassword() {
  if (!currentUser.value) {
    return
  }

  savingPassword.value = true
  passwordError.value = ''
  passwordMessage.value = ''

  try {
    await apiFetch(`/students/${currentUser.value.id}/reset-password`, {
      method: 'POST',
      body: JSON.stringify(resetPasswordForm.value),
    })
    resetPasswordForm.value = { newPassword: '', confirmPassword: '' }
    passwordMessage.value = 'Password updated.'
  } catch (error) {
    passwordError.value = error.message || 'Unable to reset password.'
  } finally {
    savingPassword.value = false
  }
}

async function loadStudentActivities() {
  studentActivitiesError.value = ''
  studentActivitiesMessage.value = ''

  if (!currentUser.value || !currentWeek.value) {
    studentActivities.value = []
    return
  }

  try {
    const payload = await apiFetch(
      `/students/${currentUser.value.id}/activities?weekStart=${currentWeek.value.startDate}`,
    )
    studentActivities.value = payload.rows.length > 0 ? payload.rows : [blankActivityRow()]
  } catch (error) {
    studentActivitiesError.value = error.message || 'Unable to load weekly activities.'
  }
}

function blankActivityRow() {
  return {
    id: null,
    category: '',
    activity: '',
    description: '',
    plannedHours: 0,
    actualHours: 0,
    status: 'IN REVIEW',
  }
}

function addActivityRow() {
  studentActivities.value.push(blankActivityRow())
}

function removeActivityRow(index) {
  studentActivities.value.splice(index, 1)
  if (studentActivities.value.length === 0) {
    studentActivities.value.push(blankActivityRow())
  }
}

async function saveActivities() {
  if (!currentUser.value || !currentWeek.value) {
    return
  }

  savingActivities.value = true
  studentActivitiesError.value = ''
  studentActivitiesMessage.value = ''

  try {
    const payload = await apiFetch(
      `/students/${currentUser.value.id}/activities?weekStart=${currentWeek.value.startDate}`,
      {
        method: 'PUT',
        body: JSON.stringify(studentActivities.value),
      },
    )
    studentActivities.value = payload.rows.length > 0 ? payload.rows : [blankActivityRow()]
    studentActivitiesMessage.value = 'Weekly activities saved.'
    await loadTeamActivities()
  } catch (error) {
    studentActivitiesError.value = error.message || 'Unable to save weekly activities.'
  } finally {
    savingActivities.value = false
  }
}

async function loadTeamActivities() {
  teamActivitiesError.value = ''

  if (!selectedSection.value || !currentWeek.value) {
    teamActivityGroups.value = []
    return
  }

  loadingTeamActivities.value = true

  try {
    const payload = await apiFetch(
      `/sections/${selectedSection.value.id}/activities?weekStart=${currentWeek.value.startDate}`,
    )
    teamActivityGroups.value = payload.groups
  } catch (error) {
    teamActivitiesError.value = error.message || 'Unable to load team activities.'
  } finally {
    loadingTeamActivities.value = false
  }
}

function initializeSubmissionRows() {
  if (!selectedSection.value || !currentStudent.value) {
    submitRows.value = []
    return
  }

  submitRows.value = rosterStudents.value
    .filter((student) => student.id !== currentStudent.value.id)
    .map((student) => ({
      revieweeId: student.id,
      displayName: `${student.firstName} ${student.lastName}`,
      teamName: student.teamName,
      publicComment: '',
      scores: criterionColumns.value.map((criterion) => ({
        criterionId: criterion.id,
        score: criterion.maxScore,
        maxScore: criterion.maxScore,
      })),
    }))
}

async function submitEvaluations() {
  if (!currentUser.value || !selectedSection.value || !currentWeek.value || submitRows.value.length === 0) {
    return
  }

  submittingEvaluations.value = true
  submitError.value = ''
  submitMessage.value = ''

  try {
    await apiFetch(
      `/students/${currentUser.value.id}/evaluations?sectionId=${selectedSection.value.id}&weekStart=${currentWeek.value.startDate}`,
      {
        method: 'POST',
        body: JSON.stringify({
          submissions: submitRows.value.map((row) => ({
            revieweeId: row.revieweeId,
            publicComment: row.publicComment,
            scores: row.scores.map((score) => ({
              criterionId: score.criterionId,
              score: Number(score.score),
            })),
          })),
        }),
      },
    )
    submitMessage.value = 'Peer evaluations submitted.'
  } catch (error) {
    submitError.value = error.message || 'Unable to submit peer evaluations.'
  } finally {
    submittingEvaluations.value = false
  }
}

async function queryEvaluations() {
  evaluationError.value = ''
  evaluationMessage.value = ''

  if (!currentUser.value || !selectedSection.value || !evaluationFilters.value.startWeek || !evaluationFilters.value.endWeek) {
    evaluationRows.value = []
    return
  }

  loadingEvaluations.value = true

  try {
    const payload = await apiFetch(
      `/students/${currentUser.value.id}/evaluations?sectionId=${selectedSection.value.id}&startWeek=${evaluationFilters.value.startWeek}&endWeek=${evaluationFilters.value.endWeek}`,
    )
    evaluationRows.value = payload.rows
  } catch (error) {
    evaluationError.value = error.message || 'Unable to load evaluations.'
  } finally {
    loadingEvaluations.value = false
  }
}

function resetEvaluations() {
  if (weekOptions.value.length === 0) {
    return
  }

  evaluationFilters.value = {
    startWeek: weekOptions.value[0].startDate,
    endWeek: weekOptions.value[weekOptions.value.length - 1].startDate,
  }
  queryEvaluations()
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
  const todayIso = toIsoDate(new Date())
  const matchingIndex = findWeekIndexForDate(todayIso)
  selectedWeekIndex.value = matchingIndex >= 0 ? matchingIndex : 0
}

watch(selectedWeekIndex, async () => {
  if (!selectedSection.value || !currentUser.value) {
    return
  }

  await Promise.all([loadStudentActivities(), loadTeamActivities()])
})

watch(activeView, async (view) => {
  if (!selectedSection.value) {
    return
  }

  if (view === 'my-evaluations') {
    await queryEvaluations()
  } else if (view === 'submit-evaluations') {
    initializeSubmissionRows()
  } else if (view === 'my-activities') {
    await loadStudentActivities()
  } else if (view === 'team-activities') {
    await loadTeamActivities()
  } else if (view === 'user-profile') {
    await loadProfile()
  }
})

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
          <button class="nav-group-title" type="button" @click="toggleNavGroup(group.label)">
            <span>{{ group.label }}</span>
            <span v-if="group.items.length > 1" class="caret">{{ collapsedGroups[group.label] ? '›' : '⌄' }}</span>
          </button>

          <div v-show="!collapsedGroups[group.label]" class="nav-items">
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
        <div v-if="loadingSections || loadingDetail" class="page-card empty-state">
          <p>Loading Project Pulse...</p>
        </div>

        <div v-else-if="sectionsError || detailError" class="page-card empty-state error-state">
          <p>{{ sectionsError || detailError }}</p>
        </div>

        <div v-else-if="!selectedSection" class="page-card empty-state">
          <p>No sections available.</p>
        </div>

        <template v-else>
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
                  <strong>{{ currentWeek?.label || 'No week' }}</strong>
                  <p>{{ currentWeek?.range || 'No date range available' }}</p>
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

            <div v-else-if="activeView === 'my-activities'" class="card-body">
              <div class="week-toolbar">
                <div class="field-group inline">
                  <label>Pick a week:</label>
                  <div class="fake-input">{{ currentWeek?.label || 'No week selected' }}</div>
                </div>
                <button
                  class="action-btn nav-btn"
                  :disabled="previousWeekDisabled"
                  @click="goToPreviousWeek"
                >
                  ‹ Previous Week
                </button>
                <div class="week-range">{{ currentWeek?.range || 'No range available' }}</div>
                <button class="action-btn nav-btn" :disabled="nextWeekDisabled" @click="goToNextWeek">
                  Next Week ›
                </button>
                <button class="action-btn nav-btn" @click="goToCurrentWeek">Go to current week</button>
              </div>

              <p v-if="studentActivitiesMessage" class="message-banner success">{{ studentActivitiesMessage }}</p>
              <p v-if="studentActivitiesError" class="message-banner error">{{ studentActivitiesError }}</p>

              <div class="stack-actions">
                <button class="action-btn" @click="addActivityRow">Add row</button>
                <button class="action-btn primary" :disabled="savingActivities" @click="saveActivities">
                  {{ savingActivities ? 'Saving...' : 'Save Activities' }}
                </button>
              </div>

              <div class="table-scroll">
                <table class="data-table wide">
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
                    <tr v-for="(row, index) in studentActivities" :key="row.id ?? index">
                      <td><input v-model="row.category" class="table-input" /></td>
                      <td><input v-model="row.activity" class="table-input" /></td>
                      <td><textarea v-model="row.description" class="table-textarea"></textarea></td>
                      <td><input v-model.number="row.plannedHours" type="number" min="0" class="table-input small" /></td>
                      <td><input v-model.number="row.actualHours" type="number" min="0" class="table-input small" /></td>
                      <td>
                        <select v-model="row.status" class="table-select">
                          <option>COMPLETED</option>
                          <option>IN REVIEW</option>
                          <option>PLANNED</option>
                        </select>
                      </td>
                      <td><button class="table-action" @click="removeActivityRow(index)">Remove</button></td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>

            <div v-else-if="activeView === 'team-activities'" class="card-body">
              <div class="week-toolbar">
                <div class="field-group inline">
                  <label>Pick a week:</label>
                  <div class="fake-input">{{ currentWeek?.label || 'No week selected' }}</div>
                </div>
                <button
                  class="action-btn nav-btn"
                  :disabled="previousWeekDisabled"
                  @click="goToPreviousWeek"
                >
                  ‹ Previous Week
                </button>
                <div class="week-range">{{ currentWeek?.range || 'No range available' }}</div>
                <button class="action-btn nav-btn" :disabled="nextWeekDisabled" @click="goToNextWeek">
                  Next Week ›
                </button>
                <button class="action-btn nav-btn" @click="goToCurrentWeek">Go to current week</button>
              </div>

              <p v-if="teamActivitiesError" class="message-banner error">{{ teamActivitiesError }}</p>

              <div class="activity-stack">
                <section v-for="group in teamActivityGroups" :key="group.studentId" class="activity-group">
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
                      <tr v-if="group.rows.length === 0">
                        <td colspan="7">No activity rows for this week.</td>
                      </tr>
                      <tr v-for="row in group.rows" :key="row.id">
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

            <div v-else-if="activeView === 'my-evaluations'" class="card-body">
              <div class="toolbar-row">
                <div class="filter-grid">
                  <div class="field-group">
                    <label>Start Week:</label>
                    <select v-model="evaluationFilters.startWeek" class="section-filter">
                      <option v-for="option in weekOptions" :key="option.startDate" :value="option.startDate">
                        {{ option.label }}
                      </option>
                    </select>
                  </div>
                  <div class="field-group">
                    <label>End Week:</label>
                    <select v-model="evaluationFilters.endWeek" class="section-filter">
                      <option v-for="option in weekOptions" :key="option.startDate" :value="option.startDate">
                        {{ option.label }}
                      </option>
                    </select>
                  </div>
                  <button class="action-btn primary" :disabled="loadingEvaluations" @click="queryEvaluations">
                    {{ loadingEvaluations ? 'Loading...' : 'Query' }}
                  </button>
                  <button class="action-btn" @click="resetEvaluations">Reset</button>
                </div>

                <div class="range-row">
                  <span>
                    Start Week:
                    <strong>{{ findWeekOption(evaluationFilters.startWeek)?.range || 'Not selected' }}</strong>
                  </span>
                  <span>
                    End Week:
                    <strong>{{ findWeekOption(evaluationFilters.endWeek)?.range || 'Not selected' }}</strong>
                  </span>
                </div>
              </div>

              <p v-if="evaluationError" class="message-banner error">{{ evaluationError }}</p>

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
                    <tr v-if="evaluationRows.length === 0">
                      <td :colspan="criterionColumns.length + 2">No evaluations found for this range.</td>
                    </tr>
                    <tr v-for="row in evaluationRows" :key="row.weekStart">
                      <td>{{ formatWeekLabel(findWeekOption(row.weekStart)) }}</td>
                      <td>{{ row.averageTotalScore.toFixed(2) }}</td>
                      <td v-for="(score, index) in row.criterionScores" :key="`${row.weekStart}-${index}`">
                        {{ score.toFixed(2) }}
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
                  <span class="highlight-chip">{{ submitWeekLabel }}</span>
                </p>
                <p class="subcopy">
                  Scores respect each criterion maximum. Comments and scores are saved when you submit.
                </p>
              </div>

              <p v-if="submitMessage" class="message-banner success">{{ submitMessage }}</p>
              <p v-if="submitError" class="message-banner error">{{ submitError }}</p>

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
                    <tr v-if="submitRows.length === 0">
                      <td :colspan="criterionColumns.length + 2">No peers available to evaluate.</td>
                    </tr>
                    <tr v-for="student in submitRows" :key="student.revieweeId">
                      <td>
                        <strong>{{ student.displayName }}</strong>
                        <div class="subcell">{{ student.teamName }}</div>
                      </td>
                      <td v-for="score in student.scores" :key="`${student.revieweeId}-${score.criterionId}`">
                        <input
                          v-model.number="score.score"
                          type="number"
                          min="0"
                          :max="score.maxScore"
                          class="score-input"
                        />
                      </td>
                      <td class="comment-cell">
                        <textarea v-model="student.publicComment" class="table-textarea"></textarea>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>

              <div class="stack-actions">
                <button
                  class="action-btn primary"
                  :disabled="submittingEvaluations || submitRows.length === 0"
                  @click="submitEvaluations"
                >
                  {{ submittingEvaluations ? 'Submitting...' : 'Submit Evaluations' }}
                </button>
              </div>
            </div>

            <div v-else-if="activeView === 'user-profile'" class="card-body form-body">
              <p v-if="profileMessage" class="message-banner success">{{ profileMessage }}</p>
              <p v-if="profileError" class="message-banner error">{{ profileError }}</p>

              <div class="profile-form">
                <div class="form-row">
                  <label>Id</label>
                  <div class="fake-field disabled">{{ currentUser?.id }}</div>
                </div>
                <div class="form-row">
                  <label><span class="required">*</span> Username</label>
                  <div class="fake-field">{{ currentUser?.username }}</div>
                </div>
                <div class="form-row">
                  <label><span class="required">*</span> First Name</label>
                  <input v-model="profileForm.firstName" class="form-input" />
                </div>
                <div class="form-row">
                  <label><span class="required">*</span> Last Name</label>
                  <input v-model="profileForm.lastName" class="form-input" />
                </div>
                <div class="form-row">
                  <label><span class="required">*</span> Email</label>
                  <input v-model="profileForm.email" class="form-input" />
                </div>
                <div class="form-row">
                  <label>Status</label>
                  <div class="fake-field disabled">{{ currentUser?.status }}</div>
                </div>
                <div class="form-row">
                  <label>Roles</label>
                  <div class="role-chip">{{ currentUser?.role }}</div>
                </div>
                <button class="action-btn primary update-btn" :disabled="savingProfile" @click="updateProfile">
                  {{ savingProfile ? 'Updating...' : 'Update' }}
                </button>
              </div>
            </div>

            <div v-else-if="activeView === 'reset-password'" class="card-body form-body">
              <p v-if="passwordMessage" class="message-banner success">{{ passwordMessage }}</p>
              <p v-if="passwordError" class="message-banner error">{{ passwordError }}</p>

              <div class="profile-form">
                <div class="form-row">
                  <label><span class="required">*</span> New Password</label>
                  <input v-model="resetPasswordForm.newPassword" type="password" class="form-input" />
                </div>
                <div class="form-row">
                  <label><span class="required">*</span> Confirm Password</label>
                  <input v-model="resetPasswordForm.confirmPassword" type="password" class="form-input" />
                </div>
                <button class="action-btn primary update-btn" :disabled="savingPassword" @click="resetPassword">
                  {{ savingPassword ? 'Saving...' : 'Update Password' }}
                </button>
              </div>
            </div>
          </section>

          <footer class="page-footer">Project Pulse 1.5.0</footer>
        </template>
      </main>
    </div>
  </div>
</template>
