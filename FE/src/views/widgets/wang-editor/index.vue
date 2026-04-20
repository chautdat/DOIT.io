<template>
  <div class="page-content mb-5">
    <!-- Full toolbar editor -->
    <ElCard class="editor-card">
      <template #header>
        <div class="card-header">
          <span>🛠️ Full Toolbar Editor</span>
          <div class="header-buttons">
            <ElButton size="small" @click="clearFullEditor">Clear</ElButton>
            <ElButton size="small" @click="getFullEditorContent">Get Content</ElButton>
            <ElButton size="small" @click="setFullEditorDemo">Set Demo</ElButton>
          </div>
        </div>
      </template>

      <ArtWangEditor
        ref="fullEditorRef"
        v-model="fullEditorHtml"
        height="400px"
        placeholder="Enter content, experience full editing features..."
        :exclude-keys="[]"
      />
    </ElCard>

    <!-- Simple toolbar editor -->
    <ElCard class="editor-card">
      <template #header>
        <div class="card-header">
          <span>✨ Simple Toolbar Editor</span>
          <div class="header-buttons">
            <ElButton size="small" @click="clearSimpleEditor">Clear</ElButton>
            <ElButton size="small" @click="getSimpleEditorContent">Get Content</ElButton>
            <ElButton size="small" @click="setSimpleEditorDemo">Set Demo</ElButton>
          </div>
        </div>
      </template>

      <ArtWangEditor
        ref="simpleEditorRef"
        v-model="simpleEditorHtml"
        height="400px"
        placeholder="Enter content, experience simplified editing features..."
        :toolbar-keys="simpleToolbarKeys"
      />
    </ElCard>

    <!-- Content preview comparison -->
    <ElCard class="preview-card">
      <template #header>
        <span>📖 Content Preview Comparison</span>
      </template>

      <ElRow :gutter="20">
        <ElCol :span="12">
          <h3>Full Editor Content</h3>
          <ElTabs v-model="fullActiveTab">
            <ElTabPane label="Preview" name="preview">
              <div class="content-preview" v-html="fullEditorHtml"></div>
            </ElTabPane>
            <ElTabPane label="HTML Source" name="html">
              <ElInput
                v-model="fullEditorHtml"
                type="textarea"
                :rows="8"
                placeholder="HTML Source"
                readonly
              />
            </ElTabPane>
          </ElTabs>
        </ElCol>

        <ElCol :span="12">
          <h3>Simple Editor Content</h3>
          <ElTabs v-model="simpleActiveTab">
            <ElTabPane label="Preview" name="preview">
              <div class="content-preview" v-html="simpleEditorHtml"></div>
            </ElTabPane>
            <ElTabPane label="HTML Source" name="html">
              <ElInput
                v-model="simpleEditorHtml"
                type="textarea"
                :rows="8"
                placeholder="HTML Source"
                readonly
              />
            </ElTabPane>
          </ElTabs>
        </ElCol>
      </ElRow>
    </ElCard>

    <!-- Usage Guide -->
    <ElCard class="usage-card">
      <template #header>
        <span>📚 Usage Guide</span>
      </template>

      <ElCollapse v-model="activeCollapse">
        <ElCollapseItem title="Basic Usage" name="basic">
          <pre><code class="language-vue">&lt;template&gt;
          &lt;ArtWangEditor v-model="content" /&gt;
          &lt;/template&gt;

          &lt;script setup lang="ts"&gt;
          import { ref } from 'vue'

          const content = ref('&lt;p&gt;Initial content&lt;/p&gt;')
          &lt;/script&gt;</code></pre>
        </ElCollapseItem>

        <ElCollapseItem title="Full Toolbar Configuration" name="full">
          <pre><code class="language-vue">&lt;template&gt;
          &lt;!-- Show all tools, exclude nothing --&gt;
          &lt;ArtWangEditor
          v-model="content"
          :exclude-keys="[]"
          /&gt;
          &lt;/template&gt;</code></pre>
        </ElCollapseItem>

        <ElCollapseItem title="Simple Toolbar Configuration" name="simple">
          <pre><code class="language-vue">&lt;template&gt;
          &lt;!-- Only show basic editing tools --&gt;
          &lt;ArtWangEditor
          v-model="content"
          :toolbar-keys="[
          'bold', 'italic', 'underline', '|',
          'bulletedList', 'numberedList', '|',
          'insertLink', 'insertImage', '|',
          'undo', 'redo'
          ]"
          /&gt;
          &lt;/template&gt;</code></pre>
        </ElCollapseItem>

        <ElCollapseItem title="Custom Configuration" name="config">
          <pre><code class="language-vue">&lt;template&gt;
          &lt;ArtWangEditor
          v-model="content"
          height="600px"
          placeholder="Enter your content..."
          :exclude-keys="['fontFamily', 'fontSize']"
          :upload-config="{
          maxFileSize: 5 * 1024 * 1024,
          maxNumberOfFiles: 5
          }"
          /&gt;
          &lt;/template&gt;</code></pre>
        </ElCollapseItem>

        <ElCollapseItem title="Component Methods" name="methods">
          <pre><code class="language-vue">&lt;template&gt;
          &lt;ArtWangEditor ref="editorRef" v-model="content" /&gt;
          &lt;el-button @click="handleClear"&gt;Clear&lt;/el-button&gt;
          &lt;el-button @click="handleFocus"&gt;Focus&lt;/el-button&gt;
          &lt;el-button @click="handleGetContent"&gt;Get Content&lt;/el-button&gt;
          &lt;/template&gt;

          &lt;script setup lang="ts"&gt;
          import { ref } from 'vue'

          const editorRef = ref()
          const content = ref('')

          const handleClear = () =&gt; {
          editorRef.value?.clear()
          }

          const handleFocus = () =&gt; {
          editorRef.value?.focus()
          }

          const handleGetContent = () =&gt; {
          const html = editorRef.value?.getHtml()
          console.log('Editor content:', html)
          }
          &lt;/script&gt;</code></pre>
        </ElCollapseItem>

        <ElCollapseItem title="Toolbar Configuration Guide" name="toolbar-config">
          <div class="toolbar-explanation">
            <h4>Full Toolbar vs Simple Toolbar</h4>
            <ElRow :gutter="16">
              <ElCol :span="12">
                <h5>✅ Full Toolbar includes:</h5>
                <ul>
                  <li>Text format: Bold, Italic, Underline, Font color, Background color</li>
                  <li>Paragraph format: Heading, Quote, Alignment, Indent</li>
                  <li>Lists: Ordered list, Unordered list, Todo list</li>
                  <li>Insert: Link, Image, Table, Divider, Emoji</li>
                  <li>Code: Code block, Inline code</li>
                  <li>Actions: Undo, Redo, Fullscreen, Clear format</li>
                </ul>
              </ElCol>
              <ElCol :span="12">
                <h5>⚡ Simple Toolbar includes:</h5>
                <ul>
                  <li>Basic format: Bold, Italic, Underline</li>
                  <li>Lists: Ordered list, Unordered list</li>
                  <li>Insert: Link, Image</li>
                  <li>Actions: Undo, Redo</li>
                </ul>
                <p class="note"
                  >Suitable for simple text editing scenarios with a cleaner interface.</p
                >
              </ElCol>
            </ElRow>
          </div>
        </ElCollapseItem>
      </ElCollapse>
    </ElCard>
  </div>
</template>

<script setup lang="ts">
  defineOptions({ name: 'WidgetsWangEditor' })

  const fullEditorRef = ref()
  const simpleEditorRef = ref()
  const fullActiveTab = ref('preview')
  const simpleActiveTab = ref('preview')
  const activeCollapse = ref(['basic'])

  /**
   * Simple toolbar configuration
   * Only includes basic editing features
   */
  const simpleToolbarKeys = [
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

  // Full editor content
  const fullEditorHtml = ref(`<h1>🎨 Full Toolbar Editor Example</h1>
<p>This editor includes all features, you can experience rich text editing functions.</p>

<h2>✨ Text Styles</h2>
<p><strong>This is bold text</strong></p>
<p><em>This is italic text</em></p>
<p><u>This is underlined text</u></p>
<p><span style="color: rgb(194, 79, 74);">This is colored text</span></p>

<h2>📝 Lists and Todos</h2>
<ul>
  <li>Unordered list item 1</li>
  <li>Unordered list item 2</li>
</ul>

<ol>
  <li>Ordered list item 1</li>
  <li>Ordered list item 2</li>
</ol>

<ul class="w-e-todo">
  <li class="w-e-todo-item"><input type="checkbox" checked="true" readonly="true" disabled="disabled"><span>Completed task</span></li>
  <li class="w-e-todo-item"><input type="checkbox" readonly="true" disabled="disabled"><span>Pending task</span></li>
</ul>

<h2>💬 Quote and Table</h2>
<blockquote>
  This is a quoted text, showing the quote format effect.
</blockquote>

<table style="border-collapse: collapse; width: 100%;" border="1">
  <thead>
    <tr><th>Feature</th><th>Description</th></tr>
  </thead>
  <tbody>
    <tr><td>Full Toolbar</td><td>Includes all editing features</td></tr>
    <tr><td>Custom Config</td><td>Supports flexible toolbar configuration</td></tr>
  </tbody>
</table>

<h2>💻 Code Block</h2>
<pre><code class="language-javascript">// Full editor supports code highlighting
function createEditor() {
  return new WangEditor({
    container: '#editor',
    toolbar: 'full' // Full toolbar
  });
}</code></pre>

<p>🔗 <a href="https://www.wangeditor.com/" target="_blank">Visit official website for more</a></p>`)

  // Simple editor content
  const simpleEditorHtml = ref(`<h1>✨ Simple Toolbar Editor Example</h1>
<p>This editor only includes basic editing features with a cleaner interface.</p>

<h2>Basic Text Formatting</h2>
<p><strong>Bold text</strong></p>
<p><em>Italic text</em></p>
<p><u>Underlined text</u></p>

<h2>List Features</h2>
<ul>
  <li>Unordered list item 1</li>
  <li>Unordered list item 2</li>
</ul>

<ol>
  <li>Ordered list item 1</li>
  <li>Ordered list item 2</li>
</ol>

<h2>Links and Images</h2>
<p>Supports inserting <a href="https://www.wangeditor.com/" target="_blank">links</a> and images.</p>

<p>The simplified editor focuses on basic features, suitable for simple content editing needs.</p>`)

  /**
   * Clear full editor content
   */
  const clearFullEditor = () => {
    fullEditorRef.value?.clear()
    ElMessage.success('Full editor cleared')
  }

  /**
   * Get full editor content
   */
  const getFullEditorContent = () => {
    const content = fullEditorRef.value?.getHtml()
    console.log('Full editor content:', content)
    ElMessage.success('Full editor content output to console')
  }

  /**
   * Set full editor demo content
   */
  const setFullEditorDemo = () => {
    const demoContent = `<h2>🎉 Full Editor Demo Content</h2>
<p>This is demo content set via method, showcasing the powerful features of the full editor.</p>
<ul>
  <li>Supports rich text formatting</li>
  <li>Includes advanced features like tables and code blocks</li>
  <li>Provides complete editing experience</li>
</ul>
<table style="border-collapse: collapse; width: 100%;" border="1">
  <tr><th>Feature</th><th>Status</th></tr>
  <tr><td>Full Toolbar</td><td>✅ Enabled</td></tr>
  <tr><td>Advanced Features</td><td>✅ Enabled</td></tr>
</table>`

    fullEditorRef.value?.setHtml(demoContent)
    ElMessage.success('Full editor demo content set')
  }

  /**
   * Clear simple editor content
   */
  const clearSimpleEditor = () => {
    simpleEditorRef.value?.clear()
    ElMessage.success('Simple editor cleared')
  }

  /**
   * Get simple editor content
   */
  const getSimpleEditorContent = () => {
    const content = simpleEditorRef.value?.getHtml()
    console.log('Simple editor content:', content)
    ElMessage.success('Simple editor content output to console')
  }

  /**
   * Set simple editor demo content
   */
  const setSimpleEditorDemo = () => {
    const demoContent = `<h2>⚡ Simple Editor Demo Content</h2>
<p>This is demo content set via method, showcasing the core features of the simple editor.</p>
<ul>
  <li><strong>Basic formatting</strong>: Bold, Italic, Underline</li>
  <li><em>List support</em>: Ordered and Unordered lists</li>
  <li><u>Media insertion</u>: Links and Images</li>
</ul>
<ol>
  <li>Clean and simple interface</li>
  <li>Focused practical features</li>
  <li>Suitable for quick editing</li>
</ol>
<p>🔗 <a href="https://example.com" target="_blank">This is a link example</a></p>`

    simpleEditorRef.value?.setHtml(demoContent)
    ElMessage.success('Simple editor demo content set')
  }
</script>

<style lang="scss" scoped>
  .page-content {
    padding: 20px;
  }

  .editor-card {
    margin-bottom: 24px;
  }

  .card-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .header-buttons {
    display: flex;
    gap: 8px;
  }

  .preview-card {
    margin-bottom: 24px;
  }

  .preview-card h3 {
    margin: 0 0 16px;
    font-size: 16px;
    color: var(--el-text-color-primary);
  }

  .content-preview {
    min-height: 200px;
    max-height: 300px;
    padding: 16px;
    overflow-y: auto;
    background-color: var(--el-bg-color);
    border: 1px solid var(--el-border-color);
    border-radius: 6px;
  }

  .content-preview :deep(h1),
  .content-preview :deep(h2),
  .content-preview :deep(h3) {
    margin: 16px 0 8px;
  }

  .content-preview :deep(p) {
    margin: 8px 0;
    line-height: 1.6;
  }

  .content-preview :deep(table) {
    margin: 16px 0;
  }

  .content-preview :deep(table th),
  .content-preview :deep(table td) {
    padding: 8px 12px;
  }

  .content-preview :deep(pre) {
    padding: 12px;
    margin: 16px 0;
    overflow-x: auto;
    background-color: var(--el-fill-color-light);
    border-radius: 4px;
  }

  .content-preview :deep(blockquote) {
    padding-left: 16px;
    margin: 16px 0;
    color: var(--el-text-color-regular);
    border-left: 4px solid var(--el-color-primary);
  }

  .usage-card :deep(.el-collapse-item__content) {
    padding-bottom: 16px;
  }

  .usage-card pre {
    padding: 16px;
    margin: 0;
    overflow-x: auto;
    background-color: var(--el-fill-color-light);
    border-radius: 6px;
  }

  .usage-card pre code {
    font-family: Consolas, Monaco, 'Courier New', monospace;
    font-size: 14px;
    line-height: 1.5;
  }

  .toolbar-explanation h4 {
    margin: 0 0 16px;
    color: var(--el-text-color-primary);
  }

  .toolbar-explanation h5 {
    margin: 0 0 8px;
    font-size: 14px;
    color: var(--el-text-color-regular);
  }

  .toolbar-explanation ul {
    padding-left: 20px;
    margin: 8px 0 16px;
  }

  .toolbar-explanation ul li {
    margin: 4px 0;
    font-size: 13px;
    color: var(--el-text-color-regular);
  }

  .toolbar-explanation .note {
    margin: 8px 0 0;
    font-size: 12px;
    font-style: italic;
    color: var(--el-text-color-placeholder);
  }

  @media (width <= 768px) {
    .page-content {
      padding: 12px;
    }

    .card-header {
      flex-direction: column;
      gap: 12px;
      align-items: stretch !important;
    }

    .header-buttons {
      justify-content: center;
    }

    .preview-card :deep(.el-col) {
      margin-bottom: 16px;
    }
  }
</style>
