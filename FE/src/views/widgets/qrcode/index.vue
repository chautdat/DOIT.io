<template>
  <div class="page-content">
    <ElRow :gutter="20">
      <ElCol
        :span="6"
        :md="12"
        :sm="12"
        :xs="24"
        v-for="preset in qrcodePresets"
        :key="preset.title"
      >
        <ElCard class="mb-5">
          <template #header>
            <div>
              <span class="text-base font-bold">{{ preset.title }}</span>
            </div>
          </template>

          <div class="flex-cc p-5 rounded">
            <QrcodeVue :value="qrValue" v-bind="preset.config" />
          </div>
        </ElCard>
      </ElCol>
    </ElRow>
  </div>
</template>

<script setup lang="ts">
  import QrcodeVue from 'qrcode.vue'
  import type { Level, RenderAs, ImageSettings } from 'qrcode.vue'

  defineOptions({ name: 'WidgetsQrcode' })

  /**
   * QR code content
   */
  const qrValue = ref('https://www.artd.pro')
  const isShowLogo = ref(false)

  /**
   * Preset QR code style configuration
   */
  const qrcodePresets = [
    {
      title: 'Render as SVG tag',
      config: {
        size: 160,
        level: 'H' as Level,
        renderAs: 'svg' as RenderAs,
        margin: 0,
        background: '#ffffff',
        foreground: '#000000'
      }
    },
    {
      title: 'Render as Canvas tag',
      config: {
        size: 160,
        level: 'H' as Level,
        renderAs: 'canvas' as RenderAs,
        margin: 0,
        background: '#ffffff',
        foreground: '#000000'
      }
    },
    {
      title: 'Custom Color',
      config: {
        size: 160,
        level: 'H' as Level,
        renderAs: 'canvas' as RenderAs,
        margin: 0,
        background: '#f0f0f0',
        foreground: '#4080ff'
      }
    },
    {
      title: 'With Logo',
      config: {
        size: 160,
        level: 'H' as Level,
        renderAs: 'canvas' as RenderAs,
        margin: 0,
        background: '#ffffff',
        foreground: '#000000',
        imageSettings: {
          src: 'https://www.artd.pro/assets/draw1-Ce1WF34i.png',
          width: 40,
          height: 40,
          excavate: true
        }
      }
    }
  ]

  /**
   * QR code configuration
   */
  const qrcodeConfig = reactive({
    size: 160,
    level: 'H' as Level,
    renderAs: 'canvas' as RenderAs,
    margin: 0,
    background: '#ffffff',
    foreground: '#000000',
    imageSettings: {
      src: 'https://www.artd.pro/assets/draw1-Ce1WF34i.png',
      width: 40,
      height: 40,
      excavate: true
    } as ImageSettings
  })

  /**
   * Watch whether to show logo
   * Dynamically set QR code center logo image based on state
   */
  watch(isShowLogo, (val) => {
    if (!val) {
      qrcodeConfig.imageSettings = {} as ImageSettings
    } else {
      qrcodeConfig.imageSettings = {
        src: 'https://www.artd.pro/assets/draw1-Ce1WF34i.png',
        width: 40,
        height: 40,
        excavate: true
      }
    }
  })
</script>
