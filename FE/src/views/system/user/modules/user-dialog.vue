<template>
  <ElDialog
    v-model="dialogVisible"
    :title="dialogType === 'add' ? 'Add user' : 'Edit user'"
    width="30%"
    align-center
  >
    <ElForm ref="formRef" :model="formData" :rules="rules" label-width="80px">
      <ElFormItem label="Username" prop="username">
        <ElInput v-model="formData.username" placeholder="Please enter a username" />
      </ElFormItem>
      <ElFormItem label="Phone" prop="phone">
        <ElInput v-model="formData.phone" placeholder="Please enter a phone number" />
      </ElFormItem>
      <ElFormItem label="Gender" prop="gender">
        <ElSelect v-model="formData.gender">
          <ElOption label="Male" value="Male" />
          <ElOption label="Female" value="Female" />
        </ElSelect>
      </ElFormItem>
      <ElFormItem label="Role" prop="role">
        <ElSelect v-model="formData.role" multiple>
          <ElOption
            v-for="role in roleList"
            :key="role.roleCode"
            :value="role.roleCode"
            :label="role.roleName"
          />
        </ElSelect>
      </ElFormItem>
    </ElForm>
    <template #footer>
      <div class="dialog-footer">
        <ElButton @click="dialogVisible = false">Cancel</ElButton>
        <ElButton type="primary" @click="handleSubmit">Submit</ElButton>
      </div>
    </template>
  </ElDialog>
</template>

<script setup lang="ts">
  import { ROLE_LIST_DATA } from '@/mock/temp/formData'
  import type { FormInstance, FormRules } from 'element-plus'

  interface Props {
    visible: boolean
    type: string
    userData?: Partial<Api.SystemManage.UserListItem>
  }

  interface Emits {
    (e: 'update:visible', value: boolean): void
    (e: 'submit'): void
  }

  const props = defineProps<Props>()
  const emit = defineEmits<Emits>()

  const roleList = ref(ROLE_LIST_DATA)

  const dialogVisible = computed({
    get: () => props.visible,
    set: (value) => emit('update:visible', value)
  })

  const dialogType = computed(() => props.type)

  const formRef = ref<FormInstance>()

  const formData = reactive({
    username: '',
    phone: '',
    gender: 'Male',
    role: [] as string[]
  })

  const rules: FormRules = {
    username: [
      { required: true, message: 'Please enter a username', trigger: 'blur' },
      { min: 2, max: 20, message: 'Length must be between 2 and 20 characters', trigger: 'blur' }
    ],
    phone: [
      { required: true, message: 'Please enter a phone number', trigger: 'blur' },
      { pattern: /^1[3-9]\d{9}$/, message: 'Please enter a valid phone number', trigger: 'blur' }
    ],
    gender: [{ required: true, message: 'Please select a gender', trigger: 'blur' }],
    role: [{ required: true, message: 'Please select a role', trigger: 'blur' }]
  }

  const initFormData = () => {
    const isEdit = props.type === 'edit' && props.userData
    const row = props.userData

    Object.assign(formData, {
      username: isEdit && row ? row.userName || '' : '',
      phone: isEdit && row ? row.userPhone || '' : '',
      gender: isEdit && row ? row.userGender || 'Male' : 'Male',
      role: isEdit && row ? (Array.isArray(row.userRoles) ? row.userRoles : []) : []
    })
  }

  watch(
    () => [props.visible, props.type, props.userData],
    ([visible]) => {
      if (visible) {
        initFormData()
        nextTick(() => {
          formRef.value?.clearValidate()
        })
      }
    },
    { immediate: true }
  )

  const handleSubmit = async () => {
    if (!formRef.value) return

    await formRef.value.validate((valid) => {
      if (valid) {
        ElMessage.success(dialogType.value === 'add' ? 'Added successfully' : 'Updated successfully')
        dialogVisible.value = false
        emit('submit')
      }
    })
  }
</script>
