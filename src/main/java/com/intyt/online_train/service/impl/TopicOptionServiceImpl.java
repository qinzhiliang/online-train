package com.intyt.online_train.service.impl;

import com.intyt.online_train.domain.TopicOption;
import com.intyt.online_train.domain.TopicOptionRepository;
import com.intyt.online_train.service.TopicOptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TopicOptionServiceImpl implements TopicOptionService {
    private TopicOptionRepository topicOptionRepository;

    @Override
    @Autowired
    public void setTopicOptionRepository(TopicOptionRepository topicOptionRepository) {
        this.topicOptionRepository = topicOptionRepository;
    }

    // 添加一条选项
    @Override
    public TopicOption create(String title, Integer flag) {
        TopicOption topicOption = new TopicOption();
        topicOption.setTitle(title);
        topicOption.setFlag(flag);
        topicOption.setIsDeleted(0);
        topicOptionRepository.save(topicOption);
        return topicOption;
    }
}
