# Firebase Setup & Backend Integration Guide

## Project Overview
Karkhana App is a comprehensive agricultural platform connecting farmers with sugar factories for sugarcane harvesting, trading, and payment management.

## Firebase Configuration Status

### ✅ Completed
1. **Google Services JSON** - Already configured at `google-services.json`
   - Project ID: `madl-7d670`
   - Storage Bucket: `madl-7d670.firebasestorage.app`
   - OAuth Client ID: `552515447782-lpqnp4v94hs0c72jpnotc0f6ft4jap7m.apps.googleusercontent.com`

2. **Data Models Created**
   - Farmer: User account & basic info
   - Profile: Extended profile information
   - Farm: Farmer's farm details
   - Harvest: Crop harvest tracking
   - HarvestTimeline: Events during harvest
   - Payment: Payment records
   - SugarFactory: Factory information
   - OTPVerification: OTP verification records

3. **Repository Pattern Implemented**
   - UserRepository: Authentication & farmer data
   - ProfileRepository: Profile management
   - FarmRepository: Farm CRUD operations
   - HarvestRepository: Harvest tracking
   - PaymentRepository: Payment management
   - SugarFactoryRepository: Factory listing
   - StorageRepository: File uploads (photos, receipts)

4. **Firebase Security Rules** - Defined in `firestore.rules`
   - User-scoped data access
   - Farm ownership validation
   - Harvest-to-farm relationship verification

## Setup Steps

### Step 1: Deploy Firestore Security Rules
```bash
# Install Firebase CLI (if not installed)
npm install -g firebase-tools

# Login to Firebase
firebase login

# Deploy rules to your Firebase project
firebase deploy --only firestore:rules
```

### Step 2: Create Firestore Collections & Documents

#### Collection: farmers
```
Document ID: {userUID}
Fields:
- farmerId: string (auto-set to document ID)
- name: string
- email: string
- phone: string
- aadhaarMasked: string
- district: string
- village: string
- totalArea: number
- lastYield: number
- createdAt: timestamp (auto)
- updatedAt: timestamp (auto)
```

#### Collection: profiles
```
Document ID: {farmerId}
Fields:
- farmerId: string
- profilePhotoUrl: string
- aadhaarMasked: string
- district: string
- village: string
- createdAt: timestamp (auto)
- updatedAt: timestamp (auto)
```

#### Collection: farms
```
Document ID: auto-generated
Fields:
- farmerId: string (reference to farmers/{farmerId})
- surveyNumber: string
- district: string
- village: string
- pLocation: string
- area: number
- cropType: string
- sugarFactoryId: string (optional)
- createdAt: timestamp (auto)
- updatedAt: timestamp (auto)
```

#### Collection: sugar_factories
```
Document ID: auto-generated
Fields:
- name: string
- location: string
- distance: number (km)
- capacityPerDay: number (tons)
- rating: number (0-5)
- createdAt: timestamp (auto)
- updatedAt: timestamp (auto)
```

#### Collection: harvests
```
Document ID: auto-generated
Fields:
- farmId: string (reference to farms/{farmId})
- cropId: string
- cropCode: string
- variety: string
- expectedYield: number
- status: string (PLANNED, IN_PROGRESS, COMPLETED, CANCELLED)
- actualWeight: number
- harvestDate: date
- sugarFactoryId: string (optional)
- createdAt: timestamp (auto)
- updatedAt: timestamp (auto)
```

#### Collection: harvest_timelines
```
Document ID: auto-generated
Fields:
- harvestId: string (reference to harvests/{harvestId})
- eventName: string
- eventDate: date
- status: string
- createdAt: timestamp (auto)
- updatedAt: timestamp (auto)
```

#### Collection: payments
```
Document ID: auto-generated
Fields:
- harvestId: string (reference to harvests/{harvestId})
- amount: number
- paymentDate: date
- status: string (PENDING, COMPLETED, FAILED)
- receiptUrl: string (optional)
- createdAt: timestamp (auto)
- updatedAt: timestamp (auto)
```

### Step 3: Create Firebase Storage Rules

Access Firebase Console → Storage → Rules and replace with:

```
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    // Farms folder - user can upload/download own farm images
    match /farms/{userId}/{allPaths=**} {
      allow read: if request.auth.uid == userId;
      allow write: if request.auth.uid == userId && request.resource.size < 5 * 1024 * 1024; // 5MB limit
    }

    // Harvests folder
    match /harvests/{userId}/{allPaths=**} {
      allow read: if request.auth.uid == userId;
      allow write: if request.auth.uid == userId && request.resource.size < 10 * 1024 * 1024; // 10MB limit
    }

    // Payments folder
    match /payments/{userId}/{allPaths=**} {
      allow read: if request.auth.uid == userId;
      allow write: if request.auth.uid == userId && request.resource.size < 5 * 1024 * 1024; // 5MB limit
    }

    // Profiles folder
    match /profiles/{userId}/{allPaths=**} {
      allow read: if request.auth.uid == userId;
      allow write: if request.auth.uid == userId && request.resource.size < 3 * 1024 * 1024; // 3MB limit
    }

    // Deny all other access
    match /{allPaths=**} {
      allow read, write: if false;
    }
  }
}
```

### Step 4: Verify Google Sign-In Configuration

Your `google-services.json` is already configured. Verify in Firebase Console:

1. Go to Firebase Console → Your Project
2. Authentication → Sign-in method
3. Enable "Google" provider
4. Go to Authentication → OAuth consent screen
5. Add test users if needed

### Step 5: Update Android App Build Configuration

Already configured in `build.gradle.kts`:
- ✅ Firebase BOM included
- ✅ Firebase Auth added
- ✅ Firebase Firestore added
- ✅ Firebase Storage added
- ✅ Google Play Services Auth added

## API Usage Examples

### 1. Google Sign-In Integration (Already Updated)
The `LoginActivity.java` now automatically syncs Google account data to Firestore after authentication.

### 2. Creating a Farm
```java
FarmRepository farmRepo = new FarmRepository();
Farm newFarm = new Farm(
    farmerId,           // Current user ID
    "surveyNum123",     // Survey number
    "Pune",             // District
    "Malegaon",         // Village
    50.5,               // Area in acres
    "Sugarcane"         // Crop type
);

farmRepo.createFarm(newFarm, new FarmRepository.OnCompleteListener() {
    @Override
    public void onSuccess(Farm farm) {
        Toast.makeText(context, "Farm created: " + farm.getFarmId(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(String error) {
        Toast.makeText(context, "Error: " + error, Toast.LENGTH_SHORT).show();
    }
});
```

### 3. Creating a Harvest
```java
HarvestRepository harvestRepo = new HarvestRepository();
Harvest harvest = new Harvest(
    farmId,             // Farm ID
    "CANE_001",         // Crop ID
    "SC-2024-001",      // Crop code
    "Variety CoS 8436", // Variety
    100.0               // Expected yield in tons
);

harvestRepo.createHarvest(harvest, new HarvestRepository.OnCompleteListener() {
    @Override
    public void onSuccess(Harvest harvest) {
        Log.d("Success", "Harvest created: " + harvest.getHarvestId());
    }

    @Override
    public void onError(String error) {
        Log.e("Error", error);
    }
});
```

### 4. Uploading Farm Photo
```java
StorageRepository storageRepo = new StorageRepository();
storageRepo.uploadFarmPhoto(farmId, imageUri, new StorageRepository.OnUploadCompleteListener() {
    @Override
    public void onSuccess(String downloadUrl) {
        // Save downloadUrl to farm document
    }

    @Override
    public void onError(String error) {
        Toast.makeText(context, "Upload failed: " + error, Toast.LENGTH_SHORT).show();
    }
});
```

### 5. Listening to Real-Time Updates
```java
FarmRepository farmRepo = new FarmRepository();
LiveData<List<Farm>> farmsLiveData = farmRepo.getFarmsByFarmerId(userId);

farmsLiveData.observe(this, farms -> {
    if (farms != null) {
        // Update UI with farms list
        adapter.submitList(farms);
    }
});
```

## Next Steps

### Activities to Update
1. **OtpVerificationActivity** - Integrate OTPVerification repository
2. **PersonalDetailsActivity** - Save to Profile collection instead of SharedPreferences
3. **FarmLocationActivity** - Save to Farm collection
4. **HarvestTrackingFragment** - Load real-time harvest data
5. **ProfileFragment** - Load profile from Firestore
6. **NearbyFactoriesFragment** - Display SugarFactory data

### Sample Implementation for OtpVerificationActivity
```java
// In OtpVerificationActivity.java
private OTPRepository otpRepo = new OTPRepository();

// Generate and send OTP
String otp = generateRandomOTP(); // "123456"
Date expiresAt = new Date(System.currentTimeMillis() + 10 * 60 * 1000); // 10 min
OTPVerification otpVerification = new OTPVerification(email, otp, expiresAt);

otpRepo.createOTP(otpVerification, new OTPRepository.OnCompleteListener() {
    @Override
    public void onSuccess(OTPVerification otp) {
        // In real app, send otp via email/SMS
        Log.d("OTP", "OTP created. (Demo: " + otp.getOtp() + ")");
    }

    @Override
    public void onError(String error) {
        Toast.makeText(context, "OTP Error: " + error, Toast.LENGTH_SHORT).show();
    }
});

// Verify OTP
String userEnteredOtp = "123456";
otpRepo.verifyOTP(email, userEnteredOtp, new OTPRepository.OnCompleteListener() {
    @Override
    public void onSuccess(OTPVerification otp) {
        Toast.makeText(context, "OTP Verified", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(String error) {
        Toast.makeText(context, "Invalid OTP", Toast.LENGTH_SHORT).show();
    }
});
```

## Important Security Notes

1. **Never commit credentials** - google-services.json is app-specific and safe
2. **Firestore Rules** - Deployed rules enforce user ownership validation
3. **Storage Rules** - File access is UID-scoped; users can only access their own files
4. **API Key** - Used only for client-side Firebase operations (safe)

## Troubleshooting

### Issue: "Permission denied" errors
- Check Firestore Security Rules are deployed
- Verify user is authenticated
- Ensure document/collection names match rules

### Issue: Google Sign-In not working
- Verify SHA-1 in google-services.json
- Check Google OAuth consent screen is configured
- Ensure Google provider is enabled in Firebase Console

### Issue: Storage uploads failing
- Check Storage Rules are properly set
- Verify file size limits
- Ensure user has valid authentication

## Testing Checklist

- [ ] Google Sign-In works
- [ ] User data syncs to Firestore
- [ ] Can create/read/update farms
- [ ] Can create harvests
- [ ] Can upload photos to Storage
- [ ] Real-time updates work (LiveData)
- [ ] Security rules prevent unauthorized access
- [ ] App works offline (with persistence enabled)

## Database Indexes

Firestore will auto-create indexes as needed, but consider these for optimization:

1. **farms** collection:
   - Index: `farmerId` (Ascending) + `createdAt` (Descending)

2. **harvests** collection:
   - Index: `farmId` (Ascending) + `status` (Ascending)

3. **payments** collection:
   - Index: `harvestId` (Ascending) + `createdAt` (Descending)

## Cloud Messaging (Future Enhancement)

For notifications (harvest reminders, payment alerts):
```
1. Enable Firebase Cloud Messaging in console
2. Add dependency: implementation("com.google.firebase:firebase-messaging")
3. Create MessagingService class
4. Subscribe users to relevant topics
```

---

**Last Updated:** April 2026
**Status:** Backend Infrastructure Ready - Activities Integration In Progress

