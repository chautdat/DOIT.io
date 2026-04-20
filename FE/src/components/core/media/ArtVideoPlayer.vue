<!-- Video Player Component: https://h5player.bytedance.com/-->
<template>
  <div :id="playerId" />
</template>

<script setup lang="ts">
  import Player from 'xgplayer'
  import 'xgplayer/dist/index.min.css'

  defineOptions({ name: 'ArtVideoPlayer' })

  interface Props {
    /** Player container ID */
    playerId: string
    /** Video source URL */
    videoUrl: string
    /** Video poster/thumbnail URL */
    posterUrl: string
    /** Whether to auto play */
    autoplay?: boolean
    /** Volume level (0-1) */
    volume?: number
    /** Available playback rates */
    playbackRates?: number[]
    /** Whether to loop playback */
    loop?: boolean
    /** Whether to mute */
    muted?: boolean
    commonStyle?: VideoPlayerStyle
  }

  const props = withDefaults(defineProps<Props>(), {
    playerId: '',
    videoUrl: '',
    posterUrl: '',
    autoplay: false,
    volume: 1,
    loop: false,
    muted: false
  })

  // Set default props

  // Player instance reference
  const playerInstance = ref<Player | null>(null)

  // Player style interface definition
  interface VideoPlayerStyle {
    progressColor?: string // Progress bar background color
    playedColor?: string // Played portion color
    cachedColor?: string // Cached portion color
    sliderBtnStyle?: Record<string, string> // Slider button style
    volumeColor?: string // Volume controller color
  }

  // Default style configuration
  const defaultStyle: VideoPlayerStyle = {
    progressColor: 'rgba(255, 255, 255, 0.3)',
    playedColor: '#00AEED',
    cachedColor: 'rgba(255, 255, 255, 0.6)',
    sliderBtnStyle: {
      width: '10px',
      height: '10px',
      backgroundColor: '#00AEED'
    },
    volumeColor: '#00AEED'
  }

  // Initialize player on component mount
  onMounted(() => {
    playerInstance.value = new Player({
      id: props.playerId,
      lang: 'en', // Set interface language to English
      volume: props.volume,
      autoplay: props.autoplay,
      screenShot: true, // Enable screenshot feature
      url: props.videoUrl,
      poster: props.posterUrl,
      fluid: true, // Enable fluid layout, auto-adapt to container size
      playbackRate: props.playbackRates,
      loop: props.loop,
      muted: props.muted,
      commonStyle: {
        ...defaultStyle,
        ...props.commonStyle
      }
    })

    // Play event listener
    playerInstance.value.on('play', () => {
      console.log('Video is playing')
    })

    // Pause event listener
    playerInstance.value.on('pause', () => {
      console.log('Video is paused')
    })

    // Error event listener
    playerInstance.value.on('error', (error) => {
      console.error('Error occurred:', error)
    })
  })

  // Clean up player instance before component unmount
  onBeforeUnmount(() => {
    if (playerInstance.value) {
      playerInstance.value.destroy()
    }
  })
</script>
