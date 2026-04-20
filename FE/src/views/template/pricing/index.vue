<template>
  <div class="pt-24 px-20 max-md:!pt-15 max-sm:!px-5 max-md:!px-8 bg-transparent !border-none">
    <div class="mb-10 text-center">
      <h1 class="mb-2 text-4xl font-medium max-sm:text-3xl">Trusted by Over 53,476 Developers</h1>
      <h2 class="mb-2.5 text-2xl font-normal text-g-600 max-sm:text-2xl"> And Many Tech Giants </h2>
      <div class="gap-2 flex-cc mt-2.5 max-sm:mt-3.5 max-sm:!block">
        <p class="text-sm italic text-g-600 max-sm:text-sm">
          This project is open source and free under MIT license. This pricing page is a demo
          template only
        </p>
        <ElTag type="success" size="large" round class="max-sm:mt-3.5"
          >Free for Commercial Use</ElTag
        >
      </div>
    </div>

    <div class="mt-20 max-md:mt-0">
      <ElRow :gutter="20" justify="center">
        <ElCol v-for="plan in pricingPlans" :key="plan.type" :xs="24" :sm="12" :md="6" class="mb-5">
          <ElCard class="flex flex-col h-full rounded-xl">
            <div class="mb-5">
              <h3 class="mb-2.5 text-xl font-medium">{{ plan.title }}</h3>
              <p
                class="h-10 pb-5 mb-5 overflow-hidden text-sm text-g-600 text-ellipsis border-b border-g-300/80 line-clamp-2"
              >
                {{ plan.description }}
              </p>
              <div class="mt-7.5">
                <span class="text-3xl font-medium">${{ plan.price }}</span>
                <span class="ml-2.5 text-sm text-g-600">/one-time payment</span>
              </div>
            </div>

            <div class="flex-grow mb-5">
              <div
                v-for="(feature, index) in plan.features"
                :key="index"
                class="flex-c mb-2.5 text-sm"
              >
                <ElIcon class="mr-2.5" :class="feature.available ? '!text-theme' : '!text-danger'">
                  <Check v-if="feature.available" />
                  <Close v-else />
                </ElIcon>
                <span>{{ feature.text }}</span>
              </div>
            </div>

            <div class="mt-auto text-center">
              <ElButton type="primary" class="w-full h-10" v-ripple>Buy Now</ElButton>
            </div>
          </ElCard>
        </ElCol>
      </ElRow>
    </div>
  </div>
</template>

<script setup lang="ts">
  import { ref } from 'vue'
  import { Check, Close } from '@element-plus/icons-vue'

  defineOptions({ name: 'Pricing' })

  interface Feature {
    text: string
    available: boolean
  }

  interface PricingPlan {
    type: string
    title: string
    description: string
    price: number
    features: Feature[]
  }

  const pricingPlans = ref<PricingPlan[]>([
    {
      type: 'single',
      title: 'Single Use License',
      description: 'For a single end product where end users are not charged.',
      price: 49,
      features: [
        { text: 'Full Source Code', available: true },
        { text: 'Technical Documentation', available: true },
        { text: 'SaaS Application License', available: false },
        { text: 'Single Project Use', available: true },
        { text: '1 Year Technical Support', available: true },
        { text: '1 Year Free Updates', available: true }
      ]
    },
    {
      type: 'multiple',
      title: 'Multiple Use License',
      description: 'For unlimited end products where end users are not charged.',
      price: 89,
      features: [
        { text: 'Full Source Code', available: true },
        { text: 'Technical Documentation', available: true },
        { text: 'SaaS Application License', available: false },
        { text: 'Unlimited Project Use', available: true },
        { text: '1 Year Technical Support', available: true },
        { text: '1 Year Free Updates', available: true }
      ]
    },
    {
      type: 'extended',
      title: 'Extended License',
      description: 'For a single end product where end users are charged.',
      price: 299,
      features: [
        { text: 'Full Source Code', available: true },
        { text: 'Technical Documentation', available: true },
        { text: 'SaaS Application License', available: true },
        { text: 'Single Project Use', available: true },
        { text: '1 Year Technical Support', available: true },
        { text: '1 Year Free Updates', available: true }
      ]
    },
    {
      type: 'unlimited',
      title: 'Unlimited License',
      description: 'For unlimited end products where end users are charged.',
      price: 499,
      features: [
        { text: 'Full Source Code', available: true },
        { text: 'Technical Documentation', available: true },
        { text: 'SaaS Application License', available: true },
        { text: 'Unlimited Project Use', available: true },
        { text: '1 Year Technical Support', available: true },
        { text: '1 Year Free Updates', available: true }
      ]
    }
  ])
</script>
