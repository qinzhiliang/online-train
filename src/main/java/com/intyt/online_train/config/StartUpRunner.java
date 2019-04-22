package com.intyt.online_train.config;

import com.intyt.online_train.domain.Role;
import com.intyt.online_train.service.impl.TopicServiceImpl;
import com.intyt.online_train.service.impl.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Configuration
@Order(1)
public class StartUpRunner implements CommandLineRunner {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private UserServiceImpl userService;
    private TopicServiceImpl topicService;

    @Autowired
    public void setTopicService(TopicServiceImpl topicService) {
        this.topicService = topicService;
    }

    @Autowired
    public void setUserService(UserServiceImpl userService) {
        this.userService = userService;
    }

    @Override
    public void run(String... args) throws Exception {

        // 添加管理员类型
        try {
            if (userService.countRole().equals(0L)) {
                userService.createRole("ADMIN");
                userService.createRole("USER");
            }
        } catch (Exception ex) {
            logger.error(ex.getLocalizedMessage(), ex);
        }

        // 添加默认老师管理员和学生
        if (userService.count().equals(0L)) {
            Role adminRole = userService.getRole("ADMIN");
            if (adminRole != null) {
                try {
                    userService.create("admin", "admin", "老师", 1L);
                    userService.create("student", "admin", "学生", 2L);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
