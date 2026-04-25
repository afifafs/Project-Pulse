<script setup>
import { computed, ref } from 'vue'
import { getSectionDetails, updateSectionActiveWeeks } from '../api/projectPulseApi'
import EmptyState from '../components/EmptyState.vue'
import ErrorMessage from '../components/ErrorMessage.vue'
import LoadingState from '../components/LoadingState.vue'

const sectionId = ref('')
const section = ref(null)
const selectedWeeks = ref([])
const isLoading = ref(false)
const isSaving = ref(false)
const loadError = ref('')
const saveError = ref('')
const successMessage = ref('')

const weeks = Array.from({ length: 16 }, (_, index) => ({
  number: index + 1,
  label: `Week ${index + 1}`,
}))

const activeWeeksLabel = computed(() => {
  if (!selectedWeeks.value.length) {
    return 'No active weeks selected.'
  }

  return selectedWeeks.value
    .slice()
    .sort((first, second) => first - second)
    .map((week) => `Week ${week}`)
    .join(', ')
})

function resetMessages() {
  loadError.value = ''
  saveError.value = ''
  successMessage.value = ''
}

function setSelectedWeeks(activeWeeks = []) {
  selectedWeeks.value = activeWeeks
    .map((week) => Number(week))
    .filter((week) => Number.isInteger(week) && week >= 1 && week <= 16)
}

async function loadSection() {
  resetMessages()

  if (!sectionId.value) {
    loadError.value = 'Enter a section ID to load.'
    return
  }

  isLoading.value = true
  section.value = null
  selectedWeeks.value = []

  try {
    const loadedSection = await getSectionDetails(sectionId.value)
    section.value = loadedSection
    setSelectedWeeks(loadedSection.activeWeeks)
  } catch (err) {
    loadError.value = err.message
  } finally {
    isLoading.value = false
  }
}

function selectAllWeeks() {
  selectedWeeks.value = weeks.map((week) => week.number)
}

function clearWeeks() {
  selectedWeeks.value = []
}

async function saveActiveWeeks() {
  saveError.value = ''
  successMessage.value = ''

  if (!section.value?.id) {
    saveError.value = 'Load a section before saving active weeks.'
    return
  }

  isSaving.value = true

  try {
    const savedSection = await updateSectionActiveWeeks(section.value.id, selectedWeeks.value)
    section.value = savedSection || {
      ...section.value,
      activeWeeks: selectedWeeks.value,
    }
    setSelectedWeeks(section.value.activeWeeks || selectedWeeks.value)
    successMessage.value = 'Active weeks were saved successfully.'
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
        <h2>Active weeks</h2>
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

    <div v-else-if="section" class="active-weeks-workspace">
      <div class="section-summary">
        <div>
          <p class="eyebrow">{{ section.courseCode }}</p>
          <h3>{{ section.name }}</h3>
        </div>
        <span class="result-count">{{ selectedWeeks.length }} active</span>
      </div>

      <div class="current-weeks-box">
        <strong>Current active weeks</strong>
        <p>{{ activeWeeksLabel }}</p>
      </div>

      <div class="week-actions">
        <button class="secondary-button" type="button" @click="selectAllWeeks">Select all</button>
        <button class="secondary-button" type="button" @click="clearWeeks">Clear all</button>
      </div>

      <fieldset class="weeks-grid">
        <legend>Select weeks</legend>
        <label v-for="week in weeks" :key="week.number" class="week-option">
          <input v-model="selectedWeeks" type="checkbox" :value="week.number" />
          <span>{{ week.label }}</span>
        </label>
      </fieldset>

      <ErrorMessage v-if="saveError" title="Could not save active weeks" :message="saveError" />
      <p v-if="successMessage" class="success-message" role="status">{{ successMessage }}</p>

      <button class="submit-button" type="button" :disabled="isSaving" @click="saveActiveWeeks">
        {{ isSaving ? 'Saving...' : 'Save active weeks' }}
      </button>
    </div>

    <EmptyState v-else message="Load a section to view and update its active weeks." />
  </section>
</template>
