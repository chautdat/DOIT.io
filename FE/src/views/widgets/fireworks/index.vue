<template>
  <div class="page-content">
    <div class="mb-5">
      <ElSpace wrap>
        <ElButton :disabled="isLaunching" v-ripple @click="handleSingleLaunch"
          >✨ Launch fireworks</ElButton
        >
        <ElButton :disabled="isLaunching" v-ripple @click="handleImageLaunch(bp)"
          >🎉 Open a lucky red packet</ElButton
        >
        <ElButton :disabled="isLaunching" v-ripple @click="handleMultipleLaunch('')"
          >🎆 Fireworks show</ElButton
        >
        <ElButton :disabled="isLaunching" v-ripple @click="handleImageLaunch(sd)"
          >❄️ Light snowfall</ElButton
        >
        <ElButton :disabled="isLaunching" v-ripple @click="handleMultipleLaunch(sd)"
          >❄️ Romantic blizzard</ElButton
        >
      </ElSpace>
    </div>

    <ElDescriptions
      title="Fireworks component notes"
      direction="vertical"
      :column="1"
      border
      style="margin-top: 50px"
    >
      <ElDescriptionsItem label="When it shows">
        The fireworks component is globally registered. Trigger timing is controlled by the config file. The default dates have already passed, so it will not trigger again during normal use.
      </ElDescriptionsItem>
      <ElDescriptionsItem label="Fireworks style">
        Geometric shapes are shown by default. You can configure images, but the image must be defined ahead of time in
        src/components/core/layouts/ArtFireworksEffect.vue
      </ElDescriptionsItem>
      <ElDescriptionsItem label="Config file">
        Configure holidays and matching fireworks styles in src/config/festival.ts
      </ElDescriptionsItem>
      <ElDescriptionsItem label="Shortcut">
        command + shift + p or ctrl + shift + p
      </ElDescriptionsItem>
    </ElDescriptions>
  </div>
</template>

<script setup lang="ts">
  import { mittBus } from '@/utils/sys'
  import bp from '@imgs/ceremony/hb.png'
  import sd from '@imgs/ceremony/sd.png'

  defineOptions({ name: 'WidgetsFireworks' })

  const timerRef = ref<ReturnType<typeof setInterval> | null>(null)
  const isLaunching = ref(false)

  const triggerFireworks = (count: number, src: string) => {
    if (timerRef.value) {
      clearInterval(timerRef.value)
      timerRef.value = null
    }

    isLaunching.value = true

    let fired = 0
    timerRef.value = setInterval(() => {
      mittBus.emit('triggerFireworks', src)
      fired++

      if (fired >= count) {
        clearInterval(timerRef.value!)
        timerRef.value = null
        isLaunching.value = false
      }
    }, 1000)
  }

  const handleSingleLaunch = () => {
    mittBus.emit('triggerFireworks')
  }

  const handleMultipleLaunch = (src: string) => {
    triggerFireworks(10, src)
  }

  const handleImageLaunch = (src: string) => {
    mittBus.emit('triggerFireworks', src)
  }

  onUnmounted(() => {
    if (timerRef.value) {
      clearInterval(timerRef.value)
      timerRef.value = null
    }
  })
</script>
