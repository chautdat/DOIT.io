<template>
  <div class="art-card p-5 h-[27.8rem] mb-5 overflow-hidden max-sm:mb-4">
    <div class="art-card-header">
      <div class="title">
        <h4>Hot Products</h4>
        <p>This Month's Sales</p>
      </div>
    </div>

    <ElScrollbar style="height: 21.55rem" class="w-full">
      <ArtTable
        :data="tableData"
        style="margin-top: 0 !important"
        :border="false"
        :stripe="false"
        :header-cell-style="{ background: 'transparent' }"
      >
        <template #default>
          <ElTableColumn label="Product" prop="product" width="220px">
            <template #default="scope">
              <div class="flex-c">
                <img class="size-12.5 object-cover rounded-md" :src="scope.row.image" />
                <div class="flex flex-col ml-3">
                  <div class="font-medium">{{ scope.row.name }}</div>
                  <div class="text-xs text-slate-500">{{ scope.row.category }}</div>
                </div>
              </div>
            </template>
          </ElTableColumn>
          <ElTableColumn label="Price" prop="price">
            <template #default="scope">
              <span class="font-semibold">${{ scope.row.price.toLocaleString() }}</span>
            </template>
          </ElTableColumn>
          <ElTableColumn label="Stock" prop="stock">
            <template #default="scope">
              <div
                class="inline-block px-2 py-1 text-xs font-medium rounded"
                :class="getStockClass(scope.row.stock)"
              >
                {{ getStockStatus(scope.row.stock) }}
              </div>
            </template>
          </ElTableColumn>
          <ElTableColumn label="Sales" prop="sales" />
          <ElTableColumn label="Sales Trend" width="240">
            <template #default="scope">
              <ElProgress :percentage="scope.row.pro" :color="scope.row.color" :stroke-width="4" />
            </template>
          </ElTableColumn>
        </template>
      </ArtTable>
    </ElScrollbar>
  </div>
</template>

<script setup lang="ts">
  import product1 from '@/assets/images/3d/icon1.webp'
  import product2 from '@/assets/images/3d/icon2.webp'
  import product3 from '@/assets/images/3d/icon3.webp'
  import product4 from '@/assets/images/3d/icon4.webp'
  import product5 from '@/assets/images/3d/icon5.webp'
  import product6 from '@/assets/images/3d/icon6.webp'

  interface ProductItem {
    name: string
    category: string
    price: number
    stock: number
    sales: number
    percentage: number
    pro: number
    color: string
    image: string
  }

  const ANIMATION_DELAY = 100
  const STOCK_THRESHOLD = {
    LOW: 20,
    MEDIUM: 50
  } as const

  /**
   * Hot products table data
   * Contains product info, stock, sales and sales trend
   */
  const tableData = reactive<ProductItem[]>([
    {
      name: 'Smart Watch Pro',
      category: 'Electronics',
      price: 1299,
      stock: 156,
      sales: 423,
      percentage: 75,
      pro: 0,
      color: 'var(--art-primary)',
      image: product1
    },
    {
      name: 'Wireless Bluetooth Earbuds',
      category: 'Audio Equipment',
      price: 499,
      stock: 89,
      sales: 652,
      percentage: 85,
      pro: 0,
      color: 'var(--art-success)',
      image: product2
    },
    {
      name: 'Mechanical Keyboard',
      category: 'Computer Accessories',
      price: 399,
      stock: 12,
      sales: 238,
      percentage: 45,
      pro: 0,
      color: 'var(--art-warning)',
      image: product3
    },
    {
      name: 'Ultra-thin Laptop',
      category: 'Electronics',
      price: 5999,
      stock: 0,
      sales: 126,
      percentage: 30,
      pro: 0,
      color: 'var(--art-error)',
      image: product4
    },
    {
      name: 'Smart Speaker',
      category: 'Smart Home',
      price: 799,
      stock: 45,
      sales: 321,
      percentage: 60,
      pro: 0,
      color: 'var(--art-info)',
      image: product5
    },
    {
      name: 'Gaming Controller',
      category: 'Gaming Accessories',
      price: 299,
      stock: 78,
      sales: 489,
      percentage: 70,
      pro: 0,
      color: 'var(--art-secondary)',
      image: product6
    }
  ])

  /**
   * Get stock status text based on stock quantity
   * @param stock Stock quantity
   * @returns Stock status text
   */
  const getStockStatus = (stock: number): string => {
    if (stock === 0) return 'Out of Stock'
    if (stock < STOCK_THRESHOLD.LOW) return 'Low Stock'
    if (stock < STOCK_THRESHOLD.MEDIUM) return 'Medium'
    return 'In Stock'
  }

  /**
   * Get stock status CSS class based on stock quantity
   * @param stock Stock quantity
   * @returns CSS class name
   */
  const getStockClass = (stock: number): string => {
    if (stock === 0) return 'text-danger bg-danger/12'
    if (stock < STOCK_THRESHOLD.LOW) return 'text-warning bg-warning/12'
    if (stock < STOCK_THRESHOLD.MEDIUM) return 'text-info bg-info/12'
    return 'text-success bg-success/12'
  }

  /**
   * Add progress bar animation effect
   * Update progress value from 0 to target percentage after delay
   */
  const addAnimation = (): void => {
    setTimeout(() => {
      tableData.forEach((item) => {
        item.pro = item.percentage
      })
    }, ANIMATION_DELAY)
  }

  onMounted(() => {
    addAnimation()
  })
</script>
