<template>
  <div class="pt-5">
    <h1 class="page-title">Basic & Custom Buttons + Background Colors</h1>
    <ElRow :gutter="20">
      <ElCol :xs="24" :sm="12" :md="12" class="mb-5">
        <ArtBasicBanner
          title="Data Center Status"
          subtitle="System traffic increased by 23% YoY, all services running stable, monitoring normal."
        />
      </ElCol>
      <ElCol :xs="24" :sm="12" :md="12" class="mb-5">
        <ArtBasicBanner
          title="Welcome to Art Design Pro"
          subtitle="A modern admin system built with Vue 3 + TypeScript + Element Plus."
          titleColor="#333"
          subtitleColor="#666"
          boxStyle="!bg-[#D4F1F7]"
          :buttonConfig="{
            show: true,
            text: 'Start Exploring',
            color: 'var(--art-success)',
            textColor: '#fff',
            radius: '6px'
          }"
          @buttonClick="handleBannerClick"
        />
      </ElCol>
    </ElRow>

    <h1 class="page-title">Custom Images & Using Slot for Custom Content</h1>

    <ElRow :gutter="20">
      <ElCol :xs="24" :sm="12" :md="12" class="mb-5">
        <ArtBasicBanner
          title="Explore the Stars"
          subtitle="Join our astronomy observation activities, discover the mysteries of the universe"
          boxStyle="!bg-[#FF8AAB]"
          :buttonConfig="{
            show: true,
            text: 'Join Now',
            color: '#FF5A89',
            textColor: '#fff'
          }"
          :imageConfig="{
            src: icon3
          }"
        />
      </ElCol>
      <ElCol :xs="24" :sm="12" :md="12" class="mb-5">
        <ArtBasicBanner
          boxStyle="!bg-[#70B1FF]"
          :imageConfig="{
            src: icon5
          }"
        >
          <template #title>
            <h2 style="margin: 0; font-size: 1.6rem; color: #fff !important"
              >Smart Component System</h2
            >
          </template>

          <template #subtitle>
            <div style="margin-top: 12px">
              <p style="position: relative; z-index: 10; font-style: italic"
                >Flexible configuration, powerful extensions, supports custom slot content</p
              >
            </div>
          </template>

          <template #button>
            <div style="margin-top: 12px">
              <ElButton type="primary" color="#04A1FF"> View Docs </ElButton>
            </div>
          </template>
        </ArtBasicBanner>
      </ElCol>
    </ElRow>

    <h1 class="page-title">Abstract Configuration (Preset Mode)</h1>
    <ElRow :gutter="20">
      <ElCol :xs="24" :sm="12" :md="12" class="mb-5">
        <ArtBasicBanner v-bind="PresetBanners.marketing" />
      </ElCol>
      <ElCol :xs="24" :sm="12" :md="12" class="mb-5">
        <ArtBasicBanner v-bind="PresetBanners.info" />
      </ElCol>
    </ElRow>

    <h1 class="page-title">Card Banners</h1>
    <ElRow :gutter="20">
      <ElCol :xs="24" :sm="12" :md="12" :lg="6" class="mb-5">
        <ArtCardBanner
          title="System Running Normal"
          description="All core services are running stable, response time is within normal range."
        />
      </ElCol>
      <ElCol :xs="24" :sm="12" :md="12" :lg="6" class="mb-5">
        <ArtCardBanner
          :image="icon2"
          title="Important Notifications"
          description="You have 3 pending important messages, please check them."
          :button="{
            show: true,
            text: 'View Details',
            color: 'var(--art-warning)',
            textColor: '#fff'
          }"
        />
      </ElCol>
      <ElCol :xs="24" :sm="12" :md="12" :lg="6" class="mb-5">
        <ArtCardBanner
          :image="icon3"
          title="Data Analysis Report"
          description="This week's business data analysis report is complete, please review key metrics."
          :button="{
            show: true,
            text: 'Download Report',
            color: 'var(--art-error)',
            textColor: '#fff'
          }"
        />
      </ElCol>
      <ElCol :xs="24" :sm="12" :md="12" :lg="6" class="mb-5">
        <ArtCardBanner
          :image="icon4"
          title="Version Update Notice"
          description="Art Design Pro v2.1.0 released with performance optimizations and new features."
          :button="{
            show: true,
            text: 'Update Now',
            color: 'var(--theme-color)',
            textColor: '#fff'
          }"
          :cancelButton="{
            show: true,
            text: 'Remind Later',
            color: '#eee',
            textColor: '#333'
          }"
          @click="handleConfirm"
          @cancel="handleCancel"
        />
      </ElCol>
    </ElRow>
  </div>
</template>

<script setup lang="ts">
  import icon2 from '@imgs/3d/icon2.webp'
  import icon3 from '@imgs/3d/icon3.webp'
  import icon4 from '@imgs/3d/icon4.webp'
  import icon5 from '@imgs/3d/icon7.webp'

  defineOptions({ name: 'TemplateBanners' })

  /**
   * Handle banner click event
   */
  const handleBannerClick = () => {
    console.log('banner clicked')
  }

  /**
   * Handle confirm button click event
   */
  const handleConfirm = () => {
    console.log('confirm clicked')
  }

  /**
   * Handle cancel button click event
   */
  const handleCancel = () => {
    console.log('cancel clicked')
  }

  /**
   * Preset banner configurations
   * Provides common banner style configurations that can be used directly via v-bind
   */
  const PresetBanners = {
    marketing: {
      title: 'Limited Time Offer',
      subtitle: 'Selected items 48-hour flash sale, up to 30% off, limited quantities!',
      titleColor: 'var(--art-gray-900)',
      subtitleColor: 'var(--art-gray-900)',
      boxStyle: '!bg-success/15',
      meteorConfig: { enabled: true, count: 15 },
      buttonConfig: {
        show: true,
        text: 'Shop Now',
        color: 'var(--art-success)',
        textColor: '#fff'
      }
    },
    info: {
      title: 'Service Expiration Notice',
      subtitle:
        'Your premium service will expire in 7 days, please renew to continue enjoying full features.',
      titleColor: 'var(--art-gray-900)',
      subtitleColor: 'var(--art-gray-900)',
      boxStyle: '!bg-theme/15',
      meteorConfig: { enabled: true, count: 15 },
      buttonConfig: {
        show: true,
        text: 'Renew Now',
        color: 'var(--art-secondary)',
        textColor: '#fff'
      }
    }
  } as const
</script>

<style scoped>
  @reference '@styles/core/tailwind.css';

  .page-title {
    @apply my-5 text-xl font-medium first:mt-0;
  }
</style>
