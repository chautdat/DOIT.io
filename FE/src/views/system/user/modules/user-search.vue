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
  interface Props {
    modelValue: Api.SystemManage.UserSearchParams
  }
  interface Emits {
    (e: 'update:modelValue', value: Api.SystemManage.UserSearchParams): void
    (e: 'search', params: Api.SystemManage.UserSearchParams): void
    (e: 'reset'): void
  }
  const props = defineProps<Props>()
  const emit = defineEmits<Emits>()

  const searchBarRef = ref()
  const formData = computed({
    get: () => props.modelValue,
    set: (val) => emit('update:modelValue', val)
  })

  const rules = {
  }

  const statusOptions = ref<{ label: string; value: string; disabled?: boolean }[]>([])

  function fetchStatusOptions(): Promise<typeof statusOptions.value> {
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve([
          { label: 'Online', value: '1' },
          { label: 'Offline', value: '2' },
          { label: 'Error', value: '3' },
          { label: 'Disabled', value: '4' }
        ])
      }, 1000)
    })
  }

  onMounted(async () => {
    statusOptions.value = await fetchStatusOptions()
  })

  const formItems = computed(() => [
    {
      label: 'Username',
      key: 'userName',
      type: 'input',
      placeholder: 'Please enter a username',
      clearable: true
    },
    {
      label: 'Phone',
      key: 'userPhone',
      type: 'input',
      props: { placeholder: 'Please enter a phone number', maxlength: '11' }
    },
    {
      label: 'Email',
      key: 'userEmail',
      type: 'input',
      props: { placeholder: 'Please enter an email' }
    },
    {
      label: 'Status',
      key: 'status',
      type: 'select',
      props: {
        placeholder: 'Please select a status',
        options: statusOptions.value
      }
    },
    {
      label: 'Gender',
      key: 'userGender',
      type: 'radiogroup',
      props: {
        options: [
          { label: 'Male', value: '1' },
          { label: 'Female', value: '2' }
        ]
      }
    }
  ])

  function handleReset() {
    console.log('Reset form')
    emit('reset')
  }

  async function handleSearch(params: Api.SystemManage.UserSearchParams) {
    await searchBarRef.value.validate()
    emit('search', params)
    console.log('Form data', params)
  }
</script>
