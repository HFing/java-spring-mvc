package vn.hoidanit.laptopshop.controller.admin;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import vn.hoidanit.laptopshop.domain.User;

import vn.hoidanit.laptopshop.service.UploadService;
import vn.hoidanit.laptopshop.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

@Controller
public class UserController {
    private final UserService userService;
    private final UploadService uploadService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder, UploadService uploadService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.uploadService = uploadService;
    }

    @RequestMapping("/")
    public String getHomePage(Model model) {
        List<User> arrUsers = this.userService.getAllUsersByEmail("2@gmail.com");
        System.out.println(arrUsers);
        model.addAttribute("name", "Hfing");
        return "hello";
    }

    @GetMapping("/admin/user/create") // GET
    public String getCreatUserPage(Model model) {
        model.addAttribute("newUser", new User());
        return "admin/user/create";
    }

    @PostMapping(value = "/admin/user/create")
    public String creatUserPage(Model model, @ModelAttribute("newUser") @Valid User newUser,
            BindingResult newUserBindingResult,
            @RequestParam("avatarFile") MultipartFile file) {
        // validate
        List<FieldError> errors = newUserBindingResult.getFieldErrors();
        for (FieldError error : errors) {
            System.out.println(error.getField() + " - " + error.getDefaultMessage());
        }

        if (newUserBindingResult.hasErrors()) {
            return "admin/user/create";
        }

        String avatar = this.uploadService.handleSaveUploadFile(file, "avatar");
        String hashPassword = this.passwordEncoder.encode(newUser.getPassword());
        newUser.setPassword(hashPassword);
        newUser.setAvatar(avatar);
        newUser.setRole(this.userService.getRoleByName(newUser.getRole().getName()));
        this.userService.handelSaveUser(newUser);
        return "redirect:/admin/user";
    }

    @GetMapping("/admin/user") // GET
    public String getUserPage(Model model) {
        List<User> users = this.userService.getAllUsers();
        model.addAttribute("users", users);
        System.out.println(">>>Check users" + users);
        return "admin/user/show";
    }

    @RequestMapping("/admin/user/{id}") // GET
    public String getUserDetailPage(Model model, @PathVariable long id) {
        User user = this.userService.getUserById(id);
        model.addAttribute("user", user);
        model.addAttribute("id", id);
        return "admin/user/detail";
    }

    @RequestMapping("/admin/user/update/{id}") // GET
    public String getUserUpdatePage(Model model, @PathVariable long id) {
        User currentUser = this.userService.getUserById(id);
        model.addAttribute("newUser", currentUser); // gan lai gia tri
        return "admin/user/update";
    }

    @PostMapping("/admin/user/update")
    public String postUserUpdate(Model model, @ModelAttribute("newUser") User currentUser) {
        User curentUser = this.userService.getUserById(currentUser.getId());
        if (curentUser != null) {
            curentUser.setAddress(currentUser.getAddress());
            curentUser.setFullName(currentUser.getFullName());
            curentUser.setPhone(currentUser.getPhone());
            this.userService.handelSaveUser(curentUser);
        }
        return "redirect:/admin/user";
    }

    @GetMapping("/admin/user/delete/{id}") // GET
    public String getUserDeletePage(Model model, @PathVariable long id) {
        // User user = new User();
        // user.setId(id);
        // model.addAttribute("newUser", user); cach 1
        model.addAttribute("id", id);
        model.addAttribute("newUser", new User());
        return "admin/user/delete";
    }

    @PostMapping("/admin/user/delete") // GET
    public String postUserDelete(Model model, @ModelAttribute("newUser") User user) {
        this.userService.deleteUserById(user.getId());
        return "redirect:/admin/user";
    }
}
