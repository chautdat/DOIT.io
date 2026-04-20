<template>
  <div class="page-content mb-5">
    <!-- Calendar main body -->
    <ElCalendar v-model="currentDate">
      <template #date-cell="{ data }">
        <div
          class="relative flex flex-col h-full min-h-30 max-h-30 p-1 overflow-hidden c-p"
          :class="{ 'is-selected': data.isSelected }"
          @click="handleCellClick(data.day)"
        >
          <!-- Date display -->
          <p class="absolute top-1 right-1 text-sm">{{ formatDate(data.day) }}</p>

          <!-- Event list -->
          <div class="flex flex-col gap-1 w-full max-h-21 pr-1 mt-6 overflow-y-auto">
            <div
              v-for="event in getEvents(data.day)"
              :key="`${event.date}-${event.content}`"
              @click.stop="handleEventClick(event)"
            >
              <div
                class="min-w-25 px-3 py-1.5 overflow-hidden text-xs/6 font-medium text-ellipsis whitespace-nowrap rounded hover:opacity-80"
                :class="[event.bgClass, event.textClass]"
              >
                {{ event.content }}
              </div>
            </div>
          </div>
        </div>
      </template>
    </ElCalendar>

    <!-- Event edit dialog -->
    <ElDialog v-model="dialogVisible" :title="dialogTitle" width="600px" @closed="resetForm">
      <ElForm :model="eventForm" label-width="100px">
        <ElFormItem label="Event Title" required>
          <ElInput v-model="eventForm.content" placeholder="Enter event title" />
        </ElFormItem>
        <ElFormItem label="Event Color">
          <ElRadioGroup v-model="eventForm.type">
            <ElRadio v-for="type in eventTypes" :key="type.value" :value="type.value">
              {{ type.label }}
            </ElRadio>
          </ElRadioGroup>
        </ElFormItem>
        <ElFormItem label="Start Date" required>
          <ElDatePicker
            style="width: 100%"
            v-model="eventForm.date"
            type="date"
            placeholder="Select date"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </ElFormItem>
        <ElFormItem label="End Date">
          <ElDatePicker
            style="width: 100%"
            v-model="eventForm.endDate"
            type="date"
            placeholder="Select end date"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            :min-date="eventForm.date"
          />
        </ElFormItem>
      </ElForm>
      <template #footer>
        <span class="dialog-footer">
          <ElButton v-if="isEditing" type="danger" @click="handleDeleteEvent"> Delete </ElButton>
          <ElButton type="primary" @click="handleSaveEvent">
            {{ isEditing ? 'Update' : 'Add' }}
          </ElButton>
        </span>
      </template>
    </ElDialog>
  </div>
</template>

<script setup lang="ts">
  defineOptions({ name: 'TemplateCalendar' })

  /**
   * Calendar event type definition
   */
  interface CalendarEvent {
    date: string
    endDate?: string
    content: string
    type?: 'primary' | 'success' | 'warning' | 'danger'
    bgClass?: string
    textClass?: string
  }

  /**
   * Event type options
   */
  const eventTypes = [
    { label: 'Primary', value: 'primary' },
    { label: 'Success', value: 'success' },
    { label: 'Warning', value: 'warning' },
    { label: 'Danger', value: 'danger' }
  ] as const

  const currentDate = ref(new Date('2025-02-07'))
  const dialogVisible = ref(false)
  const dialogTitle = ref('Add Event')
  const editingEventIndex = ref<number>(-1)

  /**
   * Event list data
   */
  const events = ref<CalendarEvent[]>([
    { date: '2025-02-01', content: 'Product Requirements Review', type: 'primary' },
    {
      date: '2025-02-03',
      endDate: '2025-02-05',
      content: 'Weekly Project Meeting (Multi-day)',
      type: 'primary'
    },
    { date: '2025-02-10', content: 'Yoga Class', type: 'success' },
    { date: '2025-02-15', content: 'Team Building Activity', type: 'primary' },
    { date: '2025-02-20', content: 'Fitness Training', type: 'success' },
    { date: '2025-02-20', content: 'Code Review', type: 'danger' },
    { date: '2025-02-20', content: 'Team Lunch', type: 'primary' },
    { date: '2025-02-20', content: 'Project Progress Report', type: 'warning' },
    { date: '2025-02-28', content: 'Monthly Summary Meeting', type: 'warning' }
  ])

  /**
   * Event form data
   */
  const eventForm = ref<CalendarEvent>({
    date: '',
    endDate: '',
    content: '',
    type: 'primary'
  })

  /**
   * Whether in edit mode
   */
  const isEditing = computed(() => editingEventIndex.value >= 0)

  /**
   * Format date, show only day
   * @param date Full date string
   * @returns Day part of the date
   */
  const formatDate = (date: string) => date.split('-')[2]

  /**
   * Get style classes for event type
   * @param type Event type
   * @returns Object containing background and text color classes
   */
  const getEventClasses = (type: CalendarEvent['type'] = 'primary') => {
    const classMap = {
      primary: { bgClass: 'bg-theme/12', textClass: 'text-theme' },
      success: { bgClass: 'bg-success/12', textClass: 'text-success' },
      warning: { bgClass: 'bg-warning/12', textClass: 'text-warning' },
      danger: { bgClass: 'bg-danger/12', textClass: 'text-danger' }
    }
    return classMap[type]
  }

  /**
   * Get all events for a specific date
   * Supports multi-day events display
   * @param day Date string
   * @returns Event list for that date
   */
  const getEvents = (day: string) => {
    return events.value
      .filter((event) => {
        const eventDate = new Date(event.date)
        const currentDate = new Date(day)
        const endDate = event.endDate ? new Date(event.endDate) : new Date(event.date)

        return currentDate >= eventDate && currentDate <= endDate
      })
      .map((event) => {
        const { bgClass, textClass } = getEventClasses(event.type)
        return { ...event, bgClass, textClass }
      })
  }

  /**
   * Reset form data
   */
  const resetForm = () => {
    eventForm.value = {
      date: '',
      endDate: '',
      content: '',
      type: 'primary'
    }
    editingEventIndex.value = -1
  }

  /**
   * Handle calendar cell click event
   * Opens add event dialog
   * @param day Clicked date
   */
  const handleCellClick = (day: string) => {
    dialogTitle.value = 'Add Event'
    eventForm.value = {
      date: day,
      content: '',
      type: 'primary'
    }
    editingEventIndex.value = -1
    dialogVisible.value = true
  }

  /**
   * Handle event click
   * Opens edit event dialog
   * @param event Clicked event object
   */
  const handleEventClick = (event: CalendarEvent) => {
    dialogTitle.value = 'Edit Event'
    eventForm.value = { ...event }
    editingEventIndex.value = events.value.findIndex(
      (e) => e.date === event.date && e.content === event.content
    )
    dialogVisible.value = true
  }

  /**
   * Save event
   * Decides to add or update based on edit mode
   */
  const handleSaveEvent = () => {
    if (!eventForm.value.content || !eventForm.value.date) return

    if (isEditing.value) {
      events.value[editingEventIndex.value] = { ...eventForm.value }
    } else {
      events.value.push({ ...eventForm.value })
    }

    dialogVisible.value = false
    resetForm()
  }

  /**
   * Delete event
   */
  const handleDeleteEvent = () => {
    if (isEditing.value) {
      events.value.splice(editingEventIndex.value, 1)
      dialogVisible.value = false
      resetForm()
    }
  }
</script>

<style scoped>
  :deep(.el-calendar) {
    height: 100%;
  }

  :deep(.el-calendar__body) {
    height: calc(100% - 70px);
  }

  :deep(.el-calendar-table) {
    height: 100%;
  }

  :deep(.is-selected) {
    background-color: var(--el-color-warning-light-9) !important;
  }

  :deep(.el-calendar-day) {
    height: 100%;
  }

  :deep(.el-calendar-day:hover) {
    background-color: transparent !important;
  }

  :deep(.el-dialog__body) {
    padding-top: 20px;
  }
</style>
