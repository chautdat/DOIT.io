import { WEB_LINKS } from '@/utils/constants'
import type { FastEnterConfig } from '@/types/config'

const fastEnterConfig: FastEnterConfig = {
  minWidth: 1200,
  applications: [
    {
      name: 'Dashboard',
      description: 'System overview and metrics',
      icon: 'ri:pie-chart-line',
      iconColor: '#377dff',
      enabled: true,
      order: 1,
      routeName: 'Console'
    },
    {
      name: 'Analytics',
      description: 'Data analysis and visualization',
      icon: 'ri:game-line',
      iconColor: '#ff3b30',
      enabled: true,
      order: 2,
      routeName: 'Analysis'
    },
    {
      name: 'Fireworks',
      description: 'Animated effect demo',
      icon: 'ri:loader-line',
      iconColor: '#7A7FFF',
      enabled: true,
      order: 3,
      routeName: 'Fireworks'
    },
    {
      name: 'Chat',
      description: 'Real-time messaging',
      icon: 'ri:user-line',
      iconColor: '#13DEB9',
      enabled: true,
      order: 4,
      routeName: 'Chat'
    },
    {
      name: 'Docs',
      description: 'User guide and developer documentation',
      icon: 'ri:bill-line',
      iconColor: '#ffb100',
      enabled: true,
      order: 5,
      link: WEB_LINKS.DOCS
    },
    {
      name: 'Support',
      description: 'Technical support and feedback',
      icon: 'ri:user-location-line',
      iconColor: '#ff6b6b',
      enabled: true,
      order: 6,
      link: WEB_LINKS.COMMUNITY
    },
    {
      name: 'Changelog',
      description: 'Version updates and change history',
      icon: 'ri:gamepad-line',
      iconColor: '#38C0FC',
      enabled: true,
      order: 7,
      routeName: 'ChangeLog'
    },
    {
      name: 'Bilibili',
      description: 'Technical sharing and community posts',
      icon: 'ri:bilibili-line',
      iconColor: '#FB7299',
      enabled: true,
      order: 8,
      link: WEB_LINKS.BILIBILI
    }
  ],
  quickLinks: [
    {
      name: 'Login',
      enabled: true,
      order: 1,
      routeName: 'Login'
    },
    {
      name: 'Register',
      enabled: true,
      order: 2,
      routeName: 'Register'
    },
    {
      name: 'Forgot Password',
      enabled: true,
      order: 3,
      routeName: 'ForgetPassword'
    },
    {
      name: 'Pricing',
      enabled: true,
      order: 4,
      routeName: 'Pricing'
    },
    {
      name: 'Profile',
      enabled: true,
      order: 5,
      routeName: 'UserCenter'
    },
    {
      name: 'Comment Management',
      enabled: true,
      order: 6,
      routeName: 'ArticleComment'
    }
  ]
}

export default Object.freeze(fastEnterConfig)
