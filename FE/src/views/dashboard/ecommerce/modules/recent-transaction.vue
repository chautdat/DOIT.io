<template>
  <ArtTimelineListCard
    :list="timelineData"
    title="Recent Transactions"
    subtitle="Today's Order Activity"
    class="h-[27.8rem] mb-5 max-sm:mb-4"
  />
</template>

<script setup lang="ts">
  interface TimelineItem {
    time: string
    status: string
    content: string
    code?: string
  }

  /**
   * Recent transaction timeline data
   * Record today's order processing, product listing, payment and other key activities
   */
  const timelineData: TimelineItem[] = [
    {
      time: '09:30 AM',
      status: 'rgb(73, 190, 255)',
      content: 'Received payment $385.90 for Order #38291'
    },
    {
      time: '10:00 AM',
      status: 'rgb(54, 158, 255)',
      content: 'New product listed',
      code: 'SKU-3467'
    },
    {
      time: '12:00 PM',
      status: 'rgb(103, 232, 207)',
      content: 'Paid $6,495.00 to supplier'
    },
    {
      time: '02:30 PM',
      status: 'rgb(255, 193, 7)',
      content: 'Promotion campaign started',
      code: 'PROMO-2023'
    },
    {
      time: '03:45 PM',
      status: 'rgb(255, 105, 105)',
      content: 'Order cancellation alert',
      code: 'ORD-9876'
    },
    {
      time: '05:00 PM',
      status: 'rgb(103, 232, 207)',
      content: 'Completed daily sales report'
    }
  ]
</script>
