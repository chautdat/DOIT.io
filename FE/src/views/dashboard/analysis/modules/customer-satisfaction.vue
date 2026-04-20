<template>
  <div class="art-card h-100 p-5 mb-5 max-sm:mb-4">
    <div class="art-card-header">
      <div class="title">
        <h4>Customer Satisfaction</h4>
      </div>
    </div>
    <ArtLineChart
      height="calc(100% - 30px)"
      :data="chartData"
      :xAxisData="xAxisData"
      :showLegend="true"
      :showAxisLabel="true"
      :showAxisLine="false"
      :showSplitLine="true"
    />
  </div>
</template>

<script setup lang="ts">
  import type { LineDataItem } from '@/types/component/chart'

  const AREA_STYLE_CONFIG = {
    startOpacity: 0.08,
    endOpacity: 0
  } as const

  /**
   * X-axis data configuration
   * Represents days of a week (1-7)
   */
  const xAxisData = ['1', '2', '3', '4', '5', '6', '7']

  /**
   * Customer satisfaction chart data
   * Compares last month and this month satisfaction trends
   */
  const chartData = computed<LineDataItem[]>(() => [
    {
      name: 'Last Month',
      data: [65, 72, 68, 75, 82, 78, 85],
      areaStyle: AREA_STYLE_CONFIG
    },
    {
      name: 'This Month',
      data: [78, 85, 82, 88, 92, 89, 95],
      areaStyle: AREA_STYLE_CONFIG
    }
  ])
</script>
