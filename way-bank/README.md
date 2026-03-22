<div align="center">
<img width="1200" height="475" alt="GHBanner" src="https://github.com/user-attachments/assets/0aa67016-6eaf-458a-adb2-6e31a0763ed6" />
</div>

# Run Way Bank Locally

## Run Locally

Prerequisites:
- Node.js
- The Spring backend in `bank-app` running on `http://localhost:8080`
- PostgreSQL running for the backend

1. Install dependencies:
   `npm install`
2. Optionally set `BACKEND_URL` in `.env.local` if your backend is not on `http://localhost:8080`
3. Run the app:
   `npm run dev`

The frontend dev server runs on `http://localhost:3000` and proxies `/api` requests to your Spring backend.
