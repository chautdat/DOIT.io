<!-- Search Bar Example -->
<template>
  <div class="pb-5">
    <h2 class="mb-1 text-lg font-medium">Basic Example (Collapsed by default)</h2>
    <ArtSearchBar
      ref="searchBarBasicRef"
      v-model="formDataBasic"
      :items="formItemsBasic"
      @reset="handleBasicReset"
      @search="handleBasicSearch"
    >
    </ArtSearchBar>

    <h2 class="mb-1 mt-3.5 text-lg font-medium">Full Example (Expanded by default)</h2>
    <ArtSearchBar
      ref="searchBarAdvancedRef"
      v-model="formDataAdvanced"
      :items="formItemsAdvanced"
      :rules="rulesAdvanced"
      :defaultExpanded="true"
      :labelWidth="labelWidthAdvanced"
      :labelPosition="labelPositionAdvanced"
      :span="spanAdvanced"
      :gutter="gutterAdvanced"
      @reset="handleAdvancedReset"
      @search="handleAdvancedSearch"
    >
      <template #slots>
        <ElInput v-model="formDataAdvanced.slots" placeholder="I am a component rendered by slot" />
      </template>
    </ArtSearchBar>

    <div class="art-card p-5 !rounded-lg mt-5">
      <pre><code>{{ formDataAdvanced }}</code></pre>
    </div>

    <div class="mt-3.5">
      <h3 class="mb-2 text-base font-medium">Dynamic Form Operations</h3>
      <ElSpace wrap class="mb-3">
        <ElButton @click="getLevelOptions"> Get User Level Data </ElButton>
        <ElButton @click="addFormItem"> Add Form Item </ElButton>
        <ElButton @click="updateFormItem"> Update Form Item </ElButton>
        <ElButton @click="deleteFormItem"> Delete Form Item </ElButton>
        <ElButton @click="batchAddFormItems"> Batch Add </ElButton>
        <ElButton @click="resetDynamicItems"> Reset Dynamic Items </ElButton>
      </ElSpace>

      <h3 class="mb-2 text-base font-medium">Other Operations</h3>
      <ElSpace wrap>
        <ElButton @click="advancedValidate"> Validate Form </ElButton>
        <ElButton @click="advancedReset"> Reset </ElButton>
        <ElButton v-if="showUserName" @click="updateUserName"> Update Username </ElButton>
        <ElButton v-if="showUserName" @click="deleteUserName"> Delete Username </ElButton>
        <ElButton @click="labelWidthAdvanced = 120"> Change Label Width </ElButton>
        <ElButton @click="spanAdvanced = 8"> Set Components Per Row </ElButton>
        <ElButton @click="gutterAdvanced = 50"> Change Gutter </ElButton>
        <ElButton @click="labelPositionAdvanced = 'left'"> Label Left Align </ElButton>
        <ElButton @click="labelPositionAdvanced = 'right'"> Label Right Align </ElButton>
        <ElButton @click="labelPositionAdvanced = 'top'"> Label Top Align </ElButton>
      </ElSpace>
    </div>
  </div>
</template>

<script setup lang="ts">
  import { ElInput } from 'element-plus'
  import { SearchFormItem } from '@/components/core/forms/ArtSearchBar.vue'

  interface Emits {
    (e: 'update:modelValue', value: Record<string, any>): void
    (e: 'search', params: Record<string, any>): void
    (e: 'reset'): void
  }

  interface OptionItem {
    label: string
    value: string
    disabled?: boolean
  }

  interface BasicFormData {
    name?: string
    phone?: string
    level?: string
    address?: string
    date?: string
    daterange?: string[]
    status?: boolean
  }

  interface AdvancedFormData extends BasicFormData {
    slots?: string
    cascader?: string[]
    checkboxgroup?: string[]
    userGender?: string
    iconSelector?: string
    systemName?: string
  }

  const emit = defineEmits<Emits>()

  const FETCH_DELAY = 500

  const searchBarBasicRef = ref()
  const searchBarAdvancedRef = ref()

  /**
   * Basic form data
   */
  const formDataBasic = ref<BasicFormData>({
    name: undefined,
    phone: undefined,
    level: undefined,
    address: undefined,
    date: undefined,
    daterange: undefined,
    status: undefined
  })

  /**
   * Full example form data
   */
  const formDataAdvanced = ref<AdvancedFormData>({
    name: undefined,
    phone: undefined,
    level: undefined,
    address: undefined,
    slots: undefined,
    date: undefined,
    daterange: undefined,
    cascader: undefined,
    checkboxgroup: undefined,
    userGender: undefined,
    iconSelector: undefined,
    status: undefined,
    systemName: undefined
  })

  /**
   * Full example validation rules
   */
  const rulesAdvanced = {
    name: [{ required: true, message: 'Please enter username', trigger: 'blur' }],
    phone: [
      { required: true, message: 'Please enter phone number', trigger: 'blur' },
      { min: 11, max: 11, message: 'Please enter 11-digit phone number', trigger: 'blur' },
      { pattern: /^1[3456789]\d{9}$/, message: 'Please enter valid phone number', trigger: 'blur' }
    ],
    level: [{ required: true, message: 'Please select level', trigger: 'change' }],
    address: [{ required: true, message: 'Please enter address', trigger: 'blur' }]
  }

  const labelWidthAdvanced = ref(100)
  const labelPositionAdvanced = ref<'right' | 'left' | 'top'>('right')
  const spanAdvanced = ref(6)
  const gutterAdvanced = ref(12)

  const levelOptions = ref<OptionItem[]>([])

  /**
   * User level options
   */
  const LEVEL_OPTIONS: OptionItem[] = [
    { label: 'Regular User', value: 'normal' },
    { label: 'VIP User', value: 'vip' },
    { label: 'Premium VIP', value: 'svip' },
    { label: 'Enterprise User', value: 'enterprise', disabled: true }
  ]

  /**
   * Gender options
   */
  const GENDER_OPTIONS: OptionItem[] = [
    { label: 'Male', value: '1' },
    { label: 'Female', value: '2' }
  ]

  /**
   * Date shortcut options
   */
  const DATE_SHORTCUTS = [
    { text: 'Today', value: new Date() },
    { text: 'Yesterday', value: () => new Date(Date.now() - 86400000) },
    { text: 'A week ago', value: () => new Date(Date.now() - 604800000) }
  ]

  /**
   * Simulate API to fetch user level data
   * @returns User level options list
   */
  const fetchLevelOptions = (): Promise<OptionItem[]> => {
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve(LEVEL_OPTIONS)
      }, FETCH_DELAY)
    })
  }

  /**
   * Get user level data
   */
  const getLevelOptions = async (): Promise<void> => {
    levelOptions.value = await fetchLevelOptions()
    if (levelOptions.value.length) {
      ElMessage.success('Data fetched successfully')
    }
  }

  /**
   * Form item configuration type
   */
  interface FormItemConfig {
    label: string
    key: string
    type: string
    placeholder?: string
    clearable?: boolean
    props?: Record<string, any>
    [key: string]: any
  }

  /**
   * Factory function to create form items
   */
  const createFormItem = (config: FormItemConfig) => config

  // Basic form item configuration
  const baseFormItems = {
    username: createFormItem({
      label: 'Username',
      key: 'name',
      type: 'input',
      placeholder: 'Please enter username',
      clearable: true
    }),
    phone: createFormItem({
      label: 'Phone',
      key: 'phone',
      type: 'input',
      props: { placeholder: 'Please enter phone number', maxlength: '11' }
    }),
    level: createFormItem({
      label: 'User Level',
      key: 'level',
      type: 'select',
      props: {
        placeholder: 'Please select level',
        options: LEVEL_OPTIONS
      }
    }),
    address: createFormItem({
      label: 'Address',
      key: 'address',
      type: 'input',
      placeholder: 'Please enter address'
    }),
    date: createFormItem({
      label: 'Date',
      key: 'date',
      type: 'datetime',
      props: {
        style: { width: '100%' },
        placeholder: 'Please select date',
        type: 'date',
        valueFormat: 'YYYY-MM-DD',
        shortcuts: DATE_SHORTCUTS
      }
    }),
    gender: createFormItem({
      label: 'Gender',
      key: 'userGender',
      type: 'radiogroup',
      props: {
        options: GENDER_OPTIONS
      }
    })
  }

  // Form configuration
  const formItemsBasic = computed(() => [
    baseFormItems.username,
    {
      label: 'Password',
      key: 'password',
      type: 'input',
      props: {
        type: 'password',
        placeholder: 'Please enter password',
        clearable: true
      }
    },
    baseFormItems.phone,
    baseFormItems.level,
    baseFormItems.address,
    baseFormItems.date,
    baseFormItems.gender
  ])

  const userItem = ref<SearchFormItem>({
    label: 'Username',
    key: 'name',
    type: 'input',
    props: {
      placeholder: 'Please enter username',
      clearable: true
    }
  })

  // Control whether username field is displayed
  const showUserName = ref(true)

  // Dynamic form items list
  const dynamicFormItems = ref<SearchFormItem[]>([])

  // Dynamic form item counter (used to generate unique key)
  let dynamicItemCounter = 0

  // Cascader data
  const cascaderOptions = [
    {
      value: 'guide',
      label: 'Guide',
      children: [
        {
          value: 'disciplines',
          label: 'Standards',
          children: [
            { value: 'consistency', label: 'Consistency' },
            { value: 'feedback', label: 'Feedback' },
            { value: 'efficiency', label: 'Efficiency' },
            { value: 'controllability', label: 'Controllability' }
          ]
        }
      ]
    },
    {
      value: 'components',
      label: 'Components',
      children: [
        {
          value: 'basic',
          label: 'Basic Components',
          children: [
            { value: 'button', label: 'Button' },
            { value: 'form', label: 'Form' },
            { value: 'table', label: 'Table' }
          ]
        }
      ]
    }
  ]

  // Tree select data
  const treeSelectData = [
    {
      value: '1',
      label: 'Level 1',
      children: [
        {
          value: '1-1',
          label: 'Level 1-1',
          children: [{ value: '1-1-1', label: 'Level 1-1-1' }]
        }
      ]
    },
    {
      value: '2',
      label: 'Level 2',
      children: [
        {
          value: '2-1',
          label: 'Level 2-1',
          children: [{ value: '2-1-1', label: 'Level 2-1-1' }]
        },
        {
          value: '2-2',
          label: 'Level 2-2',
          children: [{ value: '2-2-1', label: 'Level 2-2-1' }]
        }
      ]
    }
  ]

  // Checkbox options
  const checkboxOptions = [
    { label: 'Option 1', value: 'option1' },
    { label: 'Option 2', value: 'option2' },
    { label: 'Option 3', value: 'option3' },
    { label: 'Option 4', value: 'option4' },
    { label: 'Option 5 (disabled)', value: 'option5', disabled: true }
  ]

  // Full example form configuration
  const formItemsAdvanced = computed(() => [
    ...(showUserName.value ? [userItem.value] : []),
    // Dynamic form items
    ...dynamicFormItems.value,
    {
      ...baseFormItems.phone
    },
    {
      ...baseFormItems.level,
      props: { placeholder: 'Please select level', options: levelOptions.value }
    },
    baseFormItems.address,
    baseFormItems.date,
    // Date time
    {
      label: 'Date Time',
      key: 'datetime',
      type: 'datetime',
      props: {
        style: { width: '100%' },
        placeholder: 'Please select date time',
        type: 'datetime',
        valueFormat: 'YYYY-MM-DD HH:mm:ss'
      }
    },
    {
      label: 'Date Range',
      key: 'daterange',
      type: 'datetime',
      props: {
        type: 'daterange',
        valueFormat: 'YYYY-MM-DD',
        rangeSeparator: 'to',
        startPlaceholder: 'Start date',
        endPlaceholder: 'End date'
      }
    },
    // Date time range
    {
      label: 'Date Time Range',
      key: 'datetimerange',
      type: 'datetime',
      props: {
        type: 'datetimerange',
        valueFormat: 'YYYY-MM-DD HH:mm:ss',
        rangeSeparator: 'to',
        startPlaceholder: 'Start date time',
        endPlaceholder: 'End date time'
      }
    },
    // Time select
    {
      label: 'Time Select',
      key: 'timeselect',
      type: 'timeselect',
      props: {
        placeholder: 'Please select time',
        type: 'time',
        valueFormat: 'HH:mm:ss'
      }
    },
    // Time picker
    {
      label: 'Time Picker',
      key: 'timepicker',
      type: 'timepicker',
      props: {
        style: { width: '100%' },
        placeholder: 'Please select time',
        type: 'time',
        valueFormat: 'HH:mm:ss'
      }
    },
    // Cascader
    {
      label: 'Cascader',
      key: 'cascader',
      type: 'cascader',
      props: {
        placeholder: 'Please select cascader',
        clearable: true,
        style: { width: '100%' },
        collapseTags: true,
        maxCollapseTags: 1,
        props: { multiple: true },
        options: cascaderOptions
      }
    },
    // Tree select
    {
      label: 'Tree Select',
      key: 'treeSelect',
      type: 'treeselect',
      props: {
        showCheckbox: true,
        multiple: true,
        clearable: true,
        data: treeSelectData
      }
    },
    { label: 'Slot', key: 'slots', type: 'input', placeholder: 'Please enter email' },
    {
      label: 'Render Component',
      key: 'iconSelector',
      render: () => h(ElInput, { placeholder: 'Render custom input' })
    },
    {
      label: 'Custom Component',
      key: 'customComponent',
      render: () =>
        h(
          'div',
          {
            style:
              'color: var(--art-gray-600); border: 1px solid var(--default-border-dashed); padding: 0px 15px; border-radius: 6px'
          },
          'I am a custom component'
        )
    },
    {
      label: 'Checkbox',
      key: 'checkboxgroup',
      type: 'checkboxgroup',
      span: 12,
      props: {
        options: checkboxOptions
      }
    },
    {
      ...baseFormItems.gender
    },

    {
      label: 'Enable',
      key: 'isEnabled',
      type: 'switch',
      props: {
        placeholder: 'Please select to enable'
      }
    },
    {
      label: 'Age',
      key: 'age',
      type: 'number',
      slots: {
        suffix: () => h('span', { style: 'color: #909399; font-size: 12px' }, 'years')
      }
    },
    {
      label: 'Website URL',
      key: 'website',
      type: 'input',
      placeholder: 'Please enter website name',
      slots: {
        prepend: () => h('span', 'https://'),
        append: () => h('span', '.com')
      }
    },
    {
      label: 'Event Demo',
      key: 'event',
      type: 'input',
      props: {
        placeholder: 'Type to trigger event, check console',
        clearable: true,
        prefixIcon: 'Search',
        // prefix: () => h('span', {}, '123'),
        // Events must start with "on", then camelCase the ElementPlus event name
        onInput(val: string) {
          console.log('Input event', val)
        },
        onClear() {
          console.log('Clear event')
        }
      }
    },

    {
      label: 'Multiline Input',
      key: 'remark',
      type: 'input',
      props: {
        placeholder: 'Please enter remarks',
        type: 'textarea',
        rows: 2
      }
    },
    {
      label: 'Rating',
      key: 'rate',
      type: 'rate',
      props: {
        size: 'large',
        placeholder: 'Please select rating'
      }
    },
    {
      label: 'Disabled',
      key: 'diaabled',
      type: 'input',
      placeholder: 'I am disabled',
      disabled: true // Disabled
    },
    {
      label: 'Slider',
      key: 'slider',
      type: 'slider'
      // props: {
      //   step: 10,
      //   showStops: true
      // }
    },

    {
      label: 'Hidden',
      key: 'email',
      type: 'input',
      hidden: true
    },
    // Hide based on condition
    {
      label: 'Conditional Hidden',
      key: 'systemName',
      type: 'input',
      hidden: formDataAdvanced.value.systemName === 'mac',
      placeholder: 'Type mac to hide this component'
    },
    {
      label: 'Grid Layout',
      key: 'sg1',
      type: 'input',
      span: 12,
      placeholder: 'Example: span=12 takes half width, span=24 takes full width'
    }
  ])

  /**
   * Create unified form handler function
   * @param ref Form reference
   * @param formData Form data
   * @param type Form type description
   */
  const createFormHandler = (ref: Ref<any>, formData: Record<string, any>, type: string) => ({
    reset: () => {
      console.log(`Reset ${type} form`)
      emit('reset')
    },
    search: async () => {
      await ref.value.validate()
      emit('search', formData.value)
      console.log(`${type} form data`, formData.value)
    },
    validate: () => ref.value.validate()
  })

  /**
   * Basic form handler
   */
  const basicFormHandler = computed(() =>
    createFormHandler(searchBarBasicRef, formDataBasic, 'Basic')
  )

  /**
   * Full form handler
   */
  const advancedFormHandler = computed(() =>
    createFormHandler(searchBarAdvancedRef, formDataAdvanced, 'Full')
  )

  /**
   * Handle basic form reset event
   */
  const handleBasicReset = () => basicFormHandler.value.reset()

  /**
   * Handle basic form search event
   */
  const handleBasicSearch = () => basicFormHandler.value.search()

  /**
   * Handle full form reset event
   */
  const handleAdvancedReset = () => advancedFormHandler.value.reset()

  /**
   * Handle full form search event
   */
  const handleAdvancedSearch = () => advancedFormHandler.value.search()

  /**
   * Validate full form
   */
  const advancedValidate = () => advancedFormHandler.value.validate()

  /**
   * Reset full form
   */
  const advancedReset = () => searchBarAdvancedRef.value.reset()

  /**
   * Update username field configuration
   */
  const updateUserName = (): void => {
    userItem.value = {
      ...userItem.value,
      label: 'Nickname',
      props: {
        placeholder: 'Please enter nickname'
      }
    }
  }

  /**
   * Delete username field
   */
  const deleteUserName = (): void => {
    showUserName.value = false
    formDataAdvanced.value.name = undefined
  }

  /**
   * Add form item
   */
  const addFormItem = (): void => {
    dynamicItemCounter++
    const newItem: SearchFormItem = {
      label: `Dynamic Field ${dynamicItemCounter}`,
      key: `dynamic_${dynamicItemCounter}`,
      type: 'input',
      props: {
        placeholder: `Please enter dynamic field ${dynamicItemCounter}`,
        clearable: true
      }
    }
    dynamicFormItems.value.push(newItem)
    ElMessage.success(`Added form item: ${newItem.label}`)
  }

  /**
   * Update form item (update the last dynamic form item)
   */
  const updateFormItem = (): void => {
    if (dynamicFormItems.value.length === 0) {
      ElMessage.warning('No dynamic form items to update, please add one first')
      return
    }

    const lastIndex = dynamicFormItems.value.length - 1
    const lastItem = dynamicFormItems.value[lastIndex]

    // Update the last form item configuration
    dynamicFormItems.value[lastIndex] = {
      ...lastItem,
      label: `Updated`,
      type: 'select',
      props: {
        placeholder: 'Changed to dropdown select',
        options: [
          { label: 'Option A', value: 'a' },
          { label: 'Option B', value: 'b' },
          { label: 'Option C', value: 'c' }
        ]
      }
    }

    ElMessage.success(`Updated form item: ${lastItem.label}`)
  }

  /**
   * Delete form item (delete the last dynamic form item)
   */
  const deleteFormItem = (): void => {
    if (dynamicFormItems.value.length === 0) {
      ElMessage.warning('No dynamic form items to delete')
      return
    }

    const deletedItem = dynamicFormItems.value.pop()
    if (deletedItem) {
      // Clear the corresponding form data
      delete formDataAdvanced.value[deletedItem.key as keyof AdvancedFormData]
      ElMessage.success(`Deleted form item: ${deletedItem.label}`)
    }
  }

  /**
   * Batch add form items
   */
  const batchAddFormItems = (): void => {
    const batchItems: SearchFormItem[] = [
      {
        label: 'Company Name',
        key: `company_${++dynamicItemCounter}`,
        type: 'input',
        props: {
          placeholder: 'Please enter company name',
          clearable: true
        }
      },
      {
        label: 'Department',
        key: `department_${++dynamicItemCounter}`,
        type: 'select',
        props: {
          placeholder: 'Please select department',
          options: [
            { label: 'Technology', value: 'tech' },
            { label: 'Product', value: 'product' },
            { label: 'Operations', value: 'operation' }
          ]
        }
      },
      {
        label: 'Join Date',
        key: `joinDate_${++dynamicItemCounter}`,
        type: 'datetime',
        props: {
          style: { width: '100%' },
          placeholder: 'Please select join date',
          type: 'date',
          valueFormat: 'YYYY-MM-DD'
        }
      }
    ]

    dynamicFormItems.value.push(...batchItems)
    ElMessage.success(`Batch added ${batchItems.length} form items`)
  }

  /**
   * Reset dynamic form items
   */
  const resetDynamicItems = (): void => {
    if (dynamicFormItems.value.length === 0) {
      ElMessage.info('No dynamic form items currently')
      return
    }

    // Clear all dynamic form items data
    dynamicFormItems.value.forEach((item) => {
      delete formDataAdvanced.value[item.key as keyof AdvancedFormData]
    })

    dynamicFormItems.value = []
    dynamicItemCounter = 0
    ElMessage.success('Reset all dynamic form items')
  }
</script>
