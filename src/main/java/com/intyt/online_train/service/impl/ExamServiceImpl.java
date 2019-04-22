package com.intyt.online_train.service.impl;

import com.intyt.online_train.domain.*;
import com.intyt.online_train.service.ExamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class ExamServiceImpl implements ExamService {
    private ScoresRepository scoresRepository;
    private TopicRepository topicRepository;
    private PaperRepository paperRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    @Autowired
    public void setPaperRepository(PaperRepository paperRepository) {
        this.paperRepository = paperRepository;
    }

    @Override
    @Autowired
    public void setTopicRepository(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    @Override
    @Autowired
    public void setScoresRepository(ScoresRepository scoresRepository) {
        this.scoresRepository = scoresRepository;
    }

    // 通过id获取考试
    @Override
    public Scores get(Long id) {
        return scoresRepository.findFirstById(id);
    }

    // 查询某一套试卷的所有考试记录
    @Override
    public List<Scores> getByPaper(Long paperId) {
        return scoresRepository.findAllByPaperId(paperId);
    }

    // 查询某一个学生的所有考试记录
    @Override
    public List<Scores> getByUser(User user) {
        return scoresRepository.findAllByUser(user);
    }

    // 查询某个学生有没有考过这一场考试
    @Override
    public Scores getByUserAndPaper(User user, Long paperId) {
        return scoresRepository.findFirstByUserAndPaperId(user, paperId);
    }

    // 创建考试
    @Override
    public Scores create(User user, Long paperId) {
        Scores scores = new Scores();
        scores.setUser(user);
        scores.setPaperId(paperId);
        scores.setPaperTitle(paperRepository.findFirstByIdAndIsDeleted(paperId, 0).getTitle());
        scores.setCreateTime(new Date());
        scores.setEndTime(new Date(scores.getCreateTime().getTime() + (3600 * 1000)));
        scores.setExamStatus(1);
        scoresRepository.save(scores);
        return scores;
    }

    // 提交试卷
    @Override
    public Scores update(Scores scores, List<Map> body, Integer Status) {
        if (body.size() == 0) {
            scores.setScore(0F);
            scores.setExamStatus(Status);
            scoresRepository.save(scores);
            return scores;
        }
        Float score = 0F;
        for (Map aTopic : body) {
            if (aTopic.containsKey("id") && aTopic.containsKey("answer")) {
                try {
                    Topic topic = topicRepository.findFirstByIdAndIsDeleted(Long.valueOf(aTopic.get("id").toString()), 0);
                    if (topic != null) {
                        String answer = aTopic.get("answer").toString().replace(",", ""); // 学生答案
                        String answers = topic.getAnswers(); // 正确答案
                        // 如果size相等,依次判断答案
                        if (answer.length() == answers.length()) {
                            String[] answerArr = answer.split("");
                            boolean flag = true;
                            for (String item : answerArr) {
                                if (!answers.contains(item)) {
                                    flag = false;
                                    break;
                                }
                            }
                            if (flag) {
                                score = score + topic.getScore(); // 加分数
                            }
                        }
                    }
                } catch (Exception ex) {
                    logger.error(ex.getLocalizedMessage(), ex);
                }
            }
        }
        scores.setScore(score);
        scores.setExamStatus(Status);
        scoresRepository.save(scores);
        return scores;
    }
}
