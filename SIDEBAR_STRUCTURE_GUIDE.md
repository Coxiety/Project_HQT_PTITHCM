# Hướng dẫn Cấu trúc Sidebar

## Tổng quan
Hệ thống có 3 loại sidebar khác nhau để phù hợp với từng vai trò người dùng:

## 1. Admin Sidebar (Universal)
**File**: `universal/admin/sidebar.html`
**Sử dụng cho**: Tất cả trang admin và dashboard admin
**Đặc điểm**:
- Gradient background màu tím (`linear-gradient(135deg, #667eea 0%, #764ba2 100%)`)
- Menu quản lý đầy đủ
- Active state highlighting

### Các trang sử dụng:
- `/admin/dashboard` (Dashboard Admin)
- `/admin/giaovien` (Quản lý Giáo viên)
- `/admin/sinhvien` (Quản lý Sinh viên)
- `/admin/monhoc` (Quản lý Môn học)
- `/admin/lophoc` (Quản lý Lớp học)
- `/admin/chuanbithi` (Chuẩn bị thi)

### Cách sử dụng:
```html
<nav th:replace="~{universal/admin/sidebar :: sidebar('activeMenu', ${user})}"></nav>
```

### Active Menu Values:
- `dashboard` - Dashboard Admin
- `giaovien` - Quản lý Giáo viên
- `sinhvien` - Quản lý Sinh viên
- `monhoc` - Quản lý Môn học
- `lophoc` - Quản lý Lớp học
- `chuanbithi` - Chuẩn bị thi
- `backup` - Sao lưu & Phục hồi

## 2. Student Dashboard Sidebar
**File**: Inline trong `dashboard/student.html`
**Sử dụng cho**: Dashboard sinh viên
**Đặc điểm**:
- Navbar ở trên + sidebar bên trái
- Menu cho chức năng sinh viên
- Style khác với admin

### Menu items:
- Trang chủ
- Thi trắc nghiệm
- Xem kết quả
- Bảng điểm
- Đổi mật khẩu

## 3. Teacher Dashboard Sidebar
**File**: Inline trong `dashboard/teacher.html`
**Sử dụng cho**: Dashboard giảng viên
**Đặc điểm**:
- Navbar ở trên + sidebar bên trái
- Menu cho chức năng giảng viên
- Style khác với admin

### Menu items:
- Trang chủ
- Quản lý môn học
- Quản lý câu hỏi
- Chuẩn bị thi
- Bảng điểm
- Đổi mật khẩu

## CSS Classes

### Admin Sidebar CSS:
```css
.sidebar {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    min-height: 100vh;
}

.sidebar .nav-link {
    color: rgba(255, 255, 255, 0.8);
    transition: all 0.3s ease;
}

.sidebar .nav-link:hover,
.sidebar .nav-link.active {
    color: #fff;
    background-color: rgba(255, 255, 255, 0.1);
}
```

### Dashboard Sidebar CSS:
```css
.bg-dark.sidebar {
    background-color: #343a40 !important;
}
```

## Routing và Controllers

### Admin Routes:
- `AuthController.adminDashboard()` → `dashboard/admin.html`
- `GiaoVienController` → `admin/giaovien/*`
- `SinhVienController` → `admin/sinhvien/*`
- Etc.

### User Dashboard Routes:
- `AuthController.dashboard()` → Phân tích role và redirect
  - Teacher → `dashboard/teacher.html`
  - Student → `dashboard/student.html`
  - Default → `dashboard/default.html`

## Best Practices

### 1. Khi tạo trang admin mới:
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="vi">
<head th:replace="~{universal/head :: head('Page Title')}"></head>
<body>
    <div class="container-fluid">
        <div class="row">
            <!-- Sidebar -->
            <nav th:replace="~{universal/admin/sidebar :: sidebar('menuKey', ${user})}"></nav>

            <!-- Main content -->
            <main class="col-md-9 ms-sm-auto col-lg-10 px-md-4">
                <!-- Content here -->
            </main>
        </div>
    </div>
</body>
</html>
```

### 2. Khi thêm menu item mới:
1. Thêm vào `universal/admin/sidebar.html`
2. Thêm CSS class nếu cần trong `admin.css`
3. Đảm bảo controller truyền đúng activeMenu

### 3. CSS Organization:
- `common.css` - Global styles
- `admin.css` - Admin-specific styles (sidebar, etc.)
- `dashboard.css` - Dashboard-specific styles
- `forms.css` - Form styling

## Troubleshooting

### Sidebar không hiện gradient:
- Kiểm tra CSS được load: `admin.css`
- Kiểm tra class `.sidebar` được áp dụng

### Active state không hoạt động:
- Kiểm tra parameter `activeMenu` được truyền đúng
- Kiểm tra giá trị trong controller
- Verify Thymeleaf condition: `th:classappend="${activeMenu == 'key'} ? 'active' : ''"`

### Menu không hiện:
- Kiểm tra user object được truyền
- Kiểm tra fragment syntax: `~{universal/admin/sidebar :: sidebar}`
