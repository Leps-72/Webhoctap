# Webhoctap - Giao Diện Người Dùng

## 📋 Danh Sách Các Tệp Giao Diện Đã Tạo

### Templates (HTML)
```
templates/
├── home.html                          # Trang chủ - hiển thị khóa học nổi bật
├── layout.html                        # Layout chung (được sử dụng làm mẫu)
├── access-denied.html                 # Trang lỗi 403 - truy cập bị từ chối
├── error.html                         # Trang lỗi 500 - lỗi máy chủ
├── auth/
│   ├── login.html                     # Trang đăng nhập
│   └── register.html                  # Trang đăng ký tài khoản
├── student/
│   ├── dashboard.html                 # Bảng điều khiển học sinh
│   ├── courses.html                   # Danh sách khóa học để khám phá
│   └── course-detail.html             # Chi tiết khóa học
├── teacher/
│   └── dashboard.html                 # Bảng điều khiển giáo viên
└── admin/
    ├── dashboard.html                 # Bảng điều khiển quản trị
    └── users.html                     # Quản lý người dùng
```

### Static Assets

#### CSS
```
static/css/
└── style.css                          # Stylesheet chính (11KB+)
    - Global styles & utilities
    - Navigation & header styles
    - Hero section
    - Feature & course cards
    - Stat cards
    - Form & input styles
    - Button styles
    - Table styles
    - Footer
    - Responsive design
    - Animations
```

#### JavaScript
```
static/js/
└── main.js                            # JavaScript chính (8KB+)
    - Bootstrap component initialization
    - Form validation
    - Alert/notification functions
    - Confirmation dialogs
    - Utility functions
    - Local storage helpers
    - API request helpers
```

## 🎨 Đặc Điểm Giao Diện

### Design System
- **Framework**: Bootstrap 5.3.0
- **Icons**: Font Awesome 6.4.0
- **Color Scheme**: Professional blue, green, red (for alerts)
- **Typography**: Segoe UI, modern sans-serif
- **Responsive**: Mobile-first, works on all devices

### Components Included
- Navigation bar with role-based menus
- Hero section with call-to-action
- Feature cards with hover effects
- Course cards with images and overlays
- Statistics cards
- Forms with validation
- Tables with search/filter
- Modal dialogs
- Alert messages
- Accordion for course content
- Progress bars
- Badges and badges
- Buttons with various states

## 📱 Trang Chính

### Trang Chủ (`home.html`)
- Hero section với call-to-action
- Phần giới thiệu tính năng
- Danh sách khóa học nổi bật
- Thống kê hệ thống
- Footer

### Đăng Nhập (`auth/login.html`)
- Form đăng nhập
- Remember me checkbox
- Link quên mật khẩu
- Demo credentials
- Responsive design

### Đăng Ký (`auth/register.html`)
- Form đăng ký với validation
- Lựa chọn vai trò (Student/Teacher)
- Kiểm tra mật khẩu trùng khớp
- Link điều khoản sử dụng

### Bảng Điều Khiển Học Sinh (`student/dashboard.html`)
- Danh sách khóa học đã đăng ký
- Progress bars cho mỗi khóa
- Nút tiếp tục học
- Tin nhắn khi chưa có khóa

### Khám Phá Khóa Học (`student/courses.html`)
- Bộ lọc tìm kiếm
- Danh sách khóa học dạng grid
- Hiển thị trạng thái đăng ký
- Nút đăng ký/tiếp tục

### Chi Tiết Khóa Học (`student/course-detail.html`)
- Header với thông tin khóa
- Accordion nội dung chương
- Phần yêu cầu
- Card thông tin giáo viên
- Sidebar đăng ký khóa

### Bảng Điều Khiển Giáo Viên (`teacher/dashboard.html`)
- Thống kê khóa học & học sinh
- Hành động nhanh
- Thông báo
- Danh sách khóa gần đây

### Bảng Điều Khiển Admin (`admin/dashboard.html`)
- 4 stat cards chính
- Thông tin khóa chờ duyệt
- Hành động quản lý nhanh
- Bảng hoạt động gần đây

### Quản Lý Người Dùng (`admin/users.html`)
- Tìm kiếm & lọc theo vai trò
- Bảng danh sách người dùng
- Hành động chỉnh sửa/khóa tài khoản
- Hiển thị trạng thái hoạt động

### Trang Lỗi
- `access-denied.html`: Lỗi 403
- `error.html`: Lỗi 500

## 🎯 Tính Năng JavaScript

### Initialization
- Popovers & Tooltips Bootstrap
- Scroll animations
- Form validation

### Helper Functions
- Email validation
- Password strength checker
- Currency formatting
- Date formatting
- Text truncation
- Debounce/Throttle
- Local storage management
- API request helper

### UI Functions
- Show success/error/warning alerts
- Confirmation dialogs
- Table search

## 🔐 Security Features

- Role-based navigation (Student/Teacher/Admin)
- Access denied page
- CSRF-ready forms
- Input validation on client-side
- Password strength requirements

## 📱 Responsive Breakpoints

- Mobile: < 576px
- Tablet: 576px - 768px
- Laptop: 768px - 992px
- Desktop: > 992px

## 🚀 Cách Sử Dụng

### Để sử dụng các templates này:

1. **Trang chủ** → Truy cập `/`
2. **Đăng nhập** → Truy cập `/login`
3. **Đăng ký** → Truy cập `/register`
4. **Học sinh**:
   - Dashboard: `/student/dashboard`
   - Khám phá: `/student/courses/explore`
   - Chi tiết: `/student/courses/{id}`
5. **Giáo viên**:
   - Dashboard: `/teacher/dashboard`
6. **Quản trị**:
   - Dashboard: `/admin/dashboard`
   - Người dùng: `/admin/users`

### Cấu hình trong Controllers

Các controllers cần trả về các view names:
- HomeController → "home"
- AuthController → "auth/login", "auth/register"
- StudentController → "student/dashboard", "student/courses", "student/course-detail"
- TeacherController → "teacher/dashboard"
- AdminController → "admin/dashboard", "admin/users"

## 🎨 Tùy Chỉnh

### Thay đổi màu sắc
Chỉnh sửa CSS variables trong `style.css`:
```css
:root {
    --primary-color: #0d6efd;
    --success-color: #198754;
    --danger-color: #dc3545;
    /* ... */
}
```

### Thêm favicon
```html
<link rel="icon" type="image/x-icon" href="/favicon.ico">
```

### Sử dụng logo
Thay thế `<i class="fas fa-graduation-cap"></i>` bằng `<img src="/logo.png" alt="Logo">`

## 📚 Dependencies

- Bootstrap 5.3.0 (CDN)
- Font Awesome 6.4.0 (CDN)
- Thymeleaf (Backend)
- Spring Security (Backend)

## ✨ Tính Năng Nổi Bật

✅ Responsive design  
✅ Smooth animations  
✅ Modern UI components  
✅ Form validation  
✅ Alert messages  
✅ Role-based access control  
✅ Search & filter functionality  
✅ Progress bars  
✅ Statistics cards  
✅ Accordion content sections  

## 📝 Ghi Chú

- Tất cả các images sử dụng placeholder từ `via.placeholder.com` (có thể thay đổi)
- Form action URLs cần được cập nhật theo controller endpoints
- Các demo credentials hiển thị trong login có thể thay đổi
- CSS classes sử dụng utility-first approach (Bootstrap)

---
**Tạo ngày**: 2026-04-20  
**Version**: 1.0
