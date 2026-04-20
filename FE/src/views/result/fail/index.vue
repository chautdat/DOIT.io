<template>
  <ArtResultPage
    type="fail"
    title="Submission Failed"
    message="Please verify and modify the following information before resubmitting."
    iconCode="ri:close-fill"
  >
    <template #content>
      <p>Your submission contains the following errors:</p>
      <p>
        <ArtSvgIcon icon="ri:close-circle-line" class="text-red-500 mr-1" />
        <span>Your account has been frozen</span>
      </p>
      <p>
        <ArtSvgIcon icon="ri:close-circle-line" class="text-red-500 mr-1" />
        <span>Your account is not eligible to apply</span>
      </p>
    </template>
    <template #buttons>
      <ElButton type="primary" v-ripple>Edit</ElButton>
      <ElButton v-ripple>View</ElButton>
    </template>
  </ArtResultPage>
</template>

<script setup lang="ts">
  defineOptions({ name: 'ResultFail' })
</script>
