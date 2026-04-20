<template>
  <ElDialog
    :title="dialogTitle"
    :model-value="visible"
    @update:model-value="handleCancel"
    width="860px"
    align-center
    class="menu-dialog"
    @closed="handleClosed"
  >
    <ArtForm
      ref="formRef"
      v-model="form"
      :items="formItems"
      :rules="rules"
      :span="width > 640 ? 12 : 24"
      :gutter="20"
      label-width="100px"
      :show-reset="false"
      :show-submit="false"
    >
      <template #menuType>
        <ElRadioGroup v-model="form.menuType" :disabled="disableMenuType">
          <ElRadioButton value="menu" label="menu">Menu</ElRadioButton>
          <ElRadioButton value="button" label="button">Button</ElRadioButton>
        </ElRadioGroup>
      </template>
    </ArtForm>

    <template #footer>
      <span class="dialog-footer">
        <ElButton @click="handleCancel">Cancel</ElButton>
        <ElButton type="primary" @click="handleSubmit">Confirm</ElButton>
      </span>
    </template>
  </ElDialog>
</template>

<script setup lang="ts">
  import type { FormRules } from 'element-plus'
  import { ElIcon, ElTooltip } from 'element-plus'
  import { QuestionFilled } from '@element-plus/icons-vue'
  import { formatMenuTitle } from '@/utils/router'
  import type { AppRouteRecord } from '@/types/router'
  import type { FormItem } from '@/components/core/forms/ArtForm.vue'
  import ArtForm from '@/components/core/forms/ArtForm.vue'
  import { useWindowSize } from '@vueuse/core'

  const { width } = useWindowSize()

  /**
   * Create label with tooltip
   * @param label Label text
   * @param tooltip Tooltip text
   * @returns Render function
   */
  const createLabelTooltip = (label: string, tooltip: string) => {
    return () =>
      h('span', { class: 'flex items-center' }, [
        h('span', label),
        h(
          ElTooltip,
          {
            content: tooltip,
            placement: 'top'
          },
          () => h(ElIcon, { class: 'ml-0.5 cursor-help' }, () => h(QuestionFilled))
        )
      ])
  }

  interface MenuFormData {
    id: number
    name: string
    path: string
    label: string
    component: string
    icon: string
    isEnable: boolean
    sort: number
    isMenu: boolean
    keepAlive: boolean
    isHide: boolean
    isHideTab: boolean
    link: string
    isIframe: boolean
    showBadge: boolean
    showTextBadge: string
    fixedTab: boolean
    activePath: string
    roles: string[]
    isFullPage: boolean
    authName: string
    authLabel: string
    authIcon: string
    authSort: number
  }

  interface Props {
    visible: boolean
    editData?: AppRouteRecord | any
    type?: 'menu' | 'button'
    lockType?: boolean
  }

  interface Emits {
    (e: 'update:visible', value: boolean): void
    (e: 'submit', data: MenuFormData): void
  }

  const props = withDefaults(defineProps<Props>(), {
    visible: false,
    type: 'menu',
    lockType: false
  })

  const emit = defineEmits<Emits>()

  const formRef = ref()
  const isEdit = ref(false)

  const form = reactive<MenuFormData & { menuType: 'menu' | 'button' }>({
    menuType: 'menu',
    id: 0,
    name: '',
    path: '',
    label: '',
    component: '',
    icon: '',
    isEnable: true,
    sort: 1,
    isMenu: true,
    keepAlive: true,
    isHide: false,
    isHideTab: false,
    link: '',
    isIframe: false,
    showBadge: false,
    showTextBadge: '',
    fixedTab: false,
    activePath: '',
    roles: [],
    isFullPage: false,
    authName: '',
    authLabel: '',
    authIcon: '',
    authSort: 1
  })

  const rules = reactive<FormRules>({
    name: [
      { required: true, message: 'Please enter menu name', trigger: 'blur' },
      { min: 2, max: 20, message: 'Length should be 2 to 20 characters', trigger: 'blur' }
    ],
    path: [{ required: true, message: 'Please enter route path', trigger: 'blur' }],
    label: [{ required: true, message: 'Please enter permission identifier', trigger: 'blur' }],
    authName: [{ required: true, message: 'Please enter permission name', trigger: 'blur' }],
    authLabel: [{ required: true, message: 'Please enter permission identifier', trigger: 'blur' }]
  })

  /**
   * Form items configuration
   */
  const formItems = computed<FormItem[]>(() => {
    const baseItems: FormItem[] = [{ label: 'Menu Type', key: 'menuType', span: 24 }]

    // Switch component span: 12 on small screen, 6 on large screen
    const switchSpan = width.value < 640 ? 12 : 6

    if (form.menuType === 'menu') {
      return [
        ...baseItems,
        { label: 'Menu Name', key: 'name', type: 'input', props: { placeholder: 'Menu name' } },
        {
          label: createLabelTooltip(
            'Route Path',
            'First level: absolute path starting with / (e.g. /dashboard)\nSecond level and below: relative path (e.g. console, user)'
          ),
          key: 'path',
          type: 'input',
          props: { placeholder: 'e.g.: /dashboard or console' }
        },
        {
          label: 'Permission ID',
          key: 'label',
          type: 'input',
          props: { placeholder: 'e.g.: User' }
        },
        {
          label: createLabelTooltip(
            'Component Path',
            'First level parent menu: enter /index/index\nSpecific page: enter component path (e.g. /system/user)\nDirectory menu: leave empty'
          ),
          key: 'component',
          type: 'input',
          props: { placeholder: 'e.g.: /system/user or leave empty' }
        },
        { label: 'Icon', key: 'icon', type: 'input', props: { placeholder: 'e.g.: ri:user-line' } },
        {
          label: createLabelTooltip(
            'Role Permissions',
            'Frontend permission mode only: configure role identifiers (e.g. R_SUPER, R_ADMIN)\nBackend permission mode: no configuration needed'
          ),
          key: 'roles',
          type: 'inputtag',
          props: { placeholder: 'Enter role identifier and press Enter, e.g.: R_SUPER' }
        },
        {
          label: 'Menu Sort',
          key: 'sort',
          type: 'number',
          props: { min: 1, controlsPosition: 'right', style: { width: '100%' } }
        },
        {
          label: 'External Link',
          key: 'link',
          type: 'input',
          props: { placeholder: 'e.g.: https://www.example.com' }
        },
        {
          label: 'Text Badge',
          key: 'showTextBadge',
          type: 'input',
          props: { placeholder: 'e.g.: New, Hot' }
        },
        {
          label: createLabelTooltip(
            'Active Path',
            'For detail pages and hidden menus, specify the parent menu path to highlight\nExample: User detail page highlights "User Management" menu'
          ),
          key: 'activePath',
          type: 'input',
          props: { placeholder: 'e.g.: /system/user' }
        },
        { label: 'Enabled', key: 'isEnable', type: 'switch', span: switchSpan },
        { label: 'Page Cache', key: 'keepAlive', type: 'switch', span: switchSpan },
        { label: 'Hide Menu', key: 'isHide', type: 'switch', span: switchSpan },
        { label: 'Is Iframe', key: 'isIframe', type: 'switch', span: switchSpan },
        { label: 'Show Badge', key: 'showBadge', type: 'switch', span: switchSpan },
        { label: 'Fixed Tab', key: 'fixedTab', type: 'switch', span: switchSpan },
        { label: 'Hide Tab', key: 'isHideTab', type: 'switch', span: switchSpan },
        { label: 'Full Page', key: 'isFullPage', type: 'switch', span: switchSpan }
      ]
    } else {
      return [
        ...baseItems,
        {
          label: 'Permission Name',
          key: 'authName',
          type: 'input',
          props: { placeholder: 'e.g.: Add, Edit, Delete' }
        },
        {
          label: 'Permission ID',
          key: 'authLabel',
          type: 'input',
          props: { placeholder: 'e.g.: add, edit, delete' }
        },
        {
          label: 'Permission Sort',
          key: 'authSort',
          type: 'number',
          props: { min: 1, controlsPosition: 'right', style: { width: '100%' } }
        }
      ]
    }
  })

  const dialogTitle = computed(() => {
    const type = form.menuType === 'menu' ? 'Menu' : 'Button'
    return isEdit.value ? `Edit ${type}` : `New ${type}`
  })

  /**
   * Whether to disable menu type switch
   */
  const disableMenuType = computed(() => {
    if (isEdit.value) return true
    if (!isEdit.value && form.menuType === 'menu' && props.lockType) return true
    return false
  })

  /**
   * Reset form data
   */
  const resetForm = (): void => {
    formRef.value?.reset()
    form.menuType = 'menu'
  }

  /**
   * Load form data (edit mode)
   */
  const loadFormData = (): void => {
    if (!props.editData) return

    isEdit.value = true

    if (form.menuType === 'menu') {
      const row = props.editData
      form.id = row.id || 0
      form.name = formatMenuTitle(row.meta?.title || '')
      form.path = row.path || ''
      form.label = row.name || ''
      form.component = row.component || ''
      form.icon = row.meta?.icon || ''
      form.sort = row.meta?.sort || 1
      form.isMenu = row.meta?.isMenu ?? true
      form.keepAlive = row.meta?.keepAlive ?? false
      form.isHide = row.meta?.isHide ?? false
      form.isHideTab = row.meta?.isHideTab ?? false
      form.isEnable = row.meta?.isEnable ?? true
      form.link = row.meta?.link || ''
      form.isIframe = row.meta?.isIframe ?? false
      form.showBadge = row.meta?.showBadge ?? false
      form.showTextBadge = row.meta?.showTextBadge || ''
      form.fixedTab = row.meta?.fixedTab ?? false
      form.activePath = row.meta?.activePath || ''
      form.roles = row.meta?.roles || []
      form.isFullPage = row.meta?.isFullPage ?? false
    } else {
      const row = props.editData
      form.authName = row.title || ''
      form.authLabel = row.authMark || ''
      form.authIcon = row.icon || ''
      form.authSort = row.sort || 1
    }
  }

  /**
   * Submit form
   */
  const handleSubmit = async (): Promise<void> => {
    if (!formRef.value) return

    try {
      await formRef.value.validate()
      emit('submit', { ...form })
      ElMessage.success(`${isEdit.value ? 'Edit' : 'Add'} successful`)
      handleCancel()
    } catch {
      ElMessage.error('Form validation failed, please check your input')
    }
  }

  /**
   * Cancel operation
   */
  const handleCancel = (): void => {
    emit('update:visible', false)
  }

  /**
   * Callback after dialog closed
   */
  const handleClosed = (): void => {
    resetForm()
    isEdit.value = false
  }

  /**
   * Watch dialog visibility
   */
  watch(
    () => props.visible,
    (newVal) => {
      if (newVal) {
        form.menuType = props.type
        nextTick(() => {
          if (props.editData) {
            loadFormData()
          }
        })
      }
    }
  )

  /**
   * Watch menu type change
   */
  watch(
    () => props.type,
    (newType) => {
      if (props.visible) {
        form.menuType = newType
      }
    }
  )
</script>
