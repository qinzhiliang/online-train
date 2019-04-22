package com.intyt.online_train.controller;

import com.intyt.online_train.domain.Topic;
import com.intyt.online_train.domain.TopicOption;
import com.intyt.online_train.service.impl.TopicOptionServiceImpl;
import com.intyt.online_train.service.impl.TopicServiceImpl;
import com.intyt.online_train.utility.ResultData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

@CrossOrigin(origins = {"http://10.18.43.3:8000", "http://127.0.0.1:8000"}, allowCredentials = "true", maxAge = 3600)
@RestController
@RequestMapping(value = "/api/topics")
public class TopicController {
    private TopicServiceImpl topicService;
    private TopicOptionServiceImpl topicOptionService;

    @Autowired
    public void setTopicOptionService(TopicOptionServiceImpl topicOptionService) {
        this.topicOptionService = topicOptionService;
    }

    @Autowired
    public void setTopicService(TopicServiceImpl topicService) {
        this.topicService = topicService;
    }

    // 获取所有试题
    @RequestMapping(value = "")
    public ResponseEntity list(@RequestParam(defaultValue = "0") Integer topicType,
                               @RequestParam(defaultValue = "") String beginDate,
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
            Page<Topic> topics = topicService.list(beginTime, endTime, topicType, keyword, page, pageSize);
            return ResultData.success(topics);
        } catch (Exception ex) {
            return ResultData.error(ex.getMessage());
        }
    }

    // 通过id获取单个题目
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity get(@PathVariable(value = "id") Long id) {
        Topic topic = topicService.get(id);
        if (topic == null) {
            return ResultData.error("没有这个题目或已被删除");
        }
        return ResultData.success(topic);
    }

    // 新增试题
    @RequestMapping(value = "", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity create(@RequestParam(value = "topicType", defaultValue = "0") Integer topicType,
                                 @RequestParam(value = "title", defaultValue = "") String title,
                                 @RequestParam(value = "option1", defaultValue = "") String option1,
                                 @RequestParam(value = "option2", defaultValue = "") String option2,
                                 @RequestParam(value = "option3", defaultValue = "") String option3,
                                 @RequestParam(value = "option4", defaultValue = "") String option4,
                                 @RequestParam(value = "answer", defaultValue = "") String answer) {

        if (title.isEmpty()) {
            return ResultData.error("题目不能为空");
        }
        if (topicType < 1 || topicType > 3) {
            return ResultData.error("题目类型不正确");
        }
        if (option1.isEmpty() || option2.isEmpty()) {
            return ResultData.error("至少添加两个选项");
        }
        String answers = answer.replace(",", "");
        if (answers.isEmpty()) {
            return ResultData.error("请至少选择一个正确答案");
        }
        List<TopicOption> topicOptions = new ArrayList<>();
        TopicOption topicOption1 = topicOptionService.create(option1, 1);
        topicOptions.add(topicOption1);
        TopicOption topicOption2 = topicOptionService.create(option2, 2);
        topicOptions.add(topicOption2);
        if (!option3.isEmpty()) {
            TopicOption topicOption3 = topicOptionService.create(option3, 3);
            topicOptions.add(topicOption3);
        }
        if (!option4.isEmpty()) {
            TopicOption topicOption4 = topicOptionService.create(option4, 4);
            topicOptions.add(topicOption4);
        }
        try {
            topicService.create(title, topicType, topicOptions, answers);
        } catch (Exception ex) {
            return ResultData.error(ex.getLocalizedMessage());
        }
        return ResultData.success("添加试题成功");
    }

    // 修改试题
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity update(@PathVariable(value = "id") Long id,
                                 @RequestParam(value = "topicType", defaultValue = "0") Integer topicType,
                                 @RequestParam(value = "title", defaultValue = "") String title,
                                 @RequestParam(value = "option1", defaultValue = "") String option1,
                                 @RequestParam(value = "option2", defaultValue = "") String option2,
                                 @RequestParam(value = "option3", defaultValue = "") String option3,
                                 @RequestParam(value = "option4", defaultValue = "") String option4,
                                 @RequestParam(value = "answer", defaultValue = "") String answer) {

        if (title.isEmpty()) {
            return ResultData.error("题目不能为空");
        }
        if (topicType < 1 || topicType > 3) {
            return ResultData.error("题目类型不正确");
        }
        if (option1.isEmpty() || option2.isEmpty()) {
            return ResultData.error("至少添加两个选项");
        }
        String answers = answer.replace(",", "");
        if (answers.isEmpty()) {
            return ResultData.error("请至少选择一个正确答案");
        }
        Topic topic = topicService.get(id);
        if (topic == null) {
            return ResultData.error("没有这个试题");
        }
        List<TopicOption> topicOptions = new ArrayList<>();
        TopicOption topicOption1 = topicOptionService.create(option1, 1);
        topicOptions.add(topicOption1);
        TopicOption topicOption2 = topicOptionService.create(option2, 2);
        topicOptions.add(topicOption2);
        if (!option3.isEmpty()) {
            TopicOption topicOption3 = topicOptionService.create(option3, 3);
            topicOptions.add(topicOption3);
        }
        if (!option4.isEmpty()) {
            TopicOption topicOption4 = topicOptionService.create(option4, 4);
            topicOptions.add(topicOption4);
        }
        try {
            topicService.update(topic, title, topicType, topicOptions, answers);
        } catch (Exception ex) {
            return ResultData.error(ex.getLocalizedMessage());
        }
        return ResultData.success(topic);
    }

    // 删除试题
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity remove(@PathVariable(value = "id") Long id) {
        Topic topic = topicService.get(id);
        if (topic == null) {
            return ResultData.error("没有这个题目");
        }
        try {
            topicService.remove(topic);
        } catch (Exception ex) {
            return ResultData.error(ex.getMessage());
        }
        return ResultData.success("删除成功");
    }


}
