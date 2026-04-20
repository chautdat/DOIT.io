<template>
  <div class="w-full py-2">
    <div class="mb-6">
      <h2 class="m-0 mb-2 text-xl font-medium">{{
        $t('menus.examples.permission.pageVisibility')
      }}</h2>
      <p class="m-0 text-sm leading-[1.6] text-g-700">
        This page is visible only to <strong class="font-semibold text-warning">Super Admin</strong
        >users. It demonstrates page-level permission control. If you can see this page, you have the required access permission.
      </p>
    </div>

    <div class="mb-6">
      <ElCard class="art-card-xs">
        <template #header>
          <div class="flex-c gap-2 font-semibold">
            <span>Permission check passed</span>
          </div>
        </template>
        <div>
          <div class="flex-c gap-5">
            <div>
              <h3 class="m-0 mb-2 text-lg font-semibold">You have permission to access this page</h3>
              <p class="my-1 text-sm text-g-700">
                Current user: <strong class="font-semibold">{{ currentUser.userName }}</strong>
              </p>
              <p class="my-1 text-sm text-g-700">
                Role:
                <ElTag type="warning">{{ getRoleDisplayName(currentUser.roles?.[0] || '') }}</ElTag>
              </p>
            </div>
          </div>
        </div>
      </ElCard>
    </div>

    <div class="mb-6 last:mb-0">
      <ElCard class="art-card-xs">
        <template #header>
          <div class="flex-c font-semibold">
            <span>Page-level access control notes</span>
          </div>
        </template>
        <div>
          <ElTimeline>
            <ElTimelineItem timestamp="Frontend mode" type="primary" size="large">
              <ElCard>
                <h4 class="m-0 mb-2 text-base font-semibold">Role-based permission control</h4>
                <p class="m-0 mb-2 leading-[1.6] text-g-700">
                  In frontend mode, page access permission is defined by the 
                  <code class="px-1.5 py-0.5 font-mono text-xs text-theme bg-theme/12 rounded"
                    >meta.roles</code
                  >
                  field in the route config file. The frontend filters routes and menus based on the roles returned by the user API.
                </p>
                <pre
                  class="p-4 mt-3 mb-0 overflow-x-auto font-mono text-xs leading-[1.5] bg-g-200 border-full-d rounded-md"
                ><code class="">{
  path: 'page-visibility',
  name: 'PermissionPageVisibility',
  component: '/examples/permission/page-visibility',
  meta: {
    title: 'menus.permission.pageVisibility',
    roles: ['R_SUPER'],
    keepAlive: true
  }
}</code></pre>
                <p class="m-0 mb-2 leading-[1.6] text-g-700"><strong>Permission check flow:</strong></p>
                <ul class="pl-5 my-2">
                  <li class="my-1 leading-[1.5] text-g-700"
                    >After the user logs in, the API returns role info (for example R_SUPER, R_ADMIN, R_USER)</li
                  >
                  <li class="my-1 leading-[1.5] text-g-700">
                    In
                    <code class="px-1.5 py-0.5 font-mono text-xs text-theme bg-theme/12 rounded"
                      >beforeEach</code
                    >
                    route guard checks the target route's 
                    <code class="px-1.5 py-0.5 font-mono text-xs text-theme bg-theme/12 rounded"
                      >roles</code
                    >
                    configuration
                  </li>
                  <li class="my-1 leading-[1.5] text-g-700"
                    >Check whether the role is included in the allowed role list</li
                  >
                  <li class="my-1 leading-[1.5] text-g-700">If permission is insufficient, redirect to the 403 page</li>
                </ul>
              </ElCard>
            </ElTimelineItem>

            <ElTimelineItem timestamp="Backend mode" type="warning" size="large">
              <ElCard>
                <h4 class="m-0 mb-2 text-base font-semibold">Menu API-based access control</h4>
                <p class="m-0 mb-2 leading-[1.6] text-g-700"
                  >In backend mode, page access permission is managed centrally by the backend. The frontend parses the menu list returned by the backend API to generate accessible routes, thereby implementing permission control</p
                >
                <p class="m-0 mb-2 leading-[1.6] text-g-700"
                  >API: src/api/menuApi.ts getMenuList</p
                >
                <pre
                  class="p-4 mt-3 mb-0 overflow-x-auto font-mono text-xs leading-[1.5] bg-g-200 border-full-d rounded-md"
                ><code class="">
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "path": "/permission",
      "name": "Permission",
      "component": "Layout",
      "meta": {
        "title": "menus.permission.title",
        "icon": ""
      },
      "children": [
        {
          "id": 11,
          "path": "page-visibility",
          "name": "PermissionPageVisibility",
          "component": "permission/page-visibility/index",
          "meta": {
            "title": "menus.permission.pageVisibility",
            "keepAlive": true
          }
        }
      ]
    }
  ]
}</code></pre>
                <p><strong>Permission check flow:</strong></p>
                <ul>
                  <li>After login succeeds, the token is retrieved</li>
                  <li>The frontend calls the menu API to get the user's accessible menu list</li>
                  <li>The frontend dynamically registers routes based on the menu list</li>
                  <li>Pages present in the menu can be accessed normally; pages not present will redirect to 404</li>
                </ul>
              </ElCard>
            </ElTimelineItem>

            <ElTimelineItem timestamp="Menu visibility control" type="success" size="large">
              <ElCard>
                <h4>Sidebar menu visibility</h4>
                <p><strong>Frontend mode：</strong></p>
                <ul>
                  <li>Authorized users：menu items are shown and can be clicked</li>
                  <li>Unauthorized users：menu items are hidden and cannot be used for navigation</li>
                  <li>direct URL access is also blocked by the route guard</li>
                </ul>
                <p><strong>Backend mode：</strong></p>
                <ul>
                  <li>The sidebar menu is rendered from the backend menu list</li>
                  <li>The backend should filter menu items by user permission</li>
                  <li>The frontend only shows menus returned by the backend</li>
                </ul>
              </ElCard>
            </ElTimelineItem>
          </ElTimeline>
        </div>
      </ElCard>
    </div>

    <div class="best-practices">
      <ElCard class="art-card-xs">
        <template #header>
          <div class="card-header">
            <span>Permission best practices</span>
          </div>
        </template>
        <div class="practices-content">
          <ElRow :gutter="24">
            <ElCol :span="12" class="!mb-5">
              <div class="flex-c">
                <div class="size-10 bg-g-200 flex-cc rounded mr-2">
                  <ElIcon size="20" color="#409EFF"><Lock /></ElIcon>
                </div>
                <div>
                  <h4>Multi-layer verification</h4>
                  <p class="text-g-700 text-sm"
                    >Implement permission control across frontend routes, backend APIs, and UI components to ensure security.</p
                  >
                </div>
              </div>
            </ElCol>
            <ElCol :span="12">
              <div class="flex-c">
                <div class="size-10 bg-g-200 flex-cc rounded mr-2">
                  <ElIcon size="20" color="#67C23A"><User /></ElIcon>
                </div>
                <div>
                  <h4>Role-based access control</h4>
                  <p class="text-g-700 text-sm"
                    >Use the RBAC model to assign permissions by role and simplify permission management.</p
                  >
                </div>
              </div>
            </ElCol>
            <ElCol :span="12">
              <div class="flex-c">
                <div class="size-10 bg-g-200 flex-cc rounded mr-2">
                  <ElIcon size="20" color="#E6A23C"><Key /></ElIcon>
                </div>
                <div>
                  <h4>Fine-grained permission control</h4>
                  <p class="text-g-700 text-sm">Supports page-level, button-level, and data-level permission control.</p>
                </div>
              </div>
            </ElCol>
            <ElCol :span="12">
              <div class="flex-c">
                <div class="size-10 bg-g-200 flex-cc rounded mr-2">
                  <ElIcon size="20" color="#F56C6C"><View /></ElIcon>
                </div>
                <div>
                  <h4>Security-first principle</h4>
                  <p class="text-g-700 text-sm"
                    >Always follow the principle of least privilege so users can access only the functions and data they need.</p
                  >
                </div>
              </div>
            </ElCol>
          </ElRow>
        </div>
      </ElCard>
    </div>
  </div>
</template>

<script setup lang="ts">
  import { computed } from 'vue'

  import { Lock, User, Key, View } from '@element-plus/icons-vue'
  import { useUserStore } from '@/store/modules/user'

  defineOptions({ name: 'PermissionPageVisibility' })

  const userStore = useUserStore()

  const currentUser = computed(() => userStore.info)

  const getRoleDisplayName = (role: string) => {
    const roleMap: Record<string, string> = {
      R_SUPER: 'Super Admin',
      R_ADMIN: 'Admin',
      R_USER: 'Standard User'
    }
    return roleMap[role] || 'Unknown role'
  }
</script>
