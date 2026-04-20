<template>
  <ElDialog
    v-model="visible"
    :title="dialogType === 'add' ? 'Add Role' : 'Edit Role'"
    width="30%"
    align-center
    @close="handleClose"
  >
    <ElForm ref="formRef" :model="form" :rules="rules" label-width="120px">
      <ElFormItem label="Role Name" prop="roleName">
        <ElInput v-model="form.roleName" placeholder="Enter role name" />
      </ElFormItem>
      <ElFormItem label="Role Code" prop="roleCode">
        <ElInput v-model="form.roleCode" placeholder="Enter role code" />
      </ElFormItem>
      <ElFormItem label="Description" prop="description">
        <ElInput
          v-model="form.description"
          type="textarea"
          :rows="3"
          placeholder="Enter role description"
        />
      </ElFormItem>
      <ElFormItem label="Enabled">
        <ElSwitch v-model="form.enabled" />
      </ElFormItem>
    </ElForm>
    <template #footer>
      <ElButton @click="handleClose">Cancel</ElButton>
      <ElButton type="primary" @click="handleSubmit">Submit</ElButton>
    </template>
  </ElDialog>
</template>

<script setup lang="ts">
  import type { FormInstance, FormRules } from 'element-plus'

  type RoleListItem = Api.SystemManage.RoleListItem

  interface Props {
    modelValue: boolean
    dialogType: 'add' | 'edit'
    roleData?: RoleListItem
  }

  interface Emits {
    (e: 'update:modelValue', value: boolean): void
    (e: 'success'): void
  }

  const props = withDefaults(defineProps<Props>(), {
    modelValue: false,
    dialogType: 'add',
    roleData: undefined
  })

  const emit = defineEmits<Emits>()

  const formRef = ref<FormInstance>()

  /**
   * Dialog visibility two-way binding
   */
  const visible = computed({
    get: () => props.modelValue,
    set: (value) => emit('update:modelValue', value)
  })

  /**
   * Form validation rules
   */
  const rules = reactive<FormRules>({
    roleName: [
      { required: true, message: 'Please enter role name', trigger: 'blur' },
      { min: 2, max: 20, message: 'Length should be 2 to 20 characters', trigger: 'blur' }
    ],
    roleCode: [
      { required: true, message: 'Please enter role code', trigger: 'blur' },
      { min: 2, max: 50, message: 'Length should be 2 to 50 characters', trigger: 'blur' }
    ],
    description: [{ required: true, message: 'Please enter description', trigger: 'blur' }]
  })

  /**
   * Form data
   */
  const form = reactive<RoleListItem>({
    roleId: 0,
    roleName: '',
    roleCode: '',
    description: '',
    createTime: '',
    enabled: true
  })

  /**
   * Watch dialog open, initialize form data
   */
  watch(
    () => props.modelValue,
    (newVal) => {
      if (newVal) initForm()
    }
  )

  /**
   * Watch role data change, update form
   */
  watch(
    () => props.roleData,
    (newData) => {
      if (newData && props.modelValue) initForm()
    },
    { deep: true }
  )

  /**
   * Initialize form data
   * Fill form based on dialog type or reset form
   */
  const initForm = () => {
    if (props.dialogType === 'edit' && props.roleData) {
      Object.assign(form, props.roleData)
    } else {
      Object.assign(form, {
        roleId: 0,
        roleName: '',
        roleCode: '',
        description: '',
        createTime: '',
        enabled: true
      })
    }
  }

  /**
   * Close dialog and reset form
   */
  const handleClose = () => {
    visible.value = false
    formRef.value?.resetFields()
  }

  /**
   * Submit form
   * Validate and call API to save data
   */
  const handleSubmit = async () => {
    if (!formRef.value) return

    try {
      await formRef.value.validate()
      // TODO: Call add/edit API
      const message = props.dialogType === 'add' ? 'Added successfully' : 'Updated successfully'
      ElMessage.success(message)
      emit('success')
      handleClose()
    } catch (error) {
      console.log('Form validation failed:', error)
    }
  }
</script>
