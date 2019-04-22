package com.intyt.online_train.service;

import com.intyt.online_train.domain.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/8/22 0022.
 */
public interface ExamService {
    @Autowired
    void setPaperRepository(PaperRepository paperRepository);

    @Autowired
    void setTopicRepository(TopicRepository topicRepository);

    @Autowired
    void setScoresRepository(ScoresRepository scoresRepository);

    // 通过id获取考试
    Scores get(Long id);

    // 查询某一套试卷的所有考试记录
    List<Scores> getByPaper(Long paperId);

    // 查询某一个学生的所有考试记录
    List<Scores> getByUser(User user);

    // 查询某个学生有没有考过这一场考试
    Scores getByUserAndPaper(User user, Long paperId);

    // 创建考试
    Scores create(User user, Long paperId);

    // 提交试卷
    Scores update(Scores scores, List<Map> body, Integer Status);
}
