package com.intyt.online_train.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TopicRepository extends JpaRepository<Topic, Long>, JpaSpecificationExecutor<Topic> {
    Topic findFirstByIdAndIsDeleted(Long id, Integer isDeleted);

    Integer countAllByTopicTypeAndIsDeleted(Integer topicType, Integer isDeleted);

    // SELECT * FROM topic ORDER BY rand() LIMIT 60;
    @Query(value = "SELECT * FROM topic WHERE topic_type=:topicType AND is_deleted=0 ORDER BY rand() LIMIT :numbers", nativeQuery = true)
    List<Topic> findAllOrderByRandLimit(@Param("topicType") Integer topicType, @Param("numbers") Integer numbers);
}
