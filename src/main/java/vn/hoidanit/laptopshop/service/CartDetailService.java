package vn.hoidanit.laptopshop.service;

import org.springframework.stereotype.Service;

import vn.hoidanit.laptopshop.repository.CartDetailRepository;

@Service
public class CartDetailService {
    private final CartDetailRepository cartDetailRepository;

    public CartDetailService(CartDetailRepository cartDetailRepository) {
        this.cartDetailRepository = cartDetailRepository;
    }

    public void deleteCartDetailById(long id) {
        this.cartDetailRepository.deleteById(id);
    }
}
