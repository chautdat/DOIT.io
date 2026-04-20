<template>
  <div class="py-5">
    <h1 class="page-title">Stats Cards (Text)</h1>
    <ElRow :gutter="20">
      <ElCol :xs="24" :sm="12" :md="6" v-for="card in statsCards" :key="card.id" class="mb-5">
        <ArtStatsCard
          :icon="card.icon"
          :iconStyle="card.iconStyle"
          :title="card.title"
          :description="card.description"
          :showArrow="card.showArrow"
        />
      </ElCol>
    </ElRow>

    <h1 class="page-title">Stats Cards (Number Scroll)</h1>
    <ElRow :gutter="20">
      <ElCol :xs="24" :sm="12" :md="6" v-for="card in statsCards" :key="card.id" class="mb-5">
        <ArtStatsCard
          :icon="card.icon"
          :iconStyle="card.iconStyle"
          :count="card.count"
          :description="card.description"
          :decimals="0"
          :showArrow="card.showArrow"
          separator=","
        />
      </ElCol>
    </ElRow>

    <h1 class="page-title">Stats Cards (Custom Styles)</h1>
    <ElRow :gutter="20">
      <ElCol :xs="24" :sm="12" :md="6" v-for="card in statsCards" :key="card.id" class="mb-5">
        <ArtStatsCard
          :icon="card.icon"
          :iconStyle="card.customIconStyle"
          :boxStyle="card.boxStyle"
          :title="card.title"
          :description="card.description"
          :textColor="card.textColor"
          :showArrow="card.showArrow"
        />
      </ElCol>
    </ElRow>

    <h1 class="page-title">Progress Cards</h1>
    <ElRow :gutter="20">
      <ElCol :xs="24" :sm="12" :md="6" v-for="card in progressCards" :key="card.id" class="mb-5">
        <ArtProgressCard :percentage="card.percentage" :title="card.title" :color="card.color" />
      </ElCol>
    </ElRow>

    <h1 class="page-title">Progress Cards (Icon)</h1>
    <ElRow :gutter="20">
      <ElCol :xs="24" :sm="12" :md="6" v-for="card in progressCards" :key="card.id" class="mb-5">
        <ArtProgressCard
          :percentage="card.percentage"
          :title="card.title"
          :color="card.color"
          :icon="card.icon"
          :iconStyle="card.iconStyle"
        />
      </ElCol>
    </ElRow>

    <h1 class="page-title">Chart Cards (Small Charts)</h1>
    <ElRow :gutter="20">
      <ElCol :xs="24" :sm="12" :md="6" class="mb-5">
        <ArtLineChartCard
          :isMiniChart="true"
          :value="2545"
          label="New Users"
          date="Past 7 days"
          :percentage="1.2"
          :height="9.5"
          :chartData="[120, 132, 101, 134, 90, 230, 210]"
        />
      </ElCol>
      <ElCol :xs="24" :sm="12" :md="6" class="mb-5">
        <ArtBarChartCard
          :isMiniChart="true"
          :value="15480"
          label="Page Views"
          date="Past 14 days"
          :percentage="-4.15"
          :height="9.5"
          :barWidth="'45%'"
          :chartData="[120, 100, 150, 140, 90, 120, 130]"
        />
      </ElCol>
      <ElCol :xs="24" :sm="12" :md="6" class="mb-5">
        <ArtLineChartCard
          :isMiniChart="true"
          :value="2545"
          label="Followers"
          date="Past 30 days"
          :percentage="1.2"
          :height="9.5"
          :showAreaColor="true"
          :chartData="[150, 180, 160, 200, 180, 220, 240]"
        />
      </ElCol>
      <ElCol :xs="24" :sm="12" :md="6" class="mb-5">
        <ArtDonutChartCard
          :value="36358"
          title="Followers"
          :percentage="18"
          percentageLabel="vs Last Year"
          :data="[50, 40]"
          :height="9.5"
          currentValue="2022"
          previousValue="2021"
          :radius="['50%', '70%']"
        />
      </ElCol>
    </ElRow>

    <h1 class="page-title">Chart Cards (Large Charts)</h1>
    <ElRow :gutter="20">
      <ElCol :xs="24" :sm="12" :md="6" class="mb-5">
        <ArtLineChartCard
          :value="2545"
          label="New Users"
          :percentage="1.2"
          :height="11"
          :chartData="[120, 132, 101, 134, 90, 230, 210]"
        />
      </ElCol>
      <ElCol :xs="24" :sm="12" :md="6" class="mb-5">
        <ArtBarChartCard
          :value="15480"
          label="Page Views"
          :percentage="-4.15"
          :height="11"
          :chartData="[120, 100, 150, 140, 90, 120, 130, 110]"
        />
      </ElCol>
      <ElCol :xs="24" :sm="12" :md="6" class="mb-5">
        <ArtLineChartCard
          :value="2545"
          label="Followers"
          :percentage="1.2"
          :height="11"
          :showAreaColor="true"
          :chartData="[150, 180, 160, 200, 180, 220, 240]"
        />
      </ElCol>
      <ElCol :xs="24" :sm="12" :md="6" class="mb-5">
        <ArtDonutChartCard
          :value="36358"
          title="Followers"
          :percentage="-18"
          percentageLabel="vs 2021"
          :data="[70, 30]"
          :height="11"
          currentValue="December"
          previousValue="November"
        />
      </ElCol>
    </ElRow>

    <h1 class="page-title">Data List Cards</h1>
    <ElRow :gutter="20">
      <ElCol :xs="24" :sm="12" :lg="8" class="mb-5">
        <ArtDataListCard :list="dataList" title="To-Do List" subtitle="Tasks to process today" />
      </ElCol>
      <ElCol :xs="24" :sm="12" :lg="8" class="mb-5">
        <ArtDataListCard
          :maxCount="4"
          :list="dataList"
          title="Recent Activities"
          subtitle="Recent activity list"
          :showMoreButton="true"
          @more="handleMore"
        />
      </ElCol>
      <ElCol :xs="24" :sm="12" :lg="8" class="mb-5">
        <ArtTimelineListCard
          :list="timelineData"
          title="Recent Transactions"
          subtitle="December 20, 2024"
        />
      </ElCol>
    </ElRow>

    <h1 class="page-title">Image Cards</h1>
    <ElRow :gutter="20">
      <ElCol :xs="24" :sm="12" :md="6" v-for="card in imageCards" :key="card.id" class="mb-5">
        <ArtImageCard
          :imageUrl="card.imageUrl"
          :title="card.title"
          :category="card.category"
          :readTime="card.readTime"
          :views="card.views"
          :comments="card.comments"
          :date="card.date"
          @click="handleImageCardClick(card)"
        />
      </ElCol>
    </ElRow>
  </div>
</template>

<script setup lang="ts">
  import cover1 from '@imgs/cover/img1.webp'
  import cover2 from '@imgs/cover/img2.webp'
  import cover3 from '@imgs/cover/img3.webp'
  import cover4 from '@imgs/cover/img4.webp'

  defineOptions({ name: 'TemplateCards' })

  /**
   * Stats card data configuration
   */
  const statsCards = [
    {
      id: 1,
      title: 'Products Sold',
      count: 1235,
      description: 'Shoes, Jeans, Party Dresses, Watches',
      icon: 'ri:bar-chart-box-line',
      boxStyle: '!bg-theme/10',
      customIconStyle: '!text-theme !text-3xl',
      iconStyle: 'bg-info',
      textColor: 'var(--theme-color)',
      showArrow: false
    },
    {
      id: 2,
      title: 'Active Users',
      count: 5000,
      description: 'Daily active users over 5,000+',
      icon: 'ri:account-box-2-line',
      boxStyle: '!bg-warning/10',
      customIconStyle: '!text-warning !text-3xl',
      iconStyle: 'bg-warning',
      textColor: 'var(--art-warning)',
      showArrow: false
    },
    {
      id: 3,
      title: 'Total Revenue',
      count: 35000,
      description: 'Monthly revenue over $350,000+',
      icon: 'ri:money-cny-box-line',
      boxStyle: '!bg-secondary/10',
      customIconStyle: '!text-secondary !text-3xl',
      iconStyle: 'bg-secondary',
      textColor: 'var(--art-secondary)',
      showArrow: false
    },
    {
      id: 4,
      title: 'Customer Reviews',
      count: 4800,
      description: 'Average rating 4.8/5',
      icon: 'ri:message-3-line',
      boxStyle: '!bg-error/10',
      customIconStyle: '!text-error !text-3xl',
      iconStyle: 'bg-error',
      textColor: 'var(--art-error)',
      showArrow: false
    }
  ]

  const progressCards = [
    {
      id: 1,
      title: 'Completion Progress',
      percentage: 75,
      color: 'var(--art-success)',
      icon: 'ri:arrow-up-circle-line',
      iconStyle: 'bg-success/12 text-success'
    },
    {
      id: 2,
      title: 'Project Progress',
      percentage: 65,
      color: 'var(--theme-color)',
      icon: 'ri:twitch-line',
      iconStyle: 'bg-theme/12 text-theme'
    },
    {
      id: 3,
      title: 'Learning Progress',
      percentage: 45,
      color: 'var(--art-error)',
      icon: 'ri:game-line',
      iconStyle: '!bg-error/12 text-error'
    },
    {
      id: 4,
      title: 'Task Progress',
      percentage: 90,
      color: 'var(--art-secondary)',
      icon: 'ri:flag-2-line',
      iconStyle: 'bg-secondary/12 text-secondary'
    }
  ]

  const imageCards = [
    {
      id: 1,
      imageUrl: cover1,
      title: 'AI Technology Innovation and Development Prospects in Healthcare',
      category: 'Social',
      readTime: '2 min',
      views: 9125,
      comments: 3,
      date: 'Dec 19 Mon'
    },
    {
      id: 2,
      imageUrl: cover2,
      title: 'Big Data Analysis Helps Enterprise Decision Making Case Studies',
      category: 'Technology',
      readTime: '3 min',
      views: 7234,
      comments: 5,
      date: 'Dec 20 Tue'
    },
    {
      id: 3,
      imageUrl: cover3,
      title: 'Blockchain Technology Applications in Supply Chain Management',
      category: 'Tech',
      readTime: '4 min',
      views: 5678,
      comments: 8,
      date: 'Dec 21 Wed'
    },
    {
      id: 4,
      imageUrl: cover4,
      title: 'Cloud Computing Technology Trends and Future Outlook',
      category: 'Cloud Tech',
      readTime: '5 min',
      views: 4321,
      comments: 6,
      date: 'Dec 22 Thu'
    }
  ]

  const dataList = [
    {
      title: 'Singapore Trip',
      status: 'In Progress',
      time: '5 min',
      class: 'bg-theme/12 text-theme',
      icon: 'ri:camera-4-line'
    },
    {
      title: 'Archive Data',
      status: 'In Progress',
      time: '10 min',
      class: 'bg-secondary/12 text-secondary',
      icon: 'ri:bar-chart-box-line'
    },
    {
      title: 'Client Meeting',
      status: 'Pending',
      time: '15 min',
      class: 'bg-warning/12 text-warning',
      icon: 'ri:user-3-line'
    },
    {
      title: 'Screen Task Team',
      status: 'In Progress',
      time: '20 min',
      class: 'bg-error/12 text-error',
      icon: 'ri:account-circle-line'
    },
    {
      title: 'Send Envelope to Mike',
      status: 'Completed',
      time: '20 min',
      class: 'bg-success/12 text-success',
      icon: 'ri:message-3-line'
    }
  ]

  const timelineData = [
    {
      time: '09:30 AM',
      status: 'rgb(73, 190, 255)',
      content: 'Received $385.90 payment from John Doe'
    },
    {
      time: '10:00 AM',
      status: 'rgb(54, 158, 255)',
      content: 'New sales record',
      code: 'ML-3467'
    },
    {
      time: '12:00 PM',
      status: 'rgb(103, 232, 207)',
      content: 'Paid $64.95 to Michael'
    },
    {
      time: '02:30 PM',
      status: 'rgb(255, 193, 7)',
      content: 'System maintenance notice',
      code: 'MT-2023'
    },
    {
      time: '03:45 PM',
      status: 'rgb(255, 105, 105)',
      content: 'Urgent order cancellation alert',
      code: 'OR-9876'
    },
    {
      time: '05:00 PM',
      status: 'rgb(103, 232, 207)',
      content: 'Completed daily sales report'
    }
  ]

  /**
   * Handle view more button click
   */
  const handleMore = () => {
    ElMessage.info('View More')
  }

  /**
   * Image card type definition
   */
  interface ImageCard {
    id: number
    imageUrl: string
    title: string
    category: string
    readTime: string
    views: number
    comments: number
    date: string
  }

  /**
   * Handle image card click event
   * @param card Card data
   */
  const handleImageCardClick = (card: ImageCard) => {
    console.log('Card clicked:', card)
  }
</script>

<style scoped>
  @reference '@styles/core/tailwind.css';

  .page-title {
    @apply my-5 text-xl font-medium first:mt-0;
  }
</style>
