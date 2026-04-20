<template>
  <div class="art-card h-128 p-5 mb-5 max-sm:mb-4">
    <div class="art-card-header">
      <div class="title">
        <h4>Activity</h4>
        <p>New <span class="text-success">+6</span></p>
      </div>
    </div>

    <div class="h-9/10 mt-2 overflow-hidden">
      <ElScrollbar>
        <div
          class="h-17.5 leading-17.5 border-b border-g-300 text-sm overflow-hidden last:border-b-0"
          v-for="(item, index) in list"
          :key="index"
        >
          <span class="text-g-800 font-medium">{{ item.username }}</span>
          <span class="mx-2 text-g-600">{{ item.type }}</span>
          <span class="text-theme">{{ item.target }}</span>
        </div>
      </ElScrollbar>
    </div>
  </div>
</template>

<script setup lang="ts">
  interface DynamicItem {
    username: string
    type: string
    target: string
  }

  const list = reactive<DynamicItem[]>([
    {
      username: 'River',
      type: 'followed',
      target: 'Muse'
    },
    {
      username: 'Holly',
      type: 'published an article',
      target: 'Vue 3 + TypeScript + Vite project notes'
    },
    {
      username: 'River',
      type: 'followed',
      target: 'Muse'
    },
    {
      username: 'Holly',
      type: 'published an article',
      target: 'Vue 3 + TypeScript + Vite project notes'
    },
    {
      username: 'Muse',
      type: 'asked a question',
      target: 'Can the theme be configured?'
    },
    {
      username: 'Poppy',
      type: 'redeemed an item',
      target: 'The Novel of a Lifetime'
    },
    {
      username: 'Cone',
      type: 'closed a question',
      target: 'Poppy'
    },
    {
      username: 'Moon',
      type: 'redeemed an item',
      target: 'The 7 Habits of Highly Effective People'
    }
  ])
</script>
