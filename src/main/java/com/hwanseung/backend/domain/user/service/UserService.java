package com.hwanseung.backend.domain.user.service;

// 삭제: import jakarta.transaction.Transactional;
import com.hwanseung.backend.domain.user.config.CustomUserDetails;
import com.hwanseung.backend.domain.user.config.JwtTokenProvider;
import com.hwanseung.backend.domain.user.entity.Auth;
import com.hwanseung.backend.domain.user.repository.AuthRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
import java.util.Collections;
import java.util.UUID;
import com.hwanseung.backend.domain.admin.dto.Status;
import com.hwanseung.backend.domain.user.entity.Role;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AuthRepository authRepository;
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
    /** User 탈퇴 */
    @Transactional
    public void withdraw(Long userId, String Password) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        // 비밀번호 확인 로직 (PasswordEncoder 사용)
        if (!passwordEncoder.matches(Password, user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        // 상태 변경
        authRepository.deleteByUserId(userId);
        user.withdraw();

        // 메서드가 끝날 때 @Transactional에 의해 DB에 UPDATE 쿼리가 날아갑니다.
        // 이때 status는 'SECESSION'으로, updated_at은 현재 시간으로 업데이트됩니다!
    }

    @Transactional
    public User completeSocialSignup(String username, String contact) {
        // 1. 유저 정보 업데이트
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        user.setContact(contact);
        user.setStatus(Status.ACTIVE);
        userRepository.save(user);

        // 2. 새 토큰 생성 (컨트롤러에서 하던 걸 서비스로 가져오거나, 여기서 토큰을 미리 만듦)
        // 이 메서드 안에서 토큰 업데이트 로직을 직접 넣는 것이 좋습니다.
        return user;
    }

    @Transactional
    public void updateAuthToken(User user, String newAccessToken) {
        // 1. 유저의 고유 ID(Long)를 사용하여 인증 정보 조회
        Auth auth = authRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("인증 정보를 찾을 수 없습니다."));

        // 2. 새 토큰으로 교체
        auth.setAccessToken(newAccessToken);

        // Dirty Checking으로 자동 저장되지만 명시적으로 호출 가능
        authRepository.save(auth);
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