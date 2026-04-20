<template>
  <div>
    <ElForm @submit.prevent="addComment" class="w-full mx-auto mb-10">
      <ElFormItem prop="author" class="mt-5">
        <ElInput
          v-model="newComment.author"
          placeholder="Your name"
          class="block w-full"
          clearable
        />
      </ElFormItem>
      <ElFormItem prop="content">
        <ElInput
          v-model="newComment.content"
          placeholder="Say a few words..."
          type="textarea"
          :rows="5"
          clearable
        />
      </ElFormItem>
      <ElFormItem>
        <div class="flex justify-end w-full">
          <ElButton type="primary" @click="addComment"> Post </ElButton>
        </div>
      </ElFormItem>
    </ElForm>

    <ul>
      <div class="pb-5 text-lg font-medium"> Comments {{ comments.length }} </div>
      <CommentItem
        v-for="comment in comments.slice().reverse()"
        :key="comment.id"
        :comment="comment"
        :show-reply-form="showReplyForm"
        @toggle-reply="toggleReply"
        @add-reply="addReply"
        class="pb-2.5 mb-5 border-b border-g-400"
      />
    </ul>
  </div>
</template>

<script setup lang="ts">
  import { ref } from 'vue'
  import CommentItem from './widget/CommentItem.vue'
  import { commentList, Comment } from '@/mock/temp/commentDetail'
  const comments = commentList

  const newComment = ref<Partial<Comment>>({
    author: '',
    content: ''
  })

  const showReplyForm = ref<number | null>(null)

  const addComment = () => {
    if (!newComment.value.author?.trim() || !newComment.value.content?.trim()) {
      ElMessage.warning('Please fill in the full comment')
      return
    }

    comments.value.push({
      id: Date.now(),
      author: newComment.value.author.trim(),
      content: newComment.value.content.trim(),
      timestamp: new Date().toISOString(),
      replies: []
    })

    newComment.value.author = ''
    newComment.value.content = ''
    ElMessage.success('Comment posted successfully')
  }

  const addReply = (commentId: number, replyAuthor: string, replyContent: string) => {
    if (!replyAuthor?.trim() || !replyContent?.trim()) {
      ElMessage.warning('Please fill in the full reply')
      return
    }

    const comment = findComment(comments.value, commentId)
    if (comment) {
      comment.replies.push({
        id: Date.now(),
        author: replyAuthor.trim(),
        content: replyContent.trim(),
        timestamp: new Date().toISOString(),
        replies: []
      })
      showReplyForm.value = null
      ElMessage.success('Reply posted successfully')
    }
  }

  const toggleReply = (commentId: number) => {
    showReplyForm.value = showReplyForm.value === commentId ? null : commentId
  }

  const findComment = (comments: Comment[], commentId: number): Comment | undefined => {
    for (const comment of comments) {
      if (comment.id === commentId) {
        return comment
      }
      const found = findComment(comment.replies, commentId)
      if (found) {
        return found
      }
    }
    return undefined
  }
</script>
