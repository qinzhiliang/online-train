package com.intyt.online_train.controller;

import com.intyt.online_train.domain.Role;
import com.intyt.online_train.domain.Scores;
import com.intyt.online_train.domain.ScoresRepository;
import com.intyt.online_train.domain.User;
import com.intyt.online_train.service.impl.UserServiceImpl;
import com.intyt.online_train.utility.ResultData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = {"http://10.18.43.3:8000", "http://127.0.0.1:8000"}, allowCredentials = "true", maxAge = 3600)
@RestController
@RequestMapping(value = "/api/users")
public class UserController {
    private UserServiceImpl userService;
    private ScoresRepository scoresRepository;

    @Autowired
    public void setScoresRepository(ScoresRepository scoresRepository) {
        this.scoresRepository = scoresRepository;
    }

    @Autowired
    public void setUserService(UserServiceImpl userService) {
        this.userService = userService;
    }

    // 获取当前用户
    @RequestMapping(value = "/current", method = RequestMethod.GET)
    public ResponseEntity currentUser() {
        User user = userService.current();
        if (user == null) {
            return ResultData.error("没有查询到当前用户");
        } else {
            return ResultData.success(userService.current());
        }
    }

    // 按id获取用户
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity get(@PathVariable Long id) {
        User user = userService.get(id);
        if (user == null) {
            return ResultData.error("没有这个用户或已被删除");
        }
        return ResultData.success(user);
    }

    // 获取所有用户列表
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity list(@RequestParam(defaultValue = "0") Long roleId,
                               @RequestParam(defaultValue = "") String beginDate,
                               @RequestParam(defaultValue = "") String endDate,
                               @RequestParam(defaultValue = "") String keyword,
                               @RequestParam(defaultValue = "1") Integer page,
                               @RequestParam(defaultValue = "10") Integer pageSize) {
        Role role = null;
        Date beginTime = null;
        Date endTime = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (roleId > 0) {
            role = userService.getRole(roleId);
            if (role == null) {
                return ResultData.error("管理员类型不正确");
            }
        }
        if (!beginDate.isEmpty()) {
            try {
                beginTime = simpleDateFormat.parse(beginDate);
            } catch (Exception ex) {
                return ResultData.error("开始时间错误");
            }
        }
        if (!endDate.isEmpty()) {
            try {
                endTime = simpleDateFormat.parse(endDate);
            } catch (Exception ex) {
                return ResultData.error("结束时间错误");
            }
        }
        try {
            Page<User> users = userService.list(beginTime, endTime, role, keyword, page, pageSize);
            return ResultData.success(users);
        } catch (Exception ex) {
            return ResultData.error(ex.getMessage());
        }
    }

    // 修改密码
    @RequestMapping(value = "/password", method = RequestMethod.PUT)
    public ResponseEntity changePassword(@RequestBody Map body) throws Exception {
        User user = userService.current();
        if (user == null) {
            return ResultData.error("当前没有用户登录");
        }
        if (!body.containsKey("oldPassword")) {
            return ResultData.error("缺少旧密码");
        }
        if (!body.containsKey("newPassword")) {
            return ResultData.error("缺少新密码");
        }
        String oldPassword = body.get("oldPassword").toString();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(oldPassword, user.getPassword())) {
            return ResultData.error("原密码不正确");
        }
        String newPassword = body.get("newPassword").toString();
        if (encoder.matches(newPassword, user.getPassword())) {
            return ResultData.error("新密码与旧密码相同");
        }
        user = userService.changePassword(user, newPassword);
        return ResultData.success(user);
    }

    // 新增用户 单用户Json格式
//    @RequestMapping(value = "", method = RequestMethod.POST)
//    public ResponseEntity create(@RequestBody Map body) throws Exception {
//        String name;
//        String username;
//        String password;
//        Long roleId = 2L;
//        if (!body.containsKey("username")) {
//            return ResultData.error("缺少username");
//        }
//        if (!body.containsKey("name")) {
//            return ResultData.error("缺少name");
//        }
//        if (body.containsKey("password")) {
//            password = body.get("password").toString();
//        } else {
//            password = "123456";
//        }
//        if (body.containsKey("roleId")) {
//            roleId = Long.valueOf(body.get("roleId").toString());
//        }
//        name = body.get("name").toString();
//        username = body.get("username").toString();
//        User user = userService.getByUsername(username);
//        if (user != null) {
//            return ResultData.error("用户已经存在");
//        }
//        try {
//            user = userService.create(username, password, name, roleId);
//            return ResultData.success(user);
//        } catch (Exception ex) {
//            return ResultData.error(ex.getLocalizedMessage());
//        }
//    }

    // 新增用户 单用户form表单格式
    @RequestMapping(value = "", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity create(@RequestParam(value = "name", defaultValue = "") String name,
                                 @RequestParam(value = "username", defaultValue = "") String username,
                                 @RequestParam(value = "password", defaultValue = "") String password,
                                 @RequestParam(value = "roleId", defaultValue = "0") Long roleId) throws Exception {
        if (username.isEmpty()) {
            return ResultData.error("缺少username");
        }
        if (name.isEmpty()) {
            return ResultData.error("缺少name");
        }
        if (password.isEmpty()) {
            password = "123456";
        }
        if (roleId <= 0 || roleId > 2) {
            roleId = 2L;
        }
        User user = userService.getByUsername(username);
        if (user != null) {
            return ResultData.error("用户已经存在");
        }
        try {
            user = userService.create(username, password, name, roleId);
            return ResultData.success(user);
        } catch (Exception ex) {
            return ResultData.error(ex.getLocalizedMessage());
        }
    }

    // 新增用户 多用户
    @RequestMapping(value = "duo", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity create2(@RequestBody List<User> body) throws Exception {
        for (User aBody : body) {
            String name;
            String username;
            String password;
            Long roleId = 2L;
            if (aBody.getUsername() == null) {
                return ResultData.error("缺少username");
            }
            if (aBody.getName() == null) {
                return ResultData.error("缺少name");
            }
            if (aBody.getPassword() != null) {
                password = aBody.getPassword();
            } else {
                password = "123456";
            }
            if (aBody.getRole().getId() != null) {
                roleId = aBody.getRole().getId();
            }
            name = aBody.getName();
            username = aBody.getUsername();
            User user = userService.getByUsername(username);
            if (user != null) {
                return ResultData.error("用户已经存在");
            }
            try {
                userService.create(username, password, name, roleId);
            } catch (Exception ex) {
                return ResultData.error(ex.getLocalizedMessage());
            }
        }
        return ResultData.success("添加用户成功");
    }

    // 修改用户
    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity update(@PathVariable Long id,
                                 @RequestBody Map body) {
        User user = userService.get(id);
        String name = null;
        String password = null;
        Long roleId = null;
        Role role = null;
        if (user == null) {
            return ResultData.error("没有这个用户");
        }
        if (body.containsKey("name")) {
            name = body.get("name").toString();
        }
        if (body.containsKey("password")) {
            password = body.get("password").toString();
        }
        if (body.containsKey("roleId")) {
            roleId = Long.valueOf(body.get("roleId").toString());
        }
        if (roleId != null) {
            role = userService.getRole(roleId);
            if (role == null) {
                return ResultData.error("没有这种管理员类型");
            }
        }
        return ResultData.success(userService.update(user, name, password, role));
    }

    // 删除用户
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity remove(@PathVariable(value = "id") Long id) {
        User user = userService.get(id);
        if (user == null) {
            return ResultData.error("没有这个用户");
        }
        Scores scores = scoresRepository.findFirstByUser(user);
        if (scores != null) {
            return ResultData.error("这个用户已经参加过考试,删除失败");
        }
        userService.remove(user);
        return ResultData.success("删除成功");
    }
}
