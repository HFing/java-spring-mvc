package vn.hoidanit.laptopshop.controller.admin;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.repository.UserRepository;
import vn.hoidanit.laptopshop.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @RequestMapping("/")
    public String getHomePage(Model model) {
        List<User> arrUsers = this.userService.getAllUsersByEmail("2@gmail.com");
        System.out.println(arrUsers);
        model.addAttribute("name", "Hfing");
        return "hello";
    }

    @RequestMapping("/admin/user/create") // GET
    public String getCreatUserPage(Model model) {
        model.addAttribute("newUser", new User());
        return "admin/user/create";
    }

    @RequestMapping(value = "/admin/user/create", method = RequestMethod.POST)
    public String creatUserPage(Model model, @ModelAttribute("newUser") User newUser) {
        this.userRepository.save(newUser);
        return "redirect:/admin/user";
    }

    @RequestMapping("/admin/user") // GET
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
