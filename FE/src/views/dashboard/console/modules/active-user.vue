<template>
  <div class="art-card h-105 p-4 box-border mb-5 max-sm:mb-4">
    <ArtBarChart
      class="box-border p-2"
      barWidth="50%"
      height="13.7rem"
      :showAxisLine="false"
      :data="chartData"
      :xAxisData="xAxisLabels"
    />
    <div class="ml-1">
      <h3 class="mt-5 text-lg font-medium">User overview</h3>
      <p class="mt-1 text-sm">Compared with last week <span class="text-success font-medium">+23%</span></p>
      <p class="mt-1 text-sm">We created multiple options so you can combine them into pixel-perfect pages</p>
    </div>
    <div class="flex-b mt-2">
      <div class="flex-1" v-for="(item, index) in list" :key="index">
        <p class="text-2xl text-g-900">{{ item.num }}</p>
        <p class="text-xs text-g-500">{{ item.name }}</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
  interface UserStatItem {
    name: string
    num: string
  }

  const xAxisLabels = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep']

  const chartData = [160, 100, 150, 80, 190, 100, 175, 120, 160]

  const list: UserStatItem[] = [
    { name: 'Total users', num: '32k' },
    { name: 'Total visits', num: '128k' },
    { name: 'Daily visits', num: '1.2k' },
    { name: 'Weekly growth', num: '+5%' }
  ]
</script>
