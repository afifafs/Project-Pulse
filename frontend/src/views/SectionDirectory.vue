<script setup>
import { onMounted, ref } from 'vue'
import { getSections } from '../api/projectPulseApi'
import EmptyState from '../components/EmptyState.vue'
import ErrorMessage from '../components/ErrorMessage.vue'
import LoadingState from '../components/LoadingState.vue'

const sections = ref([])
const search = ref('')
const isLoading = ref(false)
const error = ref('')

async function loadSections() {
  isLoading.value = true
  error.value = ''

  try {
    sections.value = await getSections(search.value)
  } catch (err) {
    error.value = err.message
  } finally {
    isLoading.value = false
  }
}

function clearSearch() {
  search.value = ''
  loadSections()
}

onMounted(loadSections)
</script>

<template>
  <section class="panel section-panel">
    <div class="panel-header">
      <div>
        <p class="eyebrow">Sections</p>
        <h2>Find senior design sections</h2>
      </div>
    </div>

    <form class="search-form" @submit.prevent="loadSections">
      <label for="section-search">Section name</label>
      <div class="search-row">
        <input id="section-search" v-model="search" type="search" placeholder="Search by section name" />
        <button type="submit" :disabled="isLoading">Search</button>
        <button class="secondary-button" type="button" :disabled="isLoading || !search" @click="clearSearch">
          Clear
        </button>
      </div>
    </form>

    <ErrorMessage v-if="error" title="Could not load sections" :message="error" />
    <LoadingState v-else-if="isLoading" label="Loading sections" />

    <EmptyState v-else-if="!sections.length" message="No senior design sections match that search." />

    <div v-else class="table-wrap">
      <table class="sections-table">
        <thead>
          <tr>
            <th scope="col">Section</th>
            <th scope="col">Course code</th>
            <th scope="col">ID</th>
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
