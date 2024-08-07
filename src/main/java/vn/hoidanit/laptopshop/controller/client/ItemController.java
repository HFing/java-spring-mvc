package vn.hoidanit.laptopshop.controller.client;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import vn.hoidanit.laptopshop.domain.Cart;
import vn.hoidanit.laptopshop.domain.CartDetail;
import vn.hoidanit.laptopshop.domain.Product;
import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.service.CartDetailService;
import vn.hoidanit.laptopshop.service.CartService;
import vn.hoidanit.laptopshop.service.ProductService;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class ItemController {
    private final ProductService productService;
    private final CartDetailService cartDetailService;
    private final CartService cartService;

    public ItemController(ProductService productService, CartDetailService cartDetailService, CartService cartService) {
        this.productService = productService;
        this.cartDetailService = cartDetailService;
        this.cartService = cartService;
    }

    @GetMapping("/product/{id}")
    public String getMethodName(Model model, @PathVariable long id) {
        Product product = this.productService.getProductById(id);
        model.addAttribute("product", product);
        model.addAttribute("id", id);
        return "client/product/detail";
    }

    @PostMapping("/add-product-to-cart/{id}")
    public String addProductToCart(@PathVariable long id, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String email = session.getAttribute("email").toString();
        long productId = id;
        this.productService.handelAddProductToCart(email, productId, session);
        return "redirect:/";
    }

    @GetMapping("/cart")
    public String getCartPage(Model model, HttpServletRequest request) {
        User currentUser = new User();
        HttpSession session = request.getSession(false);
        long userId = (long) session.getAttribute("id");
        currentUser.setId(userId);
        Cart cart = this.productService.fetchByUser(currentUser);
        List<CartDetail> cartDetails = cart == null ? new ArrayList<CartDetail>() : cart.getCartDetails();
        double totalPrice = 0;
        for (CartDetail cartDetail : cartDetails) {
            totalPrice += cartDetail.getPrice() * cartDetail.getQuantity();
        }
        model.addAttribute("cartDetails", cartDetails);
        model.addAttribute("totalPrice", totalPrice);
        return "client/cart/show";
    }

    @PostMapping("/delete-cart-product/{id}")
    public String postMethodName(@PathVariable long id, HttpServletRequest request) {
        // TODO: process POST request
        HttpSession session = request.getSession(false);
        Cart cart = this.productService.getCartDetailById(id).getCart();
        this.cartDetailService.deleteCartDetailById(id);
        if (cart.getSum() > 1) {
            int s = cart.getSum() - 1;
            cart.setSum(cart.getSum() - 1);
            this.cartService.updateCart(cart);
            session.setAttribute("sum", s);
        } else {
            this.cartService.deleteCartById(cart.getId());
            session.setAttribute("sum", 0);
        }

        // cách khac
        // long cartDetailId = id;
        // this.productService.hadelRemoveCratDetail(cartDetailId, session);
        return "redirect:/cart";
    }

}
