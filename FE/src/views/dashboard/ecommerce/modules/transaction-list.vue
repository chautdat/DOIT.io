<template>
  <ArtDataListCard
    class="mb-5 max-sm:mb-4"
    :maxCount="4"
    :list="dataList"
    title="Recent Activity"
    subtitle="Order Processing Status"
    :showMoreButton="true"
    @more="handleMore"
  />
</template>

<script setup lang="ts">
  interface TransactionItem {
    title: string
    status: string
    time: string
    class: string
    icon: string
  }

  /**
   * Recent activity data list
   * Display order processing, refund requests, complaint handling and other business activity status
   */
  const dataList: TransactionItem[] = [
    {
      title: 'New Order #38291',
      status: 'Pending',
      time: '5 min',
      class: 'bg-theme/12 text-theme',
      icon: 'ri:shopping-bag-4-line'
    },
    {
      title: 'Refund Request #12845',
      status: 'Processing',
      time: '10 min',
      class: 'bg-secondary/12 text-secondary',
      icon: 'ri:profile-line'
    },
    {
      title: 'Customer Complaint',
      status: 'Pending',
      time: '15 min',
      class: 'bg-warning/12 text-warning',
      icon: 'ri:customer-service-2-line'
    },
    {
      title: 'Low Stock Alert',
      status: 'Urgent',
      time: '20 min',
      class: 'bg-danger/12 text-danger',
      icon: 'ri:box-1-line'
    },
    {
      title: 'Order #29384 Shipped',
      status: 'Completed',
      time: '20 min',
      class: 'bg-success/12 text-success',
      icon: 'ri:shopping-bag-3-line'
    }
  ]

  /**
   * Handle view more button click event
   */
  const handleMore = (): void => {
    // TODO: Add view more logic
  }
</script>
