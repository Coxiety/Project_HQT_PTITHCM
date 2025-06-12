# CSS Optimization & Testing Guide - Hệ thống Thi trắc nghiệm

## Testing Your CSS Changes

### 1. CSS Test Page
- **URL**: `http://localhost:8081/test/css`
- **Controller**: `CSSTestController.java`
- **Template**: `templates/test/css-test.html`

### Test Components Include:
- ✅ Sidebar navigation with gradient
- ✅ Card components with shadow effects
- ✅ Button styles and action buttons
- ✅ Form elements (inputs, selects, checkboxes)
- ✅ Table hover effects
- ✅ Alert messages
- ✅ Utility classes (gradients, shadows)
- ✅ Empty state styling

## CSS Files Structure

### `/static/css/common.css`
**Purpose**: Global styles for all pages
```css
- Base body and font styles
- Card components (.card, .shadow-soft, .shadow-hover)
- Table styles (.table, .table-hover)
- Button utilities (.btn-action)
- Alert styles (.alert)
- Utility classes (.gradient-primary, .gradient-secondary)
- Empty states (.empty-state)
```

### `/static/css/admin.css`
**Purpose**: Admin-specific styles
```css
- Sidebar styles (.sidebar with gradient)
- Navigation links (.nav-link hover effects)
- Admin card styling
- Class info components (.class-info)
- Info row layouts (.info-row)
- Responsive design for mobile
```

### `/static/css/dashboard.css`
**Purpose**: Dashboard layouts
```css
- General dashboard layout
- Sidebar variations for different user types
- Admin dashboard specific styling (.admin-sidebar)
- Dashboard cards (.admin-card)
- Navbar styling
- Main content area
```

### `/static/css/forms.css`
**Purpose**: Form and modal styling
```css
- Form containers (.form-container)
- Modal styling with gradients
- Input focus states
- Validation styling
- Loading button states
- Form controls and checkboxes
```

## CSS Classes Reference

### Utility Classes
```css
.gradient-primary       /* Primary gradient background */
.gradient-secondary     /* Secondary gradient background */
.shadow-soft           /* Soft shadow effect */
.shadow-hover          /* Hover shadow effect */
.border-radius-lg      /* Large border radius */
.text-gradient         /* Gradient text color */
.empty-state          /* Empty state styling */
.loading              /* Loading state */
```

### Component Classes
```css
.sidebar              /* Main sidebar with gradient */
.admin-sidebar        /* Admin-specific sidebar */
.class-info          /* Info cards with gradient */
.info-row            /* Information display rows */
.form-container      /* Form wrapper styling */
.btn-action          /* Small action buttons */
.admin-card          /* Dashboard cards with hover */
```

## Browser Testing Checklist

### ✅ Visual Elements
- [ ] Sidebar gradient displays correctly
- [ ] Card shadows and hover effects work
- [ ] Button styles match design
- [ ] Table hover effects function
- [ ] Form inputs focus properly
- [ ] Alert messages styled correctly

### ✅ Responsive Design
- [ ] Mobile sidebar collapses properly
- [ ] Cards stack correctly on small screens
- [ ] Tables scroll horizontally on mobile
- [ ] Button groups wrap appropriately

### ✅ Interactive Elements
- [ ] Hover effects work smoothly
- [ ] Focus states are visible
- [ ] Loading states display correctly
- [ ] Modal styling is consistent

## Performance Optimization

### CSS Loading Strategy
1. **Common.css** - Loaded on all pages (universal/head.html)
2. **Admin.css** - Loaded with common.css for admin pages
3. **Dashboard.css** - Loaded for dashboard pages
4. **Forms.css** - Loaded for form pages

### File Sizes (Approximate)
- `common.css`: ~2KB
- `admin.css`: ~2KB  
- `dashboard.css`: ~3KB
- `forms.css`: ~2KB
- **Total**: ~9KB (vs ~50KB+ of duplicated inline CSS)

## Troubleshooting

### CSS Not Loading
1. Check `application.properties` static path pattern
2. Verify CSS files in `target/classes/static/css/`
3. Check browser DevTools Network tab
4. Clear browser cache

### Styles Not Applied
1. Check CSS selector specificity
2. Verify Bootstrap version compatibility
3. Check for CSS syntax errors
4. Use browser DevTools to inspect elements

### Path Issues
```html
<!-- Correct format -->
<link th:href="@{/static/css/common.css}" rel="stylesheet">

<!-- Incorrect format -->
<link th:href="@{/css/common.css}" rel="stylesheet">
```

## Future Enhancements

### Theme Support
```css
/* Add CSS custom properties for theming */
:root {
  --primary-gradient: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  --shadow-color: rgba(0, 0, 0, 0.1);
  --border-radius: 0.5rem;
}
```

### Dark Mode
```css
@media (prefers-color-scheme: dark) {
  :root {
    --bg-color: #1a1a1a;
    --text-color: #ffffff;
    --card-bg: #2d2d2d;
  }
}
```

### Animation Library
```css
/* Add smooth transitions */
.card {
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.btn {
  transition: all 0.2s ease;
}
```

## Migration Summary

### Before Refactoring
- 15+ HTML files with duplicate inline CSS
- ~50KB+ of repeated CSS code
- Inconsistent styling across pages
- Difficult to maintain and update

### After Refactoring
- 4 organized CSS files
- ~9KB total CSS (80%+ reduction)
- Consistent styling across all pages
- Easy to maintain and extend
- Better performance with caching

### Files Successfully Migrated
- ✅ All admin management pages
- ✅ All dashboard pages  
- ✅ All form pages
- ✅ Universal template system
- ✅ Fragment components

### Remaining
- 🔄 Login page (kept separate intentionally)
- ✅ CSS test page for validation
