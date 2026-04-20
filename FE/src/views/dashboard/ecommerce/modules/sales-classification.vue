<template>
  <div class="art-card p-5 mb-5 h-105 max-sm:mb-4">
    <div class="art-card-header">
      <div class="title">
        <h4>Sales Classification</h4>
        <p>By Product Category</p>
      </div>
    </div>

    <ArtRingChart
      :data="[
        { value: 30, name: 'Electronics' },
        { value: 55, name: 'Clothing & Accessories' },
        { value: 36, name: 'Home & Living' }
      ]"
      :color="['#4C87F3', '#EDF2FF', '#8BD8FC']"
      :radius="['70%', '80%']"
      height="16.5rem"
      :showLabel="false"
      :borderRadius="0"
      centerText="$300,458"
    />
    <div class="flex justify-around">
      <div class="flex-c">
        <div class="flex-cc size-10.5 mr-2.5 text-theme bg-theme/10 rounded-lg">
          <ArtSvgIcon icon="ri:money-cny-circle-line" class="text-xl" />
        </div>
        <div>
          <p class="text-lg">$500,458</p>
          <span class="text-sm">Total Revenue</span>
        </div>
      </div>
      <div class="flex-c">
        <div class="flex-cc size-10.5 mr-2.5 text-theme bg-theme/10 rounded-lg">
          <ArtSvgIcon icon="ri:heart-3-line" class="text-xl" />
        </div>
        <div>
          <p class="text-lg">$130,580</p>
          <span class="text-sm">Net Profit</span>
        </div>
      </div>
    </div>
  </div>
</template>
