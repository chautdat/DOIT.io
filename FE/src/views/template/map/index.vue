<template>
  <div class="page-content mb-5" element-loading-text="Loading...">
    <ArtMapChart />
  </div>
</template>

<script setup lang="ts">
  defineOptions({ name: 'TemplateMap' })
  const ArtMapChart = defineAsyncComponent(
    () => import('@/components/core/charts/ArtMapChart.vue')
  )
</script>
