<template>
  <div class="markdown-content" v-html="renderedMarkdown"></div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js'
import 'highlight.js/styles/github.css'

interface Props {
  content: string
}

const props = defineProps<Props>()

const md: MarkdownIt = new MarkdownIt({
  html: true,
  linkify: true,
  typographer: true,
  highlight: function (str: string, lang: string): string {
    if (lang && hljs.getLanguage(lang)) {
      try {
        return (
          '<pre class="hljs"><code>' +
          hljs.highlight(str, { language: lang, ignoreIllegals: true }).value +
          '</code></pre>'
        )
      } catch {}
    }
    return '<pre class="hljs"><code>' + md.utils.escapeHtml(str) + '</code></pre>'
  },
})

const renderedMarkdown = computed(() => md.render(props.content))
</script>

<style scoped>
.markdown-content {
  line-height: 1.6;
  color: #333;
  word-wrap: break-word;
}
.markdown-content :deep(h1),
.markdown-content :deep(h2),
.markdown-content :deep(h3),
.markdown-content :deep(h4),
.markdown-content :deep(h5),
.markdown-content :deep(h6) {
  margin: 1.5em 0 0.5em 0;
  font-weight: 600;
  line-height: 1.25;
}
.markdown-content :deep(h1) { font-size: 1.5em; border-bottom: 1px solid #eee; padding-bottom: 0.3em; }
.markdown-content :deep(h2) { font-size: 1.3em; border-bottom: 1px solid #eee; padding-bottom: 0.3em; }
.markdown-content :deep(h3) { font-size: 1.1em; }
.markdown-content :deep(p) { margin: 0.8em 0; }
.markdown-content :deep(ul),
.markdown-content :deep(ol) { margin: 0.8em 0; padding-left: 1.5em; }
.markdown-content :deep(li) { margin: 0.3em 0; }
.markdown-content :deep(blockquote) { margin: 1em 0; padding: 0.5em 1em; border-left: 4px solid #ddd; background-color: #f9f9f9; color: #666; }
.markdown-content :deep(code) { background-color: #f1f1f1; padding: 0.2em 0.4em; border-radius: 3px; font-family: 'Monaco','Menlo','Ubuntu Mono', monospace; font-size: 0.9em; }
.markdown-content :deep(pre) { background-color: #f8f8f8; border: 1px solid #e1e1e1; border-radius: 6px; padding: 1em; overflow-x: auto; margin: 1em 0; }
.markdown-content :deep(pre code) { background-color: transparent; padding: 0; border-radius: 0; font-size: 0.9em; line-height: 1.4; }
.markdown-content :deep(table) { border-collapse: collapse; margin: 1em 0; width: 100%; }
.markdown-content :deep(table th), .markdown-content :deep(table td) { border: 1px solid #ddd; padding: 0.5em 0.8em; text-align: left; }
.markdown-content :deep(table th) { background-color: #f5f5f5; font-weight: 600; }
.markdown-content :deep(table tr:nth-child(even)) { background-color: #f9f9f9; }
.markdown-content :deep(a) { color: #1890ff; text-decoration: none; }
.markdown-content :deep(a:hover) { text-decoration: underline; }
.markdown-content :deep(img) { max-width: 100%; height: auto; border-radius: 4px; margin: 0.5em 0; }
.markdown-content :deep(hr) { border: none; border-top: 1px solid #eee; margin: 1.5em 0; }
.markdown-content :deep(.hljs) { background-color: #f8f8f8 !important; border-radius: 6px; font-family: 'Monaco','Menlo','Ubuntu Mono', monospace; font-size: 0.9em; line-height: 1.4; }
</style>





