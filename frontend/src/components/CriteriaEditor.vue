<script setup>
defineProps({
  criteria: {
    type: Array,
    required: true,
  },
})

const emit = defineEmits(['add', 'remove'])
</script>

<template>
  <div class="criteria-editor">
    <div class="criteria-toolbar">
      <h3>Criteria</h3>
      <button class="secondary-button" type="button" @click="emit('add')">Add criterion</button>
    </div>

    <fieldset v-for="(criterion, index) in criteria" :key="index" class="criterion-card">
      <div class="criterion-header">
        <legend>Criterion {{ index + 1 }}</legend>
        <button
          class="text-button"
          type="button"
          :disabled="criteria.length === 1"
          @click="emit('remove', index)"
        >
          Remove
        </button>
      </div>

      <label>
        Name
        <input v-model.trim="criterion.name" required type="text" placeholder="Collaboration" />
      </label>

      <label>
        Description
        <textarea
          v-model.trim="criterion.description"
          rows="3"
          placeholder="What should reviewers consider?"
        ></textarea>
      </label>

      <label>
        Max score
        <input v-model.number="criterion.maxScore" min="1" required type="number" />
      </label>
    </fieldset>
  </div>
</template>
