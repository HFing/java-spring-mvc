package vn.hoidanit.laptopshop.service;

import org.springframework.stereotype.Service;

import vn.hoidanit.laptopshop.domain.Cart;
import vn.hoidanit.laptopshop.repository.CartRepository;

@Service
public class CartService {
    private final CartRepository cartRepository;

    public CartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    public void deleteCartById(long id) {
        this.cartRepository.deleteById(id);
    }

    public Cart updateCart(Cart cart) {
        return this.cartRepository.save(cart);
    }
}
