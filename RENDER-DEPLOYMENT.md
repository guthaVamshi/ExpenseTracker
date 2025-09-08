# Deploy to Render - Step by Step Guide

## Prerequisites
- GitHub repository with your code
- Render account (free at render.com)

## Step 1: Prepare Your Repository

1. **Push all code to GitHub**:
   ```bash
   cd /Users/vamshigutha/Projects/ExpenseTracker
   git add .
   git commit -m "Prepare for Render deployment"
   git push origin main
   ```

## Step 2: Deploy Backend (Spring Boot API)

1. **Go to Render Dashboard**:
   - Visit [render.com](https://render.com)
   - Sign up/Login with GitHub

2. **Create New Web Service**:
   - Click "New +" → "Web Service"
   - Connect your GitHub repository
   - Select your repository

3. **Configure Backend Service**:
   - **Name**: `expense-tracker-api`
   - **Environment**: `Java`
   - **Build Command**: `./mvnw clean package -DskipTests`
   - **Start Command**: `java -jar target/ExpenseTracker-0.0.1-SNAPSHOT.jar`
   - **Plan**: Free

4. **Add Environment Variables**:
   - `SPRING_PROFILES_ACTIVE` = `prod`
   - `CORS_ORIGINS` = `https://your-frontend-name.onrender.com` (we'll update this later)

5. **Add PostgreSQL Database**:
   - Click "New +" → "PostgreSQL"
   - **Name**: `expense-tracker-db`
   - **Plan**: Free
   - Copy the connection details

6. **Update Backend Environment Variables**:
   - Go back to your web service
   - Add these variables (from your PostgreSQL service):
     - `DATABASE_URL` = (from PostgreSQL service)
     - `DATABASE_USERNAME` = (from PostgreSQL service)
     - `DATABASE_PASSWORD` = (from PostgreSQL service)

7. **Deploy Backend**:
   - Click "Create Web Service"
   - Wait for deployment (5-10 minutes)
   - Note the URL: `https://expense-tracker-api.onrender.com`

## Step 3: Deploy Frontend (React App)

1. **Create New Static Site**:
   - Click "New +" → "Static Site"
   - Connect your GitHub repository

2. **Configure Frontend Service**:
   - **Name**: `expense-tracker-frontend`
   - **Build Command**: `cd frontend && npm install && npm run build`
   - **Publish Directory**: `frontend/dist`
   - **Plan**: Free

3. **Add Environment Variable**:
   - `VITE_API_URL` = `https://expense-tracker-api.onrender.com/api`

4. **Deploy Frontend**:
   - Click "Create Static Site"
   - Wait for deployment (3-5 minutes)
   - Note the URL: `https://expense-tracker-frontend.onrender.com`

## Step 4: Update CORS Settings

1. **Update Backend CORS**:
   - Go to your backend service on Render
   - Go to Environment tab
   - Update `CORS_ORIGINS` to: `https://expense-tracker-frontend.onrender.com`
   - Click "Save Changes"
   - This will trigger a redeploy

## Step 5: Test Your Deployment

1. **Test Backend**:
   - Visit: `https://expense-tracker-api.onrender.com/api-docs`
   - Should show API documentation

2. **Test Frontend**:
   - Visit: `https://expense-tracker-frontend.onrender.com`
   - Should load the login page

3. **Test Full Flow**:
   - Register a user via backend
   - Login through frontend
   - Add some expenses

## Troubleshooting

### Backend Issues:
- **Build Fails**: Check Java version (should be 21)
- **Database Connection**: Verify PostgreSQL environment variables
- **CORS Errors**: Update CORS_ORIGINS with correct frontend URL

### Frontend Issues:
- **Build Fails**: Check Node.js version (should be 18+)
- **API Calls Fail**: Verify VITE_API_URL is correct
- **Blank Page**: Check browser console for errors

### Common Solutions:
```bash
# Check logs in Render dashboard
# Backend: Go to service → Logs
# Frontend: Go to service → Logs

# If you need to update code:
git add .
git commit -m "Fix deployment issues"
git push origin main
# Render will auto-deploy
```

## Quick Commands

```bash
# Update and redeploy
git add .
git commit -m "Update for Render"
git push origin main

# Check deployment status
# Go to Render dashboard → Your services
```

## URLs After Deployment:
- **Backend API**: `https://expense-tracker-api.onrender.com`
- **Frontend App**: `https://expense-tracker-frontend.onrender.com`
- **API Docs**: `https://expense-tracker-api.onrender.com/api-docs`

## Free Tier Limits:
- **Backend**: 750 hours/month (enough for personal use)
- **Database**: 1GB storage
- **Frontend**: Unlimited static hosting
- **Sleep**: Services sleep after 15 minutes of inactivity (wake up on first request)
