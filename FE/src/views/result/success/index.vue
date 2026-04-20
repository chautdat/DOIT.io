<template>
  <ArtResultPage
    type="success"
    title="Submission Successful"
    message="The result page is used to provide feedback on a series of operation tasks. For simple operations, use Message global prompt feedback. The gray area can display supplementary information."
    iconCode="ri:check-fill"
  >
    <template #content>
      <p>Application submitted, awaiting department review.</p>
    </template>
    <template #buttons>
      <ElButton type="primary" v-ripple>Edit</ElButton>
      <ElButton v-ripple>View</ElButton>
      <ElButton v-ripple>Print</ElButton>
    </template>
  </ArtResultPage>
</template>

<script setup lang="ts">
  defineOptions({ name: 'ResultSuccess' })
</script>
