<!-- Switch Permission Page -->
<template>
  <div class="py-2">
    <!-- Page Header -->
    <div class="mb-6">
      <h2 class="m-0 mb-2 text-xl font-medium">Permission Switch Demo</h2>
      <p class="m-0 leading-[1.6] text-g-700">
        Click the buttons below to switch between different user roles, simulating different users
        logging into the system. After switching, it will affect the entire system's menu display
        and button permissions.
      </p>
    </div>

    <!-- Current User Info -->
    <div class="mb-6">
      <ElCard class="art-card-xs">
        <template #header>
          <div class="font-semibold">
            <span>Current Logged-in User</span>
          </div>
        </template>
        <div>
          <div>
            <div class="flex items-start mb-3 last:mb-0">
              <span class="min-w-30 font-semibold">Username:</span>
              <span>{{ currentUser.userName || 'Not logged in' }}</span>
            </div>
            <div class="flex items-start mb-3 last:mb-0">
              <span class="min-w-30 font-semibold">Role:</span>
              <ElTag :type="getRoleTagType(currentUser.roles?.[0])">
                {{ getRoleDisplayName(currentUser.roles?.[0]) }}
              </ElTag>
            </div>
            <div class="flex items-start mb-3 last:mb-0">
              <span class="min-w-30 font-semibold">Permission Codes:</span>
              <div class="flex flex-wrap gap-2">
                <ElTag v-for="button in currentUser.buttons" :key="button" size="small" type="info">
                  {{ button }}
                </ElTag>
                <span v-if="!currentUser.buttons?.length" class="italic text-g-500"
                  >No permission codes</span
                >
              </div>
            </div>
          </div>
        </div>
      </ElCard>
    </div>

    <!-- User Role Switch -->
    <div class="mb-6">
      <ElCard class="art-card-xs">
        <template #header>
          <div class="font-semibold">
            <span>Account Switch</span>
          </div>
        </template>
        <div class="grid grid-cols-[repeat(auto-fit,minmax(300px,1fr))] gap-4">
          <div
            v-for="account in accounts"
            :key="account.key"
            class="p-5 border border-g-400 rounded-lg tad-300"
            :class="{
              'bg-theme/12 !border-theme': currentUser.userName === account.userName
            }"
          >
            <div class="mb-4">
              <div>
                <h4 class="m-0 mb-2 text-base font-semibold">{{ account.label }}</h4>
                <p class="m-0 mb-2 leading-[1.5] text-g-700">{{ account.description }}</p>
                <div class="flex flex-col gap-1">
                  <span class="text-xs text-g-600">Username: {{ account.userName }}</span>
                  <span class="text-xs text-g-600">Roles: {{ account.roles.join(', ') }}</span>
                </div>
              </div>
            </div>
            <div class="text-right">
              <ElButton
                v-if="currentUser.userName !== account.userName"
                type="primary"
                @click="switchRole(account)"
                :loading="switching"
              >
                Switch to this role
              </ElButton>
              <ElTag v-else type="success">Current User</ElTag>
            </div>
          </div>
        </div>
      </ElCard>
    </div>
  </div>
</template>

<script setup lang="ts">
  import { computed, ref } from 'vue'
  import { useUserStore } from '@/store/modules/user'
  import { useI18n } from 'vue-i18n'
  import { fetchLogin, fetchGetUserInfo } from '@/api/auth'

  defineOptions({ name: 'PermissionSwitchRole' })

  const { t } = useI18n()
  const userStore = useUserStore()

  // Reactive data
  const switching = ref(false)

  // Current user info
  const currentUser = computed(() => userStore.info)

  // Account list - consistent with login page
  const accounts = computed(() => [
    {
      key: 'super',
      label: t('login.roles.super'),
      userName: 'Super',
      password: '123456',
      roles: ['R_SUPER'],
      color: '#E6A23C',
      description: 'Has the highest system privileges, can access all functional modules'
    },
    {
      key: 'admin',
      label: t('login.roles.admin'),
      userName: 'Admin',
      password: '123456',
      roles: ['R_ADMIN'],
      color: '#409EFF',
      description: 'Has management privileges, can manage users and some system settings'
    },
    {
      key: 'user',
      label: t('login.roles.user'),
      userName: 'User',
      password: '123456',
      roles: ['R_USER'],
      color: '#67C23A',
      description: 'Regular user privileges, can only access basic functional modules'
    }
  ])

  // Get role tag type
  const getRoleTagType = (role?: string): 'info' | 'warning' | 'primary' | 'success' | 'danger' => {
    if (!role) return 'info'
    const roleMap: Record<string, 'info' | 'warning' | 'primary' | 'success' | 'danger'> = {
      R_SUPER: 'warning',
      R_ADMIN: 'primary',
      R_USER: 'success'
    }
    return roleMap[role] || 'info'
  }

  // Get role display name
  const getRoleDisplayName = (role?: string): string => {
    if (!role) return 'Unknown role'
    const roleMap: Record<string, string> = {
      R_SUPER: 'Super Admin',
      R_ADMIN: 'Admin',
      R_USER: 'Regular User'
    }
    return roleMap[role] || 'Unknown role'
  }

  /**
   * Account info type
   */
  interface AccountInfo {
    userName: string
    password: string
    role?: string
    roles?: string[]
    [key: string]: any
  }

  /**
   * Switch role
   * @param account Account info
   */
  const switchRole = async (account: AccountInfo) => {
    try {
      switching.value = true

      // Simulate login request
      const response = await fetchLogin({
        email: account.userName, // Use userName as email
        password: account.password
      })

      const token = response.accessToken || response.token

      // Verify token
      if (!token) {
        throw new Error('Login failed - no token received')
      }

      // Store token and user info
      userStore.setToken(token, response.refreshToken)
      const userInfo = await fetchGetUserInfo()
      userStore.setUserInfo(userInfo)

      // Delay page refresh to apply new permissions
      setTimeout(() => {
        window.location.reload()
      }, 100)
    } catch (error) {
      if (error !== 'cancel') {
        ElMessage.error('Failed to switch user role, please try again later')
        console.error('[SwitchRole] Error:', error)
      }
    } finally {
      switching.value = false
    }
  }
</script>
