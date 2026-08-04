# Quran Hifz Companion — KMP App Plan (Android + iOS)

## Context

Sau nhiều vòng nghiên cứu thị trường (English learning app → global subscription niches → pet/plant care → health tracker → spiritual/religious content), quyết định cuối: build 1 app **local-only, zero budget, chuyên sâu 1 ngách** thay vì dàn trải nhiều app. Ngách chốt: **Quran Memorization (Hifz) Companion** — app hỗ trợ ghi nhớ và ôn tập Kinh Coran cho người Hồi giáo, dùng Kotlin Multiplatform (KMP) + Compose Multiplatform để share UI/logic giữa Android và iOS.

**Lý do chọn ngách này:**
- Nguồn nội dung miễn phí, hợp pháp, chuẩn hoá sẵn cho developer: **Tanzil.net** (văn bản Kinh Coran chuẩn Uthmani + nhiều bản dịch tiếng Anh public domain/tự do sử dụng — Yusuf Ali, Pickthall...).
- Khác biệt hoá rõ so với Muslim Pro (utility tổng quát: giờ cầu nguyện, la bàn Qibla) — ngách này tập trung vào **ôn tập chống quên** (spaced repetition), một nỗi đau thật của người học Hifz mà chưa app nào làm tốt.
- Kiến trúc hoàn toàn local-only: không cần LLM API, không cần backend — chi phí vận hành gần bằng 0.
- **Rủi ro đã biết và chấp nhận:** người xây dựng không phải tín đồ Hồi giáo — cần cẩn trọng về tính xác thực nội dung (chỉ dùng nguyên văn từ Tanzil, không tự diễn giải giáo lý) và cân nhắc kỹ kênh phân phối/community trust khi ra mắt.

**Quyết định vị trí project:** tạo **repo Git hoàn toàn mới**, tách biệt khỏi repo "ProjectBase" hiện tại (repo đó là boilerplate Android thuần, không có KMP, không liên quan domain).

## Kiến trúc kỹ thuật

### Module structure (KMP)

Dùng **1 module `:shared` duy nhất** (package-by-feature bên trong `commonMain`) thay vì split nhiều Gradle module `core-x`/`feature-x` như ProjectBase cũ — vì đó là pattern cho team nhiều người, với solo dev nó chỉ tạo thêm overhead cấu hình KMP target ở mỗi module mà không có lợi ích tương xứng ở giai đoạn MVP.

```
/androidApp        — Android entry point (MainActivity, Koin start)
/iosApp             — Xcode project wrapping ComposeUIViewController (Phase 6, chưa cần ngay)
/shared/src/commonMain
  /quran            — Surah/Juz browser
  /hifz             — memorization progress tracking
  /review           — SM-2 spaced repetition engine + review session
  /audio            — expect AudioPlayer, download manager
  /notification     — expect NotificationScheduler
  /bookmark, /settings, /onboarding
  /core/database    — Room KMP: 2 database riêng biệt (xem dưới)
  /core/di          — Koin modules
  /core/navigation  — androidx.navigation Compose Multiplatform
  /core/designsystem
/shared/src/androidMain — actual AudioPlayer (Media3), actual NotificationScheduler (WorkManager)
/shared/src/iosMain     — actual AudioPlayer (AVFoundation), actual NotificationScheduler (UNUserNotificationCenter)
/shared/src/commonTest  — test cho SM-2 engine (giá trị an toàn cao nhất cho solo dev)
```

**Giữ nguyên từ ProjectBase (đã xác nhận qua khảo sát repo cũ):**
- **Koin 4.0.0** cho DI — hoạt động native trong KMP `commonMain`, không cần đổi.
- **kotlinx.serialization** — dùng cho script build dữ liệu Tanzil (offline, không chạy runtime) và cho settings/export.
- Convention **Fastlane + GitHub Actions** (`version.properties` VERSION_CODE/VERSION_NAME, Fastfile lanes `test`/`build_dev_debug`/`distribute_dev`/`distribute_prod` qua Firebase App Distribution, `ci.yml` với job `test` tự động + job `distribute` manual) — port nguyên pattern này sang repo mới ở Phase 5.

**Cần thêm mới (ProjectBase chưa có):** `kotlin-multiplatform` + `org.jetbrains.compose` plugin, **kotlinx-datetime** (java.time không dùng được trên Kotlin/Native), **androidx.lifecycle ViewModel KMP** 2.8+, **Compose Multiplatform resources** để bundle database Quran có sẵn.

### Data layer: Room 2.7+ (KMP), không dùng SQLDelight

Room 2.7+ đã stable cho KMP (commonMain DAO/entity qua KSP + driver `sqlite-bundled` cho iOS), tái dùng đúng kiến thức Room 2.6.1 dev đã có từ ProjectBase. **Rủi ro cần spike sớm ở Phase 0:** driver iOS còn mới hơn SQLDelight — nếu spike gặp bug chặn, fallback sang SQLDelight.

**2 database riêng biệt:**
1. `QuranContentDatabase` (read-only, bundle sẵn) — text Uthmani, bản dịch, metadata Surah/Juz/Page, danh sách audio. Build **1 lần offline** bằng script riêng (không chạy trong app) từ dữ liệu Tanzil, xuất ra file `.db` bundle như Compose resource — app không parse JSON lúc runtime.
2. `UserDataDatabase` (read-write) — `MemorizationState`, `ReviewState` (SM-2), `Bookmark`, `Streak`, `Settings`. Tách riêng để sau này cập nhật nội dung Quran/bản dịch không đụng tới tiến độ học của user.

Lần đầu mở app: copy `quran_content.db` từ bundle sang thư mục ghi được của app rồi mở qua Room (giống nhau trên cả 2 platform), gate bằng flag `contentDbVersion` trong DataStore để chỉ copy 1 lần.

**Lưu ý pháp lý:** license Tanzil.net yêu cầu giữ nguyên văn bản Uthmani không chỉnh sửa + ghi nguồn — cần màn hình **About/Attribution** ghi rõ nguồn Tanzil.net và bản quyền từng bản dịch.

### Spaced repetition: SM-2, theo từng Ayah

`ReviewState` per-ayah (không phải per-page) để chỉ đẩy đúng câu đang quên lên ôn, thay vì bắt ôn lại cả trang — đúng trọng tâm khác biệt hoá của app. Trường dữ liệu: `easeFactor`, `intervalDays`, `repetitions`, `dueDate` (kotlinx-datetime), `lastReviewedDate`, `memorizedDate`, `status`.

Chấm điểm đơn giản hoá còn **3 nút**: Quên / Ngập ngừng / Tự tin (map vào thang quality 0-5 gốc của SM-2) — dễ dùng hơn thang 4 nút kiểu Anki cho người không quen app học thuộc.

*(FSRS — thuật toán hiện đại hơn SM-2 — cân nhắc cho v2, không phải MVP vì cần fit model phức tạp hơn.)*

### Audio: expect/actual thủ công, không dùng lib KMP audio bên thứ 3

Android: Media3 ExoPlayer (dev đã quen). iOS: AVFoundation. Lib KMP audio cộng đồng hiện còn mỏng/rủi ro bị bỏ rơi, trong khi audio là tính năng lõi (nghe-lặp lại, test tự đọc).

**Không bundle audio trong app** (Quran full 1 giọng đọc đã hàng trăm MB-GB) — tải theo từng Surah từ nguồn file tĩnh miễn phí (kiểu everyayah.com — file MP3 tĩnh, không phải API trả phí), cache local, có báo "đã tải" để dùng offline sau đó.

**Rủi ro cần spike ở Phase 0:** (a) xác nhận nguồn audio free có license rõ ràng cho việc phân phối lại trong app, (b) test Media3 + AVFoundation qua `expect/actual` chạy mượt, (c) test phát liên tục nhiều ayah không bị giật.

### Notification: local hoàn toàn, không cần Firebase

`expect/actual NotificationScheduler`. Android: WorkManager (chịu Doze tốt hơn AlarmManager thô). iOS: `UNUserNotificationCenter` + `UNCalendarNotificationTrigger`. Firebase (nếu dùng) chỉ đóng vai trò App Distribution cho build test nội bộ, không liên quan tính năng nhắc nhở.

### Navigation: androidx.navigation Compose Multiplatform

Đã hỗ trợ chính thức Compose Multiplatform từ 2.8+, tái dùng đúng API dev đã quen từ ProjectBase, ít rủi ro bảo trì hơn Voyager (cộng đồng nhỏ) hay Decompose (phức tạp hơn mức cần cho flow khá tuyến tính của app này).

## Danh sách màn hình (MVP)

| Màn hình | Nội dung chính |
|---|---|
| Splash | Logo, kiểm tra trạng thái copy DB lần đầu |
| Onboarding | Mục tiêu ayat/ngày, ngôn ngữ bản dịch, giờ nhắc, chọn giọng đọc |
| Home/Dashboard | Vòng tiến độ hôm nay, số ayat cần ôn, streak, tiếp tục đọc dở |
| Surah List / Juz List | 114 surah hoặc 30 juz, % đã thuộc mỗi mục, tìm kiếm |
| Surah/Ayah Reader | Text Uthmani + bản dịch (toggle), phát audio từng ayah, đánh dấu đã thuộc/bookmark, toggle màu Tajweed (stretch) |
| Memorization Practice Mode | Ẩn/hiện chữ Ả Rập để tự kiểm tra, lặp audio N lần, đánh dấu đã thuộc |
| Review Session | Từng ayah đến hạn ôn, ẩn/hiện, 3 nút chấm điểm |
| Review Session Summary | Tổng kết phiên ôn, ngày ôn tiếp theo |
| Progress/Statistics | % thuộc theo Surah/Juz, streak calendar, độ chính xác ôn tập |
| Bookmarks | Danh sách đã đánh dấu, nhảy tới vị trí đọc |
| Settings | Giọng đọc, ngôn ngữ, giờ nhắc, mục tiêu/ngày, quản lý tải audio, reset tiến độ |
| Audio Download Manager | Trạng thái tải theo Surah, dung lượng đã dùng |
| About/Attribution | Ghi nguồn Tanzil.net + bản quyền bản dịch + nguồn audio |

## Roadmap theo pha

- **Phase 0 — Setup & spike rủi ro:** khởi tạo project KMP (`:shared`, `androidApp`, `iosApp` tối thiểu để compile), spike Room 2.7 trên cả 2 platform, spike Media3+AVFoundation qua `expect/actual`, xác nhận nguồn audio free hợp lệ, viết script build `quran_content.db` từ Tanzil, spike navigation multiplatform.
- **Phase 1 — Data layer + Quran browser:** 2 database Room, copy asset lần đầu, Surah/Juz List, Reader, Bookmarks, Settings cơ bản.
- **Phase 2 — Memorization + SRS lõi:** đánh dấu đã thuộc, % tiến độ, SM-2 engine (có `commonTest`), Review Session, Dashboard, streak.
- **Phase 3 — Audio:** download manager, phát audio trong Reader và Practice Mode, tốc độ phát.
- **Phase 4 — Notification + Onboarding + polish:** lên lịch nhắc 2 platform, luồng onboarding đầy đủ, Tajweed color (nếu còn thời gian), empty/error state.
- **Phase 5 — Android hardening & Play Store closed testing:** port Fastlane/CI pattern từ ProjectBase sang repo mới, viết privacy policy (đơn giản vì không thu thập dữ liệu), chuẩn bị store listing, **release Android trước để kiểm chứng bản free** (đúng yêu cầu ban đầu của bạn) trước khi đầu tư $99/năm cho Apple Developer.
- **Phase 6 — iOS:** dựng `iosApp` thật, nối toàn bộ `iosMain` actual, thêm Fastlane lane iOS (match/gym/scan), đăng ký Apple Developer, TestFlight, App Store.

## File quan trọng cần tạo đầu tiên (theo thứ tự build)

1. `/shared/src/commonMain/core/database/` — 2 định nghĩa Room database, mọi thứ khác phụ thuộc vào đây
2. `/shared/src/commonMain/review/Sm2Engine.kt` — logic SM-2, tính năng lõi khác biệt hoá, cần `commonTest` từ ngày đầu
3. `/shared/src/{androidMain,iosMain}/audio/AudioPlayer.kt` — phần rủi ro cross-platform cao nhất
4. `/shared/src/commonMain/core/database/QuranContentAssetLoader.kt` — logic copy DB lần đầu, cả 2 platform phụ thuộc
5. `fastlane/Fastfile`, `.github/workflows/ci.yml` — copy/adapt nguyên pattern từ ProjectBase ở Phase 5

## Verification

- Phase 0: build KMP project chạy được `androidApp` trên emulator + `iosApp` compile được trên máy Mac (cần Xcode).
- Phase 2: `commonTest` cho SM-2 engine — test các trường hợp Forgot/Hesitate/Confident thay đổi đúng `easeFactor`/`intervalDays`/`dueDate`.
- Phase 5: build release qua Fastlane lane `distribute_dev`, cài thử qua Firebase App Distribution, sau đó upload Play Console closed testing thật để lấy phản hồi trước khi mở rộng.
