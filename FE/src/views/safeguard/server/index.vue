<template>
  <div class="page-content mb-5">
    <div class="w-full">
      <div class="flex flex-wrap w-[calc(100%+20px)]">
        <div
          class="box-border w-[calc(50%-20px)] mr-5 mb-5 border border-g-300 rounded max-lg:w-full max-md:w-full"
          v-for="item in serverList"
          :key="item.name"
        >
          <div
            class="flex-cb p-5 border-b border-g-300/80 max-lg:p-2.5 max-lg:px-5 max-md:p-2.5 max-md:px-5"
          >
            <span class="text-sm font-medium">{{ item.name }}</span>
            <span class="text-sm text-g-600">{{ item.ip }}</span>
          </div>
          <div class="flex-c p-9 scale-[0.8] max-lg:p-5 max-md:block max-md:p-5 max-sm:!block">
            <div class="mx-10 max-lg:m-0 max-lg:mr-5 max-md:m-0">
              <img
                src="@imgs/safeguard/server.png"
                alt="Server"
                class="block w-47 max-md:w-37 max-md:mx-auto"
              />
              <div class="flex justify-center -mt-2.5 max-md:mt-2.5">
                <ElButtonGroup>
                  <ElButton type="primary" size="default">Power on</ElButton>
                  <ElButton type="danger" size="default">Shut down</ElButton>
                  <ElButton type="warning" size="default">Restart</ElButton>
                </ElButtonGroup>
              </div>
            </div>
            <div class="flex-1 mt-1 max-lg:mt-0 max-md:mt-7.5">
              <div class="my-3.5">
                <p class="mb-1 text-sm">CPU</p>
                <ElProgress :percentage="item.cup" :text-inside="true" :stroke-width="17" />
              </div>
              <div class="my-3.5">
                <p class="mb-1 text-sm">RAM</p>
                <ElProgress
                  :percentage="item.memory"
                  status="success"
                  :text-inside="true"
                  :stroke-width="17"
                />
              </div>
              <div class="my-3.5">
                <p class="mb-1 text-sm">SWAP</p>
                <ElProgress
                  :percentage="item.swap"
                  status="warning"
                  :text-inside="true"
                  :stroke-width="17"
                />
              </div>
              <div class="my-3.5">
                <p class="mb-1 text-sm">DISK</p>
                <ElProgress
                  :percentage="item.disk"
                  status="success"
                  :text-inside="true"
                  :stroke-width="17"
                />
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
  defineOptions({ name: 'SafeguardServer' })

  interface ServerInfo {
    name: string
    ip: string
    cup: number
    memory: number
    swap: number
    disk: number
  }

  const UPDATE_INTERVAL = 3000

  const serverList = reactive<ServerInfo[]>([
    {
      name: 'Development server',
      ip: '192.168.1.100',
      cup: 85,
      memory: 65,
      swap: 45,
      disk: 92
    },
    {
      name: 'Test server',
      ip: '192.168.1.101',
      cup: 32,
      memory: 78,
      swap: 90,
      disk: 45
    },
    {
      name: 'Staging server',
      ip: '192.168.1.102',
      cup: 95,
      memory: 42,
      swap: 67,
      disk: 88
    },
    {
      name: 'Production server',
      ip: '192.168.1.103',
      cup: 58,
      memory: 93,
      swap: 25,
      disk: 73
    }
  ])

  const generateRandomValue = (min = 0, max = 100): number => {
    return Math.floor(Math.random() * (max - min + 1)) + min
  }

  const updateServerData = (): void => {
    serverList.forEach((server) => {
      server.cup = generateRandomValue()
      server.memory = generateRandomValue()
      server.swap = generateRandomValue()
      server.disk = generateRandomValue()
    })
  }

  let timer: number | null = null

  onMounted(() => {
    timer = window.setInterval(updateServerData, UPDATE_INTERVAL)
  })

  onUnmounted(() => {
    if (timer !== null) {
      window.clearInterval(timer)
      timer = null
    }
  })
</script>
