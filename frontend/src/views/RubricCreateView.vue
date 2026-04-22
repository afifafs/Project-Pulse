<script setup>
import { reactive, ref } from 'vue'
import { createRubric } from '../api/projectPulseApi'
import CriteriaEditor from '../components/CriteriaEditor.vue'
import ErrorMessage from '../components/ErrorMessage.vue'

function newCriterion() {
  return {
    name: '',
    description: '',
    maxScore: 10,
  }
}

const form = reactive({
  name: '',
  criteria: [newCriterion()],
})

const isSubmitting = ref(false)
const error = ref('')
const successMessage = ref('')

function addCriterion() {
  form.criteria.push(newCriterion())
}

function removeCriterion(index) {
  if (form.criteria.length > 1) {
    form.criteria.splice(index, 1)
  }
}

function resetForm() {
  form.name = ''
  form.criteria.splice(0, form.criteria.length, newCriterion())
}

async function submitRubric() {
  isSubmitting.value = true
  error.value = ''
  successMessage.value = ''

  const payload = {
    name: form.name.trim(),
    criteria: form.criteria.map((criterion) => ({
      name: criterion.name.trim(),
      description: criterion.description.trim(),
      maxScore: Number(criterion.maxScore),
    })),
  }

  try {
    const createdRubric = await createRubric(payload)
    successMessage.value = `Rubric "${createdRubric.name}" was created successfully.`
    resetForm()
  } catch (err) {
    error.value = err.message
  } finally {
    isSubmitting.value = false
  }
}
</script>

<template>
  <section class="panel rubric-create-panel">
    <div class="panel-header">
      <div>
        <p class="eyebrow">Rubrics</p>
        <h2>New rubric</h2>
      </div>
    </div>

    <form class="rubric-form" @submit.prevent="submitRubric">
      <label>
        Rubric name
        <input v-model.trim="form.name" required type="text" placeholder="Sprint peer evaluation" />
      </label>

      <CriteriaEditor :criteria="form.criteria" @add="addCriterion" @remove="removeCriterion" />

      <ErrorMessage v-if="error" title="Could not create rubric" :message="error" />
      <p v-if="successMessage" class="success-message" role="status">{{ successMessage }}</p>

      <button class="submit-button" type="submit" :disabled="isSubmitting">
        {{ isSubmitting ? 'Creating...' : 'Create rubric' }}
      </button>
    </form>
  </section>
</template>
