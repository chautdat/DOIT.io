<template>
  <div class="art-card h-128 p-5 mb-5 max-sm:mb-4">
    <div class="art-card-header">
      <div class="title">
        <h4>To-do list</h4>
        <p>Pending<span class="text-danger">3</span></p>
      </div>
    </div>

    <div class="h-[calc(100%-40px)] overflow-auto">
      <ElScrollbar>
        <div
          class="flex-cb h-17.5 border-b border-g-300 text-sm last:border-b-0"
          v-for="(item, index) in list"
          :key="index"
        >
          <div>
            <p class="text-sm">{{ item.username }}</p>
            <p class="text-g-500 mt-1">{{ item.date }}</p>
          </div>
          <ElCheckbox v-model="item.complate" />
        </div>
      </ElScrollbar>
    </div>
  </div>
</template>

<script setup lang="ts">
  interface TodoItem {
    username: string
    date: string
    complate: boolean
  }

  const list = reactive<TodoItem[]>([
    {
      username: 'Review today’s tasks',
      date: '09:30 AM',
      complate: true
    },
    {
      username: 'Reply to emails',
      date: '10:30 AM',
      complate: true
    },
    {
      username: 'Prepare work report',
      date: '11:00 AM',
      complate: true
    },
    {
      username: 'Product requirements meeting',
      date: '02:00 PM',
      complate: false
    },
    {
      username: 'Organize meeting notes',
      date: '03:30 PM',
      complate: false
    },
    {
      username: 'Plan tomorrow’s work',
      date: '06:30 PM',
      complate: false
    }
  ])
</script>
