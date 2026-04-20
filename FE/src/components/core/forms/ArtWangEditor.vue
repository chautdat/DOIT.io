<!-- WangEditor Rich Text Editor Plugin: https://www.wangeditor.com/ -->
<template>
  <div class="editor-wrapper">
    <Toolbar
      class="editor-toolbar"
      :editor="editorRef"
      :mode="mode"
      :defaultConfig="toolbarConfig"
    />
    <Editor
      :style="{ height: height, overflowY: 'hidden' }"
      v-model="modelValue"
      :mode="mode"
      :defaultConfig="editorConfig"
      @onCreated="onCreateEditor"
    />
  </div>
</template>

<script setup lang="ts">
  import '@wangeditor/editor/dist/css/style.css'
  import { onBeforeUnmount, onMounted, shallowRef, computed } from 'vue'
  import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
  import { useUserStore } from '@/store/modules/user'
  import EmojiText from '@/utils/ui/emojo'
  import { IDomEditor, IToolbarConfig, IEditorConfig } from '@wangeditor/editor'
  import request from '@/utils/http'

  defineOptions({ name: 'ArtWangEditor' })

  type InsertFnType = (url: string, alt: string, href: string) => void

  const { VITE_API_URL } = import.meta.env

  // Props definition
  interface Props {
    /** Editor height */
    height?: string
    /** Custom toolbar keys */
    toolbarKeys?: string[]
    /** Insert new tools at specified position */
    insertKeys?: { index: number; keys: string[] }
    /** Excluded toolbar items */
    excludeKeys?: string[]
    /** Editor mode */
    mode?: 'default' | 'simple'
    /** Placeholder text */
    placeholder?: string
    /** Upload configuration */
    uploadConfig?: {
      maxFileSize?: number
      maxNumberOfFiles?: number
      server?: string
      // Whether to enable custom upload
      isCustomUpload?: boolean
    }
  }

  const props = withDefaults(defineProps<Props>(), {
    height: '500px',
    mode: 'default',
    placeholder: 'Enter content...',
    excludeKeys: () => ['fontFamily'],
    isCustomUpload: false
  })

  const modelValue = defineModel<string>({ required: true })

  // Editor instance
  const editorRef = shallowRef<IDomEditor>()
  const userStore = useUserStore()

  // Constant configuration
  const DEFAULT_UPLOAD_CONFIG = {
    maxFileSize: 3 * 1024 * 1024, // 3MB
    maxNumberOfFiles: 10,
    fieldName: 'file',
    allowedFileTypes: ['image/*']
  } as const

  // Computed: upload server address
  const uploadServer = computed(
    () => props.uploadConfig?.server || `${VITE_API_URL}/api/common/upload/wangeditor`
  )

  // Merge upload configuration
  const mergedUploadConfig = computed(() => ({
    ...DEFAULT_UPLOAD_CONFIG,
    ...props.uploadConfig
  }))

  // Toolbar configuration
  const toolbarConfig = computed((): Partial<IToolbarConfig> => {
    const config: Partial<IToolbarConfig> = {}

    // Fully custom toolbar
    if (props.toolbarKeys && props.toolbarKeys.length > 0) {
      config.toolbarKeys = props.toolbarKeys
    }

    // Insert new tools
    if (props.insertKeys) {
      config.insertKeys = props.insertKeys
    }

    // Exclude tools
    if (props.excludeKeys && props.excludeKeys.length > 0) {
      config.excludeKeys = props.excludeKeys
    }

    return config
  })

  // Editor configuration
  const editorConfig: Partial<IEditorConfig> = {
    placeholder: props.placeholder,
    MENU_CONF: {
      uploadImage: {
        fieldName: mergedUploadConfig.value.fieldName,
        maxFileSize: mergedUploadConfig.value.maxFileSize,
        maxNumberOfFiles: mergedUploadConfig.value.maxNumberOfFiles,
        allowedFileTypes: mergedUploadConfig.value.allowedFileTypes,
        server: uploadServer.value,
        headers: {
          Authorization: userStore.accessToken
        },
        onSuccess() {
          ElMessage.success(`Image uploaded successfully ${EmojiText[200]}`)
        },
        onError(file: File, err: any, res: any) {
          console.error('Image upload failed:', err, res)
          ElMessage.error(`Image upload failed ${EmojiText[500]}`)
        }
      }
    }
  }

  // Custom upload
  if (props.uploadConfig?.isCustomUpload && props.uploadConfig?.server && editorConfig.MENU_CONF) {
    editorConfig.MENU_CONF.uploadImage.customUpload = async (
      file: File,
      insertFn: InsertFnType
    ) => {
      try {
        const formData = new FormData()
        formData.append(mergedUploadConfig.value.fieldName, file)

        const response = await request.post<{ url: string; alt: string; href: string }>({
          url: props.uploadConfig?.server,
          data: formData,
          headers: {
            'Content-Type': 'multipart/form-data',
            Authorization: userStore.accessToken
          }
        })

        const { url, alt, href } = response

        if (!url) {
          throw new Error('Upload failed, please check server configuration')
        }

        insertFn(url, alt, href)
        ElMessage.success(`Image uploaded successfully ${EmojiText[200]}`)
      } catch (error) {
        console.error('Image upload failed:', error)
        ElMessage.error(`Image upload failed ${EmojiText[500]}`)
      }
    }
  }

  // Editor created callback
  const onCreateEditor = (editor: IDomEditor) => {
    editorRef.value = editor

    // Listen for fullscreen event
    editor.on('fullScreen', () => {
      console.log('Editor entered fullscreen mode')
    })

    // Ensure custom icons are applied after editor creation
    applyCustomIcons()
  }

  // Apply custom icons (with retry mechanism)
  const applyCustomIcons = () => {
    let retryCount = 0
    const maxRetries = 10
    const retryDelay = 100

    const tryApplyIcons = () => {
      const editor = editorRef.value
      if (!editor) {
        if (retryCount < maxRetries) {
          retryCount++
          setTimeout(tryApplyIcons, retryDelay)
        }
        return
      }

      // Get current editor toolbar container
      const editorContainer = editor.getEditableContainer().closest('.editor-wrapper')
      if (!editorContainer) {
        if (retryCount < maxRetries) {
          retryCount++
          setTimeout(tryApplyIcons, retryDelay)
        }
        return
      }

      const toolbar = editorContainer.querySelector('.w-e-toolbar')
      const toolbarButtons = editorContainer.querySelectorAll('.w-e-bar-item button[data-menu-key]')

      if (toolbar && toolbarButtons.length > 0) {
        return
      }

      // If toolbar hasn't finished rendering, continue retry
      if (retryCount < maxRetries) {
        retryCount++
        setTimeout(tryApplyIcons, retryDelay)
      } else {
        console.warn(
          'Toolbar render timeout, unable to apply custom icons - editor instance:',
          editor.id
        )
      }
    }

    // Use requestAnimationFrame to ensure execution in next frame
    requestAnimationFrame(tryApplyIcons)
  }

  // Expose editor instance and methods
  defineExpose({
    /** Get editor instance */
    getEditor: () => editorRef.value,
    /** Set editor content */
    setHtml: (html: string) => editorRef.value?.setHtml(html),
    /** Get editor content */
    getHtml: () => editorRef.value?.getHtml(),
    /** Clear editor */
    clear: () => editorRef.value?.clear(),
    /** Focus editor */
    focus: () => editorRef.value?.focus()
  })

  // Lifecycle
  onMounted(() => {
    // Icon replacement handled in onCreateEditor
  })

  onBeforeUnmount(() => {
    const editor = editorRef.value
    if (editor) {
      editor.destroy()
    }
  })
</script>

<style lang="scss">
  @use './ArtWangEditor.scss';
</style>
