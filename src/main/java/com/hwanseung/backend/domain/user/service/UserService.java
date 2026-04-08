package com.hwanseung.backend.domain.user.service;

// 삭제: import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional; // 이걸로 교체
import com.hwanseung.backend.domain.user.entity.User;
import com.hwanseung.backend.domain.user.dto.UserRequestDTO;
import com.hwanseung.backend.domain.user.dto.UserResponseDTO;
import com.hwanseung.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${custom.upload-path}")
    private String uploadPath;

    /** User 조회 */
    @Transactional
    public UserResponseDTO findById(Long id) {
        User user = this.userRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다. user_id = " + id));
        return new UserResponseDTO(user);
    }

    /** User 수정 (🌟 마법의 더티 체킹 적용) */
    @Transactional
    public void update(Long id, UserRequestDTO requestDto, MultipartFile profileImage) throws IOException {
        // 1. DB에서 수정할 유저를 찾아옵니다.
        User user = this.userRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다. user_id = " + id));

        // 🌟 새 이미지가 들어왔을 때만 실행
        if (profileImage != null && !profileImage.isEmpty()) {

            // [추가] 기존 이미지가 있다면 실제 파일 삭제
            String oldImagePath = user.getProfileImagePath(); // DB에 저장된 경로: /api/imgs/profile/uuid_name.jpg
            if (oldImagePath != null && !oldImagePath.isEmpty()) {
                // 웹 경로(/api/imgs/)를 실제 파일 경로(uploadPath)로 변환
                // 예: /api/imgs/profile/abc.jpg -> profile/abc.jpg
                String relativePath = oldImagePath.replace("/api/imgs/", "");
                File oldFile = new File(uploadPath, relativePath);

                if (oldFile.exists()) {
                    if (oldFile.delete()) {
                        System.out.println("기존 프로필 파일 삭제 성공: " + oldFile.getPath());
                    } else {
                        System.out.println("기존 프로필 파일 삭제 실패: " + oldFile.getPath());
                    }
                }
            }

            // 2. 새 파일 저장 로직
            String originalFileName = profileImage.getOriginalFilename();
            String uuid = UUID.randomUUID().toString();
            String savedFileName = uuid + "_" + originalFileName;

            File profileDir = new File(uploadPath, "profile");
            if (!profileDir.exists()) {
                profileDir.mkdirs();
            }

            File saveFile = new File(profileDir, savedFileName);
            profileImage.transferTo(saveFile);

            // DB 정보 업데이트
            user.setProfileImagePath("/api/imgs/profile/" + savedFileName);
            user.setProfileOriginalName(originalFileName);
        }

        // 2. 찾아온 유저의 정보를 프론트에서 받아온 DTO 정보로 덮어씌웁니다.
        // (@Transactional 덕분에 메서드가 끝날 때 스프링이 알아서 DB에 UPDATE 쿼리를 쏩니다!)
        user.setNickname(requestDto.getNickname());
        user.setEmail(requestDto.getEmail());
        user.setContact(requestDto.getContact());
        user.setAddress(requestDto.getAddress());
        user.setDetailAddress(requestDto.getDetailAddress());
        user.setZipCode(requestDto.getZipCode());
        user.setGender(requestDto.getGender());
        user.setBirthday(requestDto.getBirthday());
        if (requestDto.getPassword() != null && !requestDto.getPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        }
        if (requestDto.getNeighborhood() != null) {
            user.setNeighborhood(requestDto.getNeighborhood());
            user.setNeighborhoodAuthenticated(true); // 동네가 들어오면 인증 완료로 처리!
        }

    }
    /** User 삭제 */
    @Transactional
    public void delete(Long id) {
        User user = this.userRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다. user_id = " + id));
        this.userRepository.delete(user);
    }

    /* User테이블 총 사용자 수 */
    @Transactional(readOnly = true)
    public long getTotalUserCount() {
        return userRepository.count();
    }

    public boolean isUseridDuplicate(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean isNicknameDuplicate(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    public boolean isEmailDuplicate(String email) {
        return userRepository.existsByEmail(email);

    }public boolean isContactDuplicate(String contact) {
        return userRepository.existsByContact(contact);
    }
}