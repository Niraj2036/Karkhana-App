# Karkhana Functions API (Postman)

This gives you HTTP endpoints without Python/Spring.

## 1) Install and deploy

```powershell
cd C:\Users\Tanmay\Karkhana-App\functions
npm install
cd C:\Users\Tanmay\Karkhana-App
firebase deploy --only functions
```

## 2) Base URL

After deploy, Firebase prints a URL like:

`https://api-<hash>-as.a.run.app`

Use that as base URL in Postman.

## 3) Endpoints

### Health
- `GET /health`

### Register User (Personal Details)
- `POST /register-user`
- Body:
```json
{
  "farmerId": "UID_HERE",
  "name": "Tanmay Jadhav",
  "email": "user@example.com",
  "phone": "+919876543210",
  "aadhaarMasked": "XXXXXXXX1234",
  "district": "Belagavi",
  "village": "Athani"
}
```

Writes/updates both:
- `farmers/{farmerId}`
- `profiles/{farmerId}`

### Create Farm
- `POST /farms`
- Body:
```json
{
  "farmerId": "UID_HERE",
  "surveyNumber": "142/2A",
  "district": "Belagavi",
  "village": "Athani",
  "area": 3.5,
  "cropType": "Sugarcane",
  "pLocation": "Pinned on map"
}
```

### Get Farms by Farmer
- `GET /farms/{farmerId}`

### Enroll Farm (creates/updates harvest timeline with NONE)
- `POST /enroll`
- Body:
```json
{
  "farmerId": "UID_HERE",
  "farmId": "FARM_ID_HERE",
  "factoryName": "Green Valley Sugar Mill"
}
```

### Get Harvests by Farmer (optional farm filter)
- `GET /harvests/{farmerId}`
- Optional query param: `farmId`

Example:
- `GET /harvests/UID_HERE?farmId=FARM_ID_HERE`

## Notes
- Enroll sets harvest status to `NONE` by default.
- These endpoints are for quick Postman testing.


