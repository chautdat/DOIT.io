<!-- Table Search Component -->
<!-- Supports common form components, custom components, slots, validation, hidden form items -->
<!-- Same usage as ElementPlus official documentation, just put attributes in props -->
<template>
  <section class="art-search-bar art-card-xs" :class="{ 'is-expanded': isExpanded }">
    <ElForm
      ref="formRef"
      :model="modelValue"
      :label-position="labelPosition"
      v-bind="{ ...$attrs }"
    >
      <ElRow :gutter="gutter">
        <ElCol
          v-for="item in visibleFormItems"
          :key="item.key"
          :xs="getColSpan(item.span, 'xs')"
          :sm="getColSpan(item.span, 'sm')"
          :md="getColSpan(item.span, 'md')"
          :lg="getColSpan(item.span, 'lg')"
          :xl="getColSpan(item.span, 'xl')"
        >
          <ElFormItem
            :prop="item.key"
            :label-width="item.label ? item.labelWidth || labelWidth : undefined"
          >
            <template #label v-if="item.label">
              <component v-if="typeof item.label !== 'string'" :is="item.label" />
              <span v-else>{{ item.label }}</span>
            </template>
            <slot :name="item.key" :item="item" :modelValue="modelValue">
              <component
                :is="getComponent(item)"
                :model-value="getFieldValue(item.key)"
                @update:model-value="setFieldValue(item.key, $event)"
                v-bind="getProps(item)"
              >
                <template v-if="item.type === 'select' && getProps(item)?.options">
                  <ElOption
                    v-for="option in getProps(item).options"
                    v-bind="option"
                    :key="option.value"
                  />
                </template>

                <template v-if="item.type === 'checkboxgroup' && getProps(item)?.options">
                  <ElCheckbox
                    v-for="option in getProps(item).options"
                    v-bind="option"
                    :key="option.value"
                  />
                </template>

                <template v-if="item.type === 'radiogroup' && getProps(item)?.options">
                  <ElRadio
                    v-for="option in getProps(item).options"
                    v-bind="option"
                    :key="option.value"
                  />
                </template>

                <template v-for="(slotFn, slotName) in getSlots(item)" :key="slotName" #[slotName]>
                  <component :is="slotFn" />
                </template>
              </component>
            </slot>
          </ElFormItem>
        </ElCol>
        <ElCol :xs="24" :sm="24" :md="span" :lg="span" :xl="span" class="action-column">
          <div class="action-buttons-wrapper" :style="actionButtonsStyle">
            <div class="form-buttons">
              <ElButton v-if="showReset" class="reset-button" @click="handleReset" v-ripple>
                {{ t('table.searchBar.reset') }}
              </ElButton>
              <ElButton
                v-if="showSearch"
                type="primary"
                class="search-button"
                @click="handleSearch"
                v-ripple
                :disabled="disabledSearch"
              >
                {{ t('table.searchBar.search') }}
              </ElButton>
            </div>
            <div v-if="shouldShowExpandToggle" class="filter-toggle" @click="toggleExpand">
              <span>{{ expandToggleText }}</span>
              <div class="icon-wrapper">
                <ElIcon>
                  <ArrowUpBold v-if="isExpanded" />
                  <ArrowDownBold v-else />
                </ElIcon>
              </div>
            </div>
          </div>
        </ElCol>
      </ElRow>
    </ElForm>
  </section>
</template>

<script setup lang="ts">
  import { ArrowUpBold, ArrowDownBold } from '@element-plus/icons-vue'
  import { useWindowSize } from '@vueuse/core'
  import { useI18n } from 'vue-i18n'
  import { toRaw, type Component } from 'vue'
  import {
    ElCascader,
    ElCheckbox,
    ElCheckboxGroup,
    ElDatePicker,
    ElInput,
    ElInputTag,
    ElInputNumber,
    ElRadioGroup,
    ElRate,
    ElSelect,
    ElSlider,
    ElSwitch,
    ElTimePicker,
    ElTimeSelect,
    ElTreeSelect,
    type FormInstance
  } from 'element-plus'
  import { calculateResponsiveSpan, type ResponsiveBreakpoint } from '@/utils/form/responsive'

  defineOptions({ name: 'ArtSearchBar' })

  const componentMap = {
    input: ElInput, // Input
    inputTag: ElInputTag, // Tag input
    number: ElInputNumber, // Number input
    select: ElSelect, // Select
    switch: ElSwitch, // Switch
    checkbox: ElCheckbox, // Checkbox
    checkboxgroup: ElCheckboxGroup, // Checkbox group
    radiogroup: ElRadioGroup, // Radio group
    date: ElDatePicker, // Date picker
    daterange: ElDatePicker, // Date range picker
    datetime: ElDatePicker, // DateTime picker
    datetimerange: ElDatePicker, // DateTime range picker
    rate: ElRate, // Rating
    slider: ElSlider, // Slider
    cascader: ElCascader, // Cascader
    timepicker: ElTimePicker, // Time picker
    timeselect: ElTimeSelect, // Time select
    treeselect: ElTreeSelect // Tree select
  }

  const { width } = useWindowSize()
  const { t } = useI18n()
  const isMobile = computed(() => width.value < 500)

  const formInstance = useTemplateRef<FormInstance>('formRef')

  // Form item configuration
  export interface SearchFormItem {
    /** Form item unique identifier */
    key: string
    /** Form item label text or custom render function */
    label: string | (() => VNode) | Component
    /** Form item label width, overrides Form's labelWidth */
    labelWidth?: string | number
    /** Form item type, supports predefined component types */
    type?: keyof typeof componentMap | string
    /** Custom render function or component (higher priority than type) */
    render?: (() => VNode) | Component
    /** Whether to hide this form item */
    hidden?: boolean
    /** Column width occupied by form item, based on 24 grid system */
    span?: number
    /** Options data for select, checkbox-group, radio-group, etc. */
    options?: Record<string, any>
    /** Properties passed to form item component */
    props?: Record<string, any>
    /** Slot configuration for form item */
    slots?: Record<string, (() => any) | undefined>
    /** Form item placeholder text */
    placeholder?: string
    /** For more properties, refer to ElementPlus official documentation */
  }

  // Form configuration
  interface SearchBarProps {
    /** Form data */
    items: SearchFormItem[]
    /** Column width (based on 24 grid layout) */
    span?: number
    /** Form control gap */
    gutter?: number
    /** Expand/collapse */
    isExpand?: boolean
    /** Default expanded state (only effective when showExpand is true and isExpand is false) */
    defaultExpanded?: boolean
    /** Form label position */
    labelPosition?: 'left' | 'right' | 'top'
    /** Label width */
    labelWidth?: string | number
    /** Whether to show expand/collapse */
    showExpand?: boolean
    /** Button left alignment limit (when form items <= this value) */
    buttonLeftLimit?: number
    /** Whether to show reset button */
    showReset?: boolean
    /** Whether to show search button */
    showSearch?: boolean
    /** Whether to disable search button */
    disabledSearch?: boolean
    /** Whether to sanitize empty values on search */
    sanitizeOutput?: Partial<SanitizeOutputOptions>
  }

  interface SanitizeOutputOptions {
    /** Remove empty strings */
    removeEmptyString: boolean
    /** Remove empty arrays */
    removeEmptyArray: boolean
    /** Remove objects that are empty after sanitization */
    removeEmptyObject: boolean
    /** Remove empty rich text placeholder content, like <p><br></p> */
    removeEmptyRichText: boolean
    /** Keep number 0 as valid filter value */
    keepZero: boolean
    /** Keep false as valid filter value */
    keepFalse: boolean
  }

  const props = withDefaults(defineProps<SearchBarProps>(), {
    items: () => [],
    span: 6,
    gutter: 12,
    isExpand: false,
    labelPosition: 'right',
    labelWidth: '70px',
    showExpand: true,
    defaultExpanded: false,
    buttonLeftLimit: 2,
    showReset: true,
    showSearch: true,
    disabledSearch: false,
    sanitizeOutput: () => ({})
  })

  interface SearchBarEmits {
    reset: []
    search: [Record<string, any>]
  }

  const emit = defineEmits<SearchBarEmits>()

  const modelValue = defineModel<Record<string, any>>({ default: {} })
  const initialModelValue = ref<Record<string, any>>({})

  // Save form snapshot at component initialization, used for reset to restore default filter conditions.
  const cloneModelValue = (value: Record<string, any> | undefined) => {
    if (!value) return {}

    const deepClone = (source: unknown): unknown => {
      if (Array.isArray(source)) {
        return source.map((item) => deepClone(item))
      }

      if (source && typeof source === 'object') {
        const rawSource = toRaw(source)
        return Object.keys(rawSource).reduce<Record<string, unknown>>((accumulator, key) => {
          accumulator[key] = deepClone((rawSource as Record<string, unknown>)[key])
          return accumulator
        }, {})
      }

      return source
    }

    return deepClone(toRaw(value)) as Record<string, any>
  }

  initialModelValue.value = cloneModelValue(modelValue.value)

  /**
   * Expanded state
   */
  const isExpanded = ref(props.defaultExpanded)

  const rootProps = ['label', 'labelWidth', 'key', 'type', 'hidden', 'span', 'slots']
  // Search parameters default to more aggressively removing empty values, reducing invalid query parameters.
  const sanitizeOutputOptions = computed<SanitizeOutputOptions>(() => ({
    removeEmptyString: true,
    removeEmptyArray: true,
    removeEmptyObject: true,
    removeEmptyRichText: true,
    keepZero: true,
    keepFalse: true,
    ...props.sanitizeOutput
  }))

  const getProps = (item: SearchFormItem) => {
    if (item.props) return item.props
    const props = { ...item }
    rootProps.forEach((key) => delete (props as Record<string, any>)[key])
    return props
  }

  // Get slots
  const getSlots = (item: SearchFormItem) => {
    if (!item.slots) return {}
    const validSlots: Record<string, () => any> = {}
    Object.entries(item.slots).forEach(([key, slotFn]) => {
      if (slotFn) {
        validSlots[key] = slotFn
      }
    })
    return validSlots
  }

  /**
   * Get column span value
   * Smart downgrade based on screen size to avoid form items being compressed too small on small screens
   */
  const getColSpan = (itemSpan: number | undefined, breakpoint: ResponsiveBreakpoint): number => {
    return calculateResponsiveSpan(itemSpan, span.value, breakpoint)
  }

  // Search form does not keep empty strings when clearing input, avoiding empty fields in subsequent requests.
  const normalizeFieldValue = (value: unknown) => {
    return value === '' ? undefined : value
  }

  const getFieldValue = (key: string) => modelValue.value[key]

  const setFieldValue = (key: string, value: unknown) => {
    const normalizedValue = normalizeFieldValue(value)

    if (normalizedValue === undefined) {
      delete modelValue.value[key]
      return
    }

    modelValue.value[key] = normalizedValue
  }

  const isRichTextEmpty = (value: string) => {
    if (/<(img|video|audio|iframe|embed|object)\b/i.test(value)) {
      return false
    }

    // Remove common editor placeholder tags then check if there's actual content.
    return (
      value
        .replace(/&nbsp;/gi, '')
        .replace(/<br\s*\/?>/gi, '')
        .replace(/<[^>]*>/g, '')
        .trim() === ''
    )
  }

  // Sanitize empty values during search according to config, but keep 0 and false as valid filter conditions.
  const sanitizeOutputValue = (value: unknown): unknown => {
    const options = sanitizeOutputOptions.value

    if (Array.isArray(value)) {
      const sanitizedArray = value
        .map((item) => sanitizeOutputValue(item))
        .filter((item) => item !== undefined)
      return sanitizedArray.length === 0 && options.removeEmptyArray ? undefined : sanitizedArray
    }

    if (value && typeof value === 'object') {
      const rawValue = toRaw(value)
      const sanitizedObject = Object.entries(rawValue).reduce<Record<string, unknown>>(
        (accumulator, [key, item]) => {
          const sanitizedItem = sanitizeOutputValue(item)
          if (sanitizedItem !== undefined) {
            accumulator[key] = sanitizedItem
          }
          return accumulator
        },
        {}
      )
      return Object.keys(sanitizedObject).length === 0 && options.removeEmptyObject
        ? undefined
        : sanitizedObject
    }

    if (typeof value === 'string') {
      if (options.removeEmptyString && value.trim() === '') {
        return undefined
      }
      if (options.removeEmptyRichText && isRichTextEmpty(value)) {
        return undefined
      }
      return value
    }

    if (value === 0) {
      return options.keepZero ? value : undefined
    }

    if (value === false) {
      return options.keepFalse ? value : undefined
    }

    return value ?? undefined
  }

  const getSanitizedOutput = () => {
    return (sanitizeOutputValue(cloneModelValue(modelValue.value)) || {}) as Record<string, any>
  }

  const getComponent = (item: SearchFormItem) => {
    if (item.render) {
      return item.render
    }
    const { type } = item
    return componentMap[type as keyof typeof componentMap] || componentMap['input']
  }

  const visibleFormItems = computed(() => {
    const filteredItems = props.items.filter((item) => !item.hidden)
    const shouldShowLess = !props.isExpand && !isExpanded.value
    if (shouldShowLess) {
      const maxItemsPerRow = Math.floor(24 / props.span) - 1
      return filteredItems.slice(0, maxItemsPerRow)
    }
    return filteredItems
  })

  const shouldShowExpandToggle = computed(() => {
    const filteredItems = props.items.filter((item) => !item.hidden)
    return (
      !props.isExpand && props.showExpand && filteredItems.length > Math.floor(24 / props.span) - 1
    )
  })

  const expandToggleText = computed(() => {
    return isExpanded.value ? t('table.searchBar.collapse') : t('table.searchBar.expand')
  })

  const actionButtonsStyle = computed(() => ({
    'justify-content': isMobile.value
      ? 'flex-end'
      : props.items.filter((item) => !item.hidden).length <= props.buttonLeftLimit
        ? 'flex-start'
        : 'flex-end'
  }))

  const toggleExpand = () => {
    isExpanded.value = !isExpanded.value
  }

  const handleReset = () => {
    formInstance.value?.resetFields()

    Object.keys(modelValue.value).forEach((key) => {
      delete modelValue.value[key]
    })
    Object.assign(modelValue.value, cloneModelValue(initialModelValue.value))

    emit('reset')
  }

  const handleSearch = () => {
    emit('search', getSanitizedOutput())
  }

  defineExpose({
    ref: formInstance,
    validate: (...args: any[]) => formInstance.value?.validate(...args),
    reset: handleReset,
    getOutput: getSanitizedOutput
  })

  const { span, gutter, labelPosition, labelWidth } = toRefs(props)
</script>

<style lang="scss" scoped>
  .art-search-bar {
    padding: 15px 20px 0;

    .action-column {
      flex: 1;
      max-width: 100%;

      .action-buttons-wrapper {
        display: flex;
        flex-wrap: wrap;
        align-items: center;
        justify-content: flex-end;
        margin-bottom: 12px;
      }

      .form-buttons {
        display: flex;
        gap: 8px;
      }

      .filter-toggle {
        display: flex;
        align-items: center;
        margin-left: 10px;
        line-height: 32px;
        color: var(--theme-color);
        cursor: pointer;
        transition: color 0.2s ease;

        &:hover {
          color: var(--ElColor-primary);
        }

        span {
          font-size: 14px;
          user-select: none;
        }

        .icon-wrapper {
          display: flex;
          align-items: center;
          margin-left: 4px;
          font-size: 14px;
          transition: transform 0.2s ease;
        }
      }
    }
  }

  @media (width <= 768px) {
    .art-search-bar {
      padding: 16px 16px 0;

      .action-column {
        .action-buttons-wrapper {
          flex-direction: column;
          gap: 8px;
          align-items: stretch;

          .form-buttons {
            justify-content: center;
          }

          .filter-toggle {
            justify-content: center;
            margin-left: 0;
          }
        }
      }
    }
  }
</style>
