<template>
  <ArtSearchBar
    ref="searchBarRef"
    v-model="formData"
    :items="formItems"
    :rules="rules"
    @reset="handleReset"
    @search="handleSearch"
  >
  </ArtSearchBar>
</template>

<script setup lang="ts">
  type RoleSearchFormParams = Api.SystemManage.RoleSearchParams & {
    daterange?: string[]
  }

  interface Props {
    modelValue: RoleSearchFormParams
  }

  interface Emits {
    (e: 'update:modelValue', value: RoleSearchFormParams): void
    (e: 'search', params: RoleSearchFormParams): void
    (e: 'reset'): void
  }

  const props = defineProps<Props>()
  const emit = defineEmits<Emits>()

  const searchBarRef = ref()

  /**
   * Form data two-way binding
   */
  const formData = computed({
    get: () => props.modelValue,
    set: (val) => emit('update:modelValue', val)
  })

  /**
   * Form validation rules
   */
  const rules = {}

  /**
   * Role status options
   */
  const statusOptions = ref([
    { label: 'Enabled', value: true },
    { label: 'Disabled', value: false }
  ])

  /**
   * Search form configuration items
   */
  const formItems = computed(() => [
    {
      label: 'Role Name',
      key: 'roleName',
      type: 'input',
      placeholder: 'Enter role name',
      clearable: true
    },
    {
      label: 'Role Code',
      key: 'roleCode',
      type: 'input',
      placeholder: 'Enter role code',
      clearable: true
    },
    {
      label: 'Description',
      key: 'description',
      type: 'input',
      placeholder: 'Enter description',
      clearable: true
    },
    {
      label: 'Status',
      key: 'enabled',
      type: 'select',
      props: {
        placeholder: 'Select status',
        options: statusOptions.value,
        clearable: true
      }
    },
    {
      label: 'Created Date',
      key: 'daterange',
      type: 'datetime',
      props: {
        style: { width: '100%' },
        placeholder: 'Select date range',
        type: 'daterange',
        rangeSeparator: 'to',
        startPlaceholder: 'Start date',
        endPlaceholder: 'End date',
        valueFormat: 'YYYY-MM-DD',
        shortcuts: [
          { text: 'Today', value: [new Date(), new Date()] },
          { text: 'Last week', value: [new Date(Date.now() - 604800000), new Date()] },
          { text: 'Last month', value: [new Date(Date.now() - 2592000000), new Date()] }
        ]
      }
    }
  ])

  /**
   * Handle reset event
   */
  const handleReset = () => {
    emit('reset')
  }

  /**
   * Handle search event
   * Validate form before triggering search
   */
  const handleSearch = async (params: RoleSearchFormParams) => {
    await searchBarRef.value.validate()
    emit('search', params)
  }
</script>
