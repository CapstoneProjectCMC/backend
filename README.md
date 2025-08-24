# CodeCampus Backend

## Tổng quan

CodeCampus là một nền tảng trực tuyến được thiết kế để học lập trình. Nền tảng hỗ trợ nhiều vai trò như học sinh, giáo
viên, phụ huynh, quản trị viên và tổ chức (trường học/tổ chức). Các tính năng bao gồm xác thực người dùng, quản lý hồ
sơ, diễn đàn thảo luận, nộp bài tập và bài thi, đánh giá, tài nguyên học tập, thanh toán, thông báo và tích hợp AI.

Kho lưu trữ này chứa các tài liệu lập kế hoạch, câu chuyện người dùng, trường hợp sử dụng và cấu trúc ban đầu để phát
triển ứng dụng CodeCampus. Dự án được xây dựng dựa trên kiến trúc microservices, tập trung vào khả năng mở rộng, bảo mật
và trải nghiệm người dùng.

### Mục tiêu chính

- Cung cấp các bài tập lập trình tương tác với phản hồi thời gian thực.
- Thúc đẩy thảo luận cộng đồng qua diễn đàn công khai và riêng tư.
- Hỗ trợ giáo viên tạo và quản lý bài tập, bài thi và tài nguyên.
- Hỗ trợ các tính năng cao cấp qua đăng ký và tiền ảo.
- Tích hợp AI để đề xuất, đánh giá và kiểm duyệt nội dung.
- Đảm bảo xác thực an toàn và bảo mật dữ liệu.

## Các vai trò

- Là học sinh: Đăng ký, duyệt bài tập, nộp mã nguồn, tham gia diễn đàn.
- Là giáo viên: Tạo bài tập, theo dõi tiến độ, kiểm duyệt nội dung.
- Là quản trị viên: Quản lý người dùng, xem báo cáo, cấu hình hệ thống.

## Tính năng

### 1. Quản lí Xác thực và Phân quyền

- Đăng ký người dùng qua Google/Facebook hoặc email/mật khẩu.
- Xác minh email bằng OTP.
- Đăng nhập cho người dùng, quản trị viên và tổ chức.
- Đặt lại mật khẩu, khóa/mở khóa tài khoản bởi quản trị viên.
- Chức năng đăng xuất.

### 2. Quản lý Người dùng

- Cập nhật thông tin hồ sơ cá nhân.
- Lưu/yêu thích bài tập.
- Xem tiến độ học tập (bài tập đã hoàn thành, điểm số, biểu đồ).
- Thông báo về các cột mốc.
- Role giáo viên: Quản lý lớp học, bài tập đã tạo.
- Liên kết phụ huynh để theo dõi tiến độ của con.
- Tương tác xã hội: Theo dõi/chặn người dùng, xem hồ sơ người khác.

### 3. Quản lí Diễn đàn

- Tạo bài viết/chủ đề trong diễn đàn công khai hoặc nội bộ tổ chức.
- Bình luận, thích/không thích, đính kèm tệp (mã nguồn, hình ảnh).
- Tìm kiếm bài viết theo từ khóa, thẻ, bộ lọc.
- AI đề xuất bài viết liên quan.
- Kiểm duyệt: Xóa/sửa bài viết/bình luận bởi quản trị viên/giáo viên.
- Thông báo cho phản hồi, lượt thích, bài viết mới.

### 4. Quản lý Tổ chức

- Tạo tổ chức với logo, mô tả, và các thông tin liên quan khác.
- Thêm thành viên thủ công hoặc qua nhập Excel.
- Diễn đàn nội bộ với kiểm duyệt AI.
- Báo cáo vi phạm, bình luận thời gian thực.
- Bảng điều khiển cho thống kê hoạt động.
- Nhật ký kiểm tra và giới hạn tốc độ cho API.

### 5. Quản lí Nộp bài tập, thi cử & Đánh giá

- Tạo bài kiểm tra/bài tập mã nguồn với câu hỏi do AI tạo.
- Tìm kiếm/lọc bài tập theo độ khó, chủ đề.
- Nộp mã nguồn, chạy thử nghiệm thời gian thực (hỗ trợ C, C++, Java, Python, v.v.).
- Xem lịch sử nộp bài, điểm số, phản hồi AI về lỗi/khoảng trống kiến thức.
- Công cụ giáo viên: Thêm test case, đặt thời gian, chống gian lận, báo cáo hiệu suất học sinh.
- Xếp hạng, phần thưởng dựa trên điểm số/vị trí.
- Phân tích phong cách mã nguồn bằng AI, phát hiện mã độc.

### 6. Quản lí Tài nguyên Học tập

- Quản lý/tải lên tài liệu/video (hỗ trợ chuyển mã).
- Phân loại theo chủ đề/mức độ.
- Công cụ học sinh: Tìm kiếm, tạo danh sách phát, mua bằng điểm/tiền.
- Theo dõi tiến độ, thông báo về tài nguyên mới.
- Code Playground: Trình chỉnh sửa giống IDE với kiểm soát phiên bản.

### 7. Quản lí Thanh toán

- Đăng ký gói cao cấp.
- Tạo/phân phối phiếu giảm giá.
- Hủy đăng ký với hoàn tiền theo tỷ lệ.
- Nạp tiền ảo.
- CAPTCHA để ngăn chặn gian lận.
- Báo cáo doanh thu cho quản trị viên.

### 8. Quản lí Đánh giá

- Báo cáo PDF hàng tháng cho phụ huynh.
- AI dự đoán tỷ lệ hoàn thành khóa học.
- Lộ trình học tập cá nhân hóa.
- So sánh trung bình lớp học.

### 9. Quản lí Thông báo

- Thông báo qua email/SMS/push cho bài viết mới, bài tập, thanh toán.
- Tùy chỉnh tùy chọn thông báo.
- Nhắc nhở 24 giờ trước hạn chót.
- Thông báo hàng loạt cho tổ chức.

### 10. Quản lí Tìm kiếm

- Tìm kiếm được lưu vào bộ nhớ cache (sử dụng Redis) cho bài viết diễn đàn.

### 11. Tích hợp AI

- API Gemini để đề xuất câu hỏi, dịch thuật, tóm tắt.
- Phân tích khoảng trống kiến thức, phát hiện spam.

### 12. Cơ sở hạ tầng

- CI/CD với GitHub Actions.
- Giám sát qua Grafana/Prometheus.
- Yêu cầu phi chức năng: Bảo mật dữ liệu, hiệu suất, khả năng mở rộng.

## Công nghệ sử dụng

- Backend: Microservices (Java/Spring Boot, C#/.NET Core Web API).
- Frontend: Angular 19.2.14, NgRx, TypeScript, RxJS, Angular CLI
- Cơ sở dữ liệu: Neo4j, PostgreSQL, MongoDB, MS SQL Server.
- AI: Google Gemini API, mô hình NLP để phát hiện spam.
- Bộ nhớ cache: Redis.
- Giám sát: Grafana, Prometheus.
- Triển khai: Docker, Kubernetes, GitHub Actions cho CI/CD.
- Ngôn ngữ cho bài tập mã nguồn: C, C++, Java, Python, JavaScript, SQL (với thực thi trong môi trường sandbox).

## Cài đặt & Thiết lập

1. Git clone [repository-url]
2. Cài đặt phụ thuộc
3. Thiết lập biến môi trường
4. Chạy cục bộ.

## Đóng góp

1. Fork kho lưu trữ.
2. Tạo nhánh tính năng: `git checkout -b feature/new-feature`.
3. Commit thay đổi: `git commit -m 'Thêm tính năng mới'`.
4. Đẩy lên nhánh: `git push origin feature/new-feature`.
5. Mở Pull Request.

## Nhóm phát triển

1. Nguyễn Đình An - BE: Java/Spring Boot
2. Lưu Minh Nhật - BE: Java/Spring Boot
3. Nguyễn Thị Xuân - BE: C#/.Net Core Web API
4. Tô Quang Đức - FE
5. Nguyễn Trà My - FE

*Cập nhật lần cuối: 19 tháng 8, 2025*
