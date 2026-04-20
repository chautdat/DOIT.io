<template>
  <div class="pb-5">
    <h2 class="mb-1 text-lg font-medium">Form component demo</h2>

    <ElCard class="art-card-xs">
      <ArtForm
        ref="formRef"
        v-model="formData"
        :items="formItems"
        :rules="formRules"
        :defaultExpanded="true"
        :labelWidth="labelWidth"
        :labelPosition="labelPosition"
        :span="span"
        :gutter="gutter"
        @reset="handleReset"
        @submit="handleSubmit"
      >
        <template #slots>
          <ElInput v-model="formData.slots" placeholder="I am a slot-rendered component" />
        </template>
      </ArtForm>
    </ElCard>

    <div class="art-card p-5 !rounded-lg mt-5">
      <pre><code>{{ formData }}</code></pre>
    </div>

    <div class="mt-3.5">
      <h3 class="mb-2 text-base font-medium">Dynamic form actions</h3>
      <ElSpace wrap class="mb-3">
        <ElButton @click="getLevelOptions"> Fetch user level data </ElButton>
        <ElButton @click="addFormItem"> Add form item </ElButton>
        <ElButton @click="updateFormItem"> Update form item </ElButton>
        <ElButton @click="deleteFormItem"> Delete form item </ElButton>
        <ElButton @click="batchAddFormItems"> Batch add </ElButton>
        <ElButton @click="resetDynamicItems"> Reset dynamic items </ElButton>
      </ElSpace>

      <h3 class="mb-2 text-base font-medium">Other actions</h3>
      <ElSpace wrap>
        <ElButton @click="validateForm"> Validate form </ElButton>
        <ElButton @click="resetForm"> Reset </ElButton>
        <ElButton v-if="showUserName" @click="updateUserName"> Change username </ElButton>
        <ElButton v-if="showUserName" @click="deleteUserName"> Delete username </ElButton>
        <ElButton @click="labelWidth = 120"> Change label width </ElButton>
        <ElButton @click="span = 8"> Set components per row </ElButton>
        <ElButton @click="gutter = 50"> Change gutter </ElButton>
        <ElButton @click="labelPosition = 'left'"> Label left aligned </ElButton>
        <ElButton @click="labelPosition = 'right'"> Label right aligned </ElButton>
        <ElButton @click="labelPosition = 'top'"> Label top aligned </ElButton>
      </ElSpace>
    </div>

    <ElDialog v-model="dialogVisible">
      <img w-full :src="dialogImageUrl" alt="Preview Image" class="w-full h-auto" />
    </ElDialog>
  </div>
</template>

<script setup lang="ts">
  import ArtWangEditor from '@/components/core/forms/ArtWangEditor.vue'
  import { SearchFormItem } from '@/components/core/forms/ArtSearchBar.vue'
  import { ElMessage, ElUpload, ElButton, ElIcon, ElInput } from 'element-plus'
  import type { UploadFile, UploadFiles, UploadUserFile } from 'element-plus'
  import { Plus } from '@element-plus/icons-vue'

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

  interface FormData {
    name?: string
    phone?: string
    level?: string
    address?: string
    slots?: string
    date?: string
    daterange?: string[]
    cascader?: string[]
    checkboxgroup?: string[]
    userGender?: string
    iconSelector?: string
    status?: boolean
    systemName?: string
    fileUpload: UploadUserFile[]
    imageUpload: UploadUserFile[]
    multipleFiles: UploadUserFile[]
    richTextContent: string
  }

  const emit = defineEmits<Emits>()

  const FETCH_DELAY = 500

  const formRef = ref()
  const dialogVisible = ref(false)
  const dialogImageUrl = ref('')

  const formData = ref<FormData>({
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
    systemName: undefined,
    fileUpload: [],
    imageUpload: [],
    multipleFiles: [],
    richTextContent: ''
  })

  const formRules = {
    name: [{ required: true, message: 'Please enter a username', trigger: 'blur' }]
  }

  const labelWidth = ref(100)
  const labelPosition = ref<'right' | 'left' | 'top'>('right')
  const span = ref(6)
  const gutter = ref(12)

  const levelOptions = ref<OptionItem[]>([])

  const LEVEL_OPTIONS: OptionItem[] = [
    { label: 'Standard User', value: 'normal' },
    { label: 'VIP User', value: 'vip' },
    { label: 'Premium VIP', value: 'svip' },
    { label: 'Enterprise User', value: 'enterprise', disabled: true }
  ]

  const GENDER_OPTIONS: OptionItem[] = [
    { label: 'Male', value: '1' },
    { label: 'Female', value: '2' }
  ]

  const DATE_SHORTCUTS = [
    { text: 'Today', value: new Date() },
    { text: 'Yesterday', value: () => new Date(Date.now() - 86400000) },
    { text: 'One week ago', value: () => new Date(Date.now() - 604800000) }
  ]

  const fetchLevelOptions = (): Promise<OptionItem[]> => {
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve(LEVEL_OPTIONS)
      }, FETCH_DELAY)
    })
  }

  const getLevelOptions = async (): Promise<void> => {
    levelOptions.value = await fetchLevelOptions()
    if (levelOptions.value.length) {
      ElMessage.success('Data fetched successfully')
    }
  }

  interface FormItemConfig {
    label: string
    key: string
    type: string
    placeholder?: string
    props?: Record<string, any>
    [key: string]: any
  }

  const createFormItem = (config: FormItemConfig) => config

  const baseFormItems = {
    username: createFormItem({
      label: 'Username',
      key: 'name',
      type: 'input',
      placeholder: 'Please enter a username',
      clearable: true
    }),
    phone: createFormItem({
      label: 'Phone',
      key: 'phone',
      type: 'input',
      props: { placeholder: 'Please enter a phone number', maxlength: '11' }
    }),
    level: createFormItem({
      label: 'User level',
      key: 'level',
      type: 'select',
      props: {
        placeholder: 'Please select a level',
        options: LEVEL_OPTIONS
      }
    }),
    address: createFormItem({
      label: 'Address',
      key: 'address',
      type: 'input',
      placeholder: 'Please enter an address'
    }),
    date: createFormItem({
      label: 'Date',
      key: 'date',
      type: 'datetime',
      props: {
        style: { width: '100%' },
        placeholder: 'Please select a date',
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

  const userItem = ref<SearchFormItem>({
    label: 'Username',
    key: 'name',
    type: 'input',
    props: {
      placeholder: 'Please enter a username',
      clearable: true
    }
  })

  const showUserName = ref(true)

  const dynamicFormItems = ref<SearchFormItem[]>([])

  let dynamicItemCounter = 0

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
      label: 'component',
      children: [
        {
          value: 'basic',
          label: 'Basic components',
          children: [
            { value: 'button', label: 'Button' },
            { value: 'form', label: 'Form' },
            { value: 'table', label: 'Table' }
          ]
        }
      ]
    }
  ]

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

  const checkboxOptions = [
    { label: 'Option 1', value: 'option1' },
    { label: 'Option 2', value: 'option2' },
    { label: 'Option 3', value: 'option3' },
    { label: 'Option 4', value: 'option4' },
    { label: 'Option 5 (disabled)', value: 'option5', disabled: true }
  ]

  const formItems = computed(() => [
    ...(showUserName.value ? [userItem.value] : []),
    ...dynamicFormItems.value,
    {
      ...baseFormItems.phone
    },
    {
      ...baseFormItems.level,
      props: { placeholder: 'Please select a level', options: levelOptions.value }
    },
    baseFormItems.address,
    baseFormItems.date,
    {
      label: 'Date and time',
      key: 'datetime',
      type: 'datetime',
      props: {
        style: { width: '100%' },
        placeholder: 'Please select a date and time',
        type: 'datetime',
        valueFormat: 'YYYY-MM-DD HH:mm:ss'
      }
    },
    {
      label: 'Date range',
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
    {
      label: 'Date and time range',
      key: 'datetimerange',
      type: 'datetime',
      props: {
        type: 'datetimerange',
        valueFormat: 'YYYY-MM-DD HH:mm:ss',
        rangeSeparator: 'to',
        startPlaceholder: 'Start date',
        endPlaceholder: 'End date'
      }
    },
    {
      label: 'Time selection',
      key: 'timeselect',
      type: 'timeselect',
      props: {
        placeholder: 'Please select a time',
        type: 'time',
        valueFormat: 'HH:mm:ss'
      }
    },
    {
      label: 'Time picker',
      key: 'timepicker',
      type: 'timepicker',
      props: {
        style: { width: '100%' },
        placeholder: 'Please select a time',
        type: 'time',
        valueFormat: 'HH:mm:ss'
      }
    },
    {
      label: 'Cascader',
      key: 'cascader',
      type: 'cascader',
      props: {
        placeholder: 'Please select a cascader',
        clearable: true,
        style: { width: '100%' },
        collapseTags: true,
        maxCollapseTags: 1,
        props: { multiple: true },
        options: cascaderOptions
      }
    },
    {
      label: 'Tree selector',
      key: 'treeSelect',
      type: 'treeselect',
      props: {
        showCheckbox: true,
        multiple: true,
        clearable: true,
        data: treeSelectData
      }
    },
    { label: 'Slot', key: 'slots', type: 'input', placeholder: 'Please enter an email' },
    {
      label: 'Rendered component',
      key: 'iconSelector',
      render: () => h(ElInput, { placeholder: 'Render custom input' })
    },
    {
      label: 'Custom component',
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
      label: 'Enabled',
      key: 'isEnabled',
      type: 'switch',
      props: {
        placeholder: 'Please select whether to enable'
      }
    },
    {
      label: 'age',
      key: 'age',
      type: 'number',
      slots: {
        suffix: () => h('span', { style: 'color: #909399; font-size: 12px' }, 'years')
      }
    },
    {
      label: 'Website address',
      key: 'website',
      type: 'input',
      placeholder: 'Please enter a website name',
      slots: {
        prepend: () => h('span', 'https://'),
        append: () => h('span', '.com')
      }
    },
    {
      label: 'Event demo',
      key: 'event',
      type: 'input',
      props: {
        placeholder: 'Type to trigger events, then check the console',
        clearable: true,
        prefixIcon: 'Search',
        // prefix: () => h('span', {}, '123'),
        onInput(val: string) {
          console.log('Input event', val)
        },
        onClear() {
          console.log('Clear event')
        }
      }
    },

    {
      label: 'Multiline input',
      key: 'remark',
      type: 'input',
      props: {
        placeholder: 'Please enter notes',
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
        placeholder: 'Please selectRating'
      }
    },
    {
      label: 'Disabled',
      key: 'diaabled',
      type: 'input',
      placeholder: 'I am disabled',
      disabled: true
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
    {
      label: 'Conditionally hidden',
      key: 'systemName',
      type: 'input',
      hidden: formData.value.systemName === 'mac',
      placeholder: 'enter a value to hide the component on Mac'
    },
    {
      label: 'Grid layout',
      key: 'sg1',
      type: 'input',
      span: 12,
      placeholder: 'Example: `span=12` takes half the container width, and `span=24` spans the full width.'
    },
    {
      label: 'File upload',
      key: 'multipleFiles',
      span: 12,
      render: () =>
        h(
          ElUpload,
          {
            multiple: true,
            limit: 5,
            action: '#',
            autoUpload: false,
            showFileList: true,
            // accept: '.pdf,.doc,.docx,.txt',
            beforeUpload: (file: File) => {
              console.log('Preparing to upload file:', file.name)
              return true
            },
            onChange: (file: UploadFile, fileList: UploadFiles) => {
              console.log('Multiple files changed:', file, fileList)
              formData.value.multipleFiles = fileList as UploadUserFile[]
            },
            onRemove: (file: UploadFile, fileList: UploadFiles) => {
              console.log('Delete file:', file, fileList)
              formData.value.multipleFiles = fileList as UploadUserFile[]
            },
            onExceed: (files: File[], fileList: UploadUserFile[]) => {
              ElMessage.warning(
                `You can upload at most 5 files. ${files.length + fileList.length} files are currently selected.`
              )
            }
          },
          {
            default: () => [h(ElButton, { type: 'primary' }, () => 'Upload')]
          }
        )
    },
    {
      label: 'Image upload',
      key: 'imageUpload',
      span: 12,
      render: () =>
        h(
          ElUpload,
          {
            accept: '.jpg,.jpeg,.png,.gif,.webp',
            limit: 4,
            action: '#',
            autoUpload: false,
            showFileList: true,
            listType: 'picture-card',
            beforeUpload: (file: File) => {
              const isImage = file.type.startsWith('image/')
              const isLt2M = file.size / 1024 / 1024 < 2
              if (!isImage) {
                ElMessage.error('Only image files can be uploaded!')
                return false
              }
              if (!isLt2M) {
                ElMessage.error('Image size cannot exceed 2 MB!')
                return false
              }
              return true
            },
            onChange: (file: UploadFile, fileList: UploadFiles) => {
              console.log('Image changed:', file, fileList)
              formData.value.imageUpload = fileList as UploadUserFile[]
            },
            onRemove: (file: UploadFile, fileList: UploadFiles) => {
              console.log('Delete image:', file, fileList)
              formData.value.imageUpload = fileList as UploadUserFile[]
            },
            onPreview: (file: UploadFile) => {
              dialogImageUrl.value = file.url || ''
              dialogVisible.value = true
            }
          },
          {
            default: () => [h(ElIcon, { type: 'primary' }, () => h(Plus))]
          }
        )
    },
    {
      label: 'Rich text editor',
      key: 'richTextContent',
      span: 24,
      render: () =>
        h(ArtWangEditor, {
          modelValue: formData.value.richTextContent,
          height: '300px',
          placeholder: 'Please enter rich text content...',
          'onUpdate:modelValue': (value: string) => {
            formData.value.richTextContent = value
            console.log('Rich text content changed:', value)
          },
          toolbarKeys: [
            'headerSelect',
            'bold',
            'italic',
            'underline',
            '|',
            'bulletedList',
            'numberedList',
            '|',
            'insertLink',
            'insertImage',
            '|',
            'undo',
            'redo'
          ]
        })
    }
  ])

  const handleReset = (): void => {
    console.log('ResetForm')
    emit('reset')
  }

  const handleSubmit = async (params: Record<string, any>): Promise<void> => {
    await formRef.value.validate()
    emit('search', params)
    console.log('FormData', params)
  }

  const validateForm = () => formRef.value.validate()

  const resetForm = () => formRef.value.reset()

  const updateUserName = (): void => {
    userItem.value = {
      ...userItem.value,
      label: 'Nickname',
      props: {
        placeholder: 'Please enter a nickname'
      }
    }
  }

  const deleteUserName = (): void => {
    showUserName.value = false
    formData.value.name = undefined
  }

  const addFormItem = (): void => {
    dynamicItemCounter++
    const newItem: SearchFormItem = {
      label: `Dynamic field${dynamicItemCounter}`,
      key: `dynamic_${dynamicItemCounter}`,
      type: 'input',
      props: {
        placeholder: `Please enterDynamic field${dynamicItemCounter}`,
        clearable: true
      }
    }
    dynamicFormItems.value.push(newItem)
    ElMessage.success(`Added form item: ${newItem.label}`)
  }

  const updateFormItem = (): void => {
    if (dynamicFormItems.value.length === 0) {
      ElMessage.warning('No dynamic form items can be updated. Add one first.')
      return
    }

    const lastIndex = dynamicFormItems.value.length - 1
    const lastItem = dynamicFormItems.value[lastIndex]

    dynamicFormItems.value[lastIndex] = {
      ...lastItem,
      label: `Updated`,
      type: 'select',
      props: {
        placeholder: 'Change to a dropdown select',
        options: [
          { label: 'Option A', value: 'a' },
          { label: 'Option B', value: 'b' },
          { label: 'Option C', value: 'c' }
        ]
      }
    }

    ElMessage.success(`Updated form item: ${lastItem.label}`)
  }

  const deleteFormItem = (): void => {
    if (dynamicFormItems.value.length === 0) {
      ElMessage.warning('No dynamic form items can be deleted.')
      return
    }

    const deletedItem = dynamicFormItems.value.pop()
    if (deletedItem) {
      delete formData.value[deletedItem.key as keyof FormData]
      ElMessage.success(`Deleted form item: ${deletedItem.label}`)
    }
  }

  const batchAddFormItems = (): void => {
    const batchItems: SearchFormItem[] = [
      {
        label: 'Company name',
        key: `company_${++dynamicItemCounter}`,
        type: 'input',
        props: {
          placeholder: 'Please enter a company name',
          clearable: true
        }
      },
      {
        label: 'Department',
        key: `department_${++dynamicItemCounter}`,
        type: 'select',
        props: {
          placeholder: 'Please select a department',
          options: [
            { label: 'Engineering', value: 'tech' },
            { label: 'Product', value: 'product' },
            { label: 'Operations', value: 'operation' }
          ]
        }
      },
      {
        label: 'Hire date',
        key: `joinDate_${++dynamicItemCounter}`,
        type: 'datetime',
        props: {
          style: { width: '100%' },
          placeholder: 'Please select a hire date',
          type: 'date',
          valueFormat: 'YYYY-MM-DD'
        }
      }
    ]

    dynamicFormItems.value.push(...batchItems)
    ElMessage.success(`Batch added ${batchItems.length} form items`)
  }

  const resetDynamicItems = (): void => {
    if (dynamicFormItems.value.length === 0) {
      ElMessage.info('There are no active form items')
      return
    }

    dynamicFormItems.value.forEach((item) => {
      delete formData.value[item.key as keyof FormData]
    })

    dynamicFormItems.value = []
    dynamicItemCounter = 0
    ElMessage.success('Reset all active form items')
  }
</script>
