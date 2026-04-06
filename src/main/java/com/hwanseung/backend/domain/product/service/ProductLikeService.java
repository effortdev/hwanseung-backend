package com.hwanseung.backend.domain.product.service;

import com.hwanseung.backend.domain.product.dto.ProductLikeResponseDTO;
import com.hwanseung.backend.domain.product.entity.Product;
import com.hwanseung.backend.domain.product.entity.ProductLike;
import com.hwanseung.backend.domain.product.repository.ProductLikeRepository;
import com.hwanseung.backend.domain.product.repository.ProductRepository;
import com.hwanseung.backend.domain.user.entity.User;
import com.hwanseung.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductLikeService {

    private final ProductRepository productRepository;
    private final ProductLikeRepository productLikeRepository;
    private final UserRepository userRepository;

    // ✅ 찜 추가
    @Transactional
    public ProductLikeResponseDTO likeProduct(Integer productId, String loginUserId) {
        Product product = getProduct(productId);
        User user = getUser(loginUserId);

        // ✅ 본인 상품 찜 금지
        if (product.getSellerId().equals(loginUserId)) {
            throw new IllegalArgumentException("본인 상품은 찜할 수 없습니다.");
        }

        // ✅ 판매완료 상품 찜 금지
        if (product.isSoldOut()) {
            throw new IllegalArgumentException("판매완료 상품은 찜할 수 없습니다.");
        }

        boolean alreadyLiked = productLikeRepository.existsByProductAndUser(product, user);

        if (alreadyLiked) {
            return ProductLikeResponseDTO.builder()
                    .liked(true)
                    .likeCount(productLikeRepository.countByProduct(product))
                    .message("이미 찜한 상품입니다.")
                    .build();
        }

        ProductLike productLike = ProductLike.builder()
                .product(product)
                .user(user)
                .build();

        productLikeRepository.save(productLike);

        return ProductLikeResponseDTO.builder()
                .liked(true)
                .likeCount(productLikeRepository.countByProduct(product))
                .message("찜이 등록되었습니다.")
                .build();
    }

    // ✅ 찜 취소
    @Transactional
    public ProductLikeResponseDTO unlikeProduct(Integer productId, String loginUserId) {
        Product product = getProduct(productId);
        User user = getUser(loginUserId);

        boolean alreadyLiked = productLikeRepository.existsByProductAndUser(product, user);

        if (!alreadyLiked) {
            return ProductLikeResponseDTO.builder()
                    .liked(false)
                    .likeCount(productLikeRepository.countByProduct(product))
                    .message("이미 찜이 취소된 상태입니다.")
                    .build();
        }

        productLikeRepository.deleteByProductAndUser(product, user);

        return ProductLikeResponseDTO.builder()
                .liked(false)
                .likeCount(productLikeRepository.countByProduct(product))
                .message("찜이 취소되었습니다.")
                .build();
    }

    // ✅ 찜 상태 조회
    public ProductLikeResponseDTO getLikeStatus(Integer productId, String loginUserId) {
        Product product = getProduct(productId);

        boolean liked = false;

        if (loginUserId != null && !loginUserId.isBlank()) {
            User user = getUser(loginUserId);
            liked = productLikeRepository.existsByProductAndUser(product, user);
        }

        long likeCount = productLikeRepository.countByProduct(product);

        return ProductLikeResponseDTO.builder()
                .liked(liked)
                .likeCount(likeCount)
                .message("찜 상태 조회 성공")
                .build();
    }

    // ✅ 상품 조회 공통 메서드
    private Product getProduct(Integer productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));
    }

    // ✅ 회원 조회 공통 메서드
    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
    }
}