interface UpgradeLog {
  version: string
  title: string
  date: string
  detail?: string[]
  requireReLogin?: boolean
  remark?: string
}

export const upgradeLogList = ref<UpgradeLog[]>([
  {
    version: 'v3.0.2',
    title: 'Bug fixes and form / routing improvements',
    date: '2026-03-15',
    detail: [
      'Fixed rich text editor styling issues',
      'Fixed menu scrolling problems',
      'Fixed blank page issues after opening special routes',
      'Fixed WebSocket reconnect edge cases',
      'Improved form submission data cleanup before sending to the backend'
    ]
  },
  {
    version: 'v3.0.1',
    title: 'Bug fixes and new features',
    date: '2025-11-15',
    detail: [
      'Fixed duplicate route registration requests',
      'Improved route validation and error reporting',
      'Optimized the top progress bar',
      'Added offline icon loading support',
      'Added WebSocket support'
    ]
  },
  {
    version: 'v3.0.0',
    title: 'Major UI and architecture refresh',
    date: '2025-11-09',
    requireReLogin: true,
    detail: [
      'Migrated Sass to Tailwind CSS',
      'Replaced Iconfont with Iconify',
      'Reduced the overall bundle size',
      'Refactored route registration for better maintainability',
      'Reworked the color system and menu styles',
      'Improved core performance and responsive layouts'
    ],
    remark:
      'This release includes breaking changes in the styling system and icon library. New projects should start from v3.0; existing projects are not recommended to upgrade directly.'
  },
  {
    version: 'v2.6.1',
    title: 'Bug fixes and theme switch support on auth pages',
    date: '2025-10-19',
    detail: [
      'Fixed repeated user and menu fetch calls',
      'Improved dependency compatibility with Tailwind CSS',
      'Fixed Button circle styling issues',
      'Fixed keyboard selection issues in Select',
      'Fixed login redirects for parameterized static routes'
    ]
  },
  {
    version: 'v2.6.0',
    title: 'Code cleanup and bug fixes',
    date: '2025-10-16',
    requireReLogin: true,
    detail: [
      'Optimized menu data consistency in the slim build',
      'Improved the local development proxy configuration',
      'Adjusted page transition animations',
      'Fixed static route redirects and logout edge cases',
      'Added support for recommended VS Code plugins'
    ]
  },
  {
    version: 'v2.5.9',
    title: 'Code cleanup',
    date: '2025-10-12',
    detail: [
      'Optimized the views directory structure and filenames',
      'Added global pagination request field configuration',
      'Modularized route configuration',
      'Switched menu fetching to apifox mock data in backend mode'
    ]
  },
  {
    version: 'v2.5.8',
    title: 'Dependency upgrades and bug fixes',
    date: '2025-09-29',
    detail: [
      'Upgraded Vue, Vite, and Element Plus',
      'Fixed fullscreen top bar layering issues',
      'Improved custom theme behavior',
      'Fixed infinite redirects when the root path matched the home page path'
    ],
    remark: 'Node.js 20.19.0 or later is required because of dependency upgrades.'
  },
  {
    version: 'v2.5.7',
    title: 'New form components',
    date: '2025-09-14',
    detail: [
      'Added the ArtForm component',
      'Fixed theme flicker in newer Chrome versions',
      'Improved label alignment',
      'Optimized first-screen startup performance'
    ]
  }
])
