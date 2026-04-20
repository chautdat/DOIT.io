export interface Comment {
  id: number
  author: string
  content: string
  timestamp: string
  replies: Comment[]
}

export const commentList = ref<Comment[]>([
  {
    id: 1,
    author: 'Nova',
    content: 'The combat in Black Myth: Wukong looks incredible. I cannot wait for launch.',
    timestamp: '2024-09-04 09:00',
    replies: [
      {
        id: 101,
        author: 'Orbit',
        content: 'The skill effects are especially impressive.',
        timestamp: '2024-09-04 09:15',
        replies: [
          {
            id: 201,
            author: 'Aurora',
            content: 'I hope the optimization keeps up so the visuals stay smooth.',
            timestamp: '2024-09-04 09:30',
            replies: []
          }
        ]
      }
    ]
  },
  {
    id: 2,
    author: 'Echo',
    content: 'I heard the game needs a pretty powerful PC. I am not sure mine can handle it.',
    timestamp: '2024-09-04 10:00',
    replies: [
      {
        id: 102,
        author: 'Ray',
        content: 'Same here. It sounds like at least an RTX 3070 is recommended.',
        timestamp: '2024-09-04 10:20',
        replies: [
          {
            id: 202,
            author: 'Lumen',
            content: 'I am planning an upgrade just for this game.',
            timestamp: '2024-09-04 10:40',
            replies: []
          }
        ]
      }
    ]
  },
  {
    id: 3,
    author: 'Misty',
    content: 'A 130GB storage requirement is huge, but the visuals make it understandable.',
    timestamp: '2024-09-04 11:00',
    replies: [
      {
        id: 103,
        author: 'Cloud',
        content: 'It is still a lot, but that level of quality is worth it.',
        timestamp: '2024-09-04 11:15',
        replies: [
          {
            id: 203,
            author: 'Dream',
            content: 'I just hope the installer size can be optimized a bit.',
            timestamp: '2024-09-04 11:30',
            replies: []
          }
        ]
      }
    ]
  }
])
