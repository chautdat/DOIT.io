<template>
  <div class="art-card h-82 p-5 mb-5 max-sm:mb-4">
    <div class="art-card-header">
      <div class="title">
        <h4>Sales by Region</h4>
      </div>
    </div>

    <ArtRingChart
      :data="[
        { value: 30, name: 'New York' },
        { value: 25, name: 'Los Angeles' },
        { value: 45, name: 'Chicago' }
      ]"
      :color="['#4C87F3', '#93F1B4', '#8BD8FC']"
      :radius="['46%', '60%']"
    />
  </div>
</template>
