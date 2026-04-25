<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import {
  createActivity,
  deleteActivity,
  getInstructors,
  getInstructorStudentPeerEvaluationReport,
  getInstructorStudentWarReport,
  getSections,
  getSectionDetails,
  getSectionPeerEvaluationReport,
  getStudentAccounts,
  getStudentActivities,
  getStudentPeerEvaluationReport,
  getTeamWarReport,
  getTeams,
  reactivateInstructor,
  setupInstructorAccount,
  setupStudentAccount,
  submitPeerEvaluation,
  updateActivity,
  updateStudentAccount,
} from '../api/projectPulseApi'
import EmptyState from '../components/EmptyState.vue'
import ErrorMessage from '../components/ErrorMessage.vue'
import LoadingState from '../components/LoadingState.vue'

const useCases = [
  { id: 24, label: 'Reactivate instructor' },
  { id: 25, label: 'Student account setup' },
  { id: 26, label: 'Student account edit' },
  { id: 27, label: 'Manage WAR activities' },
  { id: 28, label: 'Submit peer evaluation' },
  { id: 29, label: 'Student peer report' },
  { id: 30, label: 'Instructor account setup' },
  { id: 31, label: 'Section peer report' },
  { id: 32, label: 'Team WAR report' },
  { id: 33, label: 'Instructor student peer report' },
  { id: 34, label: 'Instructor student WAR report' },
]

const isLoading = ref(false)
const loadError = ref('')
const activeUseCase = ref(24)

const sections = ref([])
const sectionDetailsById = reactive({})
const teams = ref([])
const students = ref([])
const instructors = ref([])
const activities = ref([])

const flashMessage = ref('')
const reportError = ref('')

const studentSetupForm = reactive({
  studentId: '',
  firstName: '',
  lastName: '',
  email: '',
  username: '',
  password: '',
  sectionId: '',
  teamId: '',
})

const studentEditSelection = ref('')
const studentEditForm = reactive({
  firstName: '',
  lastName: '',
  email: '',
  username: '',
  password: '',
  sectionId: '',
  teamId: '',
})

const activityForm = reactive({
  id: '',
  studentId: '',
  title: '',
  description: '',
  comments: '',
  category: 'DEVELOPMENT',
  status: 'IN_PROGRESS',
  plannedHours: 4,
  actualHours: 4,
  weekNumber: 1,
})

const peerForm = reactive({
  evaluatorId: '',
  evaluateeId: '',
  weekNumber: 1,
  publicComment: '',
  privateComment: '',
  ratings: [],
})

const instructorSetupForm = reactive({
  instructorId: '',
  firstName: '',
  lastName: '',
  email: '',
  username: '',
  password: '',
  sectionId: '',
})

const studentPeerFilters = reactive({ studentId: '', weekNumber: '' })
const sectionPeerFilters = reactive({ instructorId: '', sectionId: '', weekNumber: '' })
const teamWarFilters = reactive({ teamId: '', viewerType: 'STUDENT', viewerId: '', weekNumber: '' })
const instructorStudentPeerFilters = reactive({ instructorId: '', studentId: '', weekNumber: '' })
const instructorStudentWarFilters = reactive({ instructorId: '', studentId: '', weekNumber: '' })

const studentPeerReport = ref(null)
const sectionPeerReport = ref(null)
const teamWarReport = ref(null)
const instructorStudentPeerReport = ref(null)
const instructorStudentWarReport = ref(null)

const activityCategories = ['DESIGN', 'DEVELOPMENT', 'TESTING', 'DOCUMENTATION', 'RESEARCH', 'MEETING']
const activityStatuses = ['PLANNED', 'IN_PROGRESS', 'COMPLETE', 'BLOCKED']

const inactiveInstructors = computed(() => instructors.value.filter((instructor) => !instructor.active))
const viewerChoices = computed(() => (teamWarFilters.viewerType === 'INSTRUCTOR' ? instructors.value : students.value))

watch(studentEditSelection, (studentId) => {
  const student = students.value.find((item) => item.id === Number(studentId))
  studentEditForm.firstName = student?.firstName ?? ''
  studentEditForm.lastName = student?.lastName ?? ''
  studentEditForm.email = student?.email ?? ''
  studentEditForm.username = student?.username ?? ''
  studentEditForm.password = ''
  studentEditForm.sectionId = student?.sectionId ?? ''
  studentEditForm.teamId = student?.teamId ?? ''
})

watch(
  () => studentSetupForm.studentId,
  (studentId) => {
    const student = students.value.find((item) => item.id === Number(studentId))
    if (!student) return

    studentSetupForm.firstName = student.firstName ?? ''
    studentSetupForm.lastName = student.lastName ?? ''
    studentSetupForm.email = student.email ?? ''
    studentSetupForm.username = student.username ?? ''
    studentSetupForm.sectionId = student.sectionId ?? ''
    studentSetupForm.teamId = student.teamId ?? ''
  },
)

watch(
  () => activityForm.studentId,
  async (studentId) => {
    if (!studentId) {
      activities.value = []
      return
    }

    activities.value = await getStudentActivities(studentId)
  },
)

watch(
  () => peerForm.evaluateeId,
  async (evaluateeId) => {
    const student = students.value.find((item) => item.id === Number(evaluateeId))
    if (!student?.sectionId) {
      peerForm.ratings = []
      return
    }

    const sectionDetail = await ensureSectionDetail(student.sectionId)
    const criteria = sectionDetail?.rubric?.criteria ?? []
    peerForm.ratings = criteria.map((criterion) => ({
      criterionId: criterion.id,
      criterionName: criterion.name,
      maxScore: criterion.maxScore,
      score: Math.min(criterion.maxScore, 8),
    }))
  },
)

async function loadWorkspace() {
  loadError.value = ''
  isLoading.value = true

  try {
    const [sectionResults, teamResults, studentResults, instructorResults] = await Promise.all([
      getSections(),
      getTeams(),
      getStudentAccounts(),
      getInstructors(),
    ])

    sections.value = sectionResults
    teams.value = teamResults
    students.value = studentResults
    instructors.value = instructorResults

    await Promise.all(sectionResults.map((section) => ensureSectionDetail(section.id)))
  } catch (error) {
    loadError.value = error.message
  } finally {
    isLoading.value = false
  }
}

async function ensureSectionDetail(sectionId) {
  if (!sectionId) return null
  if (!sectionDetailsById[sectionId]) {
    sectionDetailsById[sectionId] = await getSectionDetails(sectionId)
  }

  return sectionDetailsById[sectionId]
}

async function refreshCoreData() {
  const [sectionResults, teamResults, studentResults, instructorResults] = await Promise.all([
    getSections(),
    getTeams(),
    getStudentAccounts(),
    getInstructors(),
  ])

  sections.value = sectionResults
  teams.value = teamResults
  students.value = studentResults
  instructors.value = instructorResults
}

function clearFeedback() {
  flashMessage.value = ''
  reportError.value = ''
}

function asNumber(value) {
  return value === '' || value === null || value === undefined ? null : Number(value)
}

function personName(person) {
  return [person.firstName, person.lastName].filter(Boolean).join(' ')
}

function score(value) {
  return Number(value ?? 0).toFixed(2)
}

function resetActivityEditor() {
  activityForm.id = ''
  activityForm.title = ''
  activityForm.description = ''
  activityForm.comments = ''
  activityForm.category = 'DEVELOPMENT'
  activityForm.status = 'IN_PROGRESS'
  activityForm.plannedHours = 4
  activityForm.actualHours = 4
  activityForm.weekNumber = 1
}

async function submitStudentSetup() {
  clearFeedback()
  await setupStudentAccount({
    studentId: asNumber(studentSetupForm.studentId),
    firstName: studentSetupForm.firstName,
    lastName: studentSetupForm.lastName,
    email: studentSetupForm.email,
    username: studentSetupForm.username,
    password: studentSetupForm.password,
    sectionId: asNumber(studentSetupForm.sectionId),
    teamId: asNumber(studentSetupForm.teamId),
  })

  flashMessage.value = 'Student account setup completed.'
  await refreshCoreData()
}

async function submitStudentEdit() {
  clearFeedback()
  await updateStudentAccount(studentEditSelection.value, {
    firstName: studentEditForm.firstName,
    lastName: studentEditForm.lastName,
    email: studentEditForm.email,
    username: studentEditForm.username,
    password: studentEditForm.password || 'password123',
    sectionId: asNumber(studentEditForm.sectionId),
    teamId: asNumber(studentEditForm.teamId),
  })

  flashMessage.value = 'Student account updated.'
  await refreshCoreData()
}

async function submitInstructorSetup() {
  clearFeedback()
  await setupInstructorAccount({
    instructorId: asNumber(instructorSetupForm.instructorId),
    firstName: instructorSetupForm.firstName,
    lastName: instructorSetupForm.lastName,
    email: instructorSetupForm.email,
    username: instructorSetupForm.username,
    password: instructorSetupForm.password,
    sectionId: asNumber(instructorSetupForm.sectionId),
  })

  flashMessage.value = 'Instructor account setup completed.'
  await refreshCoreData()
}

async function submitReactivateInstructor(instructorId) {
  clearFeedback()
  await reactivateInstructor(instructorId)
  flashMessage.value = 'Instructor reactivated.'
  await refreshCoreData()
}

async function submitActivity() {
  clearFeedback()
  const payload = {
    studentId: Number(activityForm.studentId),
    title: activityForm.title,
    description: activityForm.description,
    comments: activityForm.comments,
    category: activityForm.category,
    status: activityForm.status,
    plannedHours: Number(activityForm.plannedHours),
    actualHours: Number(activityForm.actualHours),
    weekNumber: Number(activityForm.weekNumber),
  }

  if (activityForm.id) {
    await updateActivity(activityForm.id, payload)
    flashMessage.value = 'WAR activity updated.'
  } else {
    await createActivity(payload)
    flashMessage.value = 'WAR activity created.'
  }

  activities.value = await getStudentActivities(activityForm.studentId)
  resetActivityEditor()
}

function startEditingActivity(activity) {
  activityForm.id = activity.id
  activityForm.studentId = activity.studentId
  activityForm.title = activity.title
  activityForm.description = activity.description ?? ''
  activityForm.comments = activity.comments ?? ''
  activityForm.category = activity.category
  activityForm.status = activity.status
  activityForm.plannedHours = activity.plannedHours ?? 0
  activityForm.actualHours = activity.actualHours ?? 0
  activityForm.weekNumber = activity.weekNumber
}

async function removeActivity(activityId) {
  clearFeedback()
  await deleteActivity(activityId)
  flashMessage.value = 'WAR activity deleted.'
  activities.value = await getStudentActivities(activityForm.studentId)
}

async function submitPeerForm() {
  clearFeedback()
  await submitPeerEvaluation({
    evaluatorId: Number(peerForm.evaluatorId),
    evaluateeId: Number(peerForm.evaluateeId),
    weekNumber: Number(peerForm.weekNumber),
    publicComment: peerForm.publicComment,
    privateComment: peerForm.privateComment,
    ratings: peerForm.ratings.map((rating) => ({
      criterionId: rating.criterionId,
      score: Number(rating.score),
    })),
  })

  flashMessage.value = 'Peer evaluation submitted.'
}

async function loadStudentPeerReport() {
  clearFeedback()
  studentPeerReport.value = null

  try {
    studentPeerReport.value = await getStudentPeerEvaluationReport(studentPeerFilters.studentId, studentPeerFilters.weekNumber)
  } catch (error) {
    reportError.value = error.message
  }
}

async function loadSectionPeerReport() {
  clearFeedback()
  sectionPeerReport.value = null

  try {
    sectionPeerReport.value = await getSectionPeerEvaluationReport(
      sectionPeerFilters.instructorId,
      sectionPeerFilters.sectionId,
      sectionPeerFilters.weekNumber,
    )
  } catch (error) {
    reportError.value = error.message
  }
}

async function loadTeamWarReport() {
  clearFeedback()
  teamWarReport.value = null

  try {
    teamWarReport.value = await getTeamWarReport(
      teamWarFilters.teamId,
      teamWarFilters.viewerType,
      teamWarFilters.viewerId,
      teamWarFilters.weekNumber,
    )
  } catch (error) {
    reportError.value = error.message
  }
}

async function loadInstructorStudentPeerReport() {
  clearFeedback()
  instructorStudentPeerReport.value = null

  try {
    instructorStudentPeerReport.value = await getInstructorStudentPeerEvaluationReport(
      instructorStudentPeerFilters.instructorId,
      instructorStudentPeerFilters.studentId,
      instructorStudentPeerFilters.weekNumber,
    )
  } catch (error) {
    reportError.value = error.message
  }
}

async function loadInstructorStudentWarReport() {
  clearFeedback()
  instructorStudentWarReport.value = null

  try {
    instructorStudentWarReport.value = await getInstructorStudentWarReport(
      instructorStudentWarFilters.instructorId,
      instructorStudentWarFilters.studentId,
      instructorStudentWarFilters.weekNumber,
    )
  } catch (error) {
    reportError.value = error.message
  }
}

onMounted(loadWorkspace)
</script>

<template>
  <section class="panel workspace-panel">
    <div class="panel-header workspace-header">
      <div>
        <p class="eyebrow">Combined implementation</p>
        <h2>Use Cases 24-34 Workspace</h2>
      </div>
      <p class="header-copy compact-copy">
        This dashboard covers the new account, WAR, and peer-evaluation flows in one place.
      </p>
    </div>

    <ErrorMessage v-if="loadError" title="Could not load the workspace" :message="loadError" />
    <LoadingState v-else-if="isLoading" label="Loading full-stack demo data" />

    <div v-else class="workspace-grid">
      <aside class="uc-nav">
        <button
          v-for="useCase in useCases"
          :key="useCase.id"
          class="section-button"
          :class="{ active: activeUseCase === useCase.id }"
          type="button"
          @click="activeUseCase = useCase.id"
        >
          <span>UC {{ useCase.id }}</span>
          <small>{{ useCase.label }}</small>
        </button>
      </aside>

      <div class="detail-panel workspace-detail">
        <p v-if="flashMessage" class="success-message">{{ flashMessage }}</p>
        <ErrorMessage v-if="reportError" title="Request failed" :message="reportError" />

        <section v-if="activeUseCase === 24" class="detail-section">
          <h3>Admin reactivates an instructor</h3>
          <div v-if="inactiveInstructors.length" class="card-grid">
            <article v-for="instructor in inactiveInstructors" :key="instructor.id" class="team-card">
              <h4>{{ personName(instructor) }}</h4>
              <p class="quiet">{{ instructor.email }}</p>
              <p class="quiet">Section: {{ instructor.sectionName || 'Unassigned' }}</p>
              <button type="button" @click="submitReactivateInstructor(instructor.id)">Reactivate</button>
            </article>
          </div>
          <EmptyState v-else message="No inactive instructors are available right now." />
        </section>

        <section v-if="activeUseCase === 25" class="detail-section">
          <h3>Student sets up a student account</h3>
          <form class="stacked-form" @submit.prevent="submitStudentSetup">
            <label>
              Existing student record
              <select v-model="studentSetupForm.studentId">
                <option value="">Create or finish setup manually</option>
                <option v-for="student in students" :key="student.id" :value="student.id">
                  {{ personName(student) || student.email }}
                </option>
              </select>
            </label>
            <div class="form-grid">
              <label><span>First name</span><input v-model="studentSetupForm.firstName" required type="text" /></label>
              <label><span>Last name</span><input v-model="studentSetupForm.lastName" required type="text" /></label>
              <label><span>Email</span><input v-model="studentSetupForm.email" required type="email" /></label>
              <label><span>Username</span><input v-model="studentSetupForm.username" required type="text" /></label>
              <label><span>Password</span><input v-model="studentSetupForm.password" required type="password" /></label>
              <label>
                <span>Section</span>
                <select v-model="studentSetupForm.sectionId">
                  <option value="">Choose a section</option>
                  <option v-for="section in sections" :key="section.id" :value="section.id">{{ section.name }}</option>
                </select>
              </label>
              <label>
                <span>Team</span>
                <select v-model="studentSetupForm.teamId">
                  <option value="">No team selected</option>
                  <option v-for="team in teams" :key="team.id" :value="team.id">{{ team.name }}</option>
                </select>
              </label>
            </div>
            <button class="submit-button" type="submit">Complete student setup</button>
          </form>
        </section>

        <section v-if="activeUseCase === 26" class="detail-section">
          <h3>Student edits an account</h3>
          <form class="stacked-form" @submit.prevent="submitStudentEdit">
            <label>
              Student
              <select v-model="studentEditSelection" required>
                <option value="">Choose a student</option>
                <option v-for="student in students" :key="student.id" :value="student.id">
                  {{ personName(student) || student.email }}
                </option>
              </select>
            </label>
            <div class="form-grid">
              <label><span>First name</span><input v-model="studentEditForm.firstName" required type="text" /></label>
              <label><span>Last name</span><input v-model="studentEditForm.lastName" required type="text" /></label>
              <label><span>Email</span><input v-model="studentEditForm.email" required type="email" /></label>
              <label><span>Username</span><input v-model="studentEditForm.username" required type="text" /></label>
              <label><span>Password</span><input v-model="studentEditForm.password" placeholder="Leave blank to reuse demo password" type="password" /></label>
              <label>
                <span>Section</span>
                <select v-model="studentEditForm.sectionId">
                  <option value="">Choose a section</option>
                  <option v-for="section in sections" :key="section.id" :value="section.id">{{ section.name }}</option>
                </select>
              </label>
              <label>
                <span>Team</span>
                <select v-model="studentEditForm.teamId">
                  <option value="">No team selected</option>
                  <option v-for="team in teams" :key="team.id" :value="team.id">{{ team.name }}</option>
                </select>
              </label>
            </div>
            <button class="submit-button" :disabled="!studentEditSelection" type="submit">Save student account</button>
          </form>
        </section>

        <section v-if="activeUseCase === 27" class="detail-section">
          <h3>Student manages activities in a weekly activity report</h3>
          <form class="stacked-form" @submit.prevent="submitActivity">
            <div class="form-grid">
              <label>
                <span>Student</span>
                <select v-model="activityForm.studentId" required>
                  <option value="">Choose a student</option>
                  <option v-for="student in students" :key="student.id" :value="student.id">{{ personName(student) }}</option>
                </select>
              </label>
              <label><span>Title</span><input v-model="activityForm.title" required type="text" /></label>
              <label><span>Week</span><input v-model="activityForm.weekNumber" min="1" required type="number" /></label>
              <label>
                <span>Category</span>
                <select v-model="activityForm.category">
                  <option v-for="category in activityCategories" :key="category" :value="category">{{ category }}</option>
                </select>
              </label>
              <label>
                <span>Status</span>
                <select v-model="activityForm.status">
                  <option v-for="status in activityStatuses" :key="status" :value="status">{{ status }}</option>
                </select>
              </label>
              <label><span>Planned hours</span><input v-model="activityForm.plannedHours" min="0" step="0.5" type="number" /></label>
              <label><span>Actual hours</span><input v-model="activityForm.actualHours" min="0" step="0.5" type="number" /></label>
            </div>
            <label><span>Description</span><textarea v-model="activityForm.description" rows="3" /></label>
            <label><span>Comments</span><textarea v-model="activityForm.comments" rows="3" /></label>
            <div class="button-row">
              <button class="submit-button" type="submit">{{ activityForm.id ? 'Update activity' : 'Create activity' }}</button>
              <button type="button" @click="resetActivityEditor">Clear</button>
            </div>
          </form>

          <div v-if="activities.length" class="card-grid">
            <article v-for="activity in activities" :key="activity.id" class="team-card">
              <div class="card-header-inline">
                <h4>{{ activity.title }}</h4>
                <span class="pill">Week {{ activity.weekNumber }}</span>
              </div>
              <p class="quiet">{{ activity.category }} · {{ activity.status }}</p>
              <p class="quiet">{{ activity.plannedHours }} planned / {{ activity.actualHours }} actual hours</p>
              <div class="button-row">
                <button type="button" @click="startEditingActivity(activity)">Edit</button>
                <button type="button" @click="removeActivity(activity.id)">Delete</button>
              </div>
            </article>
          </div>
          <EmptyState v-else message="Choose a student to manage WAR activities." />
        </section>

        <section v-if="activeUseCase === 28" class="detail-section">
          <h3>Student submits a peer evaluation for the previous week</h3>
          <form class="stacked-form" @submit.prevent="submitPeerForm">
            <div class="form-grid">
              <label>
                <span>Evaluator</span>
                <select v-model="peerForm.evaluatorId" required>
                  <option value="">Choose a student</option>
                  <option v-for="student in students" :key="student.id" :value="student.id">{{ personName(student) }}</option>
                </select>
              </label>
              <label>
                <span>Evaluatee</span>
                <select v-model="peerForm.evaluateeId" required>
                  <option value="">Choose a teammate</option>
                  <option v-for="student in students" :key="student.id" :value="student.id">{{ personName(student) }}</option>
                </select>
              </label>
              <label><span>Week</span><input v-model="peerForm.weekNumber" min="1" required type="number" /></label>
            </div>
            <div v-if="peerForm.ratings.length" class="ratings-grid">
              <article v-for="rating in peerForm.ratings" :key="rating.criterionId" class="team-card">
                <h4>{{ rating.criterionName }}</h4>
                <p class="quiet">Max {{ rating.maxScore }}</p>
                <input v-model="rating.score" min="0" :max="rating.maxScore" step="0.5" type="number" />
              </article>
            </div>
            <EmptyState v-else message="Pick the evaluatee first to load rubric criteria." />
            <label><span>Public comment</span><textarea v-model="peerForm.publicComment" rows="3" /></label>
            <label><span>Private comment</span><textarea v-model="peerForm.privateComment" rows="3" /></label>
            <button class="submit-button" type="submit">Submit peer evaluation</button>
          </form>
        </section>

        <section v-if="activeUseCase === 29" class="detail-section">
          <h3>Student views her own peer evaluation report</h3>
          <form class="search-form" @submit.prevent="loadStudentPeerReport">
            <div class="form-grid">
              <label>
                <span>Student</span>
                <select v-model="studentPeerFilters.studentId" required>
                  <option value="">Choose a student</option>
                  <option v-for="student in students" :key="student.id" :value="student.id">{{ personName(student) }}</option>
                </select>
              </label>
              <label><span>Week</span><input v-model="studentPeerFilters.weekNumber" min="1" placeholder="All weeks" type="number" /></label>
            </div>
            <button type="submit">Load report</button>
          </form>
          <div v-if="studentPeerReport" class="report-block">
            <div class="section-summary">
              <div>
                <p class="eyebrow">Student report</p>
                <h3>{{ studentPeerReport.studentName }}</h3>
              </div>
              <span class="result-count">{{ score(studentPeerReport.averageScore) }} average</span>
            </div>
            <div class="card-grid">
              <article v-for="evaluation in studentPeerReport.evaluations" :key="evaluation.id" class="team-card">
                <h4>{{ evaluation.evaluatorName }}</h4>
                <p class="quiet">Week {{ evaluation.weekNumber }} · Score {{ score(evaluation.totalScore) }}</p>
                <p>{{ evaluation.publicComment || 'No public comment submitted.' }}</p>
              </article>
            </div>
          </div>
        </section>

        <section v-if="activeUseCase === 30" class="detail-section">
          <h3>Instructor sets up an instructor account</h3>
          <form class="stacked-form" @submit.prevent="submitInstructorSetup">
            <label>
              Existing instructor record
              <select v-model="instructorSetupForm.instructorId">
                <option value="">Create or finish setup manually</option>
                <option v-for="instructor in instructors" :key="instructor.id" :value="instructor.id">
                  {{ personName(instructor) || instructor.email }}
                </option>
              </select>
            </label>
            <div class="form-grid">
              <label><span>First name</span><input v-model="instructorSetupForm.firstName" required type="text" /></label>
              <label><span>Last name</span><input v-model="instructorSetupForm.lastName" required type="text" /></label>
              <label><span>Email</span><input v-model="instructorSetupForm.email" required type="email" /></label>
              <label><span>Username</span><input v-model="instructorSetupForm.username" required type="text" /></label>
              <label><span>Password</span><input v-model="instructorSetupForm.password" required type="password" /></label>
              <label>
                <span>Section</span>
                <select v-model="instructorSetupForm.sectionId" required>
                  <option value="">Choose a section</option>
                  <option v-for="section in sections" :key="section.id" :value="section.id">{{ section.name }}</option>
                </select>
              </label>
            </div>
            <button class="submit-button" type="submit">Complete instructor setup</button>
          </form>
        </section>

        <section v-if="activeUseCase === 31" class="detail-section">
          <h3>Instructor generates a peer evaluation report of the section</h3>
          <form class="search-form" @submit.prevent="loadSectionPeerReport">
            <div class="form-grid">
              <label>
                <span>Instructor</span>
                <select v-model="sectionPeerFilters.instructorId" required>
                  <option value="">Choose an instructor</option>
                  <option v-for="instructor in instructors" :key="instructor.id" :value="instructor.id">{{ personName(instructor) }}</option>
                </select>
              </label>
              <label>
                <span>Section</span>
                <select v-model="sectionPeerFilters.sectionId" required>
                  <option value="">Choose a section</option>
                  <option v-for="section in sections" :key="section.id" :value="section.id">{{ section.name }}</option>
                </select>
              </label>
              <label><span>Week</span><input v-model="sectionPeerFilters.weekNumber" min="1" placeholder="All weeks" type="number" /></label>
            </div>
            <button type="submit">Generate report</button>
          </form>
          <div v-if="sectionPeerReport" class="table-wrap">
            <table class="sections-table">
              <thead>
                <tr>
                  <th>Student</th>
                  <th>Evaluations</th>
                  <th>Average</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="student in sectionPeerReport.students" :key="student.studentId">
                  <td>{{ student.studentName }}</td>
                  <td>{{ student.evaluationCount }}</td>
                  <td>{{ score(student.averageScore) }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </section>

        <section v-if="activeUseCase === 32" class="detail-section">
          <h3>Instructor or student generates a WAR report of a team</h3>
          <form class="search-form" @submit.prevent="loadTeamWarReport">
            <div class="form-grid">
              <label>
                <span>Team</span>
                <select v-model="teamWarFilters.teamId" required>
                  <option value="">Choose a team</option>
                  <option v-for="team in teams" :key="team.id" :value="team.id">{{ team.name }}</option>
                </select>
              </label>
              <label>
                <span>Viewer type</span>
                <select v-model="teamWarFilters.viewerType">
                  <option value="STUDENT">Student</option>
                  <option value="INSTRUCTOR">Instructor</option>
                </select>
              </label>
              <label>
                <span>Viewer</span>
                <select v-model="teamWarFilters.viewerId" required>
                  <option value="">Choose a viewer</option>
                  <option v-for="viewer in viewerChoices" :key="viewer.id" :value="viewer.id">{{ personName(viewer) || viewer.email }}</option>
                </select>
              </label>
              <label><span>Week</span><input v-model="teamWarFilters.weekNumber" min="1" placeholder="All weeks" type="number" /></label>
            </div>
            <button type="submit">Generate WAR report</button>
          </form>
          <div v-if="teamWarReport" class="card-grid">
            <article v-for="summary in teamWarReport.studentSummaries" :key="summary.studentId" class="team-card">
              <h4>{{ summary.studentName }}</h4>
              <p class="quiet">{{ summary.activityCount }} activities</p>
              <p class="quiet">{{ summary.plannedHours }} planned / {{ summary.actualHours }} actual</p>
            </article>
          </div>
        </section>

        <section v-if="activeUseCase === 33" class="detail-section">
          <h3>Instructor generates a peer evaluation report of a student</h3>
          <form class="search-form" @submit.prevent="loadInstructorStudentPeerReport">
            <div class="form-grid">
              <label>
                <span>Instructor</span>
                <select v-model="instructorStudentPeerFilters.instructorId" required>
                  <option value="">Choose an instructor</option>
                  <option v-for="instructor in instructors" :key="instructor.id" :value="instructor.id">{{ personName(instructor) }}</option>
                </select>
              </label>
              <label>
                <span>Student</span>
                <select v-model="instructorStudentPeerFilters.studentId" required>
                  <option value="">Choose a student</option>
                  <option v-for="student in students" :key="student.id" :value="student.id">{{ personName(student) }}</option>
                </select>
              </label>
              <label><span>Week</span><input v-model="instructorStudentPeerFilters.weekNumber" min="1" placeholder="All weeks" type="number" /></label>
            </div>
            <button type="submit">Generate student peer report</button>
          </form>
          <div v-if="instructorStudentPeerReport" class="card-grid">
            <article v-for="evaluation in instructorStudentPeerReport.evaluations" :key="evaluation.id" class="team-card">
              <h4>{{ evaluation.evaluatorName }}</h4>
              <p class="quiet">Week {{ evaluation.weekNumber }} · Score {{ score(evaluation.totalScore) }}</p>
              <p><strong>Public:</strong> {{ evaluation.publicComment || 'None' }}</p>
              <p><strong>Private:</strong> {{ evaluation.privateComment || 'None' }}</p>
            </article>
          </div>
        </section>

        <section v-if="activeUseCase === 34" class="detail-section">
          <h3>Instructor generates a WAR report of the student</h3>
          <form class="search-form" @submit.prevent="loadInstructorStudentWarReport">
            <div class="form-grid">
              <label>
                <span>Instructor</span>
                <select v-model="instructorStudentWarFilters.instructorId" required>
                  <option value="">Choose an instructor</option>
                  <option v-for="instructor in instructors" :key="instructor.id" :value="instructor.id">{{ personName(instructor) }}</option>
                </select>
              </label>
              <label>
                <span>Student</span>
                <select v-model="instructorStudentWarFilters.studentId" required>
                  <option value="">Choose a student</option>
                  <option v-for="student in students" :key="student.id" :value="student.id">{{ personName(student) }}</option>
                </select>
              </label>
              <label><span>Week</span><input v-model="instructorStudentWarFilters.weekNumber" min="1" placeholder="All weeks" type="number" /></label>
            </div>
            <button type="submit">Generate student WAR report</button>
          </form>
          <div v-if="instructorStudentWarReport" class="card-grid">
            <article v-for="activity in instructorStudentWarReport.activities" :key="activity.id" class="team-card">
              <div class="card-header-inline">
                <h4>{{ activity.title }}</h4>
                <span class="pill">Week {{ activity.weekNumber }}</span>
              </div>
              <p class="quiet">{{ activity.category }} · {{ activity.status }}</p>
              <p class="quiet">{{ activity.plannedHours }} planned / {{ activity.actualHours }} actual hours</p>
            </article>
          </div>
        </section>
      </div>
    </div>
  </section>
</template>
