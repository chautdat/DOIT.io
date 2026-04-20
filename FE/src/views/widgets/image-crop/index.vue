<template>
  <div class="page-content">
    <ArtCutterImg
      v-model:imgUrl="imageUrl"
      :boxWidth="530"
      :boxHeight="300"
      :cutWidth="360"
      :cutHeight="200"
      :quality="1"
      :tool="true"
      :watermarkText="'My Watermark'"
      watermarkColor="#ff0000"
      :showPreview="true"
      :originalGraph="false"
      :title="'Image crop'"
      :previewTitle="'Preview'"
      @error="handleError"
      @imageLoadComplete="handleLoadComplete"
      @imageLoadError="handleLoadError"
    />
  </div>
</template>

<script setup lang="ts">
  import lockImg from '@imgs/lock/bg_dark.webp'

  defineOptions({ name: 'WidgetsImageCrop' })

  const imageUrl = ref(lockImg)

  const handleError = (error: Error) => {
    console.error('Crop error:', error)
    ElMessage.error('Image crop failed')
  }

  const handleLoadComplete = (result: { url: string; width: number; height: number }) => {
    console.log('Image loaded:', result)
  }

  const handleLoadError = (error: Error) => {
    console.error('Image load failed:', error)
    ElMessage.error('Image load failed')
  }
</script>
