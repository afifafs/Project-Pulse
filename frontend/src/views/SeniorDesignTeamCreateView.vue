<script setup>
import { computed, reactive, ref } from 'vue'
import { createTeam } from '../api/projectPulseApi'
import ErrorMessage from '../components/ErrorMessage.vue'

const form = reactive({
  name: '',
  sectionId: '',
})

const isSubmitting = ref(false)
const error = ref('')
const successMessage = ref('')
const validationErrors = ref([])

const hasValidationErrors = computed(() => validationErrors.value.length > 0)

function validateForm() {
  const errors = []

  if (!form.name.trim()) {
    errors.push('Team name is required.')
  }

  if (!form.sectionId) {
    errors.push('Section ID is required.')
  }

  validationErrors.value = errors
  return errors.length === 0
}

function resetForm() {
  form.name = ''
  form.sectionId = ''
}

async function submitTeam() {
  error.value = ''
  successMessage.value = ''

  if (!validateForm()) {
    return
  }

  isSubmitting.value = true

  const payload = {
    name: form.name.trim(),
    sectionId: Number(form.sectionId),
  }

  try {
    const createdTeam = await createTeam(payload)
    successMessage.value = `Team "${createdTeam?.name || payload.name}" was created successfully.`
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
        <p class="eyebrow">Teams</p>
        <h2>New senior design team</h2>
      </div>
    </div>

    <form class="section-form" @submit.prevent="submitTeam">
      <label>
        Team name
        <input v-model="form.name" type="text" placeholder="Team Atlas" />
      </label>

      <label>
        Section ID
        <input v-model="form.sectionId" min="1" type="number" placeholder="Assign to section ID" />
      </label>

      <div v-if="hasValidationErrors" class="validation-box" role="alert">
        <strong>Please fix the following:</strong>
        <ul>
          <li v-for="validationError in validationErrors" :key="validationError">
            {{ validationError }}
          </li>
        </ul>
      </div>

      <ErrorMessage v-if="error" title="Could not create team" :message="error" />
      <p v-if="successMessage" class="success-message" role="status">{{ successMessage }}</p>

      <button class="submit-button" type="submit" :disabled="isSubmitting">
        {{ isSubmitting ? 'Creating...' : 'Create team' }}
      </button>
    </form>
  </section>
</template>
