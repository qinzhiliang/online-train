package com.intyt.online_train.controller;

import com.intyt.online_train.domain.Paper;
import com.intyt.online_train.domain.Scores;
import com.intyt.online_train.domain.ScoresRepository;
import com.intyt.online_train.domain.Topic;
import com.intyt.online_train.service.impl.PaperServiceImpl;
import com.intyt.online_train.service.impl.TopicServiceImpl;
import com.intyt.online_train.utility.ResultData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = {"http://10.18.43.3:8000", "http://127.0.0.1:8000"}, allowCredentials = "true", maxAge = 3600)
@RestController
@RequestMapping(value = "/api/papers")
public class PaperController {
    private PaperServiceImpl paperService;
    private TopicServiceImpl topicService;
    private ScoresRepository scoresRepository;

    @Autowired
    public void setScoresRepository(ScoresRepository scoresRepository) {
        this.scoresRepository = scoresRepository;
    }

    @Autowired
    public void setTopicService(TopicServiceImpl topicService) {
        this.topicService = topicService;
    }

    @Autowired
    public void setPaperService(PaperServiceImpl paperService) {
        this.paperService = paperService;
    }

    // 通过id获取试卷
    @RequestMapping(value = "/{id}")
    public ResponseEntity get(@PathVariable(value = "id") Long id) {
        Paper paper = paperService.get(id);
        if (paper == null) {
            return ResultData.error("没有这套试卷或已被删除");
        }
        List<Topic> list=new ArrayList<>();
        for (Topic topic:paper.getTopics()){
            char[] answers = topic.getAnswers().toCharArray();
            StringBuffer sb=new StringBuffer();
            for(char c:answers){
                sb.append((char)(c+16));
            }
            topic.setAnswers(sb.toString());
            list.add(topic);
        }
        paper.setTopics(list);
        return ResultData.success(paper);
    }

    // 获取试卷列表
    @RequestMapping(value = "")
    public ResponseEntity list(@RequestParam(defaultValue = "") String beginDate,
                               @RequestParam(defaultValue = "") String endDate,
                               @RequestParam(defaultValue = "") String keyword,
                               @RequestParam(defaultValue = "1") Integer page,
                               @RequestParam(defaultValue = "10") Integer pageSize) {
        Date beginTime = null;
        Date endTime = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (!beginDate.isEmpty()) {
            try {
                beginTime = simpleDateFormat.parse(beginDate);
            } catch (Exception ex) {
                return ResultData.error("开始时间错误");
            }
        }
        if (!endDate.isEmpty()) {
            try {
                endTime = simpleDateFormat.parse(endDate);
            } catch (Exception ex) {
                return ResultData.error("结束时间错误");
            }
        }
        try {
            Map papers = paperService.list(beginTime, endTime, keyword, page, pageSize);
            return ResultData.success(papers);
        } catch (Exception ex) {
            return ResultData.error(ex.getMessage());
        }
    }

    // 生成试卷
    @RequestMapping(value = "", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity getTopics(@RequestParam(value = "title", defaultValue = "") String title,
                                    @RequestParam(value = "number1", defaultValue = "0") Integer number1,
                                    @RequestParam(value = "number2", defaultValue = "0") Integer number2,
                                    @RequestParam(value = "number3", defaultValue = "0") Integer number3) {
        if (title.isEmpty()) {
            return ResultData.error("请输入试卷标题");
        }
        if (number1 < 0 || number2 < 0 || number3 < 0) {
            return ResultData.error("请输入正确的参数");
        }
        if (number1 == 0 && number2 == 0 && number3 == 0) {
            return ResultData.error("请至少选择一种题型");
        }
        if (number1 + number2 * 2 + number3 * 0.5 != 100) {
            return ResultData.error("请保持总分为100分");
        }
        Integer count1 = topicService.countTopicByType(1);
        if (count1 < number1) {
            return ResultData.error("当前单选题目只有" + count1 + "条");
        }
        Integer count2 = topicService.countTopicByType(2);
        if (count2 < number2) {
            return ResultData.error("当前多选题目只有" + count2 + "条");
        }
        Integer count3 = topicService.countTopicByType(3);
        if (count3 < number3) {
            return ResultData.error("当前判断题目只有" + count3 + "条");
        }
        try {
            Paper paper = paperService.create(title, number1, number2, number3);
            return ResultData.success(paper);
        } catch (Exception ex) {
            return ResultData.error(ex.getLocalizedMessage());
        }
    }

    // 删除试卷
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity remove(@PathVariable(value = "id") Long paperid) {
        Paper paper = paperService.get(paperid);
        if (paper == null) {
            return ResultData.error("没有此套试卷");
        }
        Scores scores = scoresRepository.findFirstByPaperId(paperid);
        if (scores != null) {
            return ResultData.error("此套试卷已经有考试记录,删除失败");
        }
        try {
            paperService.remove(paper);
            return ResultData.success("删除成功");
        } catch (Exception ex) {
            return ResultData.error(ex.getMessage());
        }
    }
}
