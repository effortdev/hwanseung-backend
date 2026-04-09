package com.hwanseung.backend.domain.product.service;

import com.hwanseung.backend.domain.chat.entity.RoomType;
import com.hwanseung.backend.domain.chat.repository.ChatRoomRepository;
import com.hwanseung.backend.domain.product.dto.ProductCreateRequestDTO;
import com.hwanseung.backend.domain.product.dto.ProductDetailResponseDTO;
import com.hwanseung.backend.domain.product.dto.ProductListResponseDTO;
import com.hwanseung.backend.domain.product.dto.ProductUpdateRequestDTO;
import com.hwanseung.backend.domain.product.entity.Product;
import com.hwanseung.backend.domain.product.entity.ProductImage;
import com.hwanseung.backend.domain.product.entity.ProductLike;
import com.hwanseung.backend.domain.product.repository.ProductLikeRepository;
import com.hwanseung.backend.domain.product.repository.ProductRepository;
import com.hwanseung.backend.domain.user.config.CustomUserDetails;
import com.hwanseung.backend.domain.user.entity.User;
import com.hwanseung.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value; // 💡 필수 임포트
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductLikeRepository productLikeRepository;
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;

    // 🌟 [수정 포인트 1] 하드코딩 삭제하고 @Value로 경로 가져오기 (static 제거)
    @Value("${custom.upload-path}")
    private String baseUploadPath;

    // 이미지 최대 5장
    private static final int MAX_IMAGE_COUNT = 5;

    public Integer createProduct(ProductCreateRequestDTO requestDTO, Authentication authentication) throws IOException {

        CustomUserDetails loginUser = (CustomUserDetails) authentication.getPrincipal();

        String sellerId = loginUser.getUsername();   // 아이디
        String sellerNickname = loginUser.getNickname(); // 닉네임

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

        // 이미지 개수 제한
        if (images != null) {
            long validImageCount = images.stream()
                    .filter(image -> image != null && !image.isEmpty())
                    .count();

            if (validImageCount > MAX_IMAGE_COUNT) {
                throw new IllegalArgumentException("상품 이미지는 최대 5장까지 업로드할 수 있습니다.");
            }

            // 여러 장 저장
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

    // 상품 목록 조회
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

    // 메인페이지 인기 매물 조회
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

    // 주변 매물
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

    // 상품 수정 이미지 수정 포함
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

        // 1) 기존 이미지 삭제
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

        // 2) 새 이미지 추가
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

    // 상품 삭제 (soft delete)
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

    // 실제 파일 삭제
    private void deleteStoredFile(String storedName) {
        if (storedName == null || storedName.isBlank()) return;

        // 🌟 [수정 포인트 2] 삭제할 때 baseUploadPath + "product/" 로 동적 경로 설정
        String uploadDir = baseUploadPath + "product/";
        File file = new File(uploadDir, storedName);

        if (file.exists()) {
            file.delete();
        }
    }

    // 파일 저장 + ProductImage 엔티티 생성
    private ProductImage saveProductImage(MultipartFile image) throws IOException {

        // 🌟 [수정 포인트 3] 저장할 때 baseUploadPath + "product/" 로 동적 경로 설정
        String uploadDir = baseUploadPath + "product/";
        File dir = new File(uploadDir);

        // 폴더 없으면 생성
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String originalName = image.getOriginalFilename();
        String storedName = UUID.randomUUID() + "_" + originalName;

        File dest = new File(dir, storedName);
        image.transferTo(dest);

        // 브라우저 접근용 경로
        String imagePath = "/api/imgs/product/" + storedName;

        return ProductImage.builder()
                .originalName(originalName)
                .storedName(storedName)
                .imagePath(imagePath)
                .build();
    }

    // 상품 상세 조회
    @Transactional(readOnly = true)
    public ProductDetailResponseDTO getProductDetail(Integer productId) {
        Product product = productRepository.findByProductIdAndDeletedAtIsNull(productId)
                .orElseThrow(() -> new RuntimeException("상품이 없거나 삭제된 상품입니다."));

        return ProductDetailResponseDTO.from(product);
    }

    // 판매완료 처리
    public void markProductAsSoldOut(Integer productId, Authentication authentication) {
        CustomUserDetails loginUser = (CustomUserDetails) authentication.getPrincipal();
        String loginUserId = loginUser.getUsername();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("상품 없음"));

        if (product.getDeletedAt() != null) {
            throw new RuntimeException("삭제된 상품입니다.");
        }

        if (!product.getSellerId().equals(loginUserId)) {
            throw new RuntimeException("판매완료 처리 권한이 없습니다.");
        }

        product.markAsSoldOut();
    }

    // 상품 총 갯수
    @Transactional(readOnly = true)
    public long getTotalProductCount() {
        return productRepository.count();
    }

    // 판매 중인 상품 갯수
    @Transactional(readOnly = true)
    public long getActiveProductCount() {
        return productRepository.countByDeletedAtIsNull();
    }

    // 내 판매 내역 조회
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

    // 내 관심목록 조회
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
}