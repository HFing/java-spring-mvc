package vn.hoidanit.laptopshop.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;
import vn.hoidanit.laptopshop.domain.Cart;
import vn.hoidanit.laptopshop.domain.CartDetail;
import vn.hoidanit.laptopshop.domain.Product;
import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.repository.CartDetailRepository;
import vn.hoidanit.laptopshop.repository.CartRepository;
import vn.hoidanit.laptopshop.repository.ProductRepository;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final CartDetailRepository cartDetailRepository;
    private final UserService userService;

    public ProductService(ProductRepository productRepository, CartRepository cartRepository,
            CartDetailRepository cartDetailRepository, UserService userService) {
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.cartDetailRepository = cartDetailRepository;
        this.userService = userService;
    }

    public Product handelSaveProduct(Product product) {
        return this.productRepository.save(product);
    }

    public List<Product> getAllProduct() {
        return this.productRepository.findAll();
    }

    public Product getProductById(long id) {
        return this.productRepository.findById(id).orElse(null);
    }

    public void deleteProductById(long id) {
        this.productRepository.deleteById(id);
    }

    public void handelAddProductToCart(String email, long productId, HttpSession session) {
        User user = this.userService.getUserByEmail(email);
        if (user != null) {
            Cart cart = this.cartRepository.findByUser(user);
            if (cart == null) {
                // tao moi cart
                Cart otherCart = new Cart();
                otherCart.setUser(user);
                otherCart.setSum(0);
                cart = this.cartRepository.save(otherCart);
            }
            Optional<Product> productOpt = this.productRepository.findById(productId);
            if (productOpt.isPresent()) {

                Product realProduct = productOpt.get();
                // check san pham da co trong gio hang chua
                boolean isExistProductInCart = this.cartDetailRepository.existsByCartAndProduct(cart, realProduct);
                CartDetail oDetail = this.cartDetailRepository.findByCartAndProduct(cart, realProduct);
                if (oDetail == null) {
                    CartDetail cartDetail = new CartDetail();
                    cartDetail.setCart(cart);
                    cartDetail.setProduct(realProduct);
                    cartDetail.setQuantity(1);
                    cartDetail.setPrice(realProduct.getPrice());
                    this.cartDetailRepository.save(cartDetail);
                    // update sum
                    int s = cart.getSum() + 1;
                    cart.setSum(cart.getSum() + 1);
                    this.cartRepository.save(cart);
                    session.setAttribute("sum", s);
                } else {
                    oDetail.setQuantity(oDetail.getQuantity() + 1);
                    this.cartDetailRepository.save(oDetail);
                }

            }

        }
    }

    public Cart fetchByUser(User user) {
        return this.cartRepository.findByUser(user);
    }

    public CartDetail getCartDetailById(long id) {
        return this.cartDetailRepository.findById(id).orElse(null);
    }

    public void hadelRemoveCratDetail(long cartDetailId, HttpSession session) {
        CartDetail cartDetail = this.cartDetailRepository.findById(cartDetailId).orElse(null);
        if (cartDetail != null) {
            Cart cart = cartDetail.getCart();
            this.cartDetailRepository.deleteById(cartDetailId);
            if (cart.getSum() > 1) {
                int s = cart.getSum() - 1;
                cart.setSum(cart.getSum() - 1);
                session.setAttribute("sum", s);
                this.cartRepository.save(cart);
            } else {
                this.cartRepository.delete(cart);
                session.setAttribute("sum", 0);
            }
        }
    }
}
