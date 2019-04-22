package com.intyt.online_train.service;

import com.intyt.online_train.domain.TopicOption;
import com.intyt.online_train.domain.TopicOptionRepository;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Administrator on 2018/8/22 0022.
 */
public interface TopicOptionService {
    @Autowired
    void setTopicOptionRepository(TopicOptionRepository topicOptionRepository);

    // 添加一条选项
    TopicOption create(String title, Integer flag);
}
