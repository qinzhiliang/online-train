package com.intyt.online_train.service;

import com.intyt.online_train.domain.Paper;
import com.intyt.online_train.domain.PaperRepository;
import com.intyt.online_train.domain.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;

/**
 * Created by Administrator on 2018/8/22 0022.
 */
public interface PaperService {
    @Autowired
    void setTopicRepository(TopicRepository topicRepository);

    @Autowired
    void setPaperRepository(PaperRepository paperRepository);

    // 通过id获取试卷
    Paper get(Long id);

    // 获取试卷列表
    Map list(Date beginTime, Date endTime, String keyword, Integer page, Integer pageSize) throws ParseException;

    // 新建试卷
    @Transactional
    Paper create(String title, Integer number1, Integer number2, Integer number3);

    // 删除试卷
    void remove(Paper paper);
}
