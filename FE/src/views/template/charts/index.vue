<template>
  <div class="pt-5">
    <h1 class="page-title">Charts</h1>

    <ElRow :gutter="20">
      <ElCol :xs="24" :md="12" :lg="8" class="mb-5">
        <div class="art-card">
          <div class="pb-3.5">
            <span class="text-base font-medium">Bar chart (single series)</span>
          </div>
          <ArtBarChart
            :data="singleBarData"
            :xAxisData="xAxisData"
            :showLegend="true"
            legendPosition="right"
          />
        </div>
      </ElCol>
      <ElCol :xs="24" :md="12" :lg="8" class="mb-5">
        <div class="art-card">
          <div class="pb-3.5">
            <span class="text-base font-medium">Bar chart (multiple series)</span>
          </div>
          <ArtBarChart
            :data="multiBarData"
            :xAxisData="xAxisData"
            :showLegend="true"
            barWidth="26%"
          />
        </div>
      </ElCol>
      <ElCol :xs="24" :md="12" :lg="8" class="mb-5">
        <div class="art-card">
          <div class="pb-3.5">
            <span class="text-base font-medium">Bar chart (stacked)</span>
          </div>
          <ArtBarChart
            :data="stackBarData"
            :xAxisData="stackXAxisData"
            :showLegend="true"
            :stack="true"
            barWidth="26%"
          />
        </div>
      </ElCol>
      <ElCol :xs="24" :md="12" :lg="8" class="mb-5">
        <div class="art-card">
          <div class="pb-3.5">
            <span class="text-base font-medium">Line chart</span>
          </div>
          <ArtLineChart
            :data="[58, 15, 82, 35, 120, 62, 45]"
            :xAxisData="['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul']"
            symbol="none"
            :symbolSize="7"
          />
        </div>
      </ElCol>
      <ElCol :xs="24" :md="12" :lg="8" class="mb-5">
        <div class="art-card">
          <div class="pb-3.5">
            <span class="text-base font-medium">Line chart (gradient background)</span>
          </div>
          <ArtLineChart
            :data="[28, 45, 82, 35, 100, 32, 55]"
            :xAxisData="['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul']"
            :showAreaColor="true"
          />
        </div>
      </ElCol>
      <ElCol :xs="24" :md="12" :lg="8" class="mb-5">
        <div class="art-card">
          <div class="pb-3.5">
            <span class="text-base font-medium">Line chart (multiple series)</span>
          </div>
          <ArtLineChart
            :data="multiLineData"
            :xAxisData="['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun']"
            :showLegend="true"
          />
        </div>
      </ElCol>

      <ElCol :xs="24" :md="12" :lg="8" class="mb-5">
        <div class="art-card">
          <div class="pb-3.5">
            <span class="text-base font-medium">Horizontal bar chart</span>
          </div>
          <ArtHBarChart
            :data="[50, 80, 120, 90, 60]"
            :xAxisData="['Product A', 'Product B', 'Product C', 'Product D', 'Product E']"
          />
        </div>
      </ElCol>
      <ElCol :xs="24" :md="12" :lg="8" class="mb-5">
        <div class="art-card">
          <div class="pb-3.5">
            <span class="text-base font-medium">Horizontal bar chart</span>
          </div>

          <ArtHBarChart
            :data="[
              { name: 'Series 1', data: [10, 20, 30] },
              { name: 'Series 2', data: [15, 25, 35] }
            ]"
            :xAxisData="['Category 1', 'Category 2', 'Category 3']"
            :showLegend="true"
            barWidth="30%"
          />
        </div>
      </ElCol>
      <ElCol :xs="24" :md="12" :lg="8" class="mb-5">
        <div class="art-card">
          <div class="pb-3.5">
            <span class="text-base font-medium">Horizontal stacked bar chart</span>
          </div>
          <ArtHBarChart
            :data="[
              { name: 'Series 1', data: [10, 20, 30] },
              { name: 'Series 2', data: [15, 25, 35] }
            ]"
            :xAxisData="['Category 1', 'Category 2', 'Category 3']"
            :showLegend="true"
            :stack="true"
            barWidth="30%"
          />
        </div>
      </ElCol>

      <ElCol :xs="24" :md="12" :lg="8" class="mb-5">
        <div class="art-card">
          <div class="pb-3.5">
            <span class="text-base font-medium">Donut chart</span>
          </div>
          <ArtRingChart
            :data="[
              { value: 35, name: 'Group A' },
              { value: 28, name: 'Group B' },
              { value: 42, name: 'Group C' }
            ]"
            :radius="['54%', '70%']"
            legendPosition="bottom"
          />
        </div>
      </ElCol>

      <ElCol :xs="24" :md="12" :lg="8" class="mb-5">
        <div class="art-card">
          <div class="pb-3.5">
            <span class="text-base font-medium">Donut chart</span>
          </div>
          <ArtRingChart
            :data="[
              { value: 35, name: 'Group A' },
              { value: 28, name: 'Group B' },
              { value: 42, name: 'Group C' },
              { value: 32, name: 'Group D' },
              { value: 26, name: 'Group E' },
              { value: 37, name: 'Group F' }
            ]"
            :radius="['54%', '70%']"
            :borderRadius="0"
            :showLegend="true"
            legendPosition="bottom"
            centerText="¥300,458"
          />
        </div>
      </ElCol>
      <ElCol :xs="24" :md="12" :lg="8" class="mb-5">
        <div class="art-card">
          <div class="pb-3.5">
            <span class="text-base font-medium">Pie chart</span>
          </div>
          <ArtRingChart
            :data="[
              { value: 30, name: 'Group A' },
              { value: 25, name: 'Group B' },
              { value: 45, name: 'Group C' }
            ]"
            :radius="['0%', '70%']"
            :showLegend="true"
            legendPosition="right"
          />
        </div>
      </ElCol>
      <ElCol :xs="24" :md="12" :lg="8" class="mb-5">
        <div class="art-card">
          <div class="pb-3.5">
            <span class="text-base font-medium">Scatter chart</span>
          </div>
          <ArtScatterChart
            :data="[
              { value: [1, 3] },
              { value: [2, 4] },
              { value: [3, 5] },
              { value: [4, 6] },
              { value: [5, 7] },
              { value: [6, 8] },
              { value: [7, 7] },
              { value: [8, 9] },
              { value: [9, 8] },
              { value: [10, 6] },
              { value: [11, 7] },
              { value: [12, 8] }
            ]"
          />
        </div>
      </ElCol>

      <ElCol :xs="24" :md="12" :lg="8" class="mb-5">
        <div class="art-card">
          <div class="pb-3.5">
            <span class="text-base font-medium">Radar chart</span>
          </div>
          <ArtRadarChart
            :indicator="[
              { name: 'Sales', max: 100 },
              { name: 'Management', max: 100 },
              { name: 'Tech', max: 100 },
              { name: 'Support', max: 100 },
              { name: 'Development', max: 100 }
            ]"
            :data="[
              {
                name: 'Budget allocation',
                value: [80, 70, 90, 85, 75]
              },
              {
                name: 'Actual spend',
                value: [70, 75, 85, 80, 70]
              }
            ]"
          />
        </div>
      </ElCol>
      <ElCol :xs="24" :md="12" :lg="8" class="mb-5">
        <div class="art-card">
          <div class="pb-3.5">
            <span class="text-base font-medium">Candlestick chart</span>
          </div>
          <ArtKLineChart
            :data="[
              { time: '2024-01-01', open: 20, close: 23, high: 25, low: 18 },
              { time: '2024-01-02', open: 23, close: 21, high: 24, low: 20 },
              { time: '2024-01-03', open: 21, close: 25, high: 26, low: 21 }
            ]"
            :showDataZoom="false"
            :dataZoomStart="0"
            :dataZoomEnd="100"
          />
        </div>
      </ElCol>
      <ElCol :xs="24" :md="12" :lg="8" class="mb-5">
        <div class="art-card">
          <div class="pb-3.5">
            <span class="text-base font-medium">Bi-directional stacked bar chart</span>
          </div>
          <ArtDualBarCompareChart
            :positiveData="[50, 28, 80, 65, 68, 70, 60, 55]"
            :negativeData="[50, 28, 40, 45, 38, 50, 42, 48]"
            :xAxisData="[
              '0-4',
              '5-14',
              '15-24',
              '25-34',
              '35-44',
              '45-54',
              '55-64',
              '65+'
            ]"
            positiveName="Male age distribution"
            negativeName="Female age distribution"
          />
        </div>
      </ElCol>
    </ElRow>
  </div>
</template>

<script setup lang="ts">
  import type { LineDataItem } from '@/types/component/chart'

  defineOptions({ name: 'TemplateCharts' })

  const multiLineData: LineDataItem[] = [
    {
      name: 'Revenue',
      data: [120, 132, 101, 134, 90, 130],
      areaStyle: {
        startOpacity: 0.1,
        endOpacity: 0
      }
    },
    {
      name: 'Profit',
      data: [80, 92, 71, 94, 60, 80],
      areaStyle: {
        startOpacity: 0.1,
        endOpacity: 0
      }
    }
  ]

  const singleBarData = ref([120, 200, 150, 80, 70, 110, 130])
  const xAxisData = ref(['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'])

  const multiBarData = ref([
    { name: 'Revenue', data: [120, 200, 150, 80, 70, 110, 130] },
    { name: 'Profit', data: [20, 50, 30, 15, 10, 25, 35] }
  ])

  const stackBarData = ref([
    { name: 'Q1', data: [20, 25, 30, 35, 40], stack: 'total' },
    { name: 'Q2', data: [30, 35, 40, 45, 50], stack: 'total' }
  ])

  const stackXAxisData = ref(['Product A', 'Product B', 'Product C', 'Product D', 'Product E'])
</script>

<style scoped>
  @reference '@styles/core/tailwind.css';

  .page-title {
    @apply my-5 text-xl font-medium first:mt-0;
  }

  .art-card {
    @apply p-5;
  }
</style>
