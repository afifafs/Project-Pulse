<script setup>
import { computed, reactive, ref } from 'vue'
import { inviteStudentsToSection } from '../api/projectPulseApi'
import ErrorMessage from '../components/ErrorMessage.vue'

const form = reactive({
  sectionId: '',
  emailsText: '',
})

const isSubmitting = ref(false)
const error = ref('')
const successMessage = ref('')
const validationErrors = ref([])

const hasValidationErrors = computed(() => validationErrors.value.length > 0)

function parseEmails(value) {
  return value
    .split(/[\s,;]+/)
    .map((email) => email.trim())
    .filter(Boolean)
}

function isValidEmail(email) {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)
}

function validateForm() {
  const errors = []
  const emails = parseEmails(form.emailsText)

  if (!form.sectionId) {
    errors.push('Section ID is required.')
  }

  if (!emails.length) {
    errors.push('Enter at least one student email address.')
  }

  const invalidEmails = emails.filter((email) => !isValidEmail(email))

  if (invalidEmails.length) {
    errors.push(`Invalid email address: ${invalidEmails.join(', ')}`)
  }

  validationErrors.value = errors
  return errors.length === 0
}

function resetForm() {
  form.sectionId = ''
  form.emailsText = ''
}

async function submitInvitations() {
  error.value = ''
  successMessage.value = ''

  if (!validateForm()) {
    return
  }

  isSubmitting.value = true
  const emails = parseEmails(form.emailsText)

  try {
    await inviteStudentsToSection(form.sectionId, emails)
    successMessage.value = `${emails.length} ${emails.length === 1 ? 'invitation was' : 'invitations were'} sent.`
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
        <p class="eyebrow">Invitations</p>
        <h2>Invite students to section</h2>
      </div>
    </div>

    <form class="section-form" @submit.prevent="submitInvitations">
      <label>
        Section ID
        <input v-model="form.sectionId" min="1" type="number" placeholder="Enter section ID" />
      </label>

      <label>
        Student emails
        <textarea
          v-model="form.emailsText"
          rows="7"
          placeholder="student.one@example.com, student.two@example.com"
        ></textarea>
      </label>

      <div v-if="hasValidationErrors" class="validation-box" role="alert">
        <strong>Please fix the following:</strong>
        <ul>
          <li v-for="validationError in validationErrors" :key="validationError">
            {{ validationError }}
          </li>
        </ul>
      </div>

      <ErrorMessage v-if="error" title="Could not send invitations" :message="error" />
      <p v-if="successMessage" class="success-message" role="status">{{ successMessage }}</p>

      <button class="submit-button" type="submit" :disabled="isSubmitting">
        {{ isSubmitting ? 'Sending...' : 'Send invitations' }}
      </button>
    </form>
  </section>
</template>
