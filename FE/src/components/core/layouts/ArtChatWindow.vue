<template>
  <div>
    <ElDrawer v-model="isDrawerVisible" :size="isMobile ? '100%' : '480px'" :with-header="false">
      <div class="mb-5 flex-cb">
        <div>
          <span class="text-base font-medium">Art Bot</span>
          <div class="mt-1.5 flex-c gap-1">
            <div
              class="h-2 w-2 rounded-full"
              :class="isOnline ? 'bg-success/100' : 'bg-danger/100'"
            ></div>
            <span class="text-xs text-g-600">{{ isOnline ? 'Online' : 'Offline' }}</span>
          </div>
        </div>
        <div>
          <ElIcon class="c-p" :size="20" @click="closeChat">
            <Close />
          </ElIcon>
        </div>
      </div>
      <div class="flex h-[calc(100%-70px)] flex-col">
        <div
          class="flex-1 overflow-y-auto border-t-d px-4 py-7.5 [&::-webkit-scrollbar]:!w-1"
          ref="messageContainer"
        >
          <template v-for="(message, index) in messages" :key="index">
            <div
              :class="[
                'mb-7.5 flex w-full items-start gap-2',
                message.isMe ? 'flex-row-reverse' : 'flex-row'
              ]"
            >
              <ElAvatar :size="32" :src="message.avatar" class="shrink-0" />
              <div
                :class="['flex max-w-[70%] flex-col', message.isMe ? 'items-end' : 'items-start']"
              >
                <div
                  :class="[
                    'mb-1 flex gap-2 text-xs',
                    message.isMe ? 'flex-row-reverse' : 'flex-row'
                  ]"
                >
                  <span class="font-medium">{{ message.sender }}</span>
                  <span class="text-g-600">{{ message.time }}</span>
                </div>
                <div
                  :class="[
                    'rounded-md px-3.5 py-2.5 text-sm leading-[1.4] text-g-900',
                    message.isMe ? 'message-right bg-theme/15' : 'message-left bg-g-300/50'
                  ]"
                  >{{ message.content }}</div
                >
              </div>
            </div>
          </template>
        </div>

        <div class="px-4 pt-4">
          <ElInput
            v-model="messageText"
            type="textarea"
            :rows="3"
            placeholder="Type a message"
            resize="none"
            @keyup.enter.prevent="sendMessage"
          >
            <template #append>
              <div class="flex gap-2 py-2">
                <ElButton :icon="Paperclip" circle plain />
                <ElButton :icon="Picture" circle plain />
                <ElButton type="primary" @click="sendMessage" v-ripple>Send</ElButton>
              </div>
            </template>
          </ElInput>
          <div class="mt-3 flex-cb">
            <div class="flex-c">
              <ArtSvgIcon icon="ri:image-line" class="mr-5 c-p text-g-600 text-lg" />
              <ArtSvgIcon icon="ri:emotion-happy-line" class="mr-5 c-p text-g-600 text-lg" />
            </div>
            <ElButton type="primary" @click="sendMessage" v-ripple class="min-w-20">Send</ElButton>
          </div>
        </div>
      </div>
    </ElDrawer>
  </div>
</template>

<script setup lang="ts">
  import { Picture, Paperclip, Close } from '@element-plus/icons-vue'
  import { mittBus } from '@/utils/sys'
  import meAvatar from '@/assets/images/avatar/avatar5.webp'
  import aiAvatar from '@/assets/images/avatar/avatar10.webp'

  defineOptions({ name: 'ArtChatWindow' })

  interface ChatMessage {
    id: number
    sender: string
    content: string
    time: string
    isMe: boolean
    avatar: string
  }

  const MOBILE_BREAKPOINT = 640
  const SCROLL_DELAY = 100
  const BOT_NAME = 'Art Bot'
  const USER_NAME = 'Ricky'

  const { width } = useWindowSize()
  const isMobile = computed(() => width.value < MOBILE_BREAKPOINT)

  const isDrawerVisible = ref(false)
  const isOnline = ref(true)

  const messageText = ref('')
  const messageId = ref(10)
  const messageContainer = ref<HTMLElement | null>(null)

  const initializeMessages = (): ChatMessage[] => [
    {
      id: 1,
      sender: BOT_NAME,
      content: "Hello! I'm your AI assistant. How can I help you?",
      time: '10:00',
      isMe: false,
      avatar: aiAvatar
    },
    {
      id: 2,
      sender: USER_NAME,
      content: 'I want to learn how to use the system.',
      time: '10:01',
      isMe: true,
      avatar: meAvatar
    },
    {
      id: 3,
      sender: BOT_NAME,
      content: 'Sure, I will walk you through the main features. First, you can use the left menu to access different modules...',
      time: '10:02',
      isMe: false,
      avatar: aiAvatar
    },
    {
      id: 4,
      sender: USER_NAME,
      content: 'That sounds great. Can you explain the analytics section in more detail?',
      time: '10:05',
      isMe: true,
      avatar: meAvatar
    },
    {
      id: 5,
      sender: BOT_NAME,
      content: 'Of course. The analytics module helps you monitor key metrics in real time and generate detailed reports...',
      time: '10:06',
      isMe: false,
      avatar: aiAvatar
    },
    {
      id: 6,
      sender: USER_NAME,
      content: 'Great, how do I get started?',
      time: '10:08',
      isMe: true,
      avatar: meAvatar
    },
    {
      id: 7,
      sender: BOT_NAME,
      content: 'You can start by creating a project and adding the relevant data sources. The system will analyze them automatically.',
      time: '10:09',
      isMe: false,
      avatar: aiAvatar
    },
    {
      id: 8,
      sender: USER_NAME,
      content: 'Got it, thanks for the help!',
      time: '10:10',
      isMe: true,
      avatar: meAvatar
    },
    {
      id: 9,
      sender: BOT_NAME,
      content: "You're welcome. Let me know if you have any questions.",
      time: '10:11',
      isMe: false,
      avatar: aiAvatar
    }
  ]

  const messages = ref<ChatMessage[]>(initializeMessages())

  const formatCurrentTime = (): string => {
    return new Date().toLocaleTimeString([], {
      hour: '2-digit',
      minute: '2-digit'
    })
  }

  const scrollToBottom = (): void => {
    nextTick(() => {
      setTimeout(() => {
        if (messageContainer.value) {
          messageContainer.value.scrollTop = messageContainer.value.scrollHeight
        }
      }, SCROLL_DELAY)
    })
  }

  const sendMessage = (): void => {
    const text = messageText.value.trim()
    if (!text) return

    const newMessage: ChatMessage = {
      id: messageId.value++,
      sender: USER_NAME,
      content: text,
      time: formatCurrentTime(),
      isMe: true,
      avatar: meAvatar
    }

    messages.value.push(newMessage)
    messageText.value = ''
    scrollToBottom()
  }

  const openChat = (): void => {
    isDrawerVisible.value = true
    scrollToBottom()
  }

  const closeChat = (): void => {
    isDrawerVisible.value = false
  }

  onMounted(() => {
    scrollToBottom()
    mittBus.on('openChat', openChat)
  })

  onUnmounted(() => {
    mittBus.off('openChat', openChat)
  })
</script>
