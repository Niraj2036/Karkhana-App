# Backend Implementation Quick Reference

## What's Ready to Use?

✅ All 8 data models with Firebase serialization  
✅ All 9 repositories with CRUD + LiveData support  
✅ Firebase helper utility for initialization  
✅ Google Sign-In integration with Firestore sync  
✅ Security rules for all collections  
✅ Storage rules for file uploads  
✅ Complete documentation  

## Quick Start: 3 Steps to Deploy

### Step 1: Deploy Firestore Rules
```bash
cd C:\Users\Tanmay\Karkhana-App
firebase login
firebase deploy --only firestore:rules
```

### Step 2: Create Collections in Firebase Console
Visit https://console.firebase.google.com → madl-7d670 → Firestore

Create 8 collections: farmers, profiles, farms, harvests, harvest_timelines, payments, sugar_factories, otp_verifications

### Step 3: Test Google Sign-In
Build and run app → LoginActivity → Click Google Sign-In → Check Firestore for new farmer document

## Using the Backend

### Get Current User ID
```java
String userId = FirebaseHelper.getInstance().getCurrentUserId();
```

### Save Farmer Data
```java
UserRepository userRepo = new UserRepository();
Farmer farmer = new Farmer("John", "john@example.com", "9876543210", "Pune", "Malegaon");
userRepo.updateFarmer(userId, farmer);
```

### Create Farm
```java
FarmRepository farmRepo = new FarmRepository();
Farm farm = new Farm(userId, "SN123", "Pune", "Malegaon", 50.0, "Sugarcane");
farmRepo.createFarm(farm, new FarmRepository.OnCompleteListener() {
    @Override
    public void onSuccess(Farm savedFarm) { /* Handle success */ }
    @Override
    public void onError(String error) { /* Handle error */ }
});
```

### Listen to Farms in Real-Time
```java
FarmRepository farmRepo = new FarmRepository();
farmRepo.getFarmsByFarmerId(userId).observe(this, farms -> {
    // Update UI with farms list
});
```

### Create Harvest
```java
HarvestRepository harvestRepo = new HarvestRepository();
Harvest harvest = new Harvest(farmId, "CANE_001", "SC-2024", "CoS 8436", 100.0);
harvestRepo.createHarvest(harvest, new HarvestRepository.OnCompleteListener() {
    @Override
    public void onSuccess(Harvest harvest) { /* Handle */ }
    @Override
    public void onError(String error) { /* Handle */ }
});
```

### Upload Photo
```java
StorageRepository storageRepo = new StorageRepository();
storageRepo.uploadFarmPhoto(farmId, imageUri, new StorageRepository.OnUploadCompleteListener() {
    @Override
    public void onSuccess(String downloadUrl) { /* Save URL to DB */ }
    @Override
    public void onError(String error) { /* Handle */ }
});
```

### Verify OTP
```java
OTPRepository otpRepo = new OTPRepository();
otpRepo.verifyOTP(email, userEnteredOtp, new OTPRepository.OnCompleteListener() {
    @Override
    public void onSuccess(OTPVerification otp) { /* OTP Valid */ }
    @Override
    public void onError(String error) { /* OTP Invalid */ }
});
```

## Files to Update Next

1. **OtpVerificationActivity.java** - Use OTPRepository
2. **PersonalDetailsActivity.java** - Use ProfileRepository
3. **FarmLocationActivity.java** - Use FarmRepository
4. **HarvestTrackingFragment.java** - Use HarvestRepository + Adapter
5. **ProfileFragment.java** - Use ProfileRepository + UserRepository
6. **NearbyFactoriesFragment.java** - Use SugarFactoryRepository + Adapter

See **ACTIVITIES_INTEGRATION_GUIDE.md** for complete code examples.

## Firestore Collection Schema

### farmers/{userUID}
```json
{
  "farmerId": "string",
  "name": "string",
  "email": "string",
  "phone": "string",
  "aadhaarMasked": "string",
  "district": "string",
  "village": "string",
  "totalArea": "number",
  "lastYield": "number",
  "createdAt": "timestamp",
  "updatedAt": "timestamp"
}
```

### farms/{farmId}
```json
{
  "farmId": "string",
  "farmerId": "string",
  "surveyNumber": "string",
  "district": "string",
  "village": "string",
  "pLocation": "string",
  "area": "number",
  "cropType": "string",
  "sugarFactoryId": "string",
  "createdAt": "timestamp",
  "updatedAt": "timestamp"
}
```

### harvests/{harvestId}
```json
{
  "harvestId": "string",
  "farmId": "string",
  "cropId": "string",
  "cropCode": "string",
  "variety": "string",
  "expectedYield": "number",
  "status": "string",
  "actualWeight": "number",
  "harvestDate": "date",
  "sugarFactoryId": "string",
  "createdAt": "timestamp",
  "updatedAt": "timestamp"
}
```

### payments/{paymentId}
```json
{
  "paymentId": "string",
  "harvestId": "string",
  "amount": "number",
  "paymentDate": "date",
  "status": "string",
  "receiptUrl": "string",
  "createdAt": "timestamp",
  "updatedAt": "timestamp"
}
```

## Security Rules Summary

- **Farmers**: Users can only read/write their own document
- **Profiles**: Users can only access their own profile
- **Farms**: Users can only access their own farms
- **Harvests**: Users can only access harvests for their farms
- **Payments**: Users can only access payments for their harvests
- **Factories**: All users can read, none can write (admin only)
- **OTP**: System-managed, users can verify for their email

## Common Issues & Fixes

| Problem | Solution |
|---------|----------|
| Firebase not initialized | Call `FirebaseHelper.getInstance()` once in Application class |
| "Permission denied" on Firestore | Ensure rules are deployed: `firebase deploy --only firestore:rules` |
| Google Sign-In returns null | Verify SHA-1 in google-services.json matches console |
| LiveData not updating | Enable Firestore persistence (already done in FirebaseHelper) |
| Storage upload fails | Check Storage rules and file size (<5MB for most) |

## Next Immediate Actions

1. ✅ Review FIREBASE_SETUP_GUIDE.md
2. ✅ Review ACTIVITIES_INTEGRATION_GUIDE.md
3. ⏳ Deploy Firestore rules: `firebase deploy --only firestore:rules`
4. ⏳ Create collections in Firebase Console
5. ⏳ Update OtpVerificationActivity (see guide for code)
6. ⏳ Update PersonalDetailsActivity
7. ⏳ Update FarmLocationActivity
8. ⏳ Create HarvestAdapter and update HarvestTrackingFragment
9. ⏳ Create SugarFactoryAdapter and update NearbyFactoriesFragment
10. ⏳ Test end-to-end (sign in → profile → farm → harvest)

## Important Reminders

- 🔒 Never expose credentials (already safe with google-services.json)
- 📱 Test on physical device if possible (emulator may have connectivity issues)
- 🌐 Ensure internet permission in AndroidManifest.xml (already present)
- 🔄 Use LiveData with observe() in Activities/Fragments
- 💾 Firestore offline persistence is enabled by default
- 📊 Monitor Firestore usage in Firebase Console (free tier: 50k reads/day)

---

**Quick Deploy Time: ~1 hour**  
**Complete Implementation: ~4-5 hours**  
**Status**: Backend Ready ✅ | Awaiting Activity Integration 🚀

