import avatar1 from '@/assets/images/avatar/avatar1.webp'
import avatar2 from '@/assets/images/avatar/avatar2.webp'
import avatar3 from '@/assets/images/avatar/avatar3.webp'
import avatar4 from '@/assets/images/avatar/avatar4.webp'
import avatar5 from '@/assets/images/avatar/avatar5.webp'
import avatar6 from '@/assets/images/avatar/avatar6.webp'
import avatar7 from '@/assets/images/avatar/avatar7.webp'
import avatar8 from '@/assets/images/avatar/avatar8.webp'
import avatar9 from '@/assets/images/avatar/avatar9.webp'
import avatar10 from '@/assets/images/avatar/avatar10.webp'

export interface User {
  id: number
  username: string
  gender: 1 | 0
  mobile: string
  email: string
  dep: string
  status: string
  create_time: string
  avatar: string
}

export const ACCOUNT_TABLE_DATA: User[] = [
  {
    id: 1,
    username: 'alexmorgan',
    gender: 1,
    mobile: '18670001591',
    email: 'alexmorgan@company.com',
    dep: 'Engineering',
    status: '1',
    create_time: '2020-09-09 10:01:10',
    avatar: avatar1
  },
  {
    id: 2,
    username: 'sophiabaker',
    gender: 1,
    mobile: '17766664444',
    email: 'sophiabaker@company.com',
    dep: 'E-commerce',
    status: '1',
    create_time: '2020-10-10 13:01:12',
    avatar: avatar2
  },
  {
    id: 3,
    username: 'liampark',
    gender: 1,
    mobile: '18670001597',
    email: 'liampark@company.com',
    dep: 'Human Resources',
    status: '1',
    create_time: '2020-11-14 12:01:45',
    avatar: avatar3
  },
  {
    id: 4,
    username: 'oliviagrant',
    gender: 0,
    mobile: '18670001596',
    email: 'oliviagrant@company.com',
    dep: 'Product',
    status: '1',
    create_time: '2020-11-14 09:01:20',
    avatar: avatar4
  },
  {
    id: 5,
    username: 'emmawilson',
    gender: 0,
    mobile: '18670001595',
    email: 'emmawilson@company.com',
    dep: 'Finance',
    status: '1',
    create_time: '2020-11-13 11:01:05',
    avatar: avatar5
  },
  {
    id: 6,
    username: 'noahevan',
    gender: 1,
    mobile: '18670001594',
    email: 'noahevan@company.com',
    dep: 'Operations',
    status: '1',
    create_time: '2020-10-11 13:10:26',
    avatar: avatar6
  },
  {
    id: 7,
    username: 'avamartin',
    gender: 1,
    mobile: '18123820191',
    email: 'avamartin@company.com',
    dep: 'Support',
    status: '2',
    create_time: '2020-05-14 12:05:10',
    avatar: avatar7
  },
  {
    id: 8,
    username: 'jacoblee',
    gender: 1,
    mobile: '18670001592',
    email: 'jacoblee@company.com',
    dep: 'Executive Office',
    status: '3',
    create_time: '2020-11-12 07:22:25',
    avatar: avatar8
  },
  {
    id: 9,
    username: 'miaclark',
    gender: 0,
    mobile: '18670001581',
    email: 'miaclark@company.com',
    dep: 'Engineering',
    status: '4',
    create_time: '2020-06-12 05:04:20',
    avatar: avatar9
  },
  {
    id: 10,
    username: 'ethanharris',
    gender: 1,
    mobile: '13755554444',
    email: 'ethanharris@company.com',
    dep: 'Engineering',
    status: '1',
    create_time: '2020-11-12 16:01:10',
    avatar: avatar10
  },
  {
    id: 11,
    username: 'isabellamoore',
    gender: 1,
    mobile: '13766660000',
    email: 'isabellamoore@company.com',
    dep: 'Engineering',
    status: '1',
    create_time: '2020-11-14 12:01:20',
    avatar: avatar6
  },
  {
    id: 12,
    username: 'masonwhite',
    gender: 1,
    mobile: '18670001502',
    email: 'masonwhite@company.com',
    dep: 'Engineering',
    status: '1',
    create_time: '2020-11-14 12:01:20',
    avatar: avatar7
  },
  {
    id: 13,
    username: 'charlottehall',
    gender: 1,
    mobile: '13006644977',
    email: 'charlottehall@company.com',
    dep: 'Engineering',
    status: '1',
    create_time: '2020-11-14 12:01:20',
    avatar: avatar8
  },
  {
    id: 14,
    username: 'benjaminscott',
    gender: 0,
    mobile: '13599998888',
    email: 'benjaminscott@company.com',
    dep: 'Engineering',
    status: '1',
    create_time: '2020-11-14 12:01:20',
    avatar: avatar9
  },
  {
    id: 15,
    username: 'ameliaking',
    gender: 1,
    mobile: '13799998888',
    email: 'ameliaking@company.com',
    dep: 'Engineering',
    status: '1',
    create_time: '2020-11-14 12:01:20',
    avatar: avatar10
  }
]

export interface Role {
  roleName: string
  roleCode: string
  des: string
  date: string
  enable: boolean
}

export const ROLE_LIST_DATA: Role[] = [
  {
    roleName: 'Super Admin',
    roleCode: 'R_SUPER',
    des: 'Has all system permissions',
    date: '2025-05-15 12:30:45',
    enable: true
  },
  {
    roleName: 'Administrator',
    roleCode: 'R_ADMIN',
    des: 'Has system administration permissions',
    date: '2025-05-15 12:30:45',
    enable: true
  },
  {
    roleName: 'Standard User',
    roleCode: 'R_USER',
    des: 'Has basic system permissions',
    date: '2025-05-15 12:30:45',
    enable: true
  },
  {
    roleName: 'Finance Admin',
    roleCode: 'R_FINANCE',
    des: 'Manages finance-related permissions',
    date: '2025-05-16 09:15:30',
    enable: true
  },
  {
    roleName: 'Data Analyst',
    roleCode: 'R_ANALYST',
    des: 'Has data analysis permissions',
    date: '2025-05-16 11:45:00',
    enable: false
  },
  {
    roleName: 'Support Specialist',
    roleCode: 'R_SUPPORT',
    des: 'Handles customer support requests',
    date: '2025-05-17 14:30:22',
    enable: true
  },
  {
    roleName: 'Marketing Manager',
    roleCode: 'R_MARKETING',
    des: 'Manages marketing campaign permissions',
    date: '2025-05-17 15:10:50',
    enable: true
  },
  {
    roleName: 'Guest User',
    roleCode: 'R_GUEST',
    des: 'View-only access',
    date: '2025-05-18 08:25:40',
    enable: false
  },
  {
    roleName: 'System Maintainer',
    roleCode: 'R_MAINTAINER',
    des: 'Responsible for system maintenance and updates',
    date: '2025-05-18 09:50:12',
    enable: true
  },
  {
    roleName: 'Project Manager',
    roleCode: 'R_PM',
    des: 'Manages project-related permissions',
    date: '2025-05-19 13:40:35',
    enable: true
  }
]
