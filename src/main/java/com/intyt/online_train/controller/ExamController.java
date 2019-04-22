package com.intyt.online_train.controller;

import com.intyt.online_train.domain.Paper;
import com.intyt.online_train.domain.Scores;
import com.intyt.online_train.domain.User;
import com.intyt.online_train.service.impl.ExamServiceImpl;
import com.intyt.online_train.service.impl.PaperServiceImpl;
import com.intyt.online_train.service.impl.UserServiceImpl;
import com.intyt.online_train.utility.ResultData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = {"http://10.18.43.3:8000", "http://127.0.0.1:8000"}, allowCredentials = "true", maxAge = 3600)
@RestController
@RequestMapping(value = "/api/exams")
public class ExamController {
    private UserServiceImpl userService;
    private PaperServiceImpl paperService;
    private ExamServiceImpl examService;

    private RedisTemplate redisTemplate;
    @Autowired
    public void setRedisTemplate(RedisTemplate redisTemplate){
        this.redisTemplate=redisTemplate;
    }

    @Autowired
    public void setExamService(ExamServiceImpl examService) {
        this.examService = examService;
    }

    @Autowired
    public void setPaperService(PaperServiceImpl paperService) {
        this.paperService = paperService;
    }

    @Autowired
    public void setUserService(UserServiceImpl userService) {
        this.userService = userService;
    }

    // 查询某个试卷所有考试列表
    @RequestMapping(value = "/paper/{id}")
    public ResponseEntity getByPaper(@PathVariable(value = "id") Long paperId) {
        Paper paper = paperService.get(paperId);
        if (paper == null) {
            return ResultData.error("没有这套试卷");
        }
        return ResultData.success(examService.getByPaper(paperId));
    }

    // 查询当前学生所有考试记录列表
    @RequestMapping(value = "/user")
    public ResponseEntity getByUser() {
        User user = userService.current();
        if (user == null) {
            return ResultData.error("当前没有用户登录");
        }
        return ResultData.success(examService.getByUser(user));
    }

    //查询当前用户正在考试的记录(当前用户按试卷查询考试)
    @RequestMapping(value = "/user/paper/{id}")
    public ResponseEntity getByUserAndPaper(@PathVariable(value = "id")Long paperId){
        User user = userService.current();
        if(user==null){
            return ResultData.error("当前没有用户登录");
        }
        return ResultData.success(examService.getByUserAndPaper(user,paperId));
    }

    // 创建一个考试
    @RequestMapping(value = "", method = RequestMethod.POST)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity create(@RequestParam(value = "paperId") Long paperId) {
        User user = userService.current();
        if (user == null) {
            return ResultData.error("当前没有学生登录");
        }
        Paper paper = paperService.get(paperId);
        if (paper == null) {
            return ResultData.error("没有这套试卷");
        }
        Scores scores = examService.getByUserAndPaper(user, paperId);
        if (scores != null) {
            if (scores.getExamStatus() == 1) {
                return ResultData.success(scores); // 如果未到时间或未交卷把本次考试返回
            }
            if (scores.getExamStatus() == 2) {
                return ResultData.error("已经考过这个试卷");
            }
        }
        try {
            return ResultData.success(examService.create(user, paperId));
        } catch (Exception ex) {
            return ResultData.error(ex.getMessage());
        }
    }

    // 交卷 更新分数
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity update(@PathVariable(value = "id") Long scoresId,
                                 @RequestBody List<Map> body) {
        User user = userService.current();
        if (user == null) {
            return ResultData.error("当前没有学生登录");
        }
        Scores scores = examService.get(scoresId);
        if (scores == null) {
            return ResultData.error("没有这场考试");
        }
        if (scores.getExamStatus() == 2) {
            return ResultData.error("考试已经结束,不能提交试卷");
        }
        if (scores.getExamStatus() == 1) {
            // 计算分数,改变状态
            return ResultData.success(examService.update(scores, body,2));
        }
        return ResultData.error("试卷异常:" + scores.getExamStatus());
    }

    // 暂时保存答题结果
    @RequestMapping(value = "/temp/{id}",method = RequestMethod.PUT)
    public ResponseEntity tempCommit(@PathVariable(value = "id") Long scoresId,
                                     @RequestBody List<Map> body){
        User user = userService.current();
        if (user == null) {
            return ResultData.error("当前没有学生登录");
        }
        Scores scores = examService.get(scoresId);
        if (scores == null) {
            return ResultData.error("没有这场考试");
        }
        if (scores.getExamStatus() == 2) {
            return ResultData.error("考试已经结束,不能提交试卷");
        }
        if (scores.getExamStatus() == 1) {
            try {
                redisTemplate.opsForValue().set(scoresId,body);
            }catch (Exception e){
                e.printStackTrace();
                return ResultData.error("redis异常");
            }
        }
        return ResultData.success("暂时提交成功" );
    }

    // 获得暂时保存答题结果
    @RequestMapping(value = "/temp/{id}",method = RequestMethod.GET)
    public ResponseEntity tempGet(@PathVariable(value = "id") Long scoresId){
        User user = userService.current();
        if (user == null) {
            return ResultData.error("当前没有学生登录");
        }
        Scores scores = examService.get(scoresId);
        if (scores == null) {
            return ResultData.error("没有这场考试");
        }
        if (scores.getExamStatus() == 2) {
            return ResultData.error("考试已经结束,不能提交试卷");
        }
        List<Map> body=new ArrayList<>();
        if (scores.getExamStatus() == 1) {
            try {
                body= (List<Map>) redisTemplate.opsForValue().get(scoresId);
            }catch (Exception e){
                e.printStackTrace();
                return ResultData.error("redis异常");
            }
        }
        return ResultData.success(body);
    }
}
