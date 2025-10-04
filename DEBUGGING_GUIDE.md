# Panduan Debugging - Data Forum dan Recent Posts

## Masalah yang Diperbaiki

### 1. Data Forum (Posts & Comments) Tidak Tampil Meski Sudah Ada di Firebase
**Penyebab:**
- Kurang logging detail untuk debug
- Tidak ada auto-refresh saat kembali ke fragment
- Adapter mungkin tidak ter-trigger untuk update

**Solusi:**
- ✅ Tambah detailed logging di `FirebaseRepository.getPosts()`
- ✅ Tambah `onResume()` di `DashboardFragment` untuk auto-refresh
- ✅ Improved error handling di `ProfileViewModel`

### 2. Recent Posts di Profile Tidak Tampil
**Penyebab:**
- User ID mungkin kosong saat load posts
- Tidak ada refresh mechanism yang proper

**Solusi:**
- ✅ Tambah validasi user ID sebelum load posts
- ✅ Auto-refresh posts setelah profile update
- ✅ Improved `refreshUserPosts()` dengan fallback logic

## Cara Testing & Debugging

### Step 1: Check Logcat untuk Posts
Jalankan app dan buka Logcat, filter dengan tag: `FirebaseRepository`

**Yang harus terlihat:**
```
D/FirebaseRepository: Fetching posts...
D/FirebaseRepository: Raw snapshot size: 3
D/FirebaseRepository: Processing post doc: abc123
D/FirebaseRepository: Post parsed: id=abc123, author=Nama User, content=Ini konten post...
D/FirebaseRepository: Found 3 posts after parsing
```

**Jika muncul:**
```
D/FirebaseRepository: Raw snapshot size: 0
```
Berarti tidak ada data di collection "posts" atau ada masalah permission.

### Step 2: Check Logcat untuk Comments
Filter dengan tag: `FirebaseRepository` dan cari "comment"

**Yang harus terlihat:**
```
D/FirebaseRepository: Fetching comments for post: abc123
D/FirebaseRepository: Found 2 comments
```

### Step 3: Check Profile Posts
Filter dengan tag: `ProfileViewModel`

**Yang harus terlihat:**
```
D/ProfileViewModel: Loading posts for user: xyz789
D/ProfileViewModel: Successfully loaded 5 user posts
```

## Checklist Troubleshooting

### A. Jika Posts Tidak Tampil di Forum

1. **Check Firebase Console:**
   - Buka Firebase Console > Firestore Database
   - Lihat collection "posts"
   - Pastikan ada document dengan field: `authorName`, `content`, `createdAt`, `authorId`

2. **Check Firestore Rules:**
   ```
   match /posts/{postId} {
     allow read, write: if request.auth != null;
   }
   ```

3. **Check Authentication:**
   - Pastikan user sudah login (anonymous atau email)
   - Check di Logcat: `FirebaseRepository: Signing in anonymously...`

4. **Force Refresh:**
   - Swipe down di forum untuk pull-to-refresh
   - Atau restart app

### B. Jika Comments Tidak Tampil

1. **Check Firebase Console:**
   - Collection "comments"
   - Pastikan field: `postId`, `authorName`, `content`, `createdAt`, `authorId`

2. **Check Comment Dialog:**
   - Pastikan dialog muncul saat klik icon comment
   - Setelah submit, harus muncul toast "Comment added!"

3. **Check PostDetailFragment:**
   - Klik pada post untuk buka detail
   - Lihat apakah comments muncul di RecyclerView

### C. Jika Recent Posts di Profile Tidak Tampil

1. **Check User ID:**
   - Filter Logcat dengan: `ProfileViewModel`
   - Pastikan ada: `Loading posts for user: [UID]`
   - UID tidak boleh kosong

2. **Check authorId Match:**
   - Posts di Firebase harus punya field `authorId` yang sama dengan UID user
   - Check di Firebase Console:
     ```
     posts/[postId]/authorId = [sama dengan UID di Authentication]
     ```

3. **Manual Fix di Firebase:**
   - Jika authorId salah atau kosong, edit manual di Firebase Console
   - Set authorId ke UID yang benar dari Authentication > Users

## Testing Script

### Test 1: Create Post
1. Buka Forum (Dashboard)
2. Klik FAB (+)
3. Tulis "Test post 1"
4. Klik Post
5. **Expected:** Toast "Post created successfully!" dan post muncul di list

### Test 2: Add Comment
1. Klik icon comment di post
2. Tulis "Test comment 1"
3. Klik Comment
4. **Expected:** Toast "Comment added!" dan counter comment bertambah

### Test 3: View Comments
1. Klik pada post (tidak icon, tapi area post)
2. **Expected:** Navigate ke PostDetailFragment
3. **Expected:** Lihat semua comments dengan nama author yang benar

### Test 4: Profile Recent Posts
1. Buka Profile (tab Notifications)
2. Lihat section "Recent Posts"
3. **Expected:** 3 post terakhir yang dibuat oleh user tersebut muncul

### Test 5: Like Feature
1. Klik icon heart di post
2. **Expected:** Icon berubah filled/outline
3. **Expected:** Counter like bertambah/berkurang
4. **Expected:** Tidak ada error permission

## Log Output Reference

### Successful Post Creation:
```
D/FirebaseRepository: Fetching user profile for post creation...
D/FirebaseRepository: Creating post with author: John Doe (userId: abc123)
D/FirebaseRepository: Post created successfully with ID: xyz789
D/DashboardViewModel: Post created successfully with ID: xyz789
D/DashboardFragment: Posts updated: 4 items
```

### Successful Comment Add:
```
D/FirebaseRepository: Adding comment to post: xyz789
D/FirebaseRepository: Comment added with ID: comment123
D/DashboardViewModel: Comment added successfully with ID: comment123
D/FirebaseRepository: Fetching comments for post: xyz789
D/FirebaseRepository: Found 3 comments
```

### Successful Profile Load:
```
D/ProfileViewModel: Loading current user profile...
D/ProfileViewModel: Successfully loaded user profile: John Doe
D/ProfileViewModel: Loading posts for user: abc123
D/ProfileViewModel: Successfully loaded 5 user posts
```

## Common Issues & Solutions

### Issue 1: "Raw snapshot size: 0" tapi ada data di Firebase
**Solution:**
- Check Firestore Rules sudah di-publish
- Restart app
- Check internet connection
- Try logout & login ulang

### Issue 2: Posts muncul tapi authorName = "Anonymous User"
**Solution:**
- Check collection "users" di Firebase
- Pastikan ada document dengan ID = UID user
- Pastikan field `name` terisi
- Update profile lewat Edit Profile

### Issue 3: Recent Posts kosong di Profile
**Solution:**
- Check field `authorId` di posts
- Harus match dengan UID di Authentication
- Manual fix di Firebase Console jika perlu

### Issue 4: Comments tidak muncul setelah ditambahkan
**Solution:**
- Check sudah update Firestore Rules (likes collection)
- Pastikan navigate ke PostDetailFragment
- Check field `postId` di comments match dengan post ID

## Data Structure Reference

### Post Document (collection: "posts")
```json
{
  "id": "auto-generated",
  "authorId": "user_uid_from_auth",
  "authorName": "John Doe",
  "content": "Post content here",
  "createdAt": 1234567890000,
  "likeCount": 0,
  "commentCount": 0,
  "likedBy": [],
  "tags": [],
  "category": "general"
}
```

### Comment Document (collection: "comments")
```json
{
  "id": "auto-generated",
  "postId": "post_id_here",
  "authorId": "user_uid_from_auth",
  "authorName": "John Doe",
  "authorAvatar": "",
  "content": "Comment content here",
  "createdAt": 1234567890000,
  "likeCount": 0,
  "likedBy": []
}
```

### User Document (collection: "users")
```json
{
  "id": "user_uid_from_auth",
  "name": "John Doe",
  "username": "johndoe",
  "major": "Teknik Informatika",
  "year": "2024",
  "avatarUrl": "",
  "bio": "Student at Unsoed"
}
```

---

**Setelah melakukan semua perubahan:**
1. ✅ Rebuild project
2. ✅ Update Firestore Rules di Firebase Console
3. ✅ Clear app data (Settings > Apps > BEM Unsoed > Clear Data)
4. ✅ Login ulang
5. ✅ Test semua fitur dengan checklist di atas

Jika masih ada masalah, check Logcat dan kirim log error yang muncul.

