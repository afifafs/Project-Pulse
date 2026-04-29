<script setup>
import { computed, onMounted, ref, watch } from 'vue'

const nav = ref('overview')
const loading = ref(false)
const alert = ref({ type: 'info', text: '' })

const sections = ref([])
const teams = ref([])
const students = ref([])
const instructors = ref([])
const rubrics = ref([])

const sectionDetail = ref(null)
const teamDetail = ref(null)

const selectedSectionId = ref(null)
const selectedTeamId = ref(null)

const sectionFormMode = ref('create')
const teamFormMode = ref('create')

const rubricForm = ref({
  name: '',
  criteria: [
    { name: 'Quality of work', description: 'How do you rate the quality of this teammate’s work?', maxScore: 10 },
  ],
})

const sectionForm = ref({
  id: null,
  name: '',
  courseCode: '',
  startDate: '',
  endDate: '',
  rubricId: null,
})

const teamForm = ref({
  id: null,
  name: '',
  description: '',
  website: '',
  sectionId: null,
})

const inviteStudentsForm = ref({ sectionId: null, emails: '' })
const inviteInstructorsForm = ref({ emails: '' })

const inactiveWeeks = ref([])
const assignedStudentIds = ref([])
const assignedInstructorIds = ref([])

const workspaceSectionId = ref(null)
const workspaceStudentId = ref(null)
const workspaceWeekStart = ref('')
const profile = ref(null)
const profileForm = ref({ firstName: '', lastName: '', email: '' })
const passwordForm = ref({ newPassword: '', confirmPassword: '' })
const activitiesRows = ref([])
const evaluationHistory = ref([])
const evaluationDrafts = ref([])

const reportSectionId = ref(null)
const reportTeamId = ref(null)
const reportStudentId = ref(null)
const reportWeekStart = ref('')
const sectionEvaluationReport = ref(null)
const teamActivityReport = ref(null)
const studentEvaluationReport = ref(null)

const overviewCards = computed(() => [
  { title: 'Sections', value: sections.value.length, icon: 'mdi-google-classroom' },
  { title: 'Teams', value: teams.value.length, icon: 'mdi-account-group' },
  { title: 'Students', value: students.value.length, icon: 'mdi-account-school' },
  { title: 'Instructors', value: instructors.value.length, icon: 'mdi-teach' },
])

const selectedSection = computed(() => sections.value.find((item) => item.id === selectedSectionId.value) ?? null)
const workspaceSectionDetail = computed(() => {
  if (sectionDetail.value?.id === workspaceSectionId.value) {
    return sectionDetail.value
  }
  return null
})

const workspaceWeeks = computed(() => workspaceSectionDetail.value?.weeks ?? [])
const workspaceStudents = computed(() => students.value.filter((student) => student.sectionName === selectedWorkspaceSectionName.value))
const selectedWorkspaceSectionName = computed(() => {
  const section = sections.value.find((item) => item.id === workspaceSectionId.value)
  return section?.name ?? null
})

const evaluationCriteria = computed(() => workspaceSectionDetail.value?.rubric?.criteria ?? [])

const currentStudentTeamMembers = computed(() => {
  if (!workspaceSectionDetail.value || !workspaceStudentId.value) {
    return []
  }
  const allTeams = workspaceSectionDetail.value.teams ?? []
  const currentTeam = allTeams.find((team) => team.students.some((student) => student.id === workspaceStudentId.value))
  if (!currentTeam) {
    return []
  }
  return currentTeam.students.filter((student) => student.id !== workspaceStudentId.value)
})

const sectionItems = computed(() => sections.value.map((section) => ({ title: section.name, value: section.id })))
const rubricItems = computed(() => rubrics.value.map((rubric) => ({ title: rubric.name, value: rubric.id })))
const teamItems = computed(() => teams.value.map((team) => ({ title: `${team.sectionName ?? 'No Section'} · ${team.name}`, value: team.id })))
const studentItems = computed(() =>
  students.value.map((student) => ({
    title: `${student.firstName} ${student.lastName} (${student.sectionName ?? 'No Section'})`,
    value: student.id,
  })),
)
const instructorItems = computed(() =>
  instructors.value.map((instructor) => ({
    title: `${instructor.firstName ?? ''} ${instructor.lastName ?? ''}`.trim() || instructor.email,
    value: instructor.id,
  })),
)

const availableStudentsForTeam = computed(() => {
  if (!teamDetail.value?.sectionName) {
    return []
  }
  return students.value
    .filter((student) => student.sectionName === teamDetail.value.sectionName)
    .map((student) => ({ title: `${student.firstName} ${student.lastName}`, value: student.id }))
})

async function apiFetch(url, options = {}) {
  const response = await fetch(url, {
    headers: {
      'Content-Type': 'application/json',
      ...(options.headers ?? {}),
    },
    ...options,
  })

  if (!response.ok) {
    const contentType = response.headers.get('content-type') ?? ''
    if (contentType.includes('application/json')) {
      const body = await response.json()
      throw new Error(body.message || body.error || `Request failed: ${response.status}`)
    }

    const text = await response.text()
    throw new Error(text || `Request failed: ${response.status}`)
  }

  if (response.status === 204) {
    return null
  }

  return response.json()
}

function pushAlert(type, text) {
  alert.value = { type, text }
}

function clearAlert() {
  alert.value = { type: 'info', text: '' }
}

function blankActivity() {
  return {
    category: '',
    activity: '',
    description: '',
    plannedHours: 0,
    actualHours: 0,
    status: '',
  }
}

function addCriterion() {
  rubricForm.value.criteria.push({ name: '', description: '', maxScore: 10 })
}

function removeCriterion(index) {
  rubricForm.value.criteria.splice(index, 1)
}

function addActivityRow() {
  activitiesRows.value.push(blankActivity())
}

function sectionWeekItems(section) {
  return (section?.weeks ?? []).map((week) => ({
    title: `Week ${week.weekNumber}: ${week.weekStart} - ${week.weekEnd}`,
    value: week.weekStart,
  }))
}

function activeWeekItems(section) {
  return sectionWeekItems({
    weeks: (section?.weeks ?? []).filter((week) => week.active),
  })
}

function firstUsableStudentId(sectionName) {
  const sectionStudents = students.value.filter((student) => student.sectionName === sectionName)
  const teamedStudent = sectionStudents.find((student) => student.teamName)
  return teamedStudent?.id ?? sectionStudents[0]?.id ?? null
}

function normalizeDateInput(value) {
  if (!value) return value
  if (/^\d{4}-\d{2}-\d{2}$/.test(value)) {
    return value
  }

  const parsed = new Date(value)
  if (Number.isNaN(parsed.getTime())) {
    return value
  }

  const year = parsed.getFullYear()
  const month = String(parsed.getMonth() + 1).padStart(2, '0')
  const day = String(parsed.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

function refreshEvaluationDrafts() {
  evaluationDrafts.value = currentStudentTeamMembers.value.map((student) => ({
    revieweeId: student.id,
    revieweeName: `${student.firstName} ${student.lastName}`,
    publicComment: '',
    privateComment: '',
    scores: evaluationCriteria.value.map((criterion) => ({
      criterionId: criterion.id,
      score: criterion.maxScore,
    })),
  }))
}

async function loadCatalogs() {
  loading.value = true
  try {
    const results = await Promise.allSettled([
      apiFetch('/sections'),
      apiFetch('/teams'),
      apiFetch('/students'),
      apiFetch('/instructors'),
      apiFetch('/rubrics'),
    ])

    const [sectionResult, teamResult, studentResult, instructorResult, rubricResult] = results

    sections.value = sectionResult.status === 'fulfilled' ? sectionResult.value : []
    teams.value = teamResult.status === 'fulfilled' ? teamResult.value : []
    students.value = studentResult.status === 'fulfilled' ? studentResult.value : []
    instructors.value = instructorResult.status === 'fulfilled' ? instructorResult.value : []
    rubrics.value = rubricResult.status === 'fulfilled' ? rubricResult.value : []

    const failures = results
      .filter((result) => result.status === 'rejected')
      .map((result) => result.reason?.message)
      .filter(Boolean)

    if (!selectedSectionId.value && sections.value.length) {
      selectedSectionId.value = sections.value[0].id
    }
    if (!workspaceSectionId.value && sections.value.length) {
      workspaceSectionId.value = sections.value[0].id
    }
    if (!reportSectionId.value && sections.value.length) {
      reportSectionId.value = sections.value[0].id
    }
    if (failures.length) {
      pushAlert('warning', failures[0])
    } else {
      clearAlert()
    }
  } catch (error) {
    pushAlert('error', error.message)
  } finally {
    loading.value = false
  }
}

async function loadSectionDetail(id) {
  if (!id) return
  try {
    sectionDetail.value = await apiFetch(`/sections/${id}`)
    inactiveWeeks.value = sectionDetail.value.weeks.filter((week) => !week.active).map((week) => week.weekStart)
  } catch (error) {
    pushAlert('error', error.message)
  }
}

async function loadTeamDetail(id) {
  if (!id) return
  try {
    teamDetail.value = await apiFetch(`/teams/${id}`)
    assignedStudentIds.value = teamDetail.value.students.map((student) => student.id)
    assignedInstructorIds.value = teamDetail.value.instructors.map((instructor) => instructor.id)
  } catch (error) {
    pushAlert('error', error.message)
  }
}

async function loadStudentWorkspace() {
  if (!workspaceSectionId.value) return
  await loadSectionDetail(workspaceSectionId.value)

  if (!workspaceStudentId.value || !workspaceStudents.value.some((student) => student.id === workspaceStudentId.value)) {
    workspaceStudentId.value = firstUsableStudentId(selectedWorkspaceSectionName.value)
  }

  const activeWeeks = workspaceWeeks.value.filter((week) => week.active)
  const currentWeekStillExists = workspaceWeeks.value.some((week) => week.weekStart === workspaceWeekStart.value)
  if (!workspaceWeekStart.value || !currentWeekStillExists) {
    workspaceWeekStart.value = activeWeeks[0]?.weekStart ?? workspaceWeeks.value[0]?.weekStart ?? ''
  }

  if (!workspaceStudentId.value) return

  try {
    profile.value = await apiFetch(`/students/${workspaceStudentId.value}/profile`)
    profileForm.value = {
      firstName: profile.value.firstName ?? '',
      lastName: profile.value.lastName ?? '',
      email: profile.value.email ?? '',
    }

    const activityResponse = workspaceWeekStart.value
      ? await apiFetch(`/students/${workspaceStudentId.value}/activities?weekStart=${workspaceWeekStart.value}`)
      : { rows: [] }

    activitiesRows.value = activityResponse.rows?.length ? activityResponse.rows : [blankActivity()]

    if (workspaceSectionDetail.value?.startDate && workspaceSectionDetail.value?.endDate) {
      const history = await apiFetch(
        `/students/${workspaceStudentId.value}/evaluations?sectionId=${workspaceSectionId.value}&startWeek=${workspaceSectionDetail.value.startDate}&endWeek=${workspaceSectionDetail.value.endDate}`,
      )
      evaluationHistory.value = history.rows ?? []
    } else {
      evaluationHistory.value = []
    }

    refreshEvaluationDrafts()
  } catch (error) {
    pushAlert('error', error.message)
  }
}

async function createRubric() {
  try {
    await apiFetch('/rubrics', {
      method: 'POST',
      body: JSON.stringify(rubricForm.value),
    })
    pushAlert('success', 'Rubric created.')
    rubricForm.value = {
      name: '',
      criteria: [{ name: '', description: '', maxScore: 10 }],
    }
    await loadCatalogs()
  } catch (error) {
    pushAlert('error', error.message)
  }
}

function beginEditSection() {
  if (!sectionDetail.value) return
  sectionFormMode.value = 'edit'
  sectionForm.value = {
    id: sectionDetail.value.id,
    name: sectionDetail.value.name,
    courseCode: sectionDetail.value.courseCode ?? '',
    startDate: sectionDetail.value.startDate,
    endDate: sectionDetail.value.endDate,
    rubricId: sectionDetail.value.rubric?.id ?? null,
  }
}

function resetSectionForm() {
  sectionFormMode.value = 'create'
  sectionForm.value = { id: null, name: '', courseCode: '', startDate: '', endDate: '', rubricId: null }
}

async function saveSection() {
  try {
    const isEdit = sectionFormMode.value === 'edit' && sectionForm.value.id
    const path = isEdit ? `/sections/${sectionForm.value.id}` : '/sections'
    const method = isEdit ? 'PUT' : 'POST'
    const payload = {
      ...sectionForm.value,
      startDate: normalizeDateInput(sectionForm.value.startDate),
      endDate: normalizeDateInput(sectionForm.value.endDate),
    }
    const response = await apiFetch(path, {
      method,
      body: JSON.stringify(payload),
    })
    pushAlert('success', isEdit ? 'Section updated.' : 'Section created.')
    selectedSectionId.value = response.id
    await loadCatalogs()
    await loadSectionDetail(response.id)
    resetSectionForm()
  } catch (error) {
    pushAlert('error', error.message)
  }
}

async function saveActiveWeeks() {
  if (!selectedSectionId.value) return
  try {
    await apiFetch(`/sections/${selectedSectionId.value}/active-weeks`, {
      method: 'PUT',
      body: JSON.stringify({ inactiveWeeks: inactiveWeeks.value }),
    })
    pushAlert('success', 'Active weeks updated.')
    await loadSectionDetail(selectedSectionId.value)
  } catch (error) {
    pushAlert('error', error.message)
  }
}

function beginEditTeam() {
  if (!teamDetail.value) return
  teamFormMode.value = 'edit'
  teamForm.value = {
    id: teamDetail.value.id,
    name: teamDetail.value.name,
    description: teamDetail.value.description ?? '',
    website: teamDetail.value.website ?? '',
    sectionId: sections.value.find((section) => section.name === teamDetail.value.sectionName)?.id ?? null,
  }
}

function resetTeamForm() {
  teamFormMode.value = 'create'
  teamForm.value = { id: null, name: '', description: '', website: '', sectionId: selectedSectionId.value }
}

async function saveTeam() {
  try {
    const isEdit = teamFormMode.value === 'edit' && teamForm.value.id
    const path = isEdit ? `/teams/${teamForm.value.id}` : '/teams'
    const method = isEdit ? 'PUT' : 'POST'
    const response = await apiFetch(path, {
      method,
      body: JSON.stringify(teamForm.value),
    })
    pushAlert('success', isEdit ? 'Team updated.' : 'Team created.')
    selectedTeamId.value = response.id
    await loadCatalogs()
    await loadTeamDetail(response.id)
    resetTeamForm()
  } catch (error) {
    pushAlert('error', error.message)
  }
}

async function saveTeamAssignments() {
  if (!teamDetail.value) return
  try {
    await apiFetch(`/teams/${teamDetail.value.id}/students`, {
      method: 'PUT',
      body: JSON.stringify({ studentIds: assignedStudentIds.value }),
    })
    await apiFetch(`/teams/${teamDetail.value.id}/instructors`, {
      method: 'PUT',
      body: JSON.stringify({ instructorIds: assignedInstructorIds.value }),
    })
    pushAlert('success', 'Team assignments updated.')
    await loadCatalogs()
    await loadTeamDetail(teamDetail.value.id)
  } catch (error) {
    pushAlert('error', error.message)
  }
}

async function inviteStudents() {
  try {
    await apiFetch('/students/invite', {
      method: 'POST',
      body: JSON.stringify({
        sectionId: inviteStudentsForm.value.sectionId,
        emails: inviteStudentsForm.value.emails,
      }),
    })
    pushAlert('success', 'Student invitations sent.')
    inviteStudentsForm.value.emails = ''
    await loadCatalogs()
  } catch (error) {
    pushAlert('error', error.message)
  }
}

async function inviteInstructors() {
  try {
    await apiFetch('/instructors/invite', {
      method: 'POST',
      body: JSON.stringify({ emails: inviteInstructorsForm.value.emails }),
    })
    pushAlert('success', 'Instructor invitations sent.')
    inviteInstructorsForm.value.emails = ''
    await loadCatalogs()
  } catch (error) {
    pushAlert('error', error.message)
  }
}

async function saveProfile() {
  try {
    await apiFetch(`/students/${workspaceStudentId.value}/profile`, {
      method: 'PUT',
      body: JSON.stringify(profileForm.value),
    })
    pushAlert('success', 'Profile updated.')
    await loadCatalogs()
    await loadStudentWorkspace()
  } catch (error) {
    pushAlert('error', error.message)
  }
}

async function resetPassword() {
  try {
    await apiFetch(`/students/${workspaceStudentId.value}/reset-password`, {
      method: 'POST',
      body: JSON.stringify(passwordForm.value),
    })
    pushAlert('success', 'Password updated.')
    passwordForm.value = { newPassword: '', confirmPassword: '' }
  } catch (error) {
    pushAlert('error', error.message)
  }
}

async function saveActivities() {
  if (!workspaceStudentId.value) {
    pushAlert('error', 'Select a student first.')
    return
  }
  if (!workspaceWeekStart.value) {
    pushAlert('error', 'Select an active week before saving activities.')
    return
  }
  try {
    await apiFetch(`/students/${workspaceStudentId.value}/activities?weekStart=${workspaceWeekStart.value}`, {
      method: 'PUT',
      body: JSON.stringify(activitiesRows.value),
    })
    pushAlert('success', 'Weekly activity report saved.')
    await loadStudentWorkspace()
  } catch (error) {
    pushAlert('error', error.message)
  }
}

async function submitEvaluations() {
  if (!workspaceStudentId.value) {
    pushAlert('error', 'Select a student first.')
    return
  }
  if (!workspaceWeekStart.value) {
    pushAlert('error', 'Select an active week before submitting evaluations.')
    return
  }
  if (!currentStudentTeamMembers.value.length) {
    pushAlert('error', 'Assign the selected student to a team with teammates before submitting evaluations.')
    return
  }
  if (!evaluationCriteria.value.length) {
    pushAlert('error', 'The selected section needs a rubric with criteria before evaluations can be submitted.')
    return
  }
  try {
    await apiFetch(
      `/students/${workspaceStudentId.value}/evaluations?sectionId=${workspaceSectionId.value}&weekStart=${workspaceWeekStart.value}`,
      {
        method: 'POST',
        body: JSON.stringify({ submissions: evaluationDrafts.value }),
      },
    )
    pushAlert('success', 'Peer evaluations submitted.')
    await loadStudentWorkspace()
  } catch (error) {
    pushAlert('error', error.message)
  }
}

async function loadReports() {
  if (!reportSectionId.value) return
  try {
    await loadSectionDetail(reportSectionId.value)
    const reportWeeks = (sectionDetail.value?.weeks ?? []).filter((week) => week.active)
    const currentReportWeekExists = (sectionDetail.value?.weeks ?? []).some((week) => week.weekStart === reportWeekStart.value)
    if (!reportWeekStart.value || !currentReportWeekExists) {
      reportWeekStart.value = (reportWeeks[0]?.weekStart ?? sectionDetail.value?.weeks?.[0]?.weekStart ?? '')
    }
    if (reportWeekStart.value) {
      sectionEvaluationReport.value = await apiFetch(`/reports/sections/${reportSectionId.value}/evaluations?weekStart=${reportWeekStart.value}`)
    }
    if (reportTeamId.value && reportWeekStart.value) {
      teamActivityReport.value = await apiFetch(`/teams/${reportTeamId.value}/activities?weekStart=${reportWeekStart.value}`)
    }
    if (reportStudentId.value && reportWeekStart.value && sectionDetail.value) {
      studentEvaluationReport.value = await apiFetch(
        `/reports/students/${reportStudentId.value}/evaluations?sectionId=${reportSectionId.value}&startWeek=${sectionDetail.value.startDate}&endWeek=${sectionDetail.value.endDate}`,
      )
    }
  } catch (error) {
    pushAlert('error', error.message)
  }
}

watch(selectedSectionId, async (value) => {
  if (value) {
    await loadSectionDetail(value)
  }
})

watch(selectedTeamId, async (value) => {
  if (value) {
    await loadTeamDetail(value)
  }
})

watch([workspaceSectionId, workspaceStudentId, workspaceWeekStart], async () => {
  if (workspaceSectionId.value) {
    await loadStudentWorkspace()
  }
})

watch([reportSectionId, reportTeamId, reportStudentId, reportWeekStart], async () => {
  if (nav.value === 'instructor-reports') {
    await loadReports()
  }
})

watch([currentStudentTeamMembers, evaluationCriteria], () => {
  refreshEvaluationDrafts()
})

onMounted(async () => {
  await loadCatalogs()
  resetSectionForm()
  resetTeamForm()
})
</script>

<template>
  <v-app>
    <v-navigation-drawer permanent width="280">
      <v-list-item title="Project Pulse" subtitle="AI-assisted dashboard" />
      <v-divider class="mb-2" />
      <v-list nav density="comfortable">
        <v-list-item :active="nav === 'overview'" prepend-icon="mdi-view-dashboard" title="Overview" @click="nav = 'overview'" />
        <v-list-item :active="nav === 'admin-rubrics'" prepend-icon="mdi-format-list-bulleted-square" title="Admin · Rubrics" @click="nav = 'admin-rubrics'" />
        <v-list-item :active="nav === 'admin-sections'" prepend-icon="mdi-google-classroom" title="Admin · Sections" @click="nav = 'admin-sections'" />
        <v-list-item :active="nav === 'admin-teams'" prepend-icon="mdi-account-group" title="Admin · Teams" @click="nav = 'admin-teams'" />
        <v-list-item :active="nav === 'admin-people'" prepend-icon="mdi-account-multiple" title="Admin · People" @click="nav = 'admin-people'" />
        <v-list-item :active="nav === 'student-workspace'" prepend-icon="mdi-account-school" title="Student Workspace" @click="nav = 'student-workspace'" />
        <v-list-item :active="nav === 'instructor-reports'" prepend-icon="mdi-chart-box" title="Instructor Reports" @click="nav = 'instructor-reports'" />
      </v-list>
    </v-navigation-drawer>

    <v-main>
      <v-container fluid class="pa-6">
        <v-row class="mb-3" align="center">
          <v-col cols="12" md="8">
            <div class="text-h4 font-weight-bold">Project Pulse</div>
            <div class="text-medium-emphasis">Senior design administration, student reporting, and instructor analytics.</div>
          </v-col>
          <v-col cols="12" md="4" class="d-flex justify-end">
            <v-btn color="primary" prepend-icon="mdi-refresh" :loading="loading" @click="loadCatalogs">Refresh</v-btn>
          </v-col>
        </v-row>

        <v-alert
          v-if="alert.text"
          :type="alert.type"
          variant="tonal"
          closable
          class="mb-4"
          @click:close="clearAlert"
        >
          {{ alert.text }}
        </v-alert>

        <template v-if="nav === 'overview'">
          <v-row>
            <v-col v-for="card in overviewCards" :key="card.title" cols="12" md="3">
              <v-card class="fill-height">
                <v-card-text class="d-flex align-center ga-4">
                  <v-icon size="36" color="primary">{{ card.icon }}</v-icon>
                  <div>
                    <div class="text-overline">{{ card.title }}</div>
                    <div class="text-h4 font-weight-bold">{{ card.value }}</div>
                  </div>
                </v-card-text>
              </v-card>
            </v-col>
          </v-row>
          <v-row class="mt-1">
            <v-col cols="12" md="7">
              <v-card>
                <v-card-title>Sections</v-card-title>
                <v-data-table :items="sections" :headers="[
                  { title: 'Name', key: 'name' },
                  { title: 'Course', key: 'courseCode' },
                ]" />
              </v-card>
            </v-col>
            <v-col cols="12" md="5">
              <v-card>
                <v-card-title>Team Snapshot</v-card-title>
                <v-list>
                  <v-list-item v-for="team in teams.slice(0, 6)" :key="team.id">
                    <v-list-item-title>{{ team.name }}</v-list-item-title>
                    <v-list-item-subtitle>{{ team.sectionName }} · {{ team.students.length }} students</v-list-item-subtitle>
                  </v-list-item>
                </v-list>
              </v-card>
            </v-col>
          </v-row>
        </template>

        <template v-else-if="nav === 'admin-rubrics'">
          <v-row>
            <v-col cols="12" md="6">
              <v-card>
                <v-card-title>Create Rubric</v-card-title>
                <v-card-text>
                  <v-text-field v-model="rubricForm.name" label="Rubric name" />
                  <v-card v-for="(criterion, index) in rubricForm.criteria" :key="index" class="mb-3" variant="outlined">
                    <v-card-text>
                      <div class="d-flex justify-space-between align-center mb-2">
                        <div class="text-subtitle-2">Criterion {{ index + 1 }}</div>
                        <v-btn icon="mdi-delete" size="small" variant="text" @click="removeCriterion(index)" />
                      </div>
                      <v-text-field v-model="criterion.name" label="Name" />
                      <v-textarea v-model="criterion.description" label="Description" rows="2" />
                      <v-text-field v-model.number="criterion.maxScore" type="number" label="Max score" />
                    </v-card-text>
                  </v-card>
                  <div class="d-flex ga-2">
                    <v-btn variant="tonal" prepend-icon="mdi-plus" @click="addCriterion">Add Criterion</v-btn>
                    <v-btn color="primary" prepend-icon="mdi-content-save" @click="createRubric">Create Rubric</v-btn>
                  </div>
                </v-card-text>
              </v-card>
            </v-col>
            <v-col cols="12" md="6">
              <v-card>
                <v-card-title>Existing Rubrics</v-card-title>
                <v-data-table
                  :items="rubrics"
                  :headers="[
                    { title: 'Name', key: 'name' },
                    { title: 'Criteria', key: 'criteria.length' },
                  ]"
                >
                  <template #item.criteria.length="{ item }">
                    {{ item.criteria?.length ?? 0 }}
                  </template>
                </v-data-table>
              </v-card>
            </v-col>
          </v-row>
        </template>

        <template v-else-if="nav === 'admin-sections'">
          <v-row>
            <v-col cols="12" md="5">
              <v-card class="mb-4">
                <v-card-title>{{ sectionFormMode === 'edit' ? 'Edit Section' : 'Create Section' }}</v-card-title>
                <v-card-text>
                  <v-text-field v-model="sectionForm.name" label="Section name" />
                  <v-text-field v-model="sectionForm.courseCode" label="Course code" />
                  <v-text-field v-model="sectionForm.startDate" type="date" label="Start date" />
                  <v-text-field v-model="sectionForm.endDate" type="date" label="End date" />
                  <v-select v-model="sectionForm.rubricId" :items="rubricItems" label="Rubric" />
                  <div class="d-flex ga-2">
                    <v-btn color="primary" @click="saveSection">{{ sectionFormMode === 'edit' ? 'Save Changes' : 'Create Section' }}</v-btn>
                    <v-btn variant="tonal" @click="resetSectionForm">Reset</v-btn>
                  </div>
                </v-card-text>
              </v-card>
              <v-card>
                <v-card-title>Sections</v-card-title>
                <v-list>
                  <v-list-item
                    v-for="section in sections"
                    :key="section.id"
                    :active="selectedSectionId === section.id"
                    @click="selectedSectionId = section.id"
                  >
                    <v-list-item-title>{{ section.name }}</v-list-item-title>
                    <v-list-item-subtitle>{{ section.courseCode }}</v-list-item-subtitle>
                  </v-list-item>
                </v-list>
              </v-card>
            </v-col>
            <v-col cols="12" md="7">
              <v-card v-if="sectionDetail">
                <v-card-title class="d-flex justify-space-between">
                  <span>{{ sectionDetail.name }}</span>
                  <v-btn variant="tonal" size="small" prepend-icon="mdi-pencil" @click="beginEditSection">Edit</v-btn>
                </v-card-title>
                <v-card-text>
                  <div class="mb-3">Rubric: <strong>{{ sectionDetail.rubric?.name ?? 'None' }}</strong></div>
                  <div class="mb-3">Dates: {{ sectionDetail.startDate }} to {{ sectionDetail.endDate }}</div>
                  <v-select
                    v-model="inactiveWeeks"
                    :items="sectionWeekItems(sectionDetail)"
                    label="Inactive weeks"
                    multiple
                    chips
                    closable-chips
                  />
                  <v-btn color="primary" class="mb-4" @click="saveActiveWeeks">Save Active Weeks</v-btn>
                  <div class="text-subtitle-1 mb-2">Teams</div>
                  <v-chip v-for="team in sectionDetail.teams" :key="team.id" class="ma-1">{{ team.name }}</v-chip>
                  <div class="text-subtitle-1 mt-4 mb-2">Unassigned Students</div>
                  <v-chip v-for="student in sectionDetail.studentsNotAssignedToTeams" :key="student.id" class="ma-1" color="secondary">
                    {{ student.firstName }} {{ student.lastName }}
                  </v-chip>
                </v-card-text>
              </v-card>
            </v-col>
          </v-row>
        </template>

        <template v-else-if="nav === 'admin-teams'">
          <v-row>
            <v-col cols="12" md="5">
              <v-card class="mb-4">
                <v-card-title>{{ teamFormMode === 'edit' ? 'Edit Team' : 'Create Team' }}</v-card-title>
                <v-card-text>
                  <v-text-field v-model="teamForm.name" label="Team name" />
                  <v-textarea v-model="teamForm.description" label="Description" rows="2" />
                  <v-text-field v-model="teamForm.website" label="Website" />
                  <v-select v-model="teamForm.sectionId" :items="sectionItems" label="Section" />
                  <div class="d-flex ga-2">
                    <v-btn color="primary" @click="saveTeam">{{ teamFormMode === 'edit' ? 'Save Changes' : 'Create Team' }}</v-btn>
                    <v-btn variant="tonal" @click="resetTeamForm">Reset</v-btn>
                  </div>
                </v-card-text>
              </v-card>

              <v-card>
                <v-card-title>Teams</v-card-title>
                <v-list>
                  <v-list-item
                    v-for="team in teams"
                    :key="team.id"
                    :active="selectedTeamId === team.id"
                    @click="selectedTeamId = team.id"
                  >
                    <v-list-item-title>{{ team.name }}</v-list-item-title>
                    <v-list-item-subtitle>{{ team.sectionName }}</v-list-item-subtitle>
                  </v-list-item>
                </v-list>
              </v-card>
            </v-col>
            <v-col cols="12" md="7">
              <v-card v-if="teamDetail">
                <v-card-title class="d-flex justify-space-between">
                  <span>{{ teamDetail.name }}</span>
                  <v-btn variant="tonal" size="small" prepend-icon="mdi-pencil" @click="beginEditTeam">Edit</v-btn>
                </v-card-title>
                <v-card-text>
                  <div class="mb-2">{{ teamDetail.description }}</div>
                  <div class="mb-4">{{ teamDetail.website }}</div>
                  <v-select v-model="assignedStudentIds" :items="availableStudentsForTeam" label="Assigned students" multiple chips />
                  <v-select v-model="assignedInstructorIds" :items="instructorItems" label="Assigned instructors" multiple chips />
                  <v-btn color="primary" class="mb-4" @click="saveTeamAssignments">Save Assignments</v-btn>

                  <div class="text-subtitle-1 mb-2">Current Members</div>
                  <v-chip v-for="student in teamDetail.students" :key="student.id" class="ma-1">{{ student.firstName }} {{ student.lastName }}</v-chip>
                  <div class="text-subtitle-1 mt-4 mb-2">Current Instructors</div>
                  <v-chip v-for="instructor in teamDetail.instructors" :key="instructor.id" class="ma-1" color="secondary">
                    {{ instructor.firstName }} {{ instructor.lastName }}
                  </v-chip>
                </v-card-text>
              </v-card>
            </v-col>
          </v-row>
        </template>

        <template v-else-if="nav === 'admin-people'">
          <v-row>
            <v-col cols="12" md="6">
              <v-card class="mb-4">
                <v-card-title>Invite Students</v-card-title>
                <v-card-text>
                  <v-select v-model="inviteStudentsForm.sectionId" :items="sectionItems" label="Section" />
                  <v-textarea v-model="inviteStudentsForm.emails" label="Emails (semicolon separated or array text)" rows="3" />
                  <v-btn color="primary" @click="inviteStudents">Send Student Invites</v-btn>
                </v-card-text>
              </v-card>
              <v-card>
                <v-card-title>Students</v-card-title>
                <v-data-table :items="students" :headers="[
                  { title: 'Name', key: 'firstName' },
                  { title: 'Email', key: 'email' },
                  { title: 'Section', key: 'sectionName' },
                  { title: 'Team', key: 'teamName' },
                ]">
                  <template #item.firstName="{ item }">
                    {{ item.firstName }} {{ item.lastName }}
                  </template>
                </v-data-table>
              </v-card>
            </v-col>
            <v-col cols="12" md="6">
              <v-card class="mb-4">
                <v-card-title>Invite Instructors</v-card-title>
                <v-card-text>
                  <v-textarea v-model="inviteInstructorsForm.emails" label="Emails (semicolon separated or array text)" rows="3" />
                  <v-btn color="primary" @click="inviteInstructors">Send Instructor Invites</v-btn>
                </v-card-text>
              </v-card>
              <v-card>
                <v-card-title>Instructors</v-card-title>
                <v-data-table :items="instructors" :headers="[
                  { title: 'Name', key: 'firstName' },
                  { title: 'Email', key: 'email' },
                  { title: 'Active', key: 'active' },
                ]">
                  <template #item.firstName="{ item }">
                    {{ item.firstName }} {{ item.lastName }}
                  </template>
                </v-data-table>
              </v-card>
            </v-col>
          </v-row>
        </template>

        <template v-else-if="nav === 'student-workspace'">
          <v-row>
            <v-col cols="12">
              <v-card class="mb-4">
                <v-card-text class="d-flex flex-wrap ga-4">
                  <v-select v-model="workspaceSectionId" :items="sectionItems" label="Section" class="selector" />
                  <v-select v-model="workspaceStudentId" :items="studentItems.filter((item) => item.title.includes(selectedWorkspaceSectionName ?? ''))" label="Student" class="selector" />
                  <v-select
                    v-model="workspaceWeekStart"
                    :items="activeWeekItems(workspaceSectionDetail)"
                    label="Week"
                    class="selector"
                  />
                </v-card-text>
              </v-card>
            </v-col>

            <v-col cols="12" md="4">
              <v-card class="mb-4">
                <v-card-title>Profile</v-card-title>
                <v-card-text>
                  <v-text-field v-model="profileForm.firstName" label="First name" />
                  <v-text-field v-model="profileForm.lastName" label="Last name" />
                  <v-text-field v-model="profileForm.email" label="Email" />
                  <v-btn color="primary" @click="saveProfile">Save Profile</v-btn>
                </v-card-text>
              </v-card>

              <v-card>
                <v-card-title>Reset Password</v-card-title>
                <v-card-text>
                  <v-text-field v-model="passwordForm.newPassword" label="New password" type="password" />
                  <v-text-field v-model="passwordForm.confirmPassword" label="Confirm password" type="password" />
                  <v-btn color="primary" @click="resetPassword">Update Password</v-btn>
                </v-card-text>
              </v-card>
            </v-col>

            <v-col cols="12" md="8">
              <v-card class="mb-4">
                <v-card-title class="d-flex justify-space-between">
                  <span>Weekly Activity Report</span>
                  <v-btn variant="tonal" prepend-icon="mdi-plus" @click="addActivityRow">Add Row</v-btn>
                </v-card-title>
                <v-card-text>
                  <v-row v-for="(row, index) in activitiesRows" :key="index" class="mb-2">
                    <v-col cols="12" md="2"><v-text-field v-model="row.category" label="Category" /></v-col>
                    <v-col cols="12" md="2"><v-text-field v-model="row.activity" label="Activity" /></v-col>
                    <v-col cols="12" md="3"><v-text-field v-model="row.description" label="Description" /></v-col>
                    <v-col cols="12" md="1"><v-text-field v-model.number="row.plannedHours" type="number" label="Planned" /></v-col>
                    <v-col cols="12" md="1"><v-text-field v-model.number="row.actualHours" type="number" label="Actual" /></v-col>
                    <v-col cols="12" md="2"><v-text-field v-model="row.status" label="Status" /></v-col>
                    <v-col cols="12" md="1" class="d-flex align-center">
                      <v-btn icon="mdi-delete" variant="text" @click="activitiesRows.splice(index, 1)" />
                    </v-col>
                  </v-row>
                  <v-btn color="primary" @click="saveActivities">Save Activities</v-btn>
                </v-card-text>
              </v-card>

              <v-card class="mb-4">
                <v-card-title>My Evaluation History</v-card-title>
                <v-data-table :items="evaluationHistory" :headers="[
                  { title: 'Week Start', key: 'weekStart' },
                  { title: 'Week End', key: 'weekEnd' },
                  { title: 'Average', key: 'averageTotalScore' },
                ]" />
              </v-card>

              <v-card>
                <v-card-title>Submit Peer Evaluations</v-card-title>
                <v-card-text>
                  <v-expansion-panels>
                    <v-expansion-panel v-for="submission in evaluationDrafts" :key="submission.revieweeId">
                      <v-expansion-panel-title>{{ submission.revieweeName }}</v-expansion-panel-title>
                      <v-expansion-panel-text>
                        <v-row v-for="score in submission.scores" :key="score.criterionId">
                          <v-col cols="12" md="8">
                            {{ evaluationCriteria.find((criterion) => criterion.id === score.criterionId)?.name }}
                          </v-col>
                          <v-col cols="12" md="4">
                            <v-slider
                              v-model="score.score"
                              :min="0"
                              :max="evaluationCriteria.find((criterion) => criterion.id === score.criterionId)?.maxScore ?? 10"
                              step="1"
                              thumb-label
                            />
                          </v-col>
                        </v-row>
                        <v-textarea v-model="submission.publicComment" label="Public comment" rows="2" />
                        <v-textarea v-model="submission.privateComment" label="Private comment to instructor" rows="2" />
                      </v-expansion-panel-text>
                    </v-expansion-panel>
                  </v-expansion-panels>
                  <v-btn color="primary" class="mt-4" @click="submitEvaluations">Submit Evaluations</v-btn>
                </v-card-text>
              </v-card>
            </v-col>
          </v-row>
        </template>

        <template v-else-if="nav === 'instructor-reports'">
          <v-row>
            <v-col cols="12">
              <v-card class="mb-4">
                <v-card-text class="d-flex flex-wrap ga-4">
                  <v-select v-model="reportSectionId" :items="sectionItems" label="Section" class="selector" />
                  <v-select
                    v-model="reportTeamId"
                    :items="teamItems.filter((item) => item.title.includes(sections.find((section) => section.id === reportSectionId)?.name ?? ''))"
                    label="Team"
                    class="selector"
                  />
                  <v-select
                    v-model="reportStudentId"
                    :items="studentItems.filter((item) => item.title.includes(sections.find((section) => section.id === reportSectionId)?.name ?? ''))"
                    label="Student"
                    class="selector"
                  />
                  <v-select
                    v-model="reportWeekStart"
                    :items="activeWeekItems(sectionDetail?.id === reportSectionId ? sectionDetail : null)"
                    label="Week"
                    class="selector"
                  />
                </v-card-text>
              </v-card>
            </v-col>

            <v-col cols="12" md="6">
              <v-card class="mb-4">
                <v-card-title>Section Evaluation Report</v-card-title>
                <v-data-table
                  :items="sectionEvaluationReport?.rows ?? []"
                  :headers="[
                    { title: 'Student', key: 'studentName' },
                    { title: 'Average', key: 'averageTotalScore' },
                    { title: 'Submissions', key: 'submissionCount' },
                  ]"
                />
              </v-card>

              <v-card>
                <v-card-title>Team WAR Report</v-card-title>
                <v-list v-if="teamActivityReport?.groups?.length">
                  <v-list-group v-for="group in teamActivityReport.groups" :key="group.studentId" :value="group.studentId">
                    <template #activator="{ props }">
                      <v-list-item v-bind="props" :title="group.studentName" :subtitle="`${group.rows.length} entries`" />
                    </template>
                    <v-list-item
                      v-for="(row, index) in group.rows"
                      :key="index"
                      :title="row.activity"
                      :subtitle="`${row.category} · ${row.status} · ${row.actualHours} hrs`"
                    />
                  </v-list-group>
                </v-list>
                <v-card-text v-else class="text-medium-emphasis">Select a team and week to generate the WAR report.</v-card-text>
              </v-card>
            </v-col>

            <v-col cols="12" md="6">
              <v-card>
                <v-card-title>Student Evaluation Report</v-card-title>
                <v-list v-if="studentEvaluationReport?.rows?.length">
                  <v-list-item
                    v-for="row in studentEvaluationReport.rows"
                    :key="row.weekStart"
                    :title="`${row.weekStart} to ${row.weekEnd}`"
                    :subtitle="`Average: ${row.averageTotalScore}`"
                  >
                    <template #append>
                      <v-chip size="small" color="primary">{{ row.publicComments.length }} public</v-chip>
                      <v-chip size="small" color="secondary" class="ml-2">{{ row.privateComments.length }} private</v-chip>
                    </template>
                  </v-list-item>
                </v-list>
                <v-card-text v-else class="text-medium-emphasis">Select a student and section to generate the detailed evaluation report.</v-card-text>
              </v-card>
            </v-col>
          </v-row>
        </template>
      </v-container>
    </v-main>
  </v-app>
</template>
