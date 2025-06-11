// package com.thitracnghiem.app.controller;

// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.ModelAttribute;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.ResponseBody;
// import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// import com.thitracnghiem.app.config.SessionManager;
// import com.thitracnghiem.app.exception.ErrorCode;
// import com.thitracnghiem.app.exception.SuccessCode;
// import com.thitracnghiem.app.model.GIAOVIEN;
// import com.thitracnghiem.app.service.GiaoVienService;

// @Controller
// @RequestMapping("/admin/giaovien")
// public class AdminController {

//     @Autowired
//     private GiaoVienService giaoVienService;
    
//     @Autowired
//     private SessionManager sessionManager;

//     /**
//      * Hiển thị danh sách giáo viên
//      */
//     @GetMapping
//     public String listGiaoVien(Model model) {
//         // Kiểm tra quyền admin
//         if (!sessionManager.isAdmin()) {
//             return "redirect:/dashboard";
//         }

//         try {
//             List<GIAOVIEN> danhSachGiaoVien = giaoVienService.getAllGiaoVien();
//             model.addAttribute("danhSachGiaoVien", danhSachGiaoVien);
//             model.addAttribute("user", sessionManager.getCurrentUser());
//             return "admin/giaovien/list";
//         } catch (Exception e) {
//             model.addAttribute("error", ErrorCode.GENERAL_ERROR_MSG + ": " + e.getMessage());
//             return "admin/giaovien/list";
//         }
//     }

//     /**
//      * Hiển thị form thêm giáo viên
//      */
//     @GetMapping("/add")
//     public String showAddForm(Model model) {
//         if (!sessionManager.isAdmin()) {
//             return "redirect:/dashboard";
//         }

//         model.addAttribute("giaovien", new GIAOVIEN());
//         model.addAttribute("user", sessionManager.getCurrentUser());
//         return "admin/giaovien/form";
//     }

//     /**
//      * Xử lý thêm giáo viên
//      */
//     @PostMapping("/add")
//     public String addGiaoVien(@ModelAttribute GIAOVIEN giaovien, 
//                              RedirectAttributes redirectAttributes) {
//         if (!sessionManager.isAdmin()) {
//             return "redirect:/dashboard";
//         }

//         try {
//             boolean success = giaoVienService.addGiaoVien(giaovien);
//             if (success) {
//                 redirectAttributes.addFlashAttribute("success", SuccessCode.GV_CREATE_SUCCESS_MSG);
//             } else {
//                 redirectAttributes.addFlashAttribute("error", ErrorCode.GENERAL_ERROR_MSG);
//             }
//         } catch (Exception e) {
//             redirectAttributes.addFlashAttribute("error", ErrorCode.GENERAL_ERROR_MSG + ": " + e.getMessage());
//         }

//         return "redirect:/admin/giaovien";
//     }

//     /**
//      * API: Lấy thông tin giáo viên để xem chi tiết (Modal)
//      */
//     @GetMapping("/api/view/{maGV}")
//     @ResponseBody
//     public ResponseEntity<Map<String, Object>> getGiaoVienDetails(@PathVariable String maGV) {
//         Map<String, Object> response = new HashMap<>();
        
//         if (!sessionManager.isAdmin()) {
//             response.put("success", false);
//             response.put("message", ErrorCode.ACCESS_DENIED_MSG);
//             return ResponseEntity.ok(response);
//         }

//         try {
//             GIAOVIEN giaovien = giaoVienService.getGiaoVienById(maGV);
//             if (giaovien == null) {
//                 response.put("success", false);
//                 response.put("message", ErrorCode.GV_NOT_FOUND_MSG);
//                 return ResponseEntity.ok(response);
//             }

//             Map<String, Object> gvData = new HashMap<>();
//             gvData.put("magv", giaovien.getMAGV());
//             gvData.put("ho", giaovien.getHO());
//             gvData.put("ten", giaovien.getTEN());
//             gvData.put("sodtll", giaovien.getSODTLL());
//             gvData.put("diachi", giaovien.getDIACHI());

//             response.put("success", true);
//             response.put("giaovien", gvData);
//             return ResponseEntity.ok(response);
//         } catch (Exception e) {
//             response.put("success", false);
//             response.put("message", ErrorCode.GENERAL_ERROR_MSG + ": " + e.getMessage());
//             return ResponseEntity.ok(response);
//         }
//     }

//     /**
//      * API: Lấy thông tin giáo viên để chỉnh sửa (Modal)
//      */
//     @GetMapping("/api/edit/{maGV}")
//     @ResponseBody
//     public ResponseEntity<Map<String, Object>> getGiaoVienForEdit(@PathVariable String maGV) {
//         Map<String, Object> response = new HashMap<>();
        
//         if (!sessionManager.isAdmin()) {
//             response.put("success", false);
//             response.put("message", ErrorCode.ACCESS_DENIED_MSG);
//             return ResponseEntity.ok(response);
//         }

//         try {
//             GIAOVIEN giaovien = giaoVienService.getGiaoVienById(maGV);
//             if (giaovien == null) {
//                 response.put("success", false);
//                 response.put("message", ErrorCode.GV_NOT_FOUND_MSG);
//                 return ResponseEntity.ok(response);
//             }

//             Map<String, Object> gvData = new HashMap<>();
//             gvData.put("magv", giaovien.getMAGV());
//             gvData.put("ho", giaovien.getHO());
//             gvData.put("ten", giaovien.getTEN());
//             gvData.put("sodtll", giaovien.getSODTLL());
//             gvData.put("diachi", giaovien.getDIACHI());

//             response.put("success", true);
//             response.put("giaovien", gvData);
//             return ResponseEntity.ok(response);
//         } catch (Exception e) {
//             response.put("success", false);
//             response.put("message", ErrorCode.GENERAL_ERROR_MSG + ": " + e.getMessage());
//             return ResponseEntity.ok(response);
//         }
//     }

//     /**
//      * API: Thêm giáo viên mới (Modal)
//      */
//     @PostMapping("/api/add")
//     @ResponseBody
//     public ResponseEntity<Map<String, Object>> addGiaoVienApi(@RequestBody Map<String, String> requestData) {
//         Map<String, Object> response = new HashMap<>();
        
//         if (!sessionManager.isAdmin()) {
//             response.put("success", false);
//             response.put("message", ErrorCode.ACCESS_DENIED_MSG);
//             return ResponseEntity.ok(response);
//         }

//         try {
//             GIAOVIEN giaovien = new GIAOVIEN();
//             giaovien.setMAGV(requestData.get("magv"));
//             giaovien.setHO(requestData.get("ho"));
//             giaovien.setTEN(requestData.get("ten"));
//             giaovien.setSODTLL(requestData.get("sodtll"));
//             giaovien.setDIACHI(requestData.get("diachi"));

//             boolean success = giaoVienService.addGiaoVien(giaovien);
//             if (success) {
//                 response.put("success", true);
//                 response.put("message", SuccessCode.GV_CREATE_SUCCESS_MSG);
//             } else {
//                 response.put("success", false);
//                 response.put("message", ErrorCode.GENERAL_ERROR_MSG);
//             }
//             return ResponseEntity.ok(response);
//         } catch (Exception e) {
//             response.put("success", false);
//             response.put("message", ErrorCode.GENERAL_ERROR_MSG + ": " + e.getMessage());
//             return ResponseEntity.ok(response);
//         }
//     }

//     /**
//      * API: Cập nhật giáo viên (Modal)
//      */
//     @PostMapping("/api/update")
//     @ResponseBody
//     public ResponseEntity<Map<String, Object>> updateGiaoVien(@RequestBody Map<String, String> requestData) {
//         Map<String, Object> response = new HashMap<>();
        
//         if (!sessionManager.isAdmin()) {
//             response.put("success", false);
//             response.put("message", ErrorCode.ACCESS_DENIED_MSG);
//             return ResponseEntity.ok(response);
//         }

//         try {
//             GIAOVIEN giaovien = new GIAOVIEN();
//             giaovien.setMAGV(requestData.get("magv"));
//             giaovien.setHO(requestData.get("ho"));
//             giaovien.setTEN(requestData.get("ten"));
//             giaovien.setSODTLL(requestData.get("sodtll"));
//             giaovien.setDIACHI(requestData.get("diachi"));

//             boolean success = giaoVienService.updateGiaoVien(giaovien);
//             if (success) {
//                 response.put("success", true);
//                 response.put("message", SuccessCode.GV_UPDATE_SUCCESS_MSG);
//             } else {
//                 response.put("success", false);
//                 response.put("message", ErrorCode.GENERAL_ERROR_MSG);
//             }
//             return ResponseEntity.ok(response);
//         } catch (Exception e) {
//             response.put("success", false);
//             response.put("message", ErrorCode.GENERAL_ERROR_MSG + ": " + e.getMessage());
//             return ResponseEntity.ok(response);
//         }
//     }

//     /**
//      * Hiển thị form sửa giáo viên (Legacy - giữ lại cho tương thích)
//      */
//     @GetMapping("/edit/{maGV}")
//     public String showEditForm(@PathVariable String maGV, Model model) {
//         if (!sessionManager.isAdmin()) {
//             return "redirect:/dashboard";
//         }

//         try {
//             GIAOVIEN giaovien = giaoVienService.getGiaoVienById(maGV);
//             if (giaovien == null) {
//                 model.addAttribute("error", ErrorCode.GV_NOT_FOUND_MSG);
//                 return "redirect:/admin/giaovien";
//             }

//             model.addAttribute("giaovien", giaovien);
//             model.addAttribute("user", sessionManager.getCurrentUser());
//             return "admin/giaovien/form";
//         } catch (Exception e) {
//             model.addAttribute("error", ErrorCode.GENERAL_ERROR_MSG + ": " + e.getMessage());
//             return "redirect:/admin/giaovien";
//         }
//     }

//     /**
//      * Xử lý cập nhật giáo viên (Legacy)
//      */
//     @PostMapping("/edit")
//     public String updateGiaoVien(@ModelAttribute GIAOVIEN giaovien,
//                                 RedirectAttributes redirectAttributes) {
//         if (!sessionManager.isAdmin()) {
//             return "redirect:/dashboard";
//         }

//         try {
//             boolean success = giaoVienService.updateGiaoVien(giaovien);
//             if (success) {
//                 redirectAttributes.addFlashAttribute("success", SuccessCode.GV_UPDATE_SUCCESS_MSG);
//             } else {
//                 redirectAttributes.addFlashAttribute("error", ErrorCode.GENERAL_ERROR_MSG);
//             }
//         } catch (Exception e) {
//             redirectAttributes.addFlashAttribute("error", ErrorCode.GENERAL_ERROR_MSG + ": " + e.getMessage());
//         }

//         return "redirect:/admin/giaovien";
//     }

//     /**
//      * Xóa giáo viên
//      */
//     @PostMapping("/delete/{maGV}")
//     public String deleteGiaoVien(@PathVariable String maGV, RedirectAttributes redirectAttributes) {
//         if (!sessionManager.isAdmin()) {
//             return "redirect:/dashboard";
//         }

//         try {
//             boolean success = giaoVienService.deleteGiaoVien(maGV);
//             if (success) {
//                 redirectAttributes.addFlashAttribute("success", SuccessCode.GV_DELETE_SUCCESS_MSG);
//             } else {
//                 redirectAttributes.addFlashAttribute("error", ErrorCode.GENERAL_ERROR_MSG);
//             }
//         } catch (Exception e) {
//             redirectAttributes.addFlashAttribute("error", e.getMessage());
//         }

//         return "redirect:/admin/giaovien";
//     }

//     /**
//      * Xem chi tiết giáo viên (Legacy - giữ lại cho tương thích)
//      */
//     @GetMapping("/view/{maGV}")
//     public String viewGiaoVien(@PathVariable String maGV, Model model) {
//         if (!sessionManager.isAdmin()) {
//             return "redirect:/dashboard";
//         }

//         try {
//             GIAOVIEN giaovien = giaoVienService.getGiaoVienById(maGV);
//             if (giaovien == null) {
//                 model.addAttribute("error", ErrorCode.GV_NOT_FOUND_MSG);
//                 return "redirect:/admin/giaovien";
//             }

//             model.addAttribute("giaovien", giaovien);
//             model.addAttribute("user", sessionManager.getCurrentUser());
//             return "admin/giaovien/view";
//         } catch (Exception e) {
//             model.addAttribute("error", ErrorCode.GENERAL_ERROR_MSG + ": " + e.getMessage());
//             return "redirect:/admin/giaovien";
//         }
//     }
// } 