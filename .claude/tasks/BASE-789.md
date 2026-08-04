# BASE-789 — Tích hợp xác thực sinh trắc học (Biometric Authentication) cho module Auth

- **Type:** task
- **Status:** in-progress
- **Branch:** feature/BASE-789
- **Created:** 2026-07-26

## Description
Triển khai tính năng đăng nhập bằng vân tay hoặc khuôn mặt vào module `feature-auth`
để tăng cường bảo mật và cải thiện trải nghiệm người dùng. Sử dụng thư viện
`androidx.biometric` và đảm bảo hoạt động ổn định trên Android API 24+.

## Acceptance Criteria
- [x] Kiểm tra tính khả dụng của phần cứng sinh trắc học trước khi hiển thị tùy chọn.
- [x] Hiển thị BiometricPrompt chuẩn của hệ thống khi người dùng chọn đăng nhập nhanh.
- [x] Lưu trữ token xác thực an toàn bằng EncryptedSharedPreferences (security-crypto có trong libs.versions.toml).
- [x] Xử lý lỗi: người dùng hủy, xác thực thất bại quá nhiều lần, hoặc thiết bị chưa đăng ký sinh trắc học.
- [x] Cơ chế fallback về nhập mật khẩu/PIN nếu sinh trắc học không khả dụng.

## Notes / Progress
- Thêm `androidx.biometric 1.1.0` vào libs.versions.toml + core-ui.
- **core-ui/biometric**: `BiometricAvailability`, `BiometricAuthOutcome`,
  `BiometricAvailabilityChecker` (+ `DefaultBiometricAvailabilityChecker` dùng
  `BiometricManager` với `BIOMETRIC_STRONG`), `BiometricPromptPresenter` (wrapper
  cho `BiometricPrompt`, ánh xạ error code → outcome ngữ nghĩa).
- **core-network**: tái sử dụng `SecurePreferencesManager` (EncryptedSharedPreferences)
  để lưu/đọc token — AC3.
- **feature-auth**: `AuthRepository` thêm `isBiometricLoginEnabled()` +
  `loginWithBiometricToken()`; usecases `BiometricLoginUseCase`,
  `IsBiometricLoginEnabledUseCase`; `LoginContract`/`LoginViewModel` xử lý
  availability, prompt, lockout, cancel, fallback; DI + strings.
- **Tests**: bổ sung 9 test biometric cho `LoginViewModel`.
- ⚠️ Đã sửa lỗi CÓ SẴN: `FakeAuthRepository.register()` override hàm không tồn tại
  trong interface → test module không compile. Đã viết lại fake cho khớp interface.
- ⚠️ Chưa build/test được tại máy agent (thiếu gradlew/JAVA_HOME) → cần verify ở bước `ready`.
- ℹ️ Chưa tạo `LoginFragment`/layout (repo chưa có UI auth ở nhánh XML này). Khi có
  màn Login, gọi `BiometricPromptPresenter.authenticate(...)` từ `handleEffect(ShowBiometricPrompt)`
  và bắn `Intent.ScreenStarted` trong `onViewCreated`.
