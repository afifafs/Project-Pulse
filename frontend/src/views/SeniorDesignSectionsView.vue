<script setup>
import { computed, onMounted, ref } from 'vue'
import { getSections } from '../api/projectPulseApi'
import EmptyState from '../components/EmptyState.vue'
import ErrorMessage from '../components/ErrorMessage.vue'
import LoadingState from '../components/LoadingState.vue'

const searchTerm = ref('')
const sections = ref([])
const isLoading = ref(false)
const error = ref('')

const resultCountLabel = computed(() => {
  const count = sections.value.length
  return `${count} ${count === 1 ? 'section' : 'sections'} found`
})

async function fetchSections() {
  isLoading.value = true
  error.value = ''

  try {
    sections.value = await getSections(searchTerm.value)
  } catch (err) {
    sections.value = []
    error.value = err.message
  } finally {
    isLoading.value = false
  }
}

function resetSearch() {
  searchTerm.value = ''
  fetchSections()
}

onMounted(fetchSections)
</script>

<template>
  <section class="panel section-search-panel">
    <div class="panel-header">
      <div>
        <p class="eyebrow">Sections</p>
        <h2>Find senior design sections</h2>
      </div>
      <span class="result-count" v-if="!isLoading && !error">{{ resultCountLabel }}</span>
    </div>

    <form class="search-form" @submit.prevent="fetchSections">
      <label for="senior-design-section-search">Search by section name</label>
      <div class="search-row">
        <input
          id="senior-design-section-search"
          v-model="searchTerm"
          type="search"
          placeholder="Enter a section name"
        />
        <button type="submit" :disabled="isLoading">Search</button>
        <button class="secondary-button" type="button" :disabled="isLoading || !searchTerm" @click="resetSearch">
          Clear
        </button>
      </div>
    </form>

    <ErrorMessage v-if="error" title="Could not load sections" :message="error" />
    <LoadingState v-else-if="isLoading" label="Loading senior design sections" />
    <EmptyState v-else-if="sections.length === 0" message="No senior design sections match that search." />

    <div v-else class="table-wrap">
      <table class="sections-table">
        <thead>
          <tr>
            <th scope="col">Section</th>
            <th scope="col">Course code</th>
            <th scope="col">Section ID</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="section in sections" :key="section.id">
            <td>{{ section.name }}</td>
            <td>{{ section.courseCode }}</td>
            <td>{{ section.id }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>
