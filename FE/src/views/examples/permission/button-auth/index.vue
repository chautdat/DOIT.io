<template>
  <div class="w-full py-2">
    <div class="mb-6">
      <h2 class="m-0 mb-2 text-xl font-medium">{{ $t('menus.examples.permission.buttonAuth') }}</h2>
      <p class="m-0 text-sm leading-[1.6] text-g-700">
        This page demonstrates button-level permission control. The visible buttons vary by user role. Permission control is based on roles and permission codes.
      </p>
    </div>

    <div class="mb-6">
      <ElCard class="art-card-xs">
        <template #header>
          <div class="flex-cb font-semibold">
            <span class="flex-1">Current user permissions</span>
          </div>
        </template>
        <div>
          <div class="flex items-start mb-4 last:mb-0">
            <span class="min-w-20 font-semibold">Role:</span>
            <ElTag :type="getRoleTagType(currentUserRole)">
              {{ getRoleDisplayName(currentUserRole) }}
            </ElTag>
          </div>
          <div class="flex items-start mb-4 last:mb-0">
            <span class="min-w-20 font-semibold">Permission codes:</span>
            <div class="flex flex-wrap gap-2">
              <ElTag
                v-for="permission in currentUserPermissions"
                :key="permission"
                size="small"
                type="info"
                class="m-0"
              >
                {{ permission }}
              </ElTag>
              <span v-if="!currentUserPermissions.length" class="italic text-red-500"
                >No permission codes</span
              >
            </div>
          </div>
        </div>
      </ElCard>
    </div>

    <div class="mb-6 last:mb-0">
      <ElCard class="art-card-xs">
        <template #header>
          <div class="flex-cb font-semibold">
            <span class="flex-1">Role-based permission control (v-roles directive)</span>
          </div>
        </template>
        <div>
          <p class="p-3 m-0 mb-5 text-sm leading-[1.6] text-g-700 bg-g-200 rounded">
            Use
            <code class="px-1.5 py-0.5 font-mono text-xs text-theme bg-theme/10 rounded"
              >v-roles</code
            >
            directive to control visibility; only users with the specified role can see the corresponding button.
          </p>

          <div class="grid grid-cols-[repeat(auto-fit,minmax(280px,1fr))] gap-5">
            <div class="flex flex-col gap-2">
              <ElButton type="primary" plain v-roles="'R_SUPER'"> Visible to super admins </ElButton>
              <div class="text-xs">
                <code class="block p-1 px-2 mb-1 font-mono bg-g-200 border-full-d rounded"
                  >v-roles="'R_SUPER'"</code
                >
                <span class="text-g-700">Visible to super admins only</span>
              </div>
            </div>

            <div class="flex flex-col gap-2">
              <ElButton type="warning" plain v-roles="['R_SUPER', 'R_ADMIN']">
                Visible to admins
              </ElButton>
              <div class="text-xs">
                <code class="block p-1 px-2 mb-1 font-mono bg-g-200 border-full-d rounded"
                  >v-roles="['R_SUPER', 'R_ADMIN']"</code
                >
                <span class="text-g-700">Visible to super admins and admins</span>
              </div>
            </div>

            <div class="flex flex-col gap-2">
              <ElButton type="success" plain v-roles="['R_SUPER', 'R_ADMIN', 'R_USER']">
                Visible to all users
              </ElButton>
              <div class="text-xs">
                <code class="block p-1 px-2 mb-1 font-mono bg-g-200 border-full-d rounded"
                  >v-roles="['R_SUPER', 'R_ADMIN', 'R_USER']"</code
                >
                <span class="text-g-700">Visible to all signed-in users</span>
              </div>
            </div>
          </div>
        </div>
      </ElCard>
    </div>

    <div class="mb-6 last:mb-0">
      <ElCard class="art-card-xs">
        <template #header>
          <div class="flex-cb font-semibold">
            <span class="flex-1">Backend-mode permission control (v-auth directive)</span>
            <ElTag v-if="!isFrontendMode" type="success" size="small">Active mode</ElTag>
            <ElTag v-else type="info" size="small">Inactive mode</ElTag>
          </div>
        </template>
        <div>
          <p class="p-3 m-0 mb-5 text-sm leading-[1.6] text-g-700 bg-g-200 rounded">
            <code class="px-1.5 py-0.5 font-mono text-xs text-theme bg-theme/10 rounded"
              >v-auth</code
            >
            In backend mode, permissions are checked against the current route's 
            <code class="px-1.5 py-0.5 font-mono text-xs text-theme bg-theme/10 rounded"
              >meta.authList</code
            >
            Configuration. The permission list comes from route metadata and is suitable for scenarios where permissions are centrally managed by the backend.
          </p>

          <div class="mb-5">
            <h4 class="m-0 mb-2 text-sm font-semibold"> Current route permission config: </h4>
            <div
              class="max-h-30 p-3 overflow-y-auto font-mono text-xs break-all whitespace-pre-wrap bg-g-200 border-full-d rounded"
            >
              <code>{{ JSON.stringify(backendAuthList, null, 2) }}</code>
            </div>
          </div>

          <div class="grid grid-cols-[repeat(auto-fit,minmax(280px,1fr))] gap-5">
            <div class="flex flex-col gap-2">
              <ElButton type="primary" plain v-auth="'add'"> Add </ElButton>
              <div class="text-xs">
                <code class="block p-1 px-2 mb-1 font-mono bg-g-200 border-full-d rounded"
                  >v-auth="'add'"</code
                >
                <span class="text-g-700">Checks whether meta.authList contains authMark: 'add'</span>
              </div>
            </div>

            <div class="flex flex-col gap-2">
              <ElButton type="warning" plain v-auth="'edit'"> Edit </ElButton>
              <div class="text-xs">
                <code class="block p-1 px-2 mb-1 font-mono bg-g-200 border-full-d rounded"
                  >v-auth="'edit'"</code
                >
                <span class="text-g-700">Checks whether meta.authList contains authMark: 'edit'</span>
              </div>
            </div>

            <div class="flex flex-col gap-2">
              <ElButton type="danger" plain v-auth="'delete'"> Delete </ElButton>
              <div class="text-xs">
                <code class="block p-1 px-2 mb-1 font-mono bg-g-200 border-full-d rounded"
                  >v-auth="'delete'"</code
                >
                <span class="text-g-700">Checks whether meta.authList contains authMark: 'delete'</span>
              </div>
            </div>

            <div class="flex flex-col gap-2">
              <ElButton type="info" plain v-auth="'export'"> Export </ElButton>
              <div class="text-xs">
                <code class="block p-1 px-2 mb-1 font-mono bg-g-200 border-full-d rounded"
                  >v-auth="'export'"</code
                >
                <span class="text-g-700">Checks whether meta.authList contains authMark: 'export'</span>
              </div>
            </div>
          </div>

          <div class="mt-4">
            <ElAlert
              :type="isFrontendMode ? 'warning' : 'success'"
              :title="
                isFrontendMode
                  ? 'Note: frontend mode is active. The `v-auth` directive only works in backend mode.'
                  : 'Backend mode is active. The `v-auth` directive works normally.'
              "
              show-icon
              :closable="false"
            />
          </div>
        </div>
      </ElCard>
    </div>

    <div class="mb-6 last:mb-0">
      <ElCard class="art-card-xs">
        <template #header>
          <div class="flex-cb font-semibold">
            <span class="flex-1">Frontend-mode permission control (hasAuth method)</span>
            <ElTag v-if="isFrontendMode" type="success" size="small">Active mode</ElTag>
            <ElTag v-else type="info" size="small">Inactive mode</ElTag>
          </div>
        </template>
        <div>
          <p class="p-3 m-0 mb-5 text-sm leading-[1.6] text-g-700 bg-g-200 rounded">
            <code class="px-1.5 py-0.5 font-mono text-xs text-theme bg-theme/10 rounded"
              >hasAuth</code
            >
            In frontend mode, the method checks the 
            <code class="px-1.5 py-0.5 font-mono text-xs text-theme bg-theme/10 rounded"
              >buttons</code
            >
            field in userInfo. The permission list is stored in userStatus, which suits scenarios where permissions are fully managed by the frontend.
          </p>

          <div class="mb-5">
            <h4 class="m-0 mb-2 text-sm font-semibold">Current user permission list (buttons field):</h4>
            <div
              class="max-h-30 p-3 overflow-y-auto font-mono text-xs break-all whitespace-pre-wrap bg-g-200 border-full-d rounded"
            >
              <code>{{ JSON.stringify(frontendAuthList, null, 2) }}</code>
            </div>
          </div>

          <div class="grid grid-cols-[repeat(auto-fit,minmax(280px,1fr))] gap-5">
            <div class="flex flex-col gap-2">
              <ElButton v-if="hasAuth('view')" type="primary"> View details </ElButton>
              <ElButton v-else type="info" disabled> No view permission </ElButton>
              <div class="text-xs">
                <code class="block p-1 px-2 mb-1 font-mono bg-g-200 border-full-d rounded"
                  >v-if="hasAuth('view')"</code
                >
                <span class="text-g-700">Checks whether the user buttons array contains 'view'</span>
              </div>
            </div>

            <div class="flex flex-col gap-2">
              <ElButton
                :disabled="!hasAuth('publish')"
                :type="hasAuth('publish') ? 'success' : 'info'"
                @click="handlePublish"
              >
                {{ hasAuth('publish') ? 'Publish' : 'No publish permission' }}
              </ElButton>
              <div class="text-xs">
                <code class="block p-1 px-2 mb-1 font-mono bg-g-200 border-full-d rounded"
                  >:disabled="!hasAuth('publish')"</code
                >
                <span class="text-g-700">Checks whether the user buttons array contains 'publish'</span>
              </div>
            </div>

            <div class="flex flex-col gap-2">
              <ElDropdown @command="handleBatchAction" :disabled="!hasBatchPermissions">
                <ElButton :type="hasBatchPermissions ? 'warning' : 'info'">
                  Batch actions
                  <ElIcon class="el-icon--right">
                    <ArrowDown />
                  </ElIcon>
                </ElButton>
                <template #dropdown>
                  <ElDropdownMenu>
                    <ElDropdownItem command="batchEdit" :disabled="!hasAuth('edit')">
                      Batch edit
                    </ElDropdownItem>
                    <ElDropdownItem command="batchDelete" :disabled="!hasAuth('delete')">
                      Batch delete
                    </ElDropdownItem>
                    <ElDropdownItem command="batchExport" :disabled="!hasAuth('export')">
                      Batch export
                    </ElDropdownItem>
                  </ElDropdownMenu>
                </template>
              </ElDropdown>
              <div class="text-xs">
                <code class="block p-1 px-2 mb-1 font-mono bg-g-200 border-full-d rounded"
                  >computed(() => hasAuth('edit') || hasAuth('delete'))</code
                >
                <span class="text-g-700">Composite permission check: checks the union of multiple permissions</span>
              </div>
            </div>
          </div>
        </div>
      </ElCard>
    </div>

    <div class="mb-6 last:mb-0">
      <ElCard class="art-card-xs">
        <template #header>
          <div class="flex-cb font-semibold">
            <span class="flex-1">Backend mode permission control (hasAuth method)</span>
            <ElTag v-if="!isFrontendMode" type="success" size="small">Active mode</ElTag>
            <ElTag v-else type="info" size="small">Inactive mode</ElTag>
          </div>
        </template>
        <div>
          <p class="p-3 m-0 mb-5 text-sm leading-[1.6] text-g-700 bg-g-200 rounded">
            <code class="px-1.5 py-0.5 font-mono text-xs text-theme bg-theme/10 rounded"
              >hasAuth</code
            >
            In backend mode, permissions are checked against the current route's 
            <code class="px-1.5 py-0.5 font-mono text-xs text-theme bg-theme/10 rounded"
              >meta.authList</code
            >
            Configuration checks permissions. It uses the same permission source as the v-auth directive, but provides programmatic permission checks.
          </p>

          <div class="grid grid-cols-[repeat(auto-fit,minmax(280px,1fr))] gap-5">
            <div class="flex flex-col gap-2">
              <ElSwitch
                v-model="dynamicFeatureEnabled"
                :disabled="!hasAuth('config')"
                active-text="Feature on"
                inactive-text="Feature off"
              />
              <div class="text-xs">
                <code class="block p-1 px-2 mb-1 font-mono bg-g-200 border-full-d rounded"
                  >:disabled="!hasAuth('config')"</code
                >
                <span class="text-g-700">Check the route permission control toggle</span>
              </div>
            </div>

            <div class="flex flex-col gap-2">
              <div v-if="hasAuth('manage')" class="flex gap-2 p-3 bg-g-200 border-full-d rounded">
                <ElButton type="primary" size="small">Admin actions</ElButton>
                <ElButton type="warning" size="small">Advanced settings</ElButton>
              </div>
              <div
                v-else
                class="flex-c gap-2 p-3 text-g-500 bg-g-200 border border-dashed border-g-400 rounded"
              >
                <ElIcon><Lock /></ElIcon>
                <span class="text-sm">This area requires admin permissions</span>
              </div>
              <div class="text-xs">
                <code class="block p-1 px-2 mb-1 font-mono bg-g-200 border-full-d rounded"
                  >v-if="hasAuth('manage')"</code
                >
                <span class="text-g-700">Conditional rendering based on backend route permissions</span>
              </div>
            </div>

            <div class="flex flex-col gap-2">
              <div class="flex flex-wrap gap-3">
                <ElBadge
                  :value="hasAuth('add') ? '✓' : '✗'"
                  :type="hasAuth('add') ? 'success' : 'danger'"
                >
                  <ElButton size="small">Add permission</ElButton>
                </ElBadge>
                <ElBadge
                  :value="hasAuth('edit') ? '✓' : '✗'"
                  :type="hasAuth('edit') ? 'success' : 'danger'"
                >
                  <ElButton size="small">Edit permission</ElButton>
                </ElBadge>
                <ElBadge
                  :value="hasAuth('delete') ? '✓' : '✗'"
                  :type="hasAuth('delete') ? 'success' : 'danger'"
                >
                  <ElButton size="small">Delete permission</ElButton>
                </ElBadge>
              </div>
              <div class="text-xs">
                <code class="block p-1 px-2 mb-1 font-mono bg-g-200 border-full-d rounded"
                  >hasAuth('permission')</code
                >
                <span class="text-g-700">Real-time permission status indicator</span>
              </div>
            </div>
          </div>
        </div>
      </ElCard>
    </div>

    <div class="mb-6 last:mb-0">
      <ElCard class="art-card-xs">
        <template #header>
          <div class="flex-cb font-semibold">
            <span class="flex-1">Permission mode comparison</span>
          </div>
        </template>
        <div>
          <div class="mt-4">
            <ElTable :data="comparisonData" border>
              <ElTableColumn prop="feature" label="Feature" width="150" />
              <ElTableColumn prop="frontend" label="Frontend mode" />
              <ElTableColumn prop="backend" label="Backend mode" />
            </ElTable>
          </div>
        </div>
      </ElCard>
    </div>
  </div>
</template>

<script setup lang="ts">
  import { computed, ref } from 'vue'
  import { ArrowDown, Lock } from '@element-plus/icons-vue'
  import { useAuth } from '@/hooks/core/useAuth'
  import { useUserStore } from '@/store/modules/user'
  import { useAppMode } from '@/hooks/core/useAppMode'
  import { useRoute } from 'vue-router'
  import type { AppRouteRecord } from '@/types/router'

  defineOptions({ name: 'PermissionButtonAuth' })

  const { hasAuth } = useAuth()
  const { isFrontendMode } = useAppMode()
  const userStore = useUserStore()
  const route = useRoute()

  const dynamicFeatureEnabled = ref(false)

  const currentUserRole = computed(() => {
    return userStore.info?.roles?.[0] || ''
  })

  const currentUserPermissions = computed(() => {
    return userStore.info?.buttons || []
  })

  const frontendAuthList = computed(() => {
    return userStore.info?.buttons || []
  })

  const backendAuthList = computed(() => {
    type AuthItem = NonNullable<AppRouteRecord['meta']['authList']>[number]
    return Array.isArray(route.meta.authList) ? (route.meta.authList as AuthItem[]) : []
  })

  const hasBatchPermissions = computed(() => {
    return hasAuth('edit') || hasAuth('delete') || hasAuth('export')
  })

  const comparisonData = computed(() => [
    {
      feature: 'Permission source',
      frontend: 'buttons field in userStatus',
      backend: 'meta.authList in the route config'
    },
    {
      feature: 'v-auth directive',
      frontend: 'Unavailable (the directive is inactive in frontend mode)',
      backend: 'Available (checks route permission config)'
    },
    {
      feature: 'hasAuth method',
      frontend: 'Available (checks the buttons array)',
      backend: 'Available (checks meta.authList)'
    },
    {
      feature: 'Permission management',
      frontend: 'Fully controlled by the frontend; highly flexible',
      backend: 'Centrally managed by the backend; more secure'
    },
    {
      feature: 'Use cases',
      frontend: 'Rapid prototypes, simple apps',
      backend: 'Enterprise apps, complex permission systems'
    }
  ])

  const getRoleTagType = (role: string): 'primary' | 'success' | 'info' | 'warning' | 'danger' => {
    const roleMap: Record<string, 'primary' | 'success' | 'info' | 'warning' | 'danger'> = {
      R_SUPER: 'warning',
      R_ADMIN: 'primary',
      R_USER: 'success'
    }
    return roleMap[role] || 'info'
  }

  const getRoleDisplayName = (role: string) => {
    const roleMap: Record<string, string> = {
      R_SUPER: 'Super Admin',
      R_ADMIN: 'Admin',
      R_USER: 'Standard User'
    }
    return roleMap[role] || 'Unknown role'
  }

  const handlePublish = () => {
    if (hasAuth('publish')) {
      ElMessage.success('Publish successful!')
    } else {
      ElMessage.warning('You do not have publish permission')
    }
  }

  const handleBatchAction = (command: string) => {
    const actions: Record<string, string> = {
      batchEdit: 'Batch edit',
      batchDelete: 'Batch delete',
      batchExport: 'Batch export'
    }

    const permission = command.replace('batch', '').toLowerCase()

    if (hasAuth(permission)) {
      ElMessage.success(`${actions[command]} action executed successfully!`)
    } else {
      ElMessage.warning(`You do not have ${actions[command]} permission`)
    }
  }
</script>
