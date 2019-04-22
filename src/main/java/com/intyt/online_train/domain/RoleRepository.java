package com.intyt.online_train.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findFirstById(Long id);

    Role findFirstByName(String name);
}
