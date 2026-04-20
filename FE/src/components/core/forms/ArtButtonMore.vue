<!-- More Button -->
<template>
  <div>
    <ElDropdown v-if="hasAnyAuthItem">
      <ArtIconButton icon="ri:more-2-fill" class="!size-8 bg-g-200 dark:bg-g-300/45 text-sm" />
      <template #dropdown>
        <ElDropdownMenu>
          <template v-for="item in list" :key="item.key">
            <ElDropdownItem
              v-if="!item.auth || hasAuth(item.auth)"
              :disabled="item.disabled"
              @click="handleClick(item)"
            >
              <div class="flex-c gap-2" :style="{ color: item.color }">
                <ArtSvgIcon v-if="item.icon" :icon="item.icon" />
                <span>{{ item.label }}</span>
              </div>
            </ElDropdownItem>
          </template>
        </ElDropdownMenu>
      </template>
    </ElDropdown>
  </div>
</template>

<script setup lang="ts">
  import { useAuth } from '@/hooks/core/useAuth'

  defineOptions({ name: 'ArtButtonMore' })

  const { hasAuth } = useAuth()

  export interface ButtonMoreItem {
    /** Button identifier, can be used for click events */
    key: string | number
    /** Button text */
    label: string
    /** Whether disabled */
    disabled?: boolean
    /** Permission identifier */
    auth?: string
    /** Icon component */
    icon?: string
    /** Text color */
    color?: string
    /** Icon color (higher priority than color) */
    iconColor?: string
  }

  interface Props {
    /** Dropdown items list */
    list: ButtonMoreItem[]
    /** Overall permission control */
    auth?: string
  }

  const props = withDefaults(defineProps<Props>(), {})

  // Check if there are any items with permission
  const hasAnyAuthItem = computed(() => {
    return props.list.some((item) => !item.auth || hasAuth(item.auth))
  })

  const emit = defineEmits<{
    (e: 'click', item: ButtonMoreItem): void
  }>()

  const handleClick = (item: ButtonMoreItem) => {
    emit('click', item)
  }
</script>
