<template>
  <a-layout-footer class="footer">
    <div class="footer-content">
      <p class="copyright">
        © 2025 奶酪AI代码平台 |
        <a
          href="https://www.baidu.com/link?url=Kr5dLW1f0KjAlJhXVJWVDYEotltB1-Q_hR5dnYNIcHnpPf1LXpXskqjrRgetNFLG2Ul47SE7tAX0E-QG8MJ2VGuomOzUosAnVwEeJFeiF7a&wd=&eqid=eaffabc2003414fc0000000568a458cd"
          target="_blank"
          rel="noopener noreferrer"
          class="footer-link"
        >
          奶酪不会永远在那里等着你去吃
        </a>
      </p>
    </div>
  </a-layout-footer>
</template>

<script setup lang="ts">
// 这个组件是纯展示组件，暂时不需要任何逻辑
</script>

<style scoped>
.footer {
  background: rgba(248, 250, 252, 0.9);
  border-top: 1px solid rgba(255, 140, 66, 0.1);
  position: sticky;
  bottom: 0;
  margin-top: auto;
}

.footer-content {
  max-width: 1450px;
  margin: 0 auto;
  padding: 0 24px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.copyright {
  margin: 0;
  color: #666;
  font-size: 14px;
  text-align: center;
  line-height: 1.5;
}

.footer-link {
  color: #ff8c42;
  text-decoration: none;
  transition: all 0.3s ease;
  font-weight: 500;
}

.footer-link:hover {
  color: #f57c00;
  text-decoration: underline;
  text-shadow: 0 0 8px rgba(255, 140, 66, 0.3);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .footer-content {
    padding: 0 16px;
  }

  .copyright {
    font-size: 12px;
  }
}

@media (max-width: 480px) {
  .copyright {
    font-size: 11px;
    line-height: 1.4;
  }
}
</style>
