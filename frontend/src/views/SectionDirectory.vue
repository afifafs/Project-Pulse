<script setup>
import { onMounted, ref } from 'vue'
import { getSectionDetails, getSections } from '../api/projectPulseApi'
import EmptyState from '../components/EmptyState.vue'
import ErrorMessage from '../components/ErrorMessage.vue'
import LoadingState from '../components/LoadingState.vue'
import SectionDetails from '../components/SectionDetails.vue'

const sections = ref([])
const selectedSection = ref(null)
const search = ref('')
const isLoading = ref(false)
const isLoadingDetails = ref(false)
const error = ref('')
const detailError = ref('')

async function loadSections() {
  isLoading.value = true
  error.value = ''

  try {
    sections.value = await getSections(search.value)

    if (!sections.value.some((section) => section.id === selectedSection.value?.id)) {
      selectedSection.value = null
    }
  } catch (err) {
    error.value = err.message
  } finally {
    isLoading.value = false
  }
}

async function selectSection(section) {
  isLoadingDetails.value = true
  detailError.value = ''

  try {
    selectedSection.value = await getSectionDetails(section.id)
  } catch (err) {
    detailError.value = err.message
  } finally {
    isLoadingDetails.value = false
  }
}

onMounted(loadSections)
</script>

<template>
  <section class="panel section-panel">
    <div class="panel-header">
      <div>
        <p class="eyebrow">Sections</p>
        <h2>Find a section</h2>
      </div>
    </div>

    <form class="search-form" @submit.prevent="loadSections">
      <label for="section-search">Section name</label>
      <div class="search-row">
        <input id="section-search" v-model="search" type="search" placeholder="Search by name" />
        <button type="submit" :disabled="isLoading">Search</button>
      </div>
    </form>

    <ErrorMessage v-if="error" title="Could not load sections" :message="error" />
    <LoadingState v-else-if="isLoading" label="Loading sections" />

    <div v-else class="section-layout">
      <aside class="section-list" aria-label="Sections">
        <button
          v-for="section in sections"
          :key="section.id"
          class="section-button"
          :class="{ active: section.id === selectedSection?.id }"
          type="button"
          @click="selectSection(section)"
        >
          <span>{{ section.name }}</span>
          <small>{{ section.courseCode }}</small>
        </button>

        <EmptyState v-if="!sections.length" message="No sections match that search." />
      </aside>

      <div class="section-detail-area">
        <ErrorMessage v-if="detailError" title="Could not load section details" :message="detailError" />
        <LoadingState v-else-if="isLoadingDetails" label="Loading section details" />
        <SectionDetails v-else-if="selectedSection" :section="selectedSection" />
        <EmptyState v-else message="Select a section to view teams, students, instructors, and rubric details." />
      </div>
    </div>
  </section>
</template>
