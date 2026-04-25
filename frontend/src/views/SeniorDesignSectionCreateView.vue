<script setup>
import { computed, reactive, ref } from 'vue'
import { createSection } from '../api/projectPulseApi'
import ErrorMessage from '../components/ErrorMessage.vue'

const form = reactive({
  name: '',
  courseCode: 'SD-',
  semester: '',
  startDate: '',
  endDate: '',
  instructorsText: '',
})

const isSubmitting = ref(false)
const error = ref('')
const successMessage = ref('')
const validationErrors = ref([])

const semesterOptions = [
  'Spring 2026',
  'Summer 2026',
  'Fall 2026',
  'Spring 2027',
]

const hasValidationErrors = computed(() => validationErrors.value.length > 0)

function parseInstructors(value) {
  return value
    .split(',')
    .map((instructor) => instructor.trim())
    .filter(Boolean)
}

function validateForm() {
  const errors = []

  if (!form.name.trim()) {
    errors.push('Section name is required.')
  }

  if (!form.courseCode.trim()) {
    errors.push('Course code is required.')
  }

  if (!form.semester) {
    errors.push('Semester is required.')
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

function resetForm() {
  form.name = ''
  form.courseCode = 'SD-'
  form.semester = ''
  form.startDate = ''
  form.endDate = ''
  form.instructorsText = ''
}

async function submitSection() {
  error.value = ''
  successMessage.value = ''

  if (!validateForm()) {
    return
  }

  isSubmitting.value = true

  const payload = {
    name: form.name.trim(),
    courseCode: form.courseCode.trim(),
    semester: form.semester,
    startDate: form.startDate,
    endDate: form.endDate,
    instructors: parseInstructors(form.instructorsText),
  }

  try {
    const createdSection = await createSection(payload)
    successMessage.value = `Section "${createdSection?.name || payload.name}" was created successfully.`
    validationErrors.value = []
    resetForm()
  } catch (err) {
    error.value = err.message
  } finally {
    isSubmitting.value = false
  }
}
</script>

<template>
  <section class="panel section-create-panel">
    <div class="panel-header">
      <div>
        <p class="eyebrow">Sections</p>
        <h2>New senior design section</h2>
      </div>
    </div>

    <form class="section-form" @submit.prevent="submitSection">
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
            <option value="" disabled>Select semester</option>
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

      <ErrorMessage v-if="error" title="Could not create section" :message="error" />
      <p v-if="successMessage" class="success-message" role="status">{{ successMessage }}</p>

      <button class="submit-button" type="submit" :disabled="isSubmitting">
        {{ isSubmitting ? 'Creating...' : 'Create section' }}
      </button>
    </form>
  </section>
</template>
