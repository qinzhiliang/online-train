package com.intyt.online_train.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface ScoresRepository extends JpaRepository<Scores, Long> {
    Scores findFirstByUserAndPaperId(User user, Long paperId);

    Scores findFirstById(Long id);

    Scores findFirstByUser(User user);

    List<Scores> findAllByUser(User user);

    Scores findFirstByPaperId(Long paperId);

    List<Scores> findAllByPaperId(Long paperId);

    List<Scores> findAllByExamStatusAndEndTimeBefore(Integer examStatus, Date endTime);
}
