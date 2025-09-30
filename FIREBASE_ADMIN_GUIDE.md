# Firebase Setup Guide untuk BEM-Unsoed App

## 🔥 Setup Firebase Console untuk Admin

Aplikasi sudah dimodifikasi untuk pull data langsung dari Firebase. Berikut cara setup collections di Firebase Console:

### 1. **Collection: `banners`**
Untuk banner slider di homepage

**Structure:**
```
Document ID: auto-generated
Fields:
- title (string): "Banner Event Gensoed" 
- imageUrl (string): "https://example.com/banner1.jpg"
- linkUrl (string): "https://gensoed-merch.com"
- isActive (boolean): true
- order (number): 1
- createdAt (number): 1696118400000
```

**Cara Input di Firebase Console:**
1. Buka Firebase Console → Firestore Database
2. Klik "Start collection" → Nama: `banners`
3. Add document dengan field-field di atas
4. Untuk menambah banner baru, klik "Add document" di collection banners

---

### 2. **Collection: `events`**
Untuk event/acara BEM

**Structure:**
```
Document ID: auto-generated
Fields:
- title (string): "Gensoed Festival 2024"
- description (string): "Festival tahunan mahasiswa Unsoed"
- imageUrl (string): "https://example.com/event1.jpg"
- date (string): "2024-12-15"
- location (string): "Gedung Rektorat Unsoed"
- isHighlight (boolean): true
- registrationUrl (string): "https://forms.google.com/..."
- status (string): "published"
- createdAt (number): 1696118400000
```

**Tips:**
- Set `isHighlight: true` untuk event yang mau muncul di homepage
- Status bisa: "draft", "published", "archived"
- Format date: "YYYY-MM-DD"

---

### 3. **Collection: `merch`**
Untuk merchandise BEM

**Structure:**
```
Document ID: auto-generated
Fields:
- name (string): "Kaos BEM Unsoed 2024"
- description (string): "Kaos cotton combed premium"
- price (string): "Rp 75.000"
- imageUrl (string): "https://example.com/kaos1.jpg"
- linkUrl (string): "https://shopee.co.id/bem-unsoed"
- category (string): "Pakaian"
- isActive (boolean): true
- createdAt (number): 1696118400000
```

---

## 🚀 Cara Menggunakan Firebase Console

### **Step 1: Buka Firebase Console**
1. Pergi ke https://console.firebase.google.com
2. Pilih project: `bem-unsoed-badfc`
3. Klik "Firestore Database"

### **Step 2: Membuat Collection Baru**
1. Klik "Start collection"
2. Masukkan nama collection (banners/events/merch)
3. Buat document pertama dengan field-field yang sudah ditentukan

### **Step 3: Menambah Data Baru**
1. Masuk ke collection yang diinginkan
2. Klik "Add document"
3. Isi semua field sesuai structure
4. Klik "Save"
5. **Data langsung muncul di aplikasi!** (real-time)

### **Step 4: Edit/Hapus Data**
1. Klik document yang mau diedit
2. Edit field yang diperlukan
3. Atau klik menu "Delete document" untuk hapus

---

## 📱 Apa yang Berubah di Aplikasi

### **Homepage Sekarang Menampilkan:**
1. **Banner Slider**: Data dari collection `banners` (yang isActive = true)
2. **Event Cards**: Data dari collection `events` (yang isHighlight = true)
3. **Merchandise**: Data dari collection `merch` (yang isActive = true)

### **Fitur Real-time:**
- Tambah data di Firebase → Langsung muncul di app
- Edit data di Firebase → Langsung update di app
- Hapus data di Firebase → Langsung hilang dari app
- Pull-to-refresh untuk manual refresh

---

## ⚠️ Tips Penting

1. **imageUrl**: Pastikan link gambar bisa diakses publik
2. **isActive/isHighlight**: Gunakan untuk show/hide content
3. **order**: Untuk mengatur urutan banner (1,2,3,...)
4. **Backup Data**: Selalu backup sebelum edit massal
5. **Testing**: Test di emulator dulu sebelum publish

---

## 🔧 Troubleshooting

**Jika data tidak muncul:**
1. Cek connection internet
2. Pastikan field name sama persis dengan structure
3. Pastikan isActive/status = true/"published"
4. Pull-to-refresh di aplikasi

**Error Firebase:**
1. Cek Firebase Rules (harus allow read)
2. Pastikan project ID benar
3. Restart aplikasi

---

Sekarang kamu bisa langsung manage content aplikasi dari Firebase Console tanpa coding! 🎉
