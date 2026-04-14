const { onRequest } = require("firebase-functions/v2/https");
const admin = require("firebase-admin");
const { FieldValue } = require("firebase-admin/firestore");
const express = require("express");
const cors = require("cors");

admin.initializeApp();
const db = admin.firestore();

const app = express();
app.use(cors({ origin: true }));
app.use(express.json());

function badRequest(res, message) {
  return res.status(400).json({ ok: false, error: message });
}

app.get("/health", (_req, res) => {
  return res.json({ ok: true, service: "karkhana-api" });
});

app.post("/register-user", async (req, res) => {
  try {
    const {
      farmerId,
      name,
      email,
      phone,
      aadhaarMasked,
      district,
      village
    } = req.body || {};

    if (!farmerId || !name || !phone || !aadhaarMasked || !district || !village) {
      return badRequest(res, "farmerId, name, phone, aadhaarMasked, district, village are required");
    }

    const now = FieldValue.serverTimestamp();

    const farmerPayload = {
      farmerId,
      name,
      email: email || "",
      phone,
      aadhaarMasked,
      district,
      village,
      totalArea: 0,
      lastYield: 0,
      updatedAt: now
    };

    const profilePayload = {
      farmerId,
      aadhaarMasked,
      district,
      village,
      updatedAt: now
    };

    const farmerRef = db.collection("farmers").doc(farmerId);
    const profileRef = db.collection("profiles").doc(farmerId);

    const farmerDoc = await farmerRef.get();
    if (!farmerDoc.exists) {
      farmerPayload.createdAt = now;
    }

    const profileDoc = await profileRef.get();
    if (!profileDoc.exists) {
      profilePayload.createdAt = now;
    }

    await farmerRef.set(farmerPayload, { merge: true });
    await profileRef.set(profilePayload, { merge: true });

    return res.status(201).json({ ok: true, farmerId, registered: true });
  } catch (error) {
    return res.status(500).json({ ok: false, error: error.message });
  }
});

app.post("/farms", async (req, res) => {
  try {
    const { farmerId, surveyNumber, district, village, area, cropType, pLocation } = req.body || {};

    if (!farmerId || !surveyNumber || !district || !village || !cropType) {
      return badRequest(res, "farmerId, surveyNumber, district, village, cropType are required");
    }

    const areaValue = Number(area);
    if (Number.isNaN(areaValue) || areaValue <= 0) {
      return badRequest(res, "area must be a positive number");
    }

    const payload = {
      farmerId,
      surveyNumber,
      district,
      village,
      area: areaValue,
      cropType,
      pLocation: pLocation || "Pinned on map",
      createdAt: FieldValue.serverTimestamp(),
      updatedAt: FieldValue.serverTimestamp()
    };

    const ref = await db.collection("farms").add(payload);
    await ref.update({ farmId: ref.id });

    return res.status(201).json({ ok: true, farmId: ref.id });
  } catch (error) {
    return res.status(500).json({ ok: false, error: error.message });
  }
});

app.get("/farms/:farmerId", async (req, res) => {
  try {
    const { farmerId } = req.params;
    const snap = await db.collection("farms").where("farmerId", "==", farmerId).get();
    const farms = snap.docs.map((d) => ({ id: d.id, ...d.data() }));
    return res.json({ ok: true, farms });
  } catch (error) {
    return res.status(500).json({ ok: false, error: error.message });
  }
});

app.post("/enroll", async (req, res) => {
  try {
    const { farmerId, farmId, factoryName } = req.body || {};
    if (!farmerId || !farmId || !factoryName) {
      return badRequest(res, "farmerId, farmId, factoryName are required");
    }

    const farmRef = db.collection("farms").doc(farmId);
    const farmDoc = await farmRef.get();
    if (!farmDoc.exists) {
      return res.status(404).json({ ok: false, error: "Farm not found" });
    }

    const farm = farmDoc.data();
    if (farm.farmerId !== farmerId) {
      return res.status(403).json({ ok: false, error: "Farm does not belong to farmer" });
    }

    await farmRef.update({
      sugarFactoryId: factoryName,
      updatedAt: FieldValue.serverTimestamp()
    });

    const existingHarvestSnap = await db.collection("harvests").where("farmId", "==", farmId).limit(1).get();

    let harvestId;
    if (!existingHarvestSnap.empty) {
      const harvestRef = existingHarvestSnap.docs[0].ref;
      harvestId = harvestRef.id;
      const existing = existingHarvestSnap.docs[0].data();
      const status = existing.status && String(existing.status).trim() ? existing.status : "NONE";
      await harvestRef.set({
        farmId,
        farmerId,
        cropType: farm.cropType || null,
        sugarFactoryId: factoryName,
        status,
        updatedAt: FieldValue.serverTimestamp()
      }, { merge: true });
    } else {
      const harvestRef = await db.collection("harvests").add({
        farmId,
        farmerId,
        cropType: farm.cropType || null,
        sugarFactoryId: factoryName,
        status: "NONE",
        actualWeight: 0,
        expectedYield: 0,
        createdAt: FieldValue.serverTimestamp(),
        updatedAt: FieldValue.serverTimestamp()
      });
      harvestId = harvestRef.id;
      await harvestRef.update({ harvestId });
    }

    return res.json({ ok: true, farmId, harvestId, status: "NONE" });
  } catch (error) {
    return res.status(500).json({ ok: false, error: error.message });
  }
});

app.get("/harvests/:farmerId", async (req, res) => {
  try {
    const { farmerId } = req.params;
    const farmId = req.query.farmId;

    let query = db.collection("harvests").where("farmerId", "==", farmerId);
    if (farmId) {
      query = query.where("farmId", "==", String(farmId));
    }

    const snap = await query.get();
    const harvests = snap.docs.map((d) => ({ id: d.id, ...d.data() }));
    return res.json({ ok: true, harvests });
  } catch (error) {
    return res.status(500).json({ ok: false, error: error.message });
  }
});

exports.api = onRequest({ region: "asia-south1" }, app);



