package com.hwanseung.backend.domain.product.service;

import com.hwanseung.backend.domain.chat.entity.ChatRoom;
import com.hwanseung.backend.domain.chat.entity.RoomType;
import com.hwanseung.backend.domain.chat.repository.ChatRoomRepository;
import com.hwanseung.backend.domain.product.dto.*;
import com.hwanseung.backend.domain.product.entity.Product;
import com.hwanseung.backend.domain.product.entity.ProductImage;
import com.hwanseung.backend.domain.product.entity.ProductLike;
import com.hwanseung.backend.domain.product.repository.ProductLikeRepository;
import com.hwanseung.backend.domain.product.repository.ProductRepository;
import com.hwanseung.backend.domain.user.config.CustomUserDetails;
import com.hwanseung.backend.domain.user.controller.PayBalanceRepository;
import com.hwanseung.backend.domain.user.controller.PayHistoryRepository;
import com.hwanseung.backend.domain.user.dto.PayBalance;
import com.hwanseung.backend.domain.user.dto.PayHistory;
import com.hwanseung.backend.domain.user.entity.User;
import com.hwanseung.backend.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import com.hwanseung.backend.domain.user.service.TrustScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductLikeRepository productLikeRepository;
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;


    private final TrustScoreService trustScoreService;


    @Value("${custom.upload-path}")
    private String baseUploadPath;

    private static final int MAX_IMAGE_COUNT = 5;

    public Integer createProduct(ProductCreateRequestDTO requestDTO, Authentication authentication) throws IOException {

        CustomUserDetails loginUser = (CustomUserDetails) authentication.getPrincipal();

        String sellerId = loginUser.getUsername();
        String sellerNickname = loginUser.getNickname();

        Product product = Product.builder()
                .title(requestDTO.getTitle())
                .category(requestDTO.getCategory())
                .price(requestDTO.getPrice())
                .location(requestDTO.getLocation())
                .content(requestDTO.getContent())
                .sellerId(sellerId)
                .sellerNickname(sellerNickname)
                .saleStatus("SALE")
                .build();

        List<MultipartFile> images = requestDTO.getImages();

        if (images != null) {
            long validImageCount = images.stream()
                    .filter(image -> image != null && !image.isEmpty())
                    .count();

            if (validImageCount > MAX_IMAGE_COUNT) {
                throw new IllegalArgumentException("상품 이미지는 최대 5장까지 업로드할 수 있습니다.");
            }

            for (MultipartFile image : images) {
                if (image != null && !image.isEmpty()) {
                    ProductImage productImage = saveProductImage(image);
                    product.addProductImage(productImage);
                }
            }
        }

        Product savedProduct = productRepository.save(product);

        return savedProduct.getProductId();
    }

    @Transactional(readOnly = true)
    public List<ProductListResponseDTO> getProductList(String loginUserId) {
        List<Product> products = productRepository.findAllVisibleOrderBySaleStatusAndCreatedAtDesc();

        User loginUser = null;
        if (loginUserId != null && !loginUserId.isBlank()) {
            loginUser = userRepository.findByUsername(loginUserId).orElse(null);
        }

        User finalLoginUser = loginUser;

        return products.stream()
                .map(product -> {
                    long likeCount = productLikeRepository.countByProduct(product);
                    long chatCount = chatRoomRepository.countByItemIdAndRoomType(
                            product.getProductId().longValue(),
                            RoomType.TRADE
                    );

                    boolean liked = false;
                    if (finalLoginUser != null) {
                        liked = productLikeRepository.existsByProductAndUser(product, finalLoginUser);
                    }

                    return ProductListResponseDTO.from(product, likeCount, chatCount, liked);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductListResponseDTO> getPopularProducts(String loginUserId) {
        List<Product> products = productRepository.findAllVisibleSaleProductsOrderByCreatedAtDesc();

        User loginUser = null;
        if (loginUserId != null && !loginUserId.isBlank()) {
            loginUser = userRepository.findByUsername(loginUserId).orElse(null);
        }

        User finalLoginUser = loginUser;

        return products.stream()
                .map(product -> {
                    long likeCount = productLikeRepository.countByProduct(product);
                    long chatCount = chatRoomRepository.countByItemIdAndRoomType(
                            product.getProductId().longValue(),
                            RoomType.TRADE
                    );

                    boolean liked = false;
                    if (finalLoginUser != null) {
                        liked = productLikeRepository.existsByProductAndUser(product, finalLoginUser);
                    }

                    return ProductListResponseDTO.from(product, likeCount, chatCount, liked);
                })
                .sorted(
                        Comparator.comparingLong(ProductListResponseDTO::getLikeCount).reversed()
                                .thenComparing(ProductListResponseDTO::getProductId, Comparator.reverseOrder())
                )
                .limit(8)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductListResponseDTO> getNearbyProducts(double lat, double lng, double radius) {
        List<Product> nearbyProducts = productRepository.findNearbyProducts(lat, lng, radius);

        return nearbyProducts.stream()
                .map(product -> {
                    long likeCount = productLikeRepository.countByProduct(product);
                    long chatCount = chatRoomRepository.countByItemIdAndRoomType(
                            product.getProductId().longValue(),
                            RoomType.TRADE
                    );

                    boolean liked = false;
                    return ProductListResponseDTO.from(product, likeCount, chatCount, liked);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductChatBuyerResponseDTO> getChatBuyers(Integer productId, Authentication authentication) {
        CustomUserDetails loginUser = (CustomUserDetails) authentication.getPrincipal();
        String loginUserId = loginUser.getUsername();

        Product product = productRepository.findByProductIdAndDeletedAtIsNull(productId)
                .orElseThrow(() -> new RuntimeException("상품이 없거나 삭제된 상품입니다."));

        if (!product.getSellerId().equals(loginUserId)) {
            throw new RuntimeException("채팅 구매자 목록 조회 권한이 없습니다.");
        }

        List<ChatRoom> chatRooms = chatRoomRepository.findByItemIdAndSellerId(productId.longValue(), loginUserId);

        if (chatRooms == null || chatRooms.isEmpty()) {
            return List.of();
        }

        List<String> buyerUsernames = chatRooms.stream()
                .map(ChatRoom::getBuyerId)
                .filter(username -> username != null && !username.isBlank())
                .distinct()
                .toList();

        if (buyerUsernames.isEmpty()) {
            return List.of();
        }

        List<User> users = userRepository.findByUsernameIn(buyerUsernames);

        return buyerUsernames.stream()
                .map(username -> {
                    String nickname = users.stream()
                            .filter(user -> username.equals(user.getUsername()))
                            .map(User::getNickname)
                            .findFirst()
                            .orElse(username);

                    return new ProductChatBuyerResponseDTO(username, nickname);
                })
                .toList();
    }

    public void updateProduct(Integer productId, ProductUpdateRequestDTO requestDTO, Authentication authentication) throws IOException {
        CustomUserDetails loginUser = (CustomUserDetails) authentication.getPrincipal();
        String loginUserId = loginUser.getUsername();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("상품 없음"));

        if (product.isDeleted()) {
            throw new RuntimeException("삭제된 상품은 수정할 수 없습니다.");
        }

        if (!product.getSellerId().equals(loginUserId)) {
            throw new RuntimeException("수정 권한이 없습니다.");
        }

        product.updateProduct(
                requestDTO.getTitle(),
                requestDTO.getCategory(),
                requestDTO.getPrice(),
                requestDTO.getContent(),
                requestDTO.getLocation()
        );

        List<Integer> deleteImageIds = requestDTO.getDeleteImageIds();
        if (deleteImageIds != null && !deleteImageIds.isEmpty()) {
            List<ProductImage> removeTargets = product.getProductImages().stream()
                    .filter(image -> deleteImageIds.contains(image.getProductImageId()))
                    .toList();

            for (ProductImage image : removeTargets) {
                deleteStoredFile(image.getStoredName());
                product.removeProductImage(image);
            }
        }

        List<MultipartFile> newImages = requestDTO.getNewImages();
        long validNewImageCount = 0;

        if (newImages != null) {
            validNewImageCount = newImages.stream()
                    .filter(image -> image != null && !image.isEmpty())
                    .count();
        }

        long finalImageCount = product.getProductImages().size() + validNewImageCount;
        if (finalImageCount > MAX_IMAGE_COUNT) {
            throw new IllegalArgumentException("상품 이미지는 최대 5장까지 업로드할 수 있습니다.");
        }

        if (newImages != null) {
            for (MultipartFile image : newImages) {
                if (image != null && !image.isEmpty()) {
                    ProductImage productImage = saveProductImage(image);
                    product.addProductImage(productImage);
                }
            }
        }
    }

    public void deleteProduct(Integer productId, Authentication authentication) {
        CustomUserDetails loginUser = (CustomUserDetails) authentication.getPrincipal();
        String loginUserId = loginUser.getUsername();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("상품 없음"));

        if (product.isDeleted()) {
            throw new RuntimeException("이미 삭제된 상품입니다.");
        }

        if (!product.getSellerId().equals(loginUserId)) {
            throw new RuntimeException("삭제 권한이 없습니다.");
        }

        product.deleteProduct();
    }

    private void deleteStoredFile(String storedName) {
        if (storedName == null || storedName.isBlank()) return;

        String uploadDir = baseUploadPath + "product/";
        File file = new File(uploadDir, storedName);

        if (file.exists()) {
            file.delete();
        }
    }

    private ProductImage saveProductImage(MultipartFile image) throws IOException {

        String uploadDir = baseUploadPath + "product/";
        File dir = new File(uploadDir);

        if (!dir.exists()) {
            dir.mkdirs();
        }

        String originalName = image.getOriginalFilename();
        String storedName = UUID.randomUUID() + "_" + originalName;

        File dest = new File(dir, storedName);
        image.transferTo(dest);

        String imagePath = "/api/imgs/product/" + storedName;

        return ProductImage.builder()
                .originalName(originalName)
                .storedName(storedName)
                .imagePath(imagePath)
                .build();
    }

    public ProductDetailResponseDTO getProductDetail(Integer productId) {
        Product product = productRepository.findByProductIdAndDeletedAtIsNull(productId)
                .orElseThrow(() -> new RuntimeException("상품이 없거나 삭제된 상품입니다."));

        product.increaseViewCount();

        return ProductDetailResponseDTO.from(product);
    }

    public void markProductAsSoldOut(Integer productId,
                                     ProductBuyerRequestDTO requestDTO,
                                     Authentication authentication) {
        CustomUserDetails loginUser = (CustomUserDetails) authentication.getPrincipal();
        String loginUserId = loginUser.getUsername();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("상품이 없습니다."));

        if (product.getDeletedAt() != null) {
            throw new RuntimeException("삭제된 상품입니다.");
        }

        if (!product.getSellerId().equals(loginUserId)) {
            throw new RuntimeException("판매완료 처리 권한이 없습니다.");
        }

        String requestBuyerUsername = (requestDTO != null && requestDTO.getBuyerUsername() != null)
                ? requestDTO.getBuyerUsername().trim()
                : null;

        String finalBuyerUsername =
                (requestBuyerUsername != null && !requestBuyerUsername.isBlank())
                        ? requestBuyerUsername
                        : product.getBuyerUsername();

        if (finalBuyerUsername == null || finalBuyerUsername.isBlank()) {
            throw new RuntimeException("판매완료할 구매자 정보가 없습니다.");
        }

        boolean exists = chatRoomRepository
                .findByRoomTypeAndItemIdAndBuyerIdAndSellerId(
                        RoomType.TRADE,
                        productId.longValue(),
                        finalBuyerUsername,
                        product.getSellerId()
                )
                .isPresent();

        if (!exists) {
            throw new RuntimeException("해당 사용자는 이 상품에 채팅한 사용자가 아닙니다.");
        }

        product.markAsSoldOut(finalBuyerUsername);

        // 👇 [추가된 로직] 판매 완료 시 판매자에게 신뢰도 점수 부여 (+10점 예시)
        User seller = userRepository.findByUsername(loginUserId)
                .orElseThrow(() -> new RuntimeException("판매자 정보를 찾을 수 없습니다."));
        trustScoreService.updateTrustScore(seller.getId(), 10, "상품 판매 완료");
    }

    public void markProductAsReserved(Integer productId,
                                      ProductBuyerRequestDTO requestDTO,
                                      Authentication authentication) {
        CustomUserDetails loginUser = (CustomUserDetails) authentication.getPrincipal();
        String loginUserId = loginUser.getUsername();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("상품이 없습니다."));

        if (product.getDeletedAt() != null) {
            throw new RuntimeException("삭제된 상품입니다.");
        }

        if (!product.getSellerId().equals(loginUserId)) {
            throw new RuntimeException("예약중 처리 권한이 없습니다.");
        }

        if (product.isSoldOut()) {
            throw new RuntimeException("판매완료된 상품은 예약중으로 변경할 수 없습니다.");
        }

        String buyerUsername = requestDTO.getBuyerUsername() == null
                ? null
                : requestDTO.getBuyerUsername().trim();

        if (buyerUsername == null || buyerUsername.isBlank()) {
            throw new RuntimeException("예약자를 선택해주세요.");
        }

        if (buyerUsername.equals(product.getSellerId())) {
            throw new RuntimeException("판매자 본인은 예약자로 선택할 수 없습니다.");
        }

        boolean exists = chatRoomRepository
                .findByRoomTypeAndItemIdAndBuyerIdAndSellerId(
                        RoomType.TRADE,
                        productId.longValue(),
                        buyerUsername,
                        product.getSellerId()
                )
                .isPresent();

        if (!exists) {
            throw new RuntimeException("해당 사용자는 이 상품에 채팅한 사용자가 아닙니다.");
        }

        product.markAsReserved(buyerUsername);

        User seller = userRepository.findByUsername(loginUserId)
                .orElseThrow(() -> new RuntimeException("판매자 정보를 찾을 수 없습니다."));
        trustScoreService.updateTrustScore(seller.getId(), 10, "상품 판매 완료");
    }

    public void markProductAsSale(Integer productId, Authentication authentication) {
        CustomUserDetails loginUser = (CustomUserDetails) authentication.getPrincipal();
        String loginUserId = loginUser.getUsername();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("상품이 없습니다."));

        if (product.getDeletedAt() != null) {
            throw new RuntimeException("삭제된 상품입니다.");
        }

        if (!product.getSellerId().equals(loginUserId)) {
            throw new RuntimeException("예약해제 권한이 없습니다.");
        }

        if (product.isSoldOut()) {
            throw new RuntimeException("판매완료된 상품은 판매중으로 변경할 수 없습니다.");
        }

        product.markAsSale();
    }

    @Transactional(readOnly = true)
    public long getTotalProductCount() {
        return productRepository.count();
    }

    @Transactional(readOnly = true)
    public long getActiveProductCount() {
        return productRepository.countByDeletedAtIsNull();
    }

    @Transactional(readOnly = true)
    public List<ProductListResponseDTO> getMySalesList(String sellerId) {
        List<Product> myProducts = productRepository.findBySellerIdAndDeletedAtIsNullOrderByCreatedAtDesc(sellerId);
        User seller = userRepository.findByUsername(sellerId).orElse(null);

        return myProducts.stream()
                .map(product -> {
                    long likeCount = productLikeRepository.countByProduct(product);
                    boolean liked = false;
                    long chatCount = chatRoomRepository.countByItemIdAndRoomType(
                            product.getProductId().longValue(),
                            RoomType.TRADE
                    );
                    if (seller != null) {
                        liked = productLikeRepository.existsByProductAndUser(product, seller);
                    }
                    return ProductListResponseDTO.from(product, likeCount, chatCount, liked);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductListResponseDTO> getWishlist(String username) {
        List<ProductLike> myLikes = productLikeRepository.findByUser_Username(username);

        return myLikes.stream()
                .map(like -> {
                    Product product = like.getProduct();
                    long likeCount = productLikeRepository.countByProduct(product);
                    long chatCount = chatRoomRepository.countByItemIdAndRoomType(
                            product.getProductId().longValue(),
                            RoomType.TRADE
                    );
                    return ProductListResponseDTO.from(product, likeCount, chatCount, true);
                })
                .toList();
    }


    private final PayHistoryRepository payHistoryRepository;
    private final PayBalanceRepository payBalanceRepository;

    @PersistenceContext
    private EntityManager em;

    @Transactional(readOnly = true)
    public List<ProductListResponseDTO> getMyPaymentsList(String buyerId) {
        List<Product> myProducts = productRepository.findByBuyerUsernameOrderByPayStatusAscCreatedAtDesc(buyerId);
        User buyer = userRepository.findByUsername(buyerId).orElse(null);

        return myProducts.stream()
                .map(product -> {
                    long likeCount = productLikeRepository.countByProduct(product);
                    boolean liked = false;
                    long chatCount = chatRoomRepository.countByItemIdAndRoomType(
                            product.getProductId().longValue(),
                            RoomType.TRADE
                    );
                    if (buyer != null) {
                        liked = productLikeRepository.existsByProductAndUser(product, buyer);
                    }
                    return ProductListResponseDTO.from(product, likeCount, chatCount, liked);
                })
                .toList();
    }

    @Transactional
    public String productAsPayment(Integer productId, Authentication authentication) {
        String message = "포인트 잔액이 부족하여 결제가 되지 않았습니다. \n포인트 충전을 해 주세요.";

        CustomUserDetails loginUser = (CustomUserDetails) authentication.getPrincipal();
        String loginUserId = loginUser.getUsername();
        System.out.println("ProductService 1111 username :::  " + loginUserId);
        Optional<PayBalance> optionalPayBalance = payBalanceRepository.findByUsername(loginUserId);
        PayBalance payBalance = optionalPayBalance.get();
        System.out.println("ProductService 2222 payBalance ::: " + payBalance);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("상품 없음"));
        System.out.println("ProductService 333 product ::: " + product);
        try {
            if (product.getBuyerUsername().equals(loginUserId) && product.getPrice() <= payBalance.getHwanseungPay()) {
                System.out.println("ProductService 4444 포인트 조정 진입");
                // 구매자 포인트 조정
                PayHistory history = PayHistory.builder().userId(payBalance.getUserId()).amount(-product.getPrice()).type("PAYMENT").username(loginUserId).build();
                em.persist(history);
                payBalance.setHwanseungPay(payHistoryRepository.sumAmountByUsername(loginUserId));
                em.persist(payBalance);

                optionalPayBalance = payBalanceRepository.findByUsername(product.getSellerId());
                payBalance = optionalPayBalance.get();

                history = PayHistory.builder().userId(payBalance.getUserId()).amount(product.getPrice()).type("HARP").username(product.getSellerId()).build();
                em.persist(history);
                payBalance.setHwanseungPay(payHistoryRepository.sumAmountByUsername(product.getSellerId()));
                em.persist(payBalance);

                product.setPayStatus(true);
                System.out.println("ProductService 5555 포인트 조정 진입완료 전 product: " + product);
                productRepository.save(product);
                message = "결제가 완료되었습니다.";
            }

        } catch (Exception e) {
            System.out.println("결제 에러");
            e.printStackTrace();
        }
        System.out.println("ProductService 5555 포인트 조정 완료 또는 미진입");
        return message;
    }
}