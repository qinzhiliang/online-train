package com.intyt.online_train.config;

import com.intyt.online_train.domain.Scores;
import com.intyt.online_train.domain.ScoresRepository;
import com.intyt.online_train.service.impl.ExamServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class ScanExam {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private ScoresRepository scoresRepository;
    private RedisTemplate redisTemplate;
    private ExamServiceImpl examService;

    @Autowired
    public void setExamService(ExamServiceImpl examService) {
        this.examService = examService;
    }

    @Autowired
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Autowired
    public void setScoresRepository(ScoresRepository scoresRepository) {
        this.scoresRepository = scoresRepository;
    }

    @Scheduled(fixedDelay = 120 * 1000)
    public void ScanExams() {
        try {
            Date nowTime = new Date(System.currentTimeMillis() + 120000); // 增加两分钟防止前端强制提交的时候提交失败
            List<Scores> scoresList = scoresRepository.findAllByExamStatusAndEndTimeBefore(1, nowTime);
            if (scoresList.size() > 0) {
                for (Scores item : scoresList) {
                    List<Map> body = (List<Map>) redisTemplate.opsForValue().get(item.getId());
                    if (body != null) {
                        examService.update(item, body, 2);
                    } else {
                        item.setExamStatus(2);
                        scoresRepository.save(item);
                    }
                }
            }
            System.out.println("扫描考试完成:" + scoresList.size() + "条记录更新");
        } catch (Exception ex) {
            logger.error(ex.getLocalizedMessage(), ex);
        }
    }
}