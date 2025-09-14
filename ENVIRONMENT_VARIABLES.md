# 🔧 Environment Variables for Railway Deployment

## ⚠️ **CRITICAL: Set These Environment Variables in Railway**

Go to your Railway project → Settings → Environment and add these:

### **Required Variables:**

```
PORT=8080
DATABASE_URL=postgresql://username:password@hostname:port/database
SPRING_PROFILES_ACTIVE=prod
```

### **Database URL Format:**

Your `DATABASE_URL` should look like this:
```
postgresql://username:password@hostname:5432/database_name
```

**Example from Neon:**
```
postgresql://user123:password456@ep-cool-cloud-123456.us-east-1.aws.neon.tech/neondb
```

### **Optional Variables:**

```
SHOW_SQL=false
JAVA_OPTS=-Xmx512m -XX:+UseSerialGC
```

## 🔍 **How to Get Your Database URL:**

### **From Neon:**
1. Go to your Neon dashboard
2. Select your database
3. Go to "Connection Details"
4. Copy the "Connection string" - this is your `DATABASE_URL`

### **From Supabase:**
1. Go to Project Settings → Database
2. Copy the "Connection string" under "Connection pooling"

## 🚨 **Common Issues:**

### **1. Wrong DATABASE_URL Format**
❌ **Wrong:** `jdbc:postgresql://host:5432/db`
✅ **Correct:** `postgresql://user:pass@host:5432/db`

### **2. Missing PORT Variable**
Railway expects `PORT` environment variable, not `--server.port`

### **3. Database Connection**
Make sure your database allows external connections and the credentials are correct.

## 🧪 **Test Your Configuration:**

Run this locally to test your environment variables:
```bash
export PORT=8080
export DATABASE_URL="your_neon_url_here"
export SPRING_PROFILES_ACTIVE=prod

java -jar target/expense-tracker.jar
```

Visit `http://localhost:8080/health` - should return JSON with status "UP"

## 📋 **Railway Deployment Checklist:**

- [ ] DATABASE_URL is set correctly
- [ ] PORT is set to 8080 (or let Railway auto-assign)
- [ ] SPRING_PROFILES_ACTIVE is set to "prod"
- [ ] Database allows external connections
- [ ] Health check endpoint `/health` works
- [ ] Latest code is pushed to GitHub

## 🔧 **If Still Failing:**

1. **Check Railway Logs** - Look for specific error messages
2. **Test Database Connection** - Try connecting to your database from another tool
3. **Verify Environment Variables** - Make sure they're set correctly in Railway
4. **Check Health Endpoint** - The health check might be failing

## 🆘 **Quick Debug Commands:**

In Railway logs, look for:
- `Started ExpenseTrackerApplication` (successful startup)
- `ERROR` messages (specific errors)
- `Port already in use` (port binding issues)
- `Connection refused` (database issues)
