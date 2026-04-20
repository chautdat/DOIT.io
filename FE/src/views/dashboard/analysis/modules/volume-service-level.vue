<template>
  <div class="art-card h-82 p-5 mb-5 max-sm:mb-4">
    <div class="art-card-header">
      <div class="title">
        <h4>Volume & Service Level</h4>
      </div>
    </div>

    <ArtBarChart
      class="mt-6"
      height="14.3rem"
      :data="volumeServiceData"
      :xAxisData="serviceCategories"
      :showLegend="true"
      :showAxisLine="false"
      :stack="true"
      barWidth="22%"
    />
  </div>
</template>

<script setup lang="ts">
  interface VolumeServiceDataItem {
    name: string
    data: number[]
    stack: string
  }

  /**
   * Service category data
   * Product category labels
   */
  const serviceCategories = ref(['Product A', 'Product B', 'Product C', 'Product D', 'Product E'])

  /**
   * Volume and service data
   * Shows volume and service comparison for each product using stacked bar chart
   */
  const volumeServiceData = ref<VolumeServiceDataItem[]>([
    {
      name: 'Volume',
      data: [20, 25, 30, 35, 40],
      stack: 'total'
    },
    {
      name: 'Service',
      data: [30, 35, 40, 45, 50],
      stack: 'total'
    }
  ])
</script>
