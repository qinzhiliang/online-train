package com.intyt.online_train.service.impl;

import com.intyt.online_train.domain.*;
import com.intyt.online_train.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class TopicServiceImpl implements TopicService {
    private TopicRepository topicRepository;
    private PaperRepository paperRepository;

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

    // 按题目类型统计试题
    @Override
    public Integer countTopicByType(Integer topicType) {
        return topicRepository.countAllByTopicTypeAndIsDeleted(topicType, 0);
    }

    // 获取所有试题
    @Override
    public Page<Topic> list(Date beginTime, Date endTime, Integer topicType, String keyword, Integer page, Integer pageSize) {
        Specification<Topic> specification = new Specification<Topic>() {
            @Override
            public Predicate toPredicate(Root<Topic> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicateList = new ArrayList<>();
                predicateList.add(criteriaBuilder.equal(root.get("isDeleted").as(Integer.class), 0));
                if (beginTime != null) {
                    predicateList.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createTime").as(Date.class), beginTime));
                }
                if (endTime != null) {
                    predicateList.add(criteriaBuilder.lessThanOrEqualTo(root.get("createTime").as(Date.class), endTime));
                }
                if (topicType != 0) {
                    predicateList.add(criteriaBuilder.isNotNull(root.get("topicType")));
                    predicateList.add(criteriaBuilder.equal(root.get("topicType").as(Integer.class), topicType));
                }
                if (keyword != null && !keyword.isEmpty()) {
                    String likeWord = "%" + keyword + "%";
                    predicateList.add(criteriaBuilder.like(root.get("title").as(String.class), likeWord));
                }
                Predicate[] arrayType = new Predicate[predicateList.size()];
                return criteriaBuilder.and(predicateList.toArray(arrayType));
            }
        };
        Sort sort = new Sort(Sort.Direction.ASC, "id");
        if (page < 1) {
            page = 1;
        }
        return topicRepository.findAll(specification, PageRequest.of(page - 1, pageSize, sort));
    }

    // 通过id获取一个题目
    @Override
    public Topic get(Long id) {
        return topicRepository.findFirstByIdAndIsDeleted(id, 0);
    }

    // 新增试题
    @Override
    public Topic create(String title, Integer topicType, List<TopicOption> topicOptions, String answers) {
        Topic topic = new Topic();
        topic.setTitle(title);
        topic.setTopicType(topicType);
        if (topicType == 1) {
            topic.setScore(1F);
        }
        if (topicType == 2) {
            topic.setScore(2F);
        }
        if (topicType == 3) {
            topic.setScore(0.5F);
        }
        topic.setTopicOptions(topicOptions);
        topic.setAnswers(answers);
        topic.setIsDeleted(0);
        topic.setCreateTime(new Date());
        topicRepository.save(topic);
        return topic;
    }

    // 修改试题
    @Override
    public Topic update(Topic topic, String title, Integer topicType, List<TopicOption> topicOptions, String answers) {
        topic.setTitle(title);
        topic.setTopicType(topicType);
        if (topicType == 1) {
            topic.setScore(1F);
        }
        if (topicType == 2) {
            topic.setScore(2F);
        }
        if (topicType == 3) {
            topic.setScore(0.5F);
        }
        topic.setTopicOptions(topicOptions);
        topic.setAnswers(answers);
        topic.setIsDeleted(0);
        topicRepository.save(topic);
        return topic;
    }

    // 删除试题
    @Override
    public void remove(Topic topic) throws Exception {
        if (paperRepository.findFirstByTopicsContainsAndIsDeleted(topic, 0) == null) {
            topic.setIsDeleted(1);
        } else {
            throw new Exception("删除失败,有试卷正在使用这个题目");
        }
        topicRepository.save(topic);
    }
}
