# Hệ thống Template Fragments - Hướng dẫn sử dụng

## Tổng quan
Đã tạo hệ thống template fragments để tái sử dụng code, giảm thiểu việc lặp lại và dễ dàng maintain.

## Cấu trúc thư mục

```
templates/
├── universal/                          # Template chung cho tất cả role
│   ├── head.html                      # Meta tags, CSS chung
│   ├── alerts.html                    # Alert messages
│   ├── commonJs.html                  # JavaScript functions chung
│   └── admin/                         # Template dành riêng cho admin
│       ├── sidebar.html               # Sidebar navigation
│       ├── header.html                # Page header với title và button
│       ├── layout.html                # Base layout template
│       ├── modals.html                # Common modals (view, edit, delete, add)
│       └── dataCard.html              # Card với table data
├── admin/
│   ├── chuanbithi/
│   │   ├── list.html                  # Template cũ (để tham khảo)
│   │   ├── list_new.html             # Template mới sử dụng fragments
│   │   └── content.html               # Content fragment riêng cho chuẩn bị thi
│   ├── giaovien/
│   ├── sinhvien/
│   └── ...
```

## Cách sử dụng

### 1. Template cơ bản cho trang admin

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="vi"
      th:with="
        pageTitle='Tên trang',
        pageIcon='fa-icon-name',
        activeMenu='menu-key',
        buttonText='Text button (optional)',
        buttonAction='functionName() (optional)'
      ">
<head th:replace="universal/head :: head(${pageTitle})">
    <!-- Custom styles cho trang này -->
</head>
<body>
    <div class="container-fluid">
        <div class="row">
            <!-- Sidebar -->
            <div th:replace="universal/admin/sidebar :: sidebar(${activeMenu}, ${user})"></div>

            <!-- Main content -->
            <main class="col-md-9 ms-sm-auto col-lg-10 px-md-4">
                <!-- Page Header -->
                <div th:replace="universal/admin/header :: header(${pageTitle}, ${pageIcon}, ${buttonText}, ${buttonAction})"></div>
                
                <!-- Alert Messages -->
                <div th:replace="universal/alerts :: alerts"></div>

                <!-- Page Content -->
                <div th:replace="admin/trang-cua-ban/content :: content"></div>
            </main>
        </div>
    </div>

    <!-- Common Modals -->
    <div th:replace="universal/admin/modals :: viewModal"></div>
    <div th:replace="universal/admin/modals :: editModal"></div>
    <div th:replace="universal/admin/modals :: deleteModal"></div>

    <!-- JavaScript -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <div th:replace="universal/commonJs :: commonJs"></div>
    
    <!-- Page specific script -->
    <script>
        // Code JavaScript riêng cho trang này
    </script>
</body>
</html>
```

### 2. Tạo content fragment cho trang mới

Tạo file `admin/trang-moi/content.html`:

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="vi">
<body>
    <div th:fragment="content">
        <!-- Nội dung riêng của trang -->
        <div class="card">
            <div class="card-header">
                <h5 class="card-title mb-0">
                    <i class="fas fa-list"></i>
                    Danh sách
                    <span id="dataCount" class="badge bg-primary ms-2">0</span>
                </h5>
            </div>
            <div class="card-body">
                <!-- Empty state -->
                <div id="emptyState" class="text-center py-5" style="display: none;">
                    <i class="fas fa-inbox text-muted fa-3x mb-3"></i>
                    <h5 class="text-muted">Chưa có dữ liệu</h5>
                    <p class="text-muted">Thêm mục đầu tiên</p>
                    <button type="button" class="btn btn-primary" onclick="showAddModal()">
                        <i class="fas fa-plus"></i> Thêm mới
                    </button>
                </div>

                <!-- Data table -->
                <div id="dataTable">
                    <div class="table-responsive">
                        <table class="table table-hover">
                            <thead>
                                <tr>
                                    <th>Cột 1</th>
                                    <th>Cột 2</th>
                                    <th class="text-center">Thao tác</th>
                                </tr>
                            </thead>
                            <tbody id="dataTableBody">
                                <!-- Dữ liệu load bằng JS -->
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>

        <!-- Custom modals cho trang này -->
    </div>
</body>
</html>
```

## Các Fragment có sẵn

### 1. universal/head.html
```html
<head th:replace="universal/head :: head('Tiêu đề trang')">
```

### 2. universal/admin/sidebar.html
```html
<div th:replace="universal/admin/sidebar :: sidebar('activeMenu', ${user})">
```

Menu keys:
- `dashboard` - Trang chủ
- `giaovien` - Quản lý giáo viên  
- `sinhvien` - Quản lý sinh viên
- `monhoc` - Quản lý môn học
- `lophoc` - Quản lý lớp học
- `chuanbithi` - Chuẩn bị thi
- `backup` - Sao lưu & Phục hồi

### 3. universal/admin/header.html
```html
<div th:replace="universal/admin/header :: header('Tiêu đề', 'fa-icon', 'Text Button', 'jsFunction()')">
```

### 4. universal/alerts.html
```html
<div th:replace="universal/alerts :: alerts">
```

### 5. universal/admin/modals.html
```html
<!-- View modal -->
<div th:replace="universal/admin/modals :: viewModal">

<!-- Edit modal -->
<div th:replace="universal/admin/modals :: editModal">

<!-- Delete modal -->
<div th:replace="universal/admin/modals :: deleteModal">

<!-- Add modal -->
<div th:replace="universal/admin/modals :: addModal('Tiêu đề modal')">
```

### 6. universal/commonJs.html
```html
<div th:replace="universal/commonJs :: commonJs">
```

**Chức năng có sẵn:**
- `showAlert(type, message)` - Hiển thị alert
- `showEmptyState(count)` - Hiển thị trạng thái rỗng
- `showDataTable(count)` - Hiển thị bảng dữ liệu
- `formatDate(dateString)` - Format ngày theo locale VN
- `createActionButtons(viewAction, editAction, deleteAction)` - Tạo buttons thao tác
- `handleFormSubmit(form, url, method)` - Xử lý submit form với loading

## Lưu ý khi sử dụng

1. **ID elements chuẩn:**
   - `emptyState` - Container cho empty state
   - `dataTable` - Container cho table
   - `dataTableBody` - Body của table
   - `dataCount` - Badge hiển thị số lượng
   - `viewContent` - Content của view modal
   - `editContent` - Content của edit modal
   - `deleteInfo` - Info trong delete modal

2. **CSS classes chuẩn:**
   - Sử dụng Bootstrap 5.1.3
   - FontAwesome 6.0.0
   - Custom classes có sẵn trong head.html

3. **Conventions:**
   - Modal ID: `viewModal`, `editModal`, `deleteModal`, `addModal`
   - Form ID: thường là `addForm`, `editForm`
   - Button confirmDelete: `confirmDeleteBtn`

## Ví dụ hoàn chỉnh

Xem file `admin/chuanbithi/list_new.html` để tham khảo cách implement đầy đủ.

## Ưu điểm

1. **Tái sử dụng:** Giảm 80% code lặp lại
2. **Consistency:** Đảm bảo tính nhất quán UI/UX
3. **Maintainability:** Sửa 1 chỗ áp dụng toàn hệ thống
4. **Scalability:** Dễ dàng thêm trang mới
5. **Performance:** Tối ưu loading và caching

## Migration từ template cũ

1. Tạo file content fragment từ phần nội dung chính
2. Thay thế template chính bằng cấu trúc fragment mới
3. Di chuyển JavaScript vào phần page-specific script
4. Test và so sánh với template cũ
5. Thay thế template cũ khi đã test xong

## Roadmap

- [ ] Tạo fragment cho teacher role
- [ ] Tạo fragment cho student role  
- [ ] Thêm theme system
- [ ] Mobile responsive components
- [ ] Advanced data table component

# Universal List Template System

## Tổng quan

Universal List Template System là hệ thống template được thiết kế để tạo ra các trang list admin với cấu trúc thống nhất, giảm thiểu việc lặp lại code và dễ dang bảo trì.

## Cấu trúc Universal Template

### 1. Template chính
- `universal/admin/list-universal.html` - Template fragment tổng quát cho trang list

### 2. Fragments hỗ trợ
- `universal/head.html` - Common meta tags và CSS
- `universal/admin/sidebar.html` - Sidebar navigation
- `universal/admin/header.html` - Page header với title và action buttons
- `universal/alerts.html` - Alert messages
- `universal/admin/dataCard.html` - Card chứa data table
- `universal/admin/modals.html` - Common modals
- `universal/commonJs.html` - Common JavaScript functions

## Cách sử dụng Universal Template

### 1. Tạo file list chính

```html
<!DOCTYPE html>
<html lang="vi" xmlns:th="http://www.thymeleaf.org">

<head th:replace="~{universal/head :: head('Tiêu đề trang')}"></head>

<body>
    <div class="container-fluid">
        <div class="row">
            <!-- Sidebar -->
            <nav th:replace="~{universal/admin/sidebar :: sidebar('menu-key')}"></nav>

            <!-- Main content -->
            <main class="col-md-9 ms-sm-auto col-lg-10 px-md-4">
                <!-- Page Header -->
                <div th:replace="~{universal/admin/header :: pageHeader(
                    icon='fas fa-icon', 
                    title='Tiêu đề', 
                    buttonText='Text button', 
                    buttonAction='showAddForm()'
                )}"></div>

                <!-- Alert Messages -->
                <div th:replace="~{universal/alerts :: alerts}"></div>

                <!-- Data Card -->
                <div th:replace="~{universal/admin/dataCard :: universalDataCard(
                    icon='fas fa-list',
                    title='Tiêu đề danh sách',
                    entityListVar=${dataList},
                    emptyIcon='fas fa-icon',
                    emptyMessage='Thông báo rỗng',
                    emptySubMessage='Hướng dẫn thêm mới',
                    addButtonText='Text button thêm',
                    tableHeaders=null,
                    tableRowFragment='~{admin/entity/fragments :: tableContent}'
                )}"></div>
            </main>
        </div>
    </div>

    <!-- Modals -->
    <div th:replace="~{admin/entity/fragments :: addModal}"></div>
    <div th:replace="~{admin/entity/fragments :: editModal}"></div>
    <div th:replace="~{admin/entity/fragments :: viewModal}"></div>
    <div th:replace="~{universal/admin/modals :: deleteModal}"></div>

    <!-- Common JS -->
    <div th:replace="~{universal/commonJs :: commonJs}"></div>
    
    <!-- Page specific JS -->
    <div th:replace="~{admin/entity/fragments :: pageJs}"></div>
</body>
</html>
```

### 2. Tạo file fragments.html

```html
<div xmlns:th="http://www.thymeleaf.org">

<!-- Table Content Fragment (Headers + Rows) -->
<div th:fragment="tableContent">
    <thead>
        <tr>
            <th><i class="fas fa-icon"></i> Header 1</th>
            <th><i class="fas fa-icon"></i> Header 2</th>
            <th class="text-center"><i class="fas fa-tools"></i> Thao tác</th>
        </tr>
    </thead>
    <tbody>
        <tr th:each="item : ${dataList}">
            <td>Data 1</td>
            <td>Data 2</td>
            <td class="text-center">
                <!-- Action buttons -->
            </td>
        </tr>
    </tbody>
</div>

<!-- Add Modal Fragment -->
<div th:fragment="addModal" class="modal fade" id="addModal" tabindex="-1">
    <!-- Modal content -->
</div>

<!-- Edit Modal Fragment -->
<div th:fragment="editModal" class="modal fade" id="editModal" tabindex="-1">
    <!-- Modal content -->
</div>

<!-- View Modal Fragment (optional) -->
<div th:fragment="viewModal" class="modal fade" id="viewModal" tabindex="-1">
    <!-- Modal content -->
</div>

<!-- Page Specific JavaScript -->
<script th:fragment="pageJs">
    // Entity specific JavaScript functions
</script>

</div>
```

## Ví dụ cụ thể

### Sinh viên (admin/sinhvien/list_universal.html)
```html
<head th:replace="~{universal/head :: head('Quản lý Sinh viên - Hệ thống Thi trắc nghiệm')}"></head>
<nav th:replace="~{universal/admin/sidebar :: sidebar('sinhvien')}"></nav>
<div th:replace="~{universal/admin/header :: pageHeader(
    icon='fas fa-user-graduate', 
    title='Quản lý Sinh viên', 
    buttonText='Thêm sinh viên', 
    buttonAction='showAddForm()'
)}"></div>
<div th:replace="~{universal/admin/dataCard :: universalDataCard(
    icon='fas fa-list',
    title='Danh sách sinh viên',
    entityListVar=${danhSachSinhVien},
    emptyIcon='fas fa-user-slash',
    emptyMessage='Chưa có sinh viên nào',
    emptySubMessage='Hãy thêm sinh viên đầu tiên cho hệ thống',
    addButtonText='Thêm sinh viên đầu tiên',
    tableHeaders=null,
    tableRowFragment='~{admin/sinhvien/fragments :: tableContent}'
)}"></div>
```

### Giáo viên (admin/giaovien/list_universal.html)
```html
<head th:replace="~{universal/head :: head('Quản lý Giáo viên - Hệ thống Thi trắc nghiệm')}"></head>
<nav th:replace="~{universal/admin/sidebar :: sidebar('giaovien')}"></nav>
<div th:replace="~{universal/admin/header :: pageHeader(
    icon='fas fa-chalkboard-teacher', 
    title='Quản lý Giáo viên', 
    buttonText='Thêm giáo viên', 
    buttonAction='showAddForm()'
)}"></div>
<div th:replace="~{universal/admin/dataCard :: universalDataCard(
    icon='fas fa-list',
    title='Danh sách giáo viên',
    entityListVar=${danhSachGiaoVien},
    emptyIcon='fas fa-user-slash',
    emptyMessage='Chưa có giáo viên nào',
    emptySubMessage='Hãy thêm giáo viên đầu tiên cho hệ thống',
    addButtonText='Thêm giáo viên đầu tiên',
    tableHeaders=null,
    tableRowFragment='~{admin/giaovien/fragments :: tableContent}'
)}"></div>
```

## Lợi ích

1. **Giảm code lặp lại**: Tất cả layout, sidebar, header, alerts được tái sử dụng
2. **Thống nhất UI/UX**: Tất cả trang list có giao diện nhất quán
3. **Dễ bảo trì**: Thay đổi 1 lần ở universal template sẽ áp dụng cho tất cả
4. **Nhanh phát triển**: Chỉ cần tạo fragments cho phần riêng biệt
5. **Flexible**: Có thể tùy chỉnh từng phần nếu cần

## Tệp đã tạo

### Universal Templates
- `templates/universal/admin/list-universal.html`
- `templates/universal/admin/dataCard.html` (updated)

### Sinh viên
- `templates/admin/sinhvien/list_universal.html`
- `templates/admin/sinhvien/fragments.html`

### Giáo viên  
- `templates/admin/giaovien/list_universal.html`
- `templates/admin/giaovien/fragments.html`

### Môn học
- `templates/admin/monhoc/list_universal.html`
- `templates/admin/monhoc/fragments.html`

### Lớp học
- `templates/admin/lophoc/list_universal.html`
- `templates/admin/lophoc/fragments.html`

## Migration từ trang cũ

1. Tạo file `list_universal.html` sử dụng universal template
2. Tạo file `fragments.html` chứa table content và modals
3. Chuyển JavaScript functions vào fragment `pageJs`
4. Test và so sánh với trang cũ
5. Thay thế trang cũ khi đã hoàn thiện

---
