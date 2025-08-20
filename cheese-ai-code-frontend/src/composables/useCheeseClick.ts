import { onMounted, onUnmounted } from 'vue'

export function useCheeseClick() {
  let clickHandler: ((event: MouseEvent) => void) | null = null

  const createFoodEmoji = (x: number, y: number) => {
    const food = document.createElement('div')

    // 随机选择美食emoji - 奶酪AI代码平台的美食狂欢！
    const foodEmojis = ['🧀', '🍪', '🍕', '🍤', '🥨', '🥯', '🥞', '🍠', '🥮', '🍯', '🍮']
    const randomEmoji = foodEmojis[Math.floor(Math.random() * foodEmojis.length)]

    // 随机大小
    const randomSize = 20 + Math.random() * 16 // 20-36px

    food.textContent = randomEmoji
    food.style.cssText = `
      position: fixed;
      left: ${x - randomSize/2}px;
      top: ${y - randomSize/2}px;
      font-size: ${randomSize}px;
      pointer-events: none;
      z-index: 9999;
      animation: foodFloat 1s ease-out forwards;
      user-select: none;
      text-shadow: 0 0 8px rgba(255, 140, 66, 0.6);
    `

    // 添加CSS动画样式（如果还没有的话）
    if (!document.querySelector('#food-animation-style')) {
      const style = document.createElement('style')
      style.id = 'food-animation-style'
      style.textContent = `
        @keyframes foodFloat {
          0% {
            opacity: 0;
            transform: translateY(0) scale(0.3) rotate(0deg);
          }
          15% {
            opacity: 1;
            transform: translateY(-15px) scale(1.3) rotate(10deg);
          }
          50% {
            opacity: 1;
            transform: translateY(-35px) scale(1.1) rotate(25deg);
          }
          100% {
            opacity: 0;
            transform: translateY(-70px) scale(0.5) rotate(45deg);
          }
        }
      `
      document.head.appendChild(style)
    }

    document.body.appendChild(food)

    // 1秒后自动移除
    setTimeout(() => {
      if (food.parentNode) {
        food.parentNode.removeChild(food)
      }
    }, 1000)
  }

  const handleClick = (event: MouseEvent) => {
    // 避免在输入框、按钮等交互元素上触发
    const target = event.target as HTMLElement
    if (
      target.tagName === 'INPUT' ||
      target.tagName === 'TEXTAREA' ||
      target.tagName === 'BUTTON' ||
      target.closest('button') ||
      target.closest('.ant-btn') ||
      target.closest('.ant-input') ||
      target.closest('a[href]')
    ) {
      return
    }

    createFoodEmoji(event.clientX, event.clientY)
  }

  onMounted(() => {
    clickHandler = handleClick
    document.addEventListener('click', clickHandler)
  })

  onUnmounted(() => {
    if (clickHandler) {
      document.removeEventListener('click', clickHandler)
    }
    // 清理样式
    const style = document.querySelector('#food-animation-style')
    if (style) {
      style.remove()
    }
  })

  return {
    // 可以返回一些控制函数，比如暂停/恢复功能
  }
}
