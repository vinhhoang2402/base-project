# BASE-205 — Nâng cấp quy tắc bảo mật mật khẩu cho màn hình Đăng ký

- **Type:** task
- **Status:** done
- **Branch:** feature/BASE-205
- **Created:** 2026-07-26

## Description

Để tăng cường tính bảo mật cho tài khoản người dùng và giảm thiểu rủi ro bị tấn công brute-force, hệ thống cần áp dụng các quy tắc kiểm tra mật khẩu nghiêm ngặt hơn trong luồng đăng ký tài khoản mới.

**User Story:** Là một người dùng mới, tôi muốn được hướng dẫn tạo một mật khẩu có độ bảo mật cao ngay trong quá trình đăng ký, để đảm bảo tài khoản của tôi không dễ dàng bị xâm nhập.

## Acceptance Criteria

- [ ] Mật khẩu hợp lệ phải đáp ứng đồng thời các tiêu chí sau:
  - [ ] Có độ dài tối thiểu 8 ký tự và tối đa 20 ký tự.
  - [ ] Chứa ít nhất một chữ cái viết hoa (A-Z).
  - [ ] Chứa ít nhất một chữ số (0-9).
  - [ ] Chứa ít nhất một ký tự đặc biệt (ví dụ: @, #, $, %, !).
- [ ] Hiển thị thông báo lỗi chi tiết theo thời gian thực (inline validation) ngay dưới ô nhập liệu khi người dùng nhập không đúng định dạng.
- [ ] Thông báo lỗi phải biến mất ngay khi tiêu chí tương ứng được thỏa mãn.
- [ ] Nút "Đăng ký" (Register) phải ở trạng thái vô hiệu hóa (Disabled) cho đến khi mật khẩu nhập vào và mật khẩu xác nhận (Confirm Password) khớp nhau và thỏa mãn các quy tắc trên.
- [ ] Đảm bảo trải nghiệm trên trình đọc màn hình (TalkBack) hoạt động chính xác cho các thông báo lỗi này.

## Notes / Progress

- [ ] Khám phá codebase: tìm RegisterFragment/ViewModel/Contract
- [ ] Implement password validation logic trong ViewModel
- [ ] Cập nhật UI: inline error messages, disable/enable nút Register
- [ ] Đảm bảo accessibility (TalkBack)
- [ ] Viết unit tests
- [ ] ktlintFormat + verify build
