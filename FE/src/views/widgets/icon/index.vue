<template>
  <div class="space-y-5 mb-5">
    <div class="text-2xl font-medium mt-5 max-sm:text-2xl max-sm:mt-3">Icons</div>
    <div class="text-g-800">
      v.3.0 icon library upgraded to iconify, you can search on
      <a href="https://icones.js.org/" target="_blank" class="text-theme hover:underline"
        >Iconify</a
      >
      , supports multiple icon libraries such as Remix Icon, Solar, Tabler Icons, etc.
    </div>
    <div class="text-g-800">
      To ensure consistent icon style across the system, the project uses Remix Icon library, you
      can search on
      <a
        href="https://icones.js.org/collection/ri"
        target="_blank"
        class="text-theme hover:underline"
        >Iconify</a
      >
      or<a href="https://remixicon.com/" target="_blank" class="text-theme hover:underline">
        Remix Icon Official Website </a
      >.
    </div>

    <!-- Iconify Icons -->
    <div class="art-card-sm p-5">
      <div class="text-lg font-semibold mb-4">Iconify</div>
      <div class="flex items-center gap-6">
        <ArtSvgIcon icon="ri:github-fill" class="text-2xl" />
        <ArtSvgIcon icon="ri:copilot-line" class="text-2xl text-theme" />
        <ArtSvgIcon icon="ri:edge-line" class="text-2xl text-secondary" />
        <ArtSvgIcon icon="ri:planet-line" class="text-2xl text-warning" />
        <ArtSvgIcon icon="ri:windows-line" class="text-2xl text-info" />
        <ArtSvgIcon icon="ri:thumb-up-line" class="text-2xl text-danger" />
        <ArtSvgIcon icon="ri:gift-2-line" class="text-2xl text-success" />
        <ArtSvgIcon icon="ri:apple-line" class="text-2xl text-secondary" />
      </div>
    </div>

    <!-- Svg Icons -->
    <div class="art-card-sm p-5">
      <div class="text-lg font-semibold mb-4">Svg Icons</div>
      <div class="flex items-center gap-6">
        <ArtSvgIcon icon="svg-spinners:3-dots-fade" class="text-2xl text-red-400" />
        <ArtSvgIcon icon="svg-spinners:3-dots-bounce" class="text-2xl text-blue-400" />
        <ArtSvgIcon icon="svg-spinners:3-dots-move" class="text-2xl text-orange-400" />
        <ArtSvgIcon icon="svg-spinners:3-dots-rotate" class="text-2xl text-purple-400" />
        <ArtSvgIcon icon="svg-spinners:blocks-shuffle-2" class="text-2xl text-pink-300" />
        <ArtSvgIcon icon="svg-spinners:clock" class="text-2xl text-yellow-500" />
        <ArtSvgIcon icon="svg-spinners:tadpole" class="text-2xl text-orange-500" />
        <ArtSvgIcon icon="svg-spinners:blocks-wave" class="text-2xl text-blue-500" />
      </div>
    </div>

    <!-- Svg Icons -->
    <div class="art-card-sm p-5">
      <div class="text-lg font-semibold mb-4">Material Line Icons</div>
      <div class="flex items-center gap-6">
        <ArtSvgIcon icon="line-md:phone-call-twotone-loop" class="text-2xl text-blue-500" />

        <ArtSvgIcon icon="line-md:switch-off" class="text-2xl text-green-500" />
        <ArtSvgIcon icon="line-md:sun-rising-filled-loop" class="text-2xl text-yellow-400" />
        <ArtSvgIcon icon="line-md:volume-high-filled" class="text-2xl text-purple-500" />
        <ArtSvgIcon icon="line-md:github-twotone" class="text-2xl text-gray-700" />
        <ArtSvgIcon icon="line-md:telegram" class="text-2xl text-sky-500" />
        <ArtSvgIcon icon="line-md:reddit-loop" class="text-2xl text-orange-400" />
        <ArtSvgIcon
          icon="line-md:coffee-half-empty-filled-loop"
          class="text-2xl text-emerald-500"
        />
      </div>
    </div>

    <!-- Usage Examples -->
    <div class="art-card-sm p-5">
      <div class="text-lg font-semibold mb-4">Usage Examples</div>
      <div class="space-y-4">
        <div>
          <div class="text-sm text-g-600 mb-2">Basic Usage</div>
          <div class="bg-g-200 dark:bg-g-300/30 p-4 rounded font-mono text-sm text-g-800">
            &lt;ArtSvgIcon icon="ri:home-line" /&gt;
          </div>
        </div>
        <div>
          <div class="text-sm text-g-600 mb-2">Custom Size</div>
          <div class="bg-g-200 dark:bg-g-300/30 p-4 rounded font-mono text-sm text-g-800">
            &lt;ArtSvgIcon icon="ri:user-line" class="text-2xl" /&gt;
          </div>
        </div>
        <div>
          <div class="text-sm text-g-600 mb-2">Custom Color</div>
          <div class="bg-g-200 dark:bg-g-300/30 p-4 rounded font-mono text-sm text-g-800">
            &lt;ArtSvgIcon icon="ri:heart-fill" class="text-red-500" /&gt;
          </div>
        </div>
        <div>
          <div class="text-sm text-g-600 mb-2">Combined Usage</div>
          <div class="bg-g-200 dark:bg-g-300/30 p-4 rounded font-mono text-sm text-g-800">
            &lt;ArtSvgIcon icon="ri:star-fill" class="text-4xl text-yellow-500" /&gt;
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
  import ArtSvgIcon from '@/components/core/base/ArtSvgIcon.vue'

  defineOptions({ name: 'IconPage' })
</script>
