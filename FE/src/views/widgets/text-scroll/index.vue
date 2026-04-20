<template>
  <div class="page-content space-y-5">
    <ArtTextScroll
      text="Art Design Pro is an admin system that combines visual design and efficient development."
      showClose
    />

    <ArtTextScroll type="success" text="This is a success-style scrolling notice" />

    <ArtTextScroll type="warning" text="This is a warning-style scrolling notice" />

    <ArtTextScroll type="danger" text="This is a danger-style scrolling notice" />

    <ArtTextScroll type="info" text="This is an info-style scrolling notice" />

    <ArtTextScroll text="This is a dismissible scrolling notice" @close="handleClose" />

    <ArtTextScroll
      type="warning"
      text="This is a slower notice that scrolls to the right"
      :speed="30"
      direction="right"
    />

    <ArtTextScroll
      text="This notice only scrolls when the text overflows the container. Otherwise it stays still."
      @close="handleClose"
      :alwaysScroll="false"
    />

    <ArtTextScroll type="danger" direction="up" :speed="30" text="This notice scrolls upward" />

    <ArtTextScroll type="info" direction="down" :speed="30" text="This notice scrolls downward" />
  </div>
</template>

<script setup lang="ts">
  defineOptions({ name: 'WidgetsTextScroll' })

  const handleClose = () => {
    console.log('Text scroll component closed')
    ElMessage.info('Closed')
  }
</script>
