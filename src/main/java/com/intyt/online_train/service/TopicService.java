package com.intyt.online_train.service;

import com.intyt.online_train.domain.PaperRepository;
import com.intyt.online_train.domain.Topic;
import com.intyt.online_train.domain.TopicOption;
import com.intyt.online_train.domain.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2018/8/22 0022.
 */
public interface TopicService {
    @Autowired
    void setPaperRepository(PaperRepository paperRepository);

    @Autowired
    void setTopicRepository(TopicRepository topicRepository);

    // 按题目类型统计试题
    Integer countTopicByType(Integer topicType);

    // 获取所有试题
    Page<Topic> list(Date beginTime, Date endTime, Integer topicType, String keyword, Integer page, Integer pageSize);

    // 通过id获取一个题目
    Topic get(Long id);

    // 新增试题
    Topic create(String title, Integer topicType, List<TopicOption> topicOptions, String answers);

    // 修改试题
    Topic update(Topic topic, String title, Integer topicType, List<TopicOption> topicOptions, String answers);

    // 删除试题
    void remove(Topic topic) throws Exception;
}
