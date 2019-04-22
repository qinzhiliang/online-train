package com.intyt.online_train.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

// 试题选项
@Entity
@Table(name = "topic_option")
public class TopicOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // 选项title
    @Column(name = "title")
    private String title;

    // 选项ABCD
    @Column(name = "flag")
    private Integer flag; // 1A 2B 3C 4D

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

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }
}
