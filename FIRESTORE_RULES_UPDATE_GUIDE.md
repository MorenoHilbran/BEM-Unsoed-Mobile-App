# Panduan Update Firestore Rules

## Masalah yang Diperbaiki
1. **Permission error pada fitur like** - Collection "likes" tidak memiliki rules yang tepat
2. **User collection tidak bisa dibaca oleh user lain** - Menyebabkan nama user tidak muncul di posts

## Cara Update Firestore Rules di Firebase Console

### Langkah 1: Buka Firebase Console
1. Buka https://console.firebase.google.com/
2. Pilih project "Duar" atau project BEM Unsoed Anda
3. Di menu sebelah kiri, klik **Firestore Database**
4. Klik tab **Rules** di bagian atas

### Langkah 2: Copy Rules Baru
Copy semua isi file `firestore-rules.txt` yang sudah diupdate, atau copy rules berikut:

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Users collection - semua authenticated users bisa READ
    match /users/{userId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && request.auth.uid == userId;
    }

    // Posts collection - semua authenticated users bisa read/write
    match /posts/{postId} {
      allow read, write: if request.auth != null;
    }

    // Comments collection - semua authenticated users bisa read/write
    match /comments/{commentId} {
      allow read, write: if request.auth != null;
    }

    // Likes collection - PENTING: UNTUK FIX PERMISSION ERROR
    match /likes/{likeId} {
      allow read, write: if request.auth != null;
    }

    // Events collection - semua authenticated users bisa read/write
    match /events/{eventId} {
      allow read, write: if request.auth != null;
    }

    // Banners collection - semua authenticated users bisa read/write
    match /banners/{bannerId} {
      allow read, write: if request.auth != null;
    }

    // Merch collection - semua authenticated users bisa read/write
    match /merch/{merchId} {
      allow read, write: if request.auth != null;
    }
  }
}
```

### Langkah 3: Paste dan Publish
1. Hapus semua rules yang lama di editor
2. Paste rules baru yang sudah dicopy
3. Klik tombol **Publish** di bagian atas
4. Konfirmasi jika ada warning

### Perubahan Penting yang Dilakukan:

#### 1. Users Collection
**Sebelum:**
```
allow read, write: if request.auth != null && request.auth.uid == userId;
```

**Sesudah:**
```
allow read: if request.auth != null;
allow write: if request.auth != null && request.auth.uid == userId;
```

**Alasan:** Sekarang semua user yang login bisa membaca profil user lain, tapi hanya bisa edit profil sendiri. Ini penting agar nama user bisa muncul di posts/comments orang lain.

#### 2. Likes Collection (BARU)
**Ditambahkan:**
```
match /likes/{likeId} {
  allow read, write: if request.auth != null;
}
```

**Alasan:** Collection "likes" tidak punya rules sebelumnya, sehingga menyebabkan permission error saat user mencoba like post/comment.

## Testing Setelah Update

1. **Test User Names:**
   - Buat post baru
   - Pastikan nama user yang sebenarnya muncul (bukan "Current User")
   
2. **Test Like Feature:**
   - Coba like sebuah post
   - Pastikan tidak ada error "permission denied"
   - Counter like harus berubah
   
3. **Test Comments:**
   - Tambah comment ke post
   - Comment harus langsung muncul dengan nama user yang benar
   - Counter comment di post harus update

## Troubleshooting

### Jika masih muncul "Current User"
- Pastikan ada data user di collection "users" dengan UID yang sama dengan auth user
- Check di Firebase Console > Firestore Database > users collection
- Jika belum ada, buat document dengan format:
  ```
  Document ID: [UID dari Authentication]
  Fields:
    - name: "Nama Lengkap"
    - username: "username"
    - major: "Jurusan"
    - year: "2024"
    - bio: "Bio singkat"
  ```

### Jika masih error permission
- Pastikan rules sudah di-publish
- Logout dan login ulang di aplikasi
- Clear app data jika perlu

## Perubahan Code yang Sudah Dilakukan

1. **FirebaseRepository.kt**
   - Fungsi `createPost()` sekarang mengambil nama user dari database users
   - Fallback ke email atau "BEM User" jika profil tidak ditemukan
   - Improved error handling dan logging

2. **DashboardViewModel.kt**
   - Fungsi `addComment()` sekarang otomatis reload comments setelah menambah comment baru
   - Ini memastikan comment langsung muncul tanpa perlu refresh manual

3. **DashboardFragment.kt**
   - Removed hardcoded "Current User" dari dialog create post
   - Nama user sekarang diambil dari repository

## Catatan Penting

⚠️ **WAJIB UPDATE FIRESTORE RULES DI FIREBASE CONSOLE!**

Perubahan code saja tidak cukup. Firestore rules HARUS diupdate di Firebase Console agar fitur like dan user names berfungsi dengan baik.

---

Last updated: 2025-10-03

