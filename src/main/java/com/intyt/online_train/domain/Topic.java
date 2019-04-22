package com.intyt.online_train.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

// 试题表
@Entity
@Table(name = "topic")
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // 题目问题
    @Column(name = "title")
    private String title;

    // 题目类型
    @Column(name = "topic_type")
    private Integer topicType; // 1单选 2多选 3判断

    // 题目分数
    @JsonIgnore
    @Column(name = "score")
    private Float score;

    // 题目选项
    @OneToMany(cascade = {CascadeType.DETACH}, fetch = FetchType.EAGER)
    private List<TopicOption> topicOptions;

    // 题目答案
//    @JsonIgnore
    @Column(name = "answers")
    private String answers;

    // 题目创建时间
    @Column(name = "create_time")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JsonIgnore
    @Column(name = "is_deleted")
    private Integer isDeleted;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getTopicType() {
        return topicType;
    }

    public void setTopicType(Integer topicType) {
        this.topicType = topicType;
    }

    public Float getScore() {
        return score;
    }

    public void setScore(Float score) {
        this.score = score;
    }

    public List<TopicOption> getTopicOptions() {
        return topicOptions;
    }

    public void setTopicOptions(List<TopicOption> topicOptions) {
        this.topicOptions = topicOptions;
    }

    public String getAnswers() {
        return answers;
    }

    public void setAnswers(String answers) {
        this.answers = answers;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }
}
