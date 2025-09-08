# Expense Tracker - Deployment Guide

## Backend Deployment Options

### Option 1: Heroku (Recommended)

1. **Install Heroku CLI** and login:
   ```bash
   heroku login
   ```

2. **Create Heroku app**:
   ```bash
   cd /Users/vamshigutha/Projects/ExpenseTracker
   heroku create your-expense-tracker-api
   ```

3. **Add PostgreSQL addon**:
   ```bash
   heroku addons:create heroku-postgresql:mini
   ```

4. **Set environment variables**:
   ```bash
   heroku config:set SPRING_PROFILES_ACTIVE=prod
   heroku config:set CORS_ORIGINS=https://your-frontend-domain.netlify.app
   ```

5. **Deploy**:
   ```bash
   mvn clean package -DskipTests
   git add .
   git commit -m "Deploy backend"
   git push heroku main
   ```

### Option 2: Railway

1. Go to [Railway.app](https://railway.app)
2. Connect your GitHub repository
3. Add PostgreSQL service
4. Set environment variables:
   - `SPRING_PROFILES_ACTIVE=prod`
   - `CORS_ORIGINS=https://your-frontend-domain.netlify.app`

### Option 3: Render

1. Go to [Render.com](https://render.com)
2. Create new Web Service
3. Connect GitHub repository
4. Add PostgreSQL database
5. Set environment variables

## Frontend Deployment (Netlify)

### Method 1: Netlify CLI

1. **Install Netlify CLI**:
   ```bash
   npm install -g netlify-cli
   ```

2. **Login to Netlify**:
   ```bash
   netlify login
   ```

3. **Deploy from frontend directory**:
   ```bash
   cd frontend
   npm run build
   netlify deploy --prod --dir=dist
   ```

### Method 2: Netlify Dashboard

1. Go to [Netlify](https://netlify.com)
2. Click "New site from Git"
3. Connect your GitHub repository
4. Set build settings:
   - **Build command**: `cd frontend && npm run build`
   - **Publish directory**: `frontend/dist`
5. Add environment variable:
   - `VITE_API_URL`: `https://your-backend-domain.herokuapp.com/api`

### Method 3: Drag & Drop

1. Build the frontend:
   ```bash
   cd frontend
   npm run build
   ```

2. Go to [Netlify](https://netlify.com)
3. Drag the `frontend/dist` folder to the deploy area

## Environment Variables

### Backend (Production)
- `SPRING_PROFILES_ACTIVE=prod`
- `DATABASE_URL` (auto-set by Heroku/Railway)
- `CORS_ORIGINS=https://your-frontend-domain.netlify.app`

### Frontend (Production)
- `VITE_API_URL=https://your-backend-domain.herokuapp.com/api`

## Testing Deployment

1. **Backend**: Visit `https://your-backend-domain.herokuapp.com/api-docs`
2. **Frontend**: Visit `https://your-frontend-domain.netlify.app`

## Troubleshooting

### CORS Issues
- Update `CORS_ORIGINS` in backend environment variables
- Check that frontend URL is included in allowed origins

### Database Issues
- Ensure PostgreSQL is running
- Check database connection string
- Verify tables are created (check logs)

### Build Issues
- Ensure Node.js version compatibility
- Check for TypeScript errors
- Verify all dependencies are installed

## Quick Deploy Commands

```bash
# Backend (Heroku)
mvn clean package -DskipTests
git add . && git commit -m "Deploy"
git push heroku main

# Frontend (Netlify)
cd frontend
npm run build
netlify deploy --prod --dir=dist
```
