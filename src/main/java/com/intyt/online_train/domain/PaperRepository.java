package com.intyt.online_train.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface PaperRepository extends JpaRepository<Paper, Long>, JpaSpecificationExecutor<Paper> {
    Paper findFirstByIdAndIsDeleted(Long id, Integer isDeleted);

    // 查询试卷预览列表
    // SELECT title,create_time FROM paper where create_time >= '2018-01-01' AND create_time <= '2018-08-03' AND title LIKE '%%%' LIMIT 1,1
    @Query(value = "SELECT id,title,create_time FROM paper where create_time >= :beginTime AND create_time <= :endTime AND title LIKE %:keyword% AND is_deleted = 0 LIMIT :skipLines,:pageSize", nativeQuery = true)
    List<Map> findAllByTitleAndCreateTime(@Param("beginTime") Date beginTime,
                                          @Param("endTime") Date endTime,
                                          @Param("keyword") String  keyword,
                                          @Param("skipLines") Integer  skipLines,
                                          @Param("pageSize") Integer  pageSize);

    // 统计总条数
    @Query(value = "SELECT count(*) FROM paper where create_time >= :beginTime AND create_time <= :endTime AND title LIKE %:keyword% AND is_deleted = 0", nativeQuery = true)
    Integer countAllByTitleAndCreateTime(@Param("beginTime") Date beginTime,
                                         @Param("endTime") Date endTime,
                                         @Param("keyword") String  keyword);

    // 查询有没有包含某个题目的试卷
    Paper findFirstByTopicsContainsAndIsDeleted(Topic topic, Integer isDeleted);
}
