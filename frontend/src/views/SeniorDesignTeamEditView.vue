<script setup>
import { computed, reactive, ref } from 'vue'
import { getSectionDetails, getTeamDetails, updateTeam } from '../api/projectPulseApi'
import EmptyState from '../components/EmptyState.vue'
import ErrorMessage from '../components/ErrorMessage.vue'
import LoadingState from '../components/LoadingState.vue'

const lookup = reactive({
  teamId: '',
  sectionId: '',
})

const form = reactive({
  id: null,
  name: '',
  sectionId: '',
  members: [],
})

const isLoading = ref(false)
const isSaving = ref(false)
const loadError = ref('')
const saveError = ref('')
const successMessage = ref('')
const validationErrors = ref([])

const hasTeamLoaded = computed(() => form.id !== null)
const hasValidationErrors = computed(() => validationErrors.value.length > 0)

function newMember(member = {}) {
  return {
    id: member.id ?? null,
    firstName: member.firstName ?? '',
    lastName: member.lastName ?? '',
    email: member.email ?? '',
  }
}

function resetMessages() {
  loadError.value = ''
  saveError.value = ''
  successMessage.value = ''
  validationErrors.value = []
}

function fillForm(team, section = null) {
  form.id = team.id
  form.name = team.name ?? ''
  form.sectionId = team.section?.id ?? section?.id ?? lookup.sectionId
  form.members.splice(0, form.members.length, ...(team.students ?? []).map(newMember))
}

async function loadTeamFromSection() {
  const section = await getSectionDetails(lookup.sectionId)
  const team = section.teams?.find((candidate) => String(candidate.id) === String(lookup.teamId))

  if (!team) {
    throw new Error('Team not found in that section.')
  }

  fillForm(team, section)
}

async function loadTeam() {
  resetMessages()
  form.id = null

  if (!lookup.teamId) {
    loadError.value = 'Enter a team ID to load.'
    return
  }

  isLoading.value = true

  try {
    if (lookup.sectionId) {
      await loadTeamFromSection()
    } else {
      fillForm(await getTeamDetails(lookup.teamId))
    }
  } catch (err) {
    loadError.value = err.message
  } finally {
    isLoading.value = false
  }
}

function addMember() {
  form.members.push(newMember())
}

function removeMember(index) {
  form.members.splice(index, 1)
}

function validateForm() {
  const errors = []

  if (!form.id) {
    errors.push('Load a team before saving changes.')
  }

  if (!form.name.trim()) {
    errors.push('Team name is required.')
  }

  form.members.forEach((member, index) => {
    const hasAnyValue = member.firstName.trim() || member.lastName.trim() || member.email.trim()

    if (hasAnyValue && !member.email.trim()) {
      errors.push(`Member ${index + 1} email is required.`)
    }
  })

  validationErrors.value = errors
  return errors.length === 0
}

function memberPayload() {
  return form.members
    .filter((member) => member.firstName.trim() || member.lastName.trim() || member.email.trim())
    .map((member) => ({
      id: member.id,
      firstName: member.firstName.trim(),
      lastName: member.lastName.trim(),
      email: member.email.trim(),
    }))
}

async function saveTeam() {
  saveError.value = ''
  successMessage.value = ''

  if (!validateForm()) {
    return
  }

  isSaving.value = true

  const payload = {
    name: form.name.trim(),
    sectionId: form.sectionId ? Number(form.sectionId) : null,
    students: memberPayload(),
  }

  try {
    const updatedTeam = await updateTeam(form.id, payload)
    fillForm(updatedTeam || { id: form.id, ...payload })
    successMessage.value = 'Team changes were saved successfully.'
  } catch (err) {
    saveError.value = err.message
  } finally {
    isSaving.value = false
  }
}
</script>

<template>
  <section class="panel team-detail-panel">
    <div class="panel-header">
      <div>
        <p class="eyebrow">Teams</p>
        <h2>Edit team</h2>
      </div>
    </div>

    <form class="search-form" @submit.prevent="loadTeam">
      <div class="form-grid">
        <label for="team-id">
          Team ID
          <input id="team-id" v-model="lookup.teamId" min="1" type="number" placeholder="Enter team ID" />
        </label>

        <label for="section-id">
          Section ID
          <input id="section-id" v-model="lookup.sectionId" min="1" type="number" placeholder="Optional section ID" />
        </label>
      </div>

      <button class="submit-button" type="submit" :disabled="isLoading">
        {{ isLoading ? 'Loading...' : 'Load team' }}
      </button>
    </form>

    <ErrorMessage v-if="loadError" title="Could not load team" :message="loadError" />
    <LoadingState v-else-if="isLoading" label="Loading team" />

    <form v-else-if="hasTeamLoaded" class="section-form edit-section-form" @submit.prevent="saveTeam">
      <label>
        Team name
        <input v-model="form.name" type="text" placeholder="Team Atlas" />
      </label>

      <label>
        Section ID
        <input v-model="form.sectionId" min="1" type="number" placeholder="Assigned section ID" />
      </label>

      <div class="criteria-toolbar">
        <h3>Members</h3>
        <button class="secondary-button" type="button" @click="addMember">Add member</button>
      </div>

      <div v-if="form.members.length" class="member-editor-list">
        <fieldset v-for="(member, index) in form.members" :key="`${member.id}-${index}`" class="criterion-card">
          <div class="criterion-header">
            <legend>Member {{ index + 1 }}</legend>
            <button class="text-button" type="button" @click="removeMember(index)">Remove</button>
          </div>

          <div class="form-grid">
            <label>
              First name
              <input v-model="member.firstName" type="text" placeholder="Avery" />
            </label>

            <label>
              Last name
              <input v-model="member.lastName" type="text" placeholder="Johnson" />
            </label>
          </div>

          <label>
            Email
            <input v-model="member.email" type="email" placeholder="avery@example.com" />
          </label>
        </fieldset>
      </div>

      <EmptyState v-else message="No members are assigned yet." />

      <div v-if="hasValidationErrors" class="validation-box" role="alert">
        <strong>Please fix the following:</strong>
        <ul>
          <li v-for="validationError in validationErrors" :key="validationError">
            {{ validationError }}
          </li>
        </ul>
      </div>

      <ErrorMessage v-if="saveError" title="Could not save team" :message="saveError" />
      <p v-if="successMessage" class="success-message" role="status">{{ successMessage }}</p>

      <button class="submit-button" type="submit" :disabled="isSaving">
        {{ isSaving ? 'Saving...' : 'Save team' }}
      </button>
    </form>

    <EmptyState v-else message="Enter a team ID to load team data." />
  </section>
</template>
