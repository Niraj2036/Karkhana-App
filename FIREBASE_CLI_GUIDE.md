# Firebase CLI Setup & Deployment Guide

## Prerequisites

Ensure you have installed:
- Node.js (v14 or higher) - [Download](https://nodejs.org/)
- Firebase CLI - Will be installed in Step 1

## Step 1: Install Firebase CLI

Open PowerShell as Administrator and run:

```powershell
npm install -g firebase-tools
```

Verify installation:
```powershell
firebase --version
```

Should output version number, e.g., `firebase-tools/13.0.0`

## Step 2: Login to Firebase

```powershell
firebase login
```

This will open a browser window. Sign in with your Google account that has access to the Firebase project.

## Step 3: Initialize Firebase in Your Project

Navigate to your project directory:

```powershell
cd C:\Users\Tanmay\Karkhana-App
```

Initialize Firebase:

```powershell
firebase init
```

When prompted:
- **Which Firebase features do you want to set up?** → Select `Firestore`, `Storage`, `Hosting` (use space to select)
- **Select a default Firebase project** → Choose `madl-7d670`
- **Set up a default location for your Firestore indexes?** → Press Enter (accept default)
- **Set up a default location for your Cloud Storage bucket?** → Press Enter (accept default)
- **Set up default hosting directory?** → Type `public` and press Enter

## Step 4: Deploy Firestore Security Rules

Create/Update `firestore.rules` in your project root with the rules from **FIREBASE_SETUP_GUIDE.md**.

Then deploy:

```powershell
firebase deploy --only firestore:rules
```

Expected output:
```
i  deploying firestore
✔  firestore:rules deployed successfully
```

## Step 5: Deploy Storage Rules (Optional)

Create/Update `storage.rules` in your project root with storage rules.

Deploy:

```powershell
firebase deploy --only storage:rules
```

## Step 6: Monitor Deployment

Check deployment history:

```powershell
firebase deploy:list
```

View recent deployments:

```powershell
firebase open firestore
```

## Useful Firebase CLI Commands

### View Firestore Data
```powershell
firebase firestore:delete farmers --recursive
```
⚠️ WARNING: This deletes data! Use with caution.

### Export Firestore Data
```powershell
firebase firestore:delete --all --recursive --confirm
```

### View Logs
```powershell
firebase functions:log
```

### Test Rules Locally
```powershell
firebase emulators:start
```

### Deploy Everything
```powershell
firebase deploy
```

## Environment-Specific Configuration

### Using Different Firebase Projects

If you have multiple projects:

```powershell
firebase projects:list
```

Switch project:

```powershell
firebase use <project-id>
```

For Karkhana App:
```powershell
firebase use madl-7d670
```

## Troubleshooting

### Issue: "Error: Cannot find module 'firebase-tools'"

**Solution:**
```powershell
npm install -g firebase-tools
```

### Issue: "Authentication Error"

**Solution:**
```powershell
firebase logout
firebase login
```

### Issue: "Permission denied" deploying rules

**Solution:**
1. Ensure you have "Editor" role in Firebase Console for the project
2. Check that your Firebase project is correct: `firebase use madl-7d670`

### Issue: Rules not deployed successfully

**Solution:**
1. Check syntax: `firebase emulators:start`
2. Verify file is named `firestore.rules`
3. Ensure file is in project root: `C:\Users\Tanmay\Karkhana-App\firestore.rules`

## Firebase.json Configuration

The `firebase.json` file contains your deployment configuration:

```json
{
  "firestore": {
    "rules": "firestore.rules",
    "indexes": "firestore.indexes.json"
  },
  "storage": {
    "rules": "storage.rules"
  }
}
```

## Firestore Indexes

Some queries require indexes. Firebase automatically suggests creating them, or manually create in:

Firebase Console → Firestore → Indexes

Recommended indexes:
1. **farms** - farmerId (Asc) + createdAt (Desc)
2. **harvests** - farmId (Asc) + status (Asc)
3. **payments** - harvestId (Asc) + createdAt (Desc)

## Cloud Shell Alternative

If you prefer, you can use Google Cloud Shell instead of local CLI:

1. Go to [Google Cloud Console](https://console.cloud.google.com)
2. Select your project
3. Click "Cloud Shell" icon (terminal icon in top-right)
4. Firebase CLI is pre-installed
5. Same commands work as above

## Continuous Deployment

To automatically deploy on code changes, set up GitHub Actions:

Create `.github/workflows/deploy.yml`:

```yaml
name: Deploy

on:
  push:
    branches: [ main ]

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v2
    - run: npm install -g firebase-tools
    - run: firebase deploy --token ${{ secrets.FIREBASE_TOKEN }}
```

Get Firebase token:
```powershell
firebase login:ci
```

Add the token as GitHub Secret `FIREBASE_TOKEN`.

## Quick Command Reference

```powershell
# Login/Logout
firebase login
firebase logout

# Project Management
firebase projects:list
firebase use madl-7d670

# Deployment
firebase deploy                        # Deploy everything
firebase deploy --only firestore:rules # Deploy only Firestore rules
firebase deploy --only storage:rules   # Deploy only Storage rules

# Local Testing
firebase emulators:start
firebase emulators:exec "npm test"

# Debugging
firebase functions:log
firebase database:profile:report
firebase firestore:indexes

# Data Management
firebase firestore:delete path/to/collection --recursive
firebase rtdb:export /path/to/file.json

# Info
firebase --version
firebase help
firebase help deploy
```

## Connecting to Firebase from Android

All necessary code is already in place:
- ✅ `google-services.json` configured
- ✅ `FirebaseHelper.java` for initialization
- ✅ Repositories with Firestore integration
- ✅ Security rules deployed

Just ensure:
1. Internet permission in `AndroidManifest.xml` (already present)
2. Gradle dependencies (already added)
3. Collections exist in Firestore

## Best Practices

1. **Always test rules before deploying**
   ```powershell
   firebase emulators:start
   ```

2. **Keep rules secure**
   - Never allow public read/write
   - Always verify user ownership
   - Use custom claims for admin users

3. **Use version control**
   - Commit `firestore.rules` and `firebase.json` to Git
   - Do NOT commit credentials

4. **Monitor usage**
   - Go to Firebase Console → Usage tab
   - Set up billing alerts to avoid surprises

5. **Backup data regularly**
   - Export Firestore data periodically
   - Keep backups in secure location

## Next Steps

1. ✅ Install Firebase CLI: `npm install -g firebase-tools`
2. ✅ Login: `firebase login`
3. ✅ Initialize: `firebase init`
4. ✅ Deploy rules: `firebase deploy --only firestore:rules`
5. ✅ Verify in Firebase Console

---

**Firebase CLI Ready! 🚀**

