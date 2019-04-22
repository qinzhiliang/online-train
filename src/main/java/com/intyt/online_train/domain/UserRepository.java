package com.intyt.online_train.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    List<User> findAllByIsDeleted(Integer isDeleted);

    User findFirstByIdAndIsDeleted(Long id, Integer isDeleted);

    User findFirstByUsernameAndIsDeleted(String username, Integer isDeleted);
}
