## Expense Tracker Frontend

React + TypeScript + Vite app with Tailwind and Recharts.

### Prerequisites
- Node.js >= 20.16.0
- Backend running at http://localhost:8080

### Install
```bash
npm install
```

### Run Dev Server
```bash
npm run dev
```
Open the URL shown (e.g., http://localhost:5173).

### Build
```bash
npm run build
npm run preview
```

### Auth
- Uses Basic Auth stored in localStorage.
- Log in with your backend user credentials on the Login page.

### API Proxy
Frontend calls `/api/*` and Vite proxies to `http://localhost:8080`.
Configured in `vite.config.ts`.

### Features
- Add/edit/delete entries marked as Expense or Income
- Live area chart for Expense vs Income (Recharts)
- Tailwind modern theme (`primary`, `accent`)

### Notable paths
- `src/pages/LoginPage.tsx`
- `src/pages/DashboardPage.tsx`
- `src/lib/api.ts`
- `src/store/auth.ts`