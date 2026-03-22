import express from 'express';
import { createProxyMiddleware } from 'http-proxy-middleware';
import { createServer as createViteServer } from 'vite';
import path from 'path';

async function startServer() {
  const app = express();
  const port = Number(process.env.PORT || 3000);
  const backendUrl = process.env.BACKEND_URL || 'http://localhost:8080';

  app.get('/proxy-health', (_req, res) => {
    res.json({ status: 'ok', backend: backendUrl });
  });

  app.use(
    '/api',
    createProxyMiddleware({
      target: backendUrl,
      changeOrigin: true,
    }),
  );

  if (process.env.NODE_ENV !== 'production') {
    const vite = await createViteServer({
      server: { middlewareMode: true },
      appType: 'spa',
    });
    app.use(vite.middlewares);
  } else {
    const distPath = path.join(process.cwd(), 'dist');
    app.use(express.static(distPath));
    app.get('*', (_req, res) => {
      res.sendFile(path.join(distPath, 'index.html'));
    });
  }

  app.listen(port, '0.0.0.0', () => {
    console.log(`Way Bank running at http://localhost:${port}`);
    console.log(`Proxying API requests to ${backendUrl}`);
  });
}

startServer();
