<script setup>
import { computed, reactive, ref } from 'vue'
import { getSectionDetails, updateSection } from '../api/projectPulseApi'
import ErrorMessage from '../components/ErrorMessage.vue'
import LoadingState from '../components/LoadingState.vue'

const sectionId = ref('')
const loadedSectionId = ref(null)
const isLoading = ref(false)
const isSaving = ref(false)
const loadError = ref('')
const saveError = ref('')
const successMessage = ref('')
const validationErrors = ref([])

const form = reactive({
  name: '',
  courseCode: '',
  semester: '',
  startDate: '',
  endDate: '',
  instructorsText: '',
})

const semesterOptions = [
  'Spring 2026',
  'Summer 2026',
  'Fall 2026',
  'Spring 2027',
]

const hasLoadedSection = computed(() => loadedSectionId.value !== null)
const hasValidationErrors = computed(() => validationErrors.value.length > 0)

function instructorsToText(instructors = []) {
  return instructors.join(', ')
}

function parseInstructors(value) {
  return value
    .split(',')
    .map((instructor) => instructor.trim())
    .filter(Boolean)
}

function resetMessages() {
  loadError.value = ''
  saveError.value = ''
  successMessage.value = ''
  validationErrors.value = []
}

function fillForm(section) {
  form.name = section.name ?? ''
  form.courseCode = section.courseCode ?? ''
  form.semester = section.semester ?? ''
  form.startDate = section.startDate ?? ''
  form.endDate = section.endDate ?? ''
  form.instructorsText = instructorsToText(section.instructors)
}

function validateForm() {
  const errors = []

  if (!loadedSectionId.value) {
    errors.push('Load a section before saving changes.')
  }

  if (!form.name.trim()) {
    errors.push('Section name is required.')
  }

  if (!form.courseCode.trim()) {
    errors.push('Course code is required.')
  }

  if (!form.startDate) {
    errors.push('Start date is required.')
  }

  if (!form.endDate) {
    errors.push('End date is required.')
  }

  if (form.startDate && form.endDate && form.endDate < form.startDate) {
    errors.push('End date must be after the start date.')
  }

  validationErrors.value = errors
  return errors.length === 0
}

async function loadSection() {
  resetMessages()

  if (!sectionId.value) {
    loadError.value = 'Enter a section ID to load.'
    return
  }

  isLoading.value = true
  loadedSectionId.value = null

  try {
    const section = await getSectionDetails(sectionId.value)
    loadedSectionId.value = section.id
    fillForm(section)
  } catch (err) {
    loadError.value = err.message
  } finally {
    isLoading.value = false
  }
}

async function saveSection() {
  saveError.value = ''
  successMessage.value = ''

  if (!validateForm()) {
    return
  }

  isSaving.value = true

  const payload = {
    name: form.name.trim(),
    courseCode: form.courseCode.trim(),
    semester: form.semester,
    startDate: form.startDate,
    endDate: form.endDate,
    instructors: parseInstructors(form.instructorsText),
  }

  try {
    const updatedSection = await updateSection(loadedSectionId.value, payload)
    successMessage.value = `Section "${updatedSection?.name || payload.name}" was saved successfully.`
  } catch (err) {
    saveError.value = err.message
  } finally {
    isSaving.value = false
  }
}
</script>

<template>
  <section class="panel section-create-panel">
    <div class="panel-header">
      <div>
        <p class="eyebrow">Sections</p>
        <h2>Edit senior design section</h2>
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
    <LoadingState v-else-if="isLoading" label="Loading section" />

    <form v-if="hasLoadedSection" class="section-form edit-section-form" @submit.prevent="saveSection">
      <label>
        Section name
        <input v-model="form.name" type="text" placeholder="Senior Design Studio A" />
      </label>

      <div class="form-grid">
        <label>
          Course code
          <input v-model="form.courseCode" type="text" placeholder="SD-401" />
        </label>

        <label>
          Semester
          <select v-model="form.semester">
            <option value="">No semester selected</option>
            <option v-for="semester in semesterOptions" :key="semester" :value="semester">
              {{ semester }}
            </option>
          </select>
        </label>
      </div>

      <div class="form-grid">
        <label>
          Start date
          <input v-model="form.startDate" type="date" />
        </label>

        <label>
          End date
          <input v-model="form.endDate" type="date" />
        </label>
      </div>

      <label>
        Instructors
        <input v-model="form.instructorsText" type="text" placeholder="Jane Lee, Marco Santos" />
      </label>

      <div v-if="hasValidationErrors" class="validation-box" role="alert">
        <strong>Please fix the following:</strong>
        <ul>
          <li v-for="validationError in validationErrors" :key="validationError">
            {{ validationError }}
          </li>
        </ul>
      </div>

      <ErrorMessage v-if="saveError" title="Could not save section" :message="saveError" />
      <p v-if="successMessage" class="success-message" role="status">{{ successMessage }}</p>

      <button class="submit-button" type="submit" :disabled="isSaving">
        {{ isSaving ? 'Saving...' : 'Save changes' }}
      </button>
    </form>
  </section>
</template>
