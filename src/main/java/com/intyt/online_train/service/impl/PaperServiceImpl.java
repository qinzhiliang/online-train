package com.intyt.online_train.service.impl;

import com.intyt.online_train.domain.Paper;
import com.intyt.online_train.domain.PaperRepository;
import com.intyt.online_train.domain.Topic;
import com.intyt.online_train.domain.TopicRepository;
import com.intyt.online_train.service.PaperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class PaperServiceImpl implements PaperService {
    private PaperRepository paperRepository;
    private TopicRepository topicRepository;

    @Override
    @Autowired
    public void setTopicRepository(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    @Override
    @Autowired
    public void setPaperRepository(PaperRepository paperRepository) {
        this.paperRepository = paperRepository;
    }

    // 通过id获取试卷
    @Override
    public Paper get(Long id) {
        return paperRepository.findFirstByIdAndIsDeleted(id, 0);
    }

    // 获取试卷列表 查询时会把所有题目也一起查询出来.
//    public Page<Paper> list(Date beginTime, Date endTime, String keyword, Integer page, Integer pageSize) {
//        Specification<Paper> specification = new Specification<Paper>() {
//            @Override
//            public Predicate toPredicate(Root<Paper> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
//                List<Predicate> predicateList = new ArrayList<>();
//                predicateList.add(criteriaBuilder.equal(root.get("isDeleted").as(Integer.class), 0));
//                if (beginTime != null) {
//                    predicateList.add(criteriaBuilder.greaterThan(root.get("createTime").as(Date.class), beginTime));
//                }
//                if (endTime != null) {
//                    predicateList.add(criteriaBuilder.lessThan(root.get("createTime").as(Date.class), endTime));
//                }
//                if (keyword != null && !keyword.isEmpty()) {
//                    String likeWord = "%" + keyword + "%";
//                    predicateList.add(criteriaBuilder.like(root.get("title").as(String.class), likeWord));
//                }
//                Predicate[] arrayType = new Predicate[predicateList.size()];
//                return criteriaBuilder.and(predicateList.toArray(arrayType));
//            }
//        };
//        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
//        if (page < 1) {
//            page = 1;
//        }
//        return paperRepository.findAll(specification, PageRequest.of(page - 1, pageSize, sort));
//    }

    // 获取试卷列表
    @Override
    public Map list(Date beginTime, Date endTime, String keyword, Integer page, Integer pageSize) throws ParseException {
        if (beginTime == null) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            beginTime = format.parse("1970-01-01");
        }
        if (endTime == null) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            endTime = format.parse("2080-01-01");
        }
        if (keyword.isEmpty()) {
            keyword = "%";
        }
        if (page < 0) {
            page = 1;
        }
        Integer skipLines = (page - 1) * pageSize;
        Integer countPaper = paperRepository.countAllByTitleAndCreateTime(beginTime, endTime, keyword);
        List<Map> paperList = paperRepository.findAllByTitleAndCreateTime(beginTime, endTime, keyword, skipLines, pageSize);
        Map responseData = new HashMap();
        responseData.put("content", paperList);
        responseData.put("page", page);
        responseData.put("pageSize", pageSize);
        responseData.put("countPapers", countPaper);
        Integer countPages = (countPaper % pageSize == 0) ? countPaper / pageSize : countPaper / pageSize + 1;
        responseData.put("countPages", countPages);
        return responseData;
    }

    // 新建试卷
    @Override
    @Transactional
    public Paper create(String title, Integer number1, Integer number2, Integer number3) {
        Paper paper = new Paper();
        List<Topic> topics = new ArrayList<>();
        if (number1 > 1) {
            topics.addAll(topicRepository.findAllOrderByRandLimit(1, number1));
        }
        if (number2 > 1) {
            topics.addAll(topicRepository.findAllOrderByRandLimit(2, number2));
        }
        if (number3 > 1) {
            topics.addAll(topicRepository.findAllOrderByRandLimit(3, number3));
        }
        paper.setTitle(title);
        paper.setTopics(topics);
        paper.setNumber1(number1);
        paper.setNumber2(number2);
        paper.setNumber3(number3);
        paper.setCreateTime(new Date());
        paper.setIsDeleted(0);
        paperRepository.save(paper);
        return paper;
    }

    // 删除试卷
    @Override
    public void remove(Paper paper) {
        paper.setIsDeleted(1);
        paperRepository.save(paper);
    }
}
