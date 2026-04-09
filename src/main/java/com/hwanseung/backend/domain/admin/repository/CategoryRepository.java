package com.hwanseung.backend.domain.admin.repository;

import com.hwanseung.backend.domain.admin.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByCategoryKey(String categoryKey);

    boolean existsByCategoryKey(String categoryKey);

    List<Category> findAllByOrderBySortOrderAsc();
}
