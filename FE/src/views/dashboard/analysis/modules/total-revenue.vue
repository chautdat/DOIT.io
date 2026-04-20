<template>
  <div class="art-card h-100 p-5 mb-5 max-sm:mb-4">
    <div class="art-card-header">
      <div class="title">
        <h4>Total Revenue</h4>
      </div>
    </div>
    <ArtBarChart
      height="calc(100% - 30px)"
      :data="revenueData"
      :xAxisData="weekDays"
      :showLegend="true"
      :showAxisLine="false"
      barWidth="18%"
    />
  </div>
</template>

<script setup lang="ts">
  interface RevenueDataItem {
    name: string
    data: number[]
  }

  /**
   * Week day labels
   */
  const weekDays = ref(['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'])

  /**
   * Total revenue data
   * Compares online and offline sales weekly revenue
   */
  const revenueData = ref<RevenueDataItem[]>([
    {
      name: 'Online Sales',
      data: [12, 13, 5, 15, 10, 15, 18]
    },
    {
      name: 'Offline Sales',
      data: [10, 11, 20, 5, 11, 13, 10]
    }
  ])
</script>
