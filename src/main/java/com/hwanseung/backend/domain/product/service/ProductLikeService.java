package com.hwanseung.backend.domain.product.service;

import com.hwanseung.backend.domain.notification.entity.Notification;
import com.hwanseung.backend.domain.notification.repository.NotificationRepository;
import com.hwanseung.backend.domain.product.dto.ProductLikeResponseDTO;
import com.hwanseung.backend.domain.product.entity.Product;
import com.hwanseung.backend.domain.product.entity.ProductLike;
import com.hwanseung.backend.domain.product.repository.ProductLikeRepository;
import com.hwanseung.backend.domain.product.repository.ProductRepository;
import com.hwanseung.backend.domain.user.entity.User;
import com.hwanseung.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductLikeService {

    private final ProductRepository productRepository;
    private final ProductLikeRepository productLikeRepository;
    private final UserRepository userRepository;

    // 🚀 [알림 기능 추가 2] 실시간 통신 템플릿과 알림 저장소 주입
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationRepository notificationRepository;

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

        // ========================================================
        // 🚀 [알림 기능 추가 3] 찜이 성공적으로 저장된 직후에 알림을 쏩니다!
        // ========================================================
        try {
            // (참고: 상품 엔티티에 상품명을 가져오는 getTitle()이나 getItemName() 메서드가 있다고 가정합니다. 맞게 수정해 주세요!)
            String productName = product.getTitle(); // 상품명 가져오기
            String message = loginUserId + "님이 [" + productName + "] 상품을 찜했습니다! ❤️";

            // A. DB에 알림 저장
            Notification notification = Notification.builder()
                    .receiverId(product.getSellerId()) // 판매자에게 알림 전송
                    .content(message)
                    .type("FAVORITE") // 알림 타입 지정
                    .relatedItemId(productId.longValue()) // 정수형 ID를 Long으로 변환 (엔티티 타입에 맞춰)
                    .build();

            Notification savedNotification = notificationRepository.save(notification);

            // B. 접속해 있는 판매자에게 실시간 STOMP 메시지 발사!
            messagingTemplate.convertAndSend(
                    "/sub/user/" + product.getSellerId() + "/notification",
                    savedNotification
            );
        } catch (Exception e) {
            // 💡 꿀팁: 알림 전송 로직에서 에러가 나더라도, '찜하기' 핵심 기능 자체가
            // 롤백(취소)되지 않도록 try-catch로 부드럽게 감싸주는 것이 실무의 정석입니다!
            System.err.println("알림 전송 중 에러 발생 (찜하기는 정상 처리됨): " + e.getMessage());
        }
        // ========================================================


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