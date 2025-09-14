# 🚀 Free Deployment Guide

This guide will help you deploy your Expense Tracker application completely for **FREE** using modern cloud platforms.

## 📋 Deployment Stack

- **Database**: Neon PostgreSQL (Free - 512MB)
- **Backend**: Railway or Render (Free tier)
- **Frontend**: Netlify or Vercel (Free tier)

---

## 🗄️ Step 1: Set up Free PostgreSQL Database

### Option A: Neon (Recommended)
1. Go to [neon.tech](https://neon.tech) and sign up
2. Create a new project called "expense-tracker"
3. Copy the connection string (looks like: `postgresql://username:password@host/database`)
4. Save this for later use

### Option B: Supabase
1. Go to [supabase.com](https://supabase.com) and sign up
2. Create a new project
3. Go to Settings → Database
4. Copy the connection string

---

## 🖥️ Step 2: Deploy Backend to Railway

### Prerequisites
- Push your code to GitHub first:
```bash
cd "/Users/vamshigutha/Projects/Git Expense Tracker/ExpenseTracker"
git init
git add .
git commit -m "Initial commit"
# Create a repo on GitHub and push
```

### Railway Deployment
1. Go to [railway.app](https://railway.app) and sign up with GitHub
2. Click "New Project" → "Deploy from GitHub repo"
3. Select your expense tracker repository
4. Railway will auto-detect it's a Spring Boot app

### Environment Variables (Set in Railway Dashboard)
```
DATABASE_URL=your_neon_postgresql_connection_string
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password
SPRING_PROFILES_ACTIVE=prod
PORT=8080
```

### Railway will give you a URL like: `https://your-app-name.railway.app`

---

## 🌐 Step 3: Deploy Frontend to Netlify

### Prepare Frontend
1. Update your API URL in the frontend code:
```bash
cd frontend
```

2. Create a production environment file:
```
# Create .env.production
VITE_API_URL=https://your-railway-app.railway.app
```

### Netlify Deployment
1. Go to [netlify.com](https://netlify.com) and sign up
2. Connect your GitHub repository
3. Set build settings:
   - **Build command**: `npm run build`
   - **Publish directory**: `dist`
   - **Base directory**: `frontend`

### Environment Variables (Set in Netlify Dashboard)
```
VITE_API_URL=https://your-railway-app.railway.app
```

---

## ⚙️ Step 4: Configure CORS & Environment

### Update CORS in Backend
Edit `CorsConfig.java` and replace the placeholder domains:
```java
config.setAllowedOrigins(List.of(
    "http://localhost:5173",
    "https://your-app-name.netlify.app",  // Replace with your actual Netlify URL
    "https://your-custom-domain.com"      // If you have a custom domain
));
```

### Database Setup
Run these SQL commands in your Neon/Supabase database:
```sql
-- Add user_id column to expenses table
ALTER TABLE expenses ADD COLUMN IF NOT EXISTS user_id INTEGER;

-- Add foreign key constraint
ALTER TABLE expenses 
ADD CONSTRAINT IF NOT EXISTS fk_expense_user 
FOREIGN KEY (user_id) REFERENCES users(id);

-- If you have existing expenses, assign them to first user
UPDATE expenses 
SET user_id = (SELECT id FROM users ORDER BY id LIMIT 1)
WHERE user_id IS NULL;
```

---

## 🧪 Step 5: Test Your Deployment

### 1. Test Backend
- Visit: `https://your-railway-app.railway.app/api-docs`
- Should show API documentation

### 2. Test Frontend
- Visit: `https://your-app-name.netlify.app`
- Try registering a new account
- Login and create expenses
- Verify user isolation works

### 3. Test Full Flow
1. Register a new user
2. Login with the new user
3. Create some expenses
4. Logout and login with different user
5. Verify you only see your own expenses

---

## 🛠️ Troubleshooting

### Common Issues

**1. CORS Errors**
- Update `CorsConfig.java` with your actual frontend URL
- Redeploy backend

**2. Database Connection Issues**
- Verify DATABASE_URL environment variable
- Check if database allows external connections

**3. Frontend API Errors**
- Verify VITE_API_URL environment variable
- Check if backend is running

**4. Build Failures**
- For backend: Check Java version (should be 17+)
- For frontend: Check Node.js version (should be 16+)

---

## 💰 Cost Breakdown (FREE!)

| Service | Free Tier Limits | Cost |
|---------|------------------|------|
| Neon PostgreSQL | 512MB storage | FREE |
| Railway | 500 hours/month | FREE |
| Netlify | 100GB bandwidth | FREE |
| **Total** | | **$0/month** |

---

## 🔄 Continuous Deployment

Once set up, any push to your GitHub repository will automatically:
1. **Railway**: Rebuild and redeploy your backend
2. **Netlify**: Rebuild and redeploy your frontend

---

## 🎉 You're Live!

Your expense tracker is now live and accessible worldwide! Share the Netlify URL with others to let them register and use your application.

### Next Steps
- Set up a custom domain (optional)
- Monitor usage in dashboards
- Set up error monitoring with Sentry (free tier)

---

## 📞 Support

If you run into issues:
1. Check Railway/Netlify build logs
2. Check browser console for errors
3. Verify environment variables are set correctly
