# Firebase Storage Rules Setup

## Masalah: "Gagal mengupload gambar"

Ini terjadi karena Firebase Storage Rules belum diatur dengan benar.

## Solusi:

1. Buka Firebase Console: https://console.firebase.google.com
2. Pilih project: `bem-unsoed-badfc`
3. Klik **Storage** di menu kiri
4. Klik tab **Rules**
5. Ganti rules dengan kode berikut:

```
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    // Allow authenticated users to upload/read their own profile images
    match /users/{userId}/{allPaths=**} {
      allow read: if true; // Anyone can read profile images
      allow write: if request.auth != null && request.auth.uid == userId;
    }
    
    // Allow authenticated users to upload/read post images
    match /posts/{postId}/{allPaths=**} {
      allow read: if true; // Anyone can read post images
      allow write: if request.auth != null;
    }
    
    // Default deny all other access
    match /{allPaths=**} {
      allow read, write: if false;
    }
  }
}
```

6. Klik **Publish** untuk menyimpan rules

## Penjelasan Rules:

- **users/{userId}/** - Hanya user yang bersangkutan yang bisa upload/update foto profil mereka
- **posts/{postId}/** - Semua user yang terautentikasi bisa upload gambar post
- Semua orang bisa membaca (read) gambar untuk ditampilkan di app
- Path lain akan ditolak secara default

## Testing:

Setelah update rules, coba:
1. Buka halaman profile
2. Tap foto profil
3. Pilih "Kamera" atau "Pilih dari Galeri"
4. Ambil/pilih foto
5. Upload seharusnya berhasil dan muncul toast "Gambar berhasil diupload!"

## Troubleshooting:

Jika masih gagal, cek Logcat untuk melihat error detail:
```
adb logcat | grep FirebaseRepository
```

Error umum:
- `Permission denied` = Rules belum diupdate
- `User not authenticated` = User belum login
- `Network error` = Tidak ada koneksi internet

